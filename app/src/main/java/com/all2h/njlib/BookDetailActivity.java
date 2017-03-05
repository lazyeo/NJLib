package com.all2h.njlib;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

public class BookDetailActivity extends AppCompatActivity {


    private String[] bookInfo ;

    Handler mHandler = new Handler() {

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case 0:
                    break;
                case 1:
                    break;
                default:
                    break;
            }


        }

    };
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_book_detail);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

//        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
//        fab.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
//                        .setAction("Action", null).show();
//            }
//        });

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        Intent intent = getIntent();
        String url = intent.getStringExtra("bookUrl");
        loadResult(url);


    }




    public void loadResult(final String url){

        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    /*
                    这里面的页面元素后续有时间应当改为读取配置文件，配置文件可动态更新，避免页面元素名称修改导致无法解析
                     */

                    //从一个URL加载一个Document对象。
                    Document doc = Jsoup.connect(url).get();

                    Elements table = doc.select("td.td1");


                    Log.i("有没有",table.toString());

                }catch(Exception e) {
                    Log.i("mytag", e.toString());
                }
                mHandler.sendEmptyMessage(0);


            }
        }).start();


    }
}
