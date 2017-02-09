package com.all2h.njlib;

import android.content.Intent;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.util.ArrayList;
import android.os.Handler;
import android.widget.TextView;


public class ResultListActivity extends AppCompatActivity {
    private String resultNumText;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;
    private ArrayList<ArrayList<String>> data = new ArrayList<>();
    Handler mHandler = new Handler() {

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case 0:
                    //完成主界面更新,拿到数据
                    initView();
                    initData(data);

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
        loadResult(keyWord);
        initView();
        initData(data);



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
                    Document doc = Jsoup.connect("http://opac.jslib.org.cn/F/#").data("find_code","WRD").data("func","find-b").data("local_base","CNBOK").data("request",word).get();
                    //选择搜索数量文字
                    Elements resultNum = doc.select("div.hitnum");
                    resultNumText = resultNum.text().replace(" ","");


                    //测试第一页搜索结果有多少条，若为零应该就是没结果
                    Log.i("数量",resultNumText);


                    Elements items = doc.select("table.items");

                    for (int i=0;i<items.size();i++){

                        ArrayList<String> itemData = new ArrayList<>();
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
                        data.add(itemData);
                    }
//                    Log.i("有没有",data.toString());

                }catch(Exception e) {
                    Log.i("mytag", e.toString());
                }
                mHandler.sendEmptyMessage(0);
                Message msg = new Message();
                Bundle bundle = new Bundle();
                bundle.putString("resultText",resultNumText);
                msg.setData(bundle);
                msg.sendToTarget();

            }
        }).start();


    }

    private void initData(ArrayList<ArrayList<String>> data) {
        mLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);

        //初始化数据

        mAdapter = new ResultListAdapter(data);
    }

    private void initView() {
        RecyclerView mRecyclerView = (RecyclerView) findViewById(R.id.result_list_view);
        // 设置布局管理器
        mRecyclerView.setLayoutManager(mLayoutManager);
        // 设置adapter
        mRecyclerView.setAdapter(mAdapter);
    }

}
