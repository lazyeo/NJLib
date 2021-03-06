package com.all2h.njlib;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.othershe.baseadapter.ViewHolder;
import com.othershe.baseadapter.interfaces.OnItemClickListener;
import com.othershe.baseadapter.interfaces.OnLoadMoreListener;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;


public class ResultListActivity extends AppCompatActivity {
    private String resultNumText;
    private ResultListAdapter mAdapter;
    private RecyclerView mRecyclerView;
    private boolean isFailed = true;
    private int itemNum = 1;
    private CardView headerView ;

    private String word;

    private RecyclerView.LayoutManager mLayoutManager;
    private List<ArrayList<String>> data = new ArrayList<>();
    private List<ArrayList<String>> moreData = new ArrayList<>();

    private String nextPageUrl ;

    Handler mHandler = new Handler() {

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case 0:
                    mAdapter.setNewData(data);
                    itemNum = itemNum + 10;
                    break;
                case 1:
                    mAdapter.setLoadMoreData(moreData);
                    moreData.clear();
                    itemNum = itemNum + 10;
                    break;
                default:
                    break;
            }


        }

    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_result_list);

        Intent intent = getIntent();
        String keyWord = intent.getStringExtra("keyWord");
        word = keyWord;
        loadResult(keyWord);

        mRecyclerView = (RecyclerView) findViewById(R.id.result_list_view);

        //初始化adapter
        mAdapter = new ResultListAdapter(this, null, true);

        //初始化EmptyView
        View emptyView = LayoutInflater.from(this).inflate(R.layout.empty_layout, (ViewGroup) mRecyclerView.getParent(), false);
        mAdapter.setEmptyView(emptyView);

        //初始化 开始加载更多的loading View
        mAdapter.setLoadingView(R.layout.load_loading_layout);

        //设置加载更多触发的事件监听
        mAdapter.setOnLoadMoreListener(new OnLoadMoreListener() {
            @Override
            public void onLoadMore(boolean isReload) {
                loadMore();
            }
        });

        //设置item点击事件监听
        mAdapter.setOnItemClickListener(new OnItemClickListener<ArrayList<String>>() {

            @Override
            public void onItemClick(ViewHolder viewHolder, ArrayList<String> data, int position) {
                Toast.makeText(ResultListActivity.this, data.get(0), Toast.LENGTH_SHORT).show();
                bookDetail(data.get(7),BookDetailActivity.class);

            }

        });

        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        mRecyclerView.setLayoutManager(layoutManager);

        mRecyclerView.setAdapter(mAdapter);







    }

    public void loadResult(final String word){

        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    /*
                    这里面的页面元素后续有时间应当改为读取配置文件，配置文件可动态更新，避免页面元素名称修改导致无法解析
                     */

                    //从一个URL加载一个Document对象。
                    Document doc = Jsoup.connect("http://opac.jslib.org.cn/F/").data("find_code","WRD").data("func","find-b").data("local_base","CNBOK").data("request",word).get();
                    //选择搜索数量文字
                    Elements resultNum = doc.select("div.hitnum");
                    resultNumText = resultNum.text().replace(" ","");

                    Elements a = doc.select("a");


                   //判断并获取下一页链接地址
                    for (int i=0;i<a.size();i++){

//                            Log.i("第"+i+"条",a.get(i).text());
                            if(Objects.equals(a.get(i).text(), "结果列表|")){
                                nextPageUrl = a.get(i).attr("href");
                                break;
                            }
                    }
//                    Log.i("下一页",nextPageUrl);
                    String[] temp = nextPageUrl.split("\\?");
                    nextPageUrl = temp[0];



//                    Log.i("下一页",nextPageUrl);

                    //测试第一页搜索结果有多少条，若为零应该就是没结果
                    Log.i("数量",resultNumText);


                    Elements items = doc.select("table.items");

                    for (int i=0;i<items.size();i++){

                        ArrayList<String> itemData = new ArrayList<>();

                        Object itemObj = new Object();

                        //书名
                        itemData.add(items.get(i).select("div.itemtitle").text());
                        //作者
                        itemData.add(items.get(i).select("td.content").get(0).text());
                        //索书号
                        itemData.add(items.get(i).select("td.content").get(1).text());
                        //出版社
                        itemData.add(items.get(i).select("td.content").get(2).text());
                        //出版年份
                        itemData.add(items.get(i).select("td.content").get(3).text());
                        //馆藏信息
                        itemData.add(items.get(i).select("td.libs").text().replace(" ",""));
                        //封面图片URL
                        itemData.add(items.get(i).select("td.cover").select("img").attr("src"));
                        //书籍详情URL
                        itemData.add(items.get(i).select("td.cover").select("a").attr("href"));

                        data.add(itemData);
                    }
//                    Log.i("有没有",data.toString());

                }catch(Exception e) {
                    Log.i("mytag", e.toString());
                }
                mHandler.sendEmptyMessage(0);


            }
        }).start();


    }



    private void loadMore() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    /*
                    这里面的页面元素后续有时间应当改为读取配置文件，配置文件可动态更新，避免页面元素名称修改导致无法解析
                     */

                    //从一个URL加载一个Document对象。
                    Document doc = Jsoup.connect(nextPageUrl).data("func","short-jump").data("jump",String.valueOf(itemNum)).get();
                    //选择搜索数量文字
                    Elements resultNum = doc.select("div.hitnum");
                    resultNumText = resultNum.text().replace(" ","");


                    //测试第一页搜索结果有多少条，若为零应该就是没结果
                    Log.i("数量",resultNumText);


                    Elements items = doc.select("table.items");

                    for (int i=0;i<items.size();i++){

                        ArrayList<String> itemData = new ArrayList<>();

                        Object itemObj = new Object();

                        //书名
                        itemData.add(items.get(i).select("div.itemtitle").text());
                        //作者
                        itemData.add(items.get(i).select("td.content").get(0).text());
                        //索书号
                        itemData.add(items.get(i).select("td.content").get(1).text());
                        //出版社
                        itemData.add(items.get(i).select("td.content").get(3).text());
                        //出版年份
                        itemData.add(items.get(i).select("td.content").get(2).text());
                        //馆藏信息
                        itemData.add(items.get(i).select("td.libs").text().replace(" ",""));
                        //封面图片URL
                        itemData.add(items.get(i).select("td.cover").select("img").attr("src"));
                        //书籍详情URL
                        itemData.add(items.get(i).select("td.cover").select("a").attr("href"));

                        moreData.add(itemData);
                    }
//                    Log.i("有没有",data.toString());

                }catch(Exception e) {
                    Log.i("mytag", e.toString());
                }
                mHandler.sendEmptyMessage(1);


            }
        }).start();

    }

    private void bookDetail(String url,Class targetClass) {
        Intent intent = new Intent(ResultListActivity.this,targetClass);

        intent.putExtra("bookUrl",url);
        startActivity(intent);

        //finish();
    }

}
