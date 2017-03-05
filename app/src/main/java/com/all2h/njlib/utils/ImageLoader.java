package com.all2h.njlib.utils;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.util.LruCache;
import android.widget.ImageView;

import com.jakewharton.disklrucache.DiskLruCache;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * 作者：Linqiang
 * 时间：2016/5/17:11:17
 * 邮箱: linqiang2010@outlook.com
 * 说明：图片加载工具类:使用内存缓存,异步,多线程,MD5,图片压缩
 * URL:http://www.codingnote.net/2016/05/17/Android批量加载网络图片存在的问题及简单实现/
 */

public class ImageLoader {
    public static final int LOAD_FINISH=101;
    public static final int NEW_TASK=102;
    //线程池中线程的数量 线程数应该与设备中CPU的核数相同(将任务队列的任务放在线程池中)
    public  static  int Thread_Count;
    //上下文
    public  static Context c;
    //内存缓存
    public static LruCache<String,Bitmap> MemreyCache;
    //任务队列 将下载的任务放在任务队列中 双向任务队列可扩展 可根据业务选择先进先出还是后进先出
    public  static LinkedBlockingDeque<Runnable> TaskQueue;
    //线程池
    public  static ExecutorService exec;
    //任务完成后刷新的UIHandler  UIhandler,工作线程取得的图片无法在主线程刷新UI,所以需要一个运行在主线程的Handler
    public static Handler uiHandler;
    //从任务队列中取任务的handler 这是运行在工作线程的Handler,通知线程池启动下载任务时使用
    public static Handler pollHandler;
    //因为工作线程的Handle无法单独存在  需要一个工作线程盛放工作线程Handler
    public static Thread pollThread;
    //磁盘缓存
    public  static DiskLruCache diskCache;
    //标识的作用,用来确保imageloader只进行一次初始化
    public  static  boolean isFirstTime=true;

