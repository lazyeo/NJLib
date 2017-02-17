package com.all2h.njlib;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.all2h.njlib.zxing.encode.EncodingHandler;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.google.zxing.WriterException;

import java.io.UnsupportedEncodingException;




public class MainActivity extends AppCompatActivity {

    private SharedPreferences preferences ;
    private TextView textView;
    private EditText editText;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        textView =(TextView)findViewById(R.id.text);
        editText = (EditText)findViewById(R.id.editText);
        preferences = getSharedPreferences("userID", Activity.MODE_PRIVATE);
        textView.setText(preferences.getString("id","暂未绑定ID"));


        // Obtain the FirebaseAnalytics instance.
        FirebaseAnalytics.getInstance(this);



    }

    public void toActivity(View button){

        switch (button.getId()){
            case R.id.search :
                startActivity(SearchActivity.class);
                break;
            case R.id.scan:
                startActivity(CommonScanActivity.class);
                break;
        }


    }

    private void startActivity(Class targetClass) {
        Intent intent = new Intent(MainActivity.this, targetClass);
        startActivity(intent);
        overridePendingTransition(android.R.anim.fade_in,
                android.R.anim.fade_out);
        //finish();
    }

    public void binding(View button){


        SharedPreferences.Editor editor = preferences.edit();
        //存储数据时选用对应类型的方法
        editor.putString("id",editText.getText().toString());
        //提交保存数据
        editor.apply();

        textView.setText(preferences.getString("id",""));


    }


    public void createImg(View button){

        ImageView code_img = (ImageView)findViewById(R.id.code_img);

        switch (button.getId()){
            case R.id.create_bar :
                code_img.setImageBitmap(createCode(editText.getText().toString(),0,300,600));
                break;
            case R.id.create_qr  :
                code_img.setImageBitmap(createCode(editText.getText().toString(),1,400,400));
                break;
        }





    }


    public Bitmap createCode(String text, int type, int length, int width){
        Bitmap codeImg = null;
        switch (type){
            //条形码生成
            case 0 :
                try {
                    codeImg = EncodingHandler.createBarCode(text, width, length);
                } catch (Exception e) {
                    Toast.makeText(this,"输入的内容条形码不支持！",Toast.LENGTH_SHORT).show();
                    e.printStackTrace();
                }
                break;

                //二维码生成
            case 1 :
                try {
                    codeImg= EncodingHandler.create2Code(text, width);
                } catch (WriterException e) {
                    e.printStackTrace();
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
                break;

        }
        return codeImg;

    }
    /*
    可用测试id：
    NL00036421
     */

//    public void test(View button){
//
//
//        new Thread(new Runnable() {
//            @Override
//            public void run() {
//                try {
//                    //从一个URL加载一个Document对象。
//                    Document doc = Jsoup.connect("http://app.jslib.org.cn/n_xj/xj.php?id=NL00036421").get();
//                    //选择“单本书”所在节点
//                    Element elements = doc.getElementById("MessageTitle");
//                    //选择
//                    Elements elements1 = doc.select("ul.wxlist");
//                    int num = elements1.size();
//
//                    Log.i("mytag",elements1.select("td.t2").get(0).select("input").attr("value") );
//                    Log.i("mytag","clicked");
//                }catch(Exception e) {
//                    Log.i("mytag", e.toString());
//                }
//            }
//        }).start();
//
//    }
}