    /**
     * Init.
     * 初始化所有属性的值
     *
     * @param context the context
     */
    public  static  void init(Context context){
        //如果是第一次进来则初始化
        if(!isFirstTime)return;
        c=context;
        //获取手机中CPU核心数
        Thread_Count=getNumberOfCores();
        //取程序的最大内存的8分之一作为图片的内存缓存 并重写方法计算图片尺寸
        MemreyCache = new LruCache<String,Bitmap>((int) (Runtime.getRuntime().maxMemory()/8)){
            @Override
            protected int sizeOf(String key, Bitmap value) {
                return value.getHeight()*value.getRowBytes();
            }
        };
        //初始化磁盘缓存: 四个参数:存储路径,版本号,一个key对应几个value,空间大小
        try {
            diskCache=DiskLruCache.open(getFilePath(),getAppVersion(),1,1024*1024*10);
        } catch (IOException e) {
            e.printStackTrace();
        }
        //初始化任务队列
        TaskQueue=new LinkedBlockingDeque<Runnable>();
        //初始化线程池
        exec= Executors.newFixedThreadPool(Thread_Count);
        //初始化uiHandler 并绑定主线程的looper 运行在主线程
        uiHandler = new Handler(Looper.getMainLooper()){
            @Override
            public void handleMessage(Message msg) {
                //利用线程池中执行完毕的内容,来刷新listview
                if(msg.what==LOAD_FINISH){
                    ValueObject vo = (ValueObject) msg.obj;
                    ImageView iv = vo.iv;
                    String url = vo.url;
                    Bitmap bitmap = vo.bitmap;
                    //判断iv在发起任务时所绑定的URL是否和下载完成后的URL是否相等
                    //如果相等则显示图片
                    if(iv.getTag().toString().equals(url)){
                        iv.setImageBitmap(bitmap);
                    }
                }else{

                    super.handleMessage(msg);
                }
            }
        };
        //创建工作线程并创建pollHandler
        pollThread = new Thread(){
            @Override
            public void run() {
                Looper.prepare();
                //创建pollhandler
                pollHandler = new Handler(){
                    @Override
                    public void handleMessage(Message msg) {
                        if(msg.what==NEW_TASK){
                            //现在任务队列中放入了新的下载任务
                            try {
                                //从线程池中取任务
                                Runnable task= TaskQueue.takeFirst();
                                //将取出的任务放到线程池中去执行
                                exec.execute(task);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                        }else{

                            super.handleMessage(msg);
                        }
                    }
                };

                Looper.loop();//这个方法一旦启动 写在此方法之后的代码都得不到执行!!
                //  super.run();
            }
        };
        //启动工作线程
        pollThread.start();
        //修改标识
        isFirstTime=false;
    }

    /**
     * 获得程序版本号
     * @return
     */
    private static int getAppVersion() {
        try {
            PackageInfo info = c.getPackageManager().getPackageInfo(c.getPackageName(),0);
            return  info.versionCode;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return 0;
    }

    /**
     * 获得缓存路径
     * @return
     */
    private static File getFilePath() {
        //获得程序默认缓存路径
        String defaultPath=  c.getExternalCacheDir().getPath();
        return new File(defaultPath,"imageloadcache");
    }

    /**
     * Load image.
     * 获取url指向的图像并放到iv中显示
     * @param url the url 路径
     * @param iv   the iv imageVIew
     */
    public  static  void loadImage(final String url, final ImageView iv){
        if(isFirstTime){
            throw  new RuntimeException("ImageLoade未做初始化!");
        }
        Log.i("TAG", "loadImage: " +url);
        //先判断url所指向的图片在缓存中是否有缓存
        //将url转为MD5格式
        String md5Url = getMd5(url);
        //给ImageView 绑定一个URL,用于判断下载完成以后图片是否需要显示
        iv.setTag(md5Url);
        //1.从内存缓存中取图像
        Bitmap bitmap =MemreyCache.get(md5Url);
        if(bitmap!=null){
            Log.i("TAG", "loadImage: 图像是从内存缓存中读取的" );
            iv.setImageBitmap(bitmap);
            return;
        }
        //2.从磁盘缓存中取图像
        try {
            DiskLruCache.Snapshot snapshot = diskCache.get(md5Url);
            if(snapshot!=null){
                Log.i("TAG", "loadImage: 图像是从磁盘缓存中获取的");
                //磁盘缓存中有此图像
                InputStream in= snapshot.getInputStream(0);
                bitmap= BitmapFactory.decodeStream(in);
                //将磁盘缓存中的图像也放在内存缓存中
                MemreyCache.put(md5Url,bitmap);
                iv.setImageBitmap(bitmap);
                return;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        //3.代码走的这说明内存缓存和磁盘缓存中都没有此图像
        //去网上下载
        TaskQueue.add(new Runnable() {
            @Override
            public void run() {
                // 下载任务
                //发起网络连接,获得图像资源
                try {
                    URL httpurl = new URL(url);
                    HttpURLConnection con = (HttpURLConnection) httpurl.openConnection();
                    con.setRequestMethod("GET");
                    con.setDoInput(true);
                    con.connect();
                    InputStream in = con.getInputStream();
                    //1.要对图片进行适当的压缩
                    Bitmap bitmap = compress(in, iv);
                    in.close();
                    //2.将图片存储到内存缓存
                    MemreyCache.put(getMd5(url), bitmap);
                    //3.将图片存储到外存缓存
                    DiskLruCache.Editor editor =  diskCache.edit(getMd5(url));
                    OutputStream os= editor.newOutputStream(0);
                    //将图像以输出流的方式存储
                    bitmap.compress(Bitmap.CompressFormat.JPEG,100,os);
                    //提交存储!!
                    editor.commit();
                    //写日志 (可选操作)
                    diskCache.flush();
                    //4.下载完成通知UI线程
                    //创建ValueObject对象 存储发起任务的ImageView,ImageView要显示图片的URL,URL对应的图片
                    //目的是为了在UI更新时判断图片是否需要显示,避免listView复用时图片刷新.跳动
                    ValueObject vo = new ValueObject(iv,getMd5(url),bitmap);
                    //通知主线程更新UI
                    Message.obtain(uiHandler, LOAD_FINISH,vo).sendToTarget();
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
        });
        //发消息给pollHandler 让它去线程池去启动下载任务
        Message.obtain(pollHandler,NEW_TASK).sendToTarget();
    }

    /**
     * 根据要显示的imageview的大小 对图像进行压缩
     * @param in 图像源
     * @param iv 要显示图像的ImageView
     * @return 图像
     */
    private static Bitmap compress(InputStream in, ImageView iv) {
        Bitmap bitmap=null;
        try{
            //1.先获取原始图像数据源的尺寸大小 在不获取图像的前提下获取图片尺寸的大小
            //将in转为byte[] 借助options实现
            ByteArrayOutputStream out =new ByteArrayOutputStream();
            byte[] buff=new byte[4096];
            int len=-1;
            //循环读取数据
            while ((len=in.read(buff))!=-1){
                //将读取的信息写入 ByteArrayOutputStream
                out.write(buff,0,len);
            }
            //设定options参数
            BitmapFactory.Options opts= new BitmapFactory.Options();
            opts.inJustDecodeBounds=true;
            //当 inJustDecodeBounds设置了true以后,BitmapFactory不再返回bitmap,只返回opts信息
            //这个方法可以用在加载图片之前先获取图片信息时使用
            BitmapFactory.decodeByteArray(out.toByteArray(),0,out.toByteArray().length,opts);
            //2.通过opts获取宽高信息
            int width = opts.outWidth;//宽度信息
            int height = opts.outHeight;//高度信息
            //以下两个值可能为0
            int targetWidth =iv.getWidth();//iv的宽度
            int targetHeight = iv.getHeight();//iv的高度
            //如果ImageView的高和宽取不到
            if(targetWidth==0||targetHeight==0){
                //在iamgeview 的宽高获取不到的情况下,有如下解决方案
                //1.可以手动指定一个值
                //2.可以用整个设备屏幕的宽高值进行压缩
                targetWidth=c.getResources().getDisplayMetrics().widthPixels;
                targetHeight=c.getResources().getDisplayMetrics().heightPixels;
            }
            //3.计算压缩比率 判断是否需要压缩
            int sanpleSize = 1;
            if(width*1.0/targetWidth>1||height*1.0/targetHeight>1){
                //说明图像源比目标显示尺寸大
                //计算压缩比 两数取其大 并向上取整(一定要向上取整!!!)否则强转容易丢失精度!!!
                sanpleSize= (int) Math.ceil( Math.max(width*1.0/targetWidth,height*1.0/targetHeight));
            }
            //4.根据压缩比率压缩图像
            //将压缩比率设置给opts
            opts.inSampleSize=sanpleSize;
            //必须要设置以下这句话,要不设置不能生效!!!!!!!
            opts.inJustDecodeBounds=false;
            bitmap = BitmapFactory.decodeByteArray(out.toByteArray(),0,out.toByteArray().length,opts);
            out.close();//关闭资源
        }catch (Exception e){
            e.printStackTrace();
        }
        return bitmap;
    }

    /**
     * MD5算法 将URL地址转换成统一的32位长度16进制数据
     * @param url 地址
     * @return 字符串
     */
    private static String getMd5(String url) {
        String result="";
        try {
            //指定md5算法
            MessageDigest md = MessageDigest.getInstance("md5");
            md.update(url.getBytes());
            byte[] bytes =md.digest();
            StringBuilder sb = new StringBuilder();
            for(byte b :bytes){
                String str = Integer.toHexString(b & 0xFF);
                if(str.length()==1){
                    sb.append("0");
                }
                sb.append(str);
            }
            result=sb.toString();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return result;
    }

    //获取手机中CPU核心数
    private static int getNumberOfCores() {
        File file = new File("/sys/devices/system/cpu/");
        File[] files= file.listFiles(new FilenameFilter() {
            @Override
            public boolean accept(File dir, String filename) {
                return filename.matches("cpu[0-9]");
            }
        });
        return files.length>0?files.length:1;
    }
    private  static class ValueObject{
        ImageView iv;//发起下载任务的那个ImageView
        String url;//IamgeView 对应的URL
        Bitmap bitmap;//URL对应的图像

        public ValueObject(ImageView iv, String url, Bitmap bitmap) {
            this.iv = iv;
            this.url = url;
            this.bitmap = bitmap;
        }
    }
}