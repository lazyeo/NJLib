package com.all2h.njlib;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class DouBanBookDetailActivity extends AppCompatActivity {

    Handler mHandler = new Handler() {

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case 0:
                    TextView json = (TextView)findViewById(R.id.json);
                    json.setText(content);
                    break;
                case 1:
                    break;
                default:
                    break;
            }


        }

    };

    protected String content ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.douban_book_detail);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(true);


        Intent intent = getIntent();
        String isbnNum = intent.getStringExtra("isbn");
        loadResult(isbnNum);







    }

    public void loadResult(final String isbn){

        new Thread(new Runnable() {
            @Override
            public void run() {
                int responseCode = 0;

                try {
                    URL url = new URL("https://api.douban.com/v2/book/isbn/"+isbn);
                    HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                    connection.setRequestMethod("GET");
                    connection.setConnectTimeout(8000);
                    connection.setReadTimeout(8000);
                    responseCode = connection.getResponseCode();

                    InputStream in = connection.getInputStream();
                    BufferedReader reader = new BufferedReader(new InputStreamReader(in, "UTF-8"));
                    StringBuilder response = new StringBuilder();
                    String line;
                    while ((line = reader.readLine()) != null) {
                        response.append(line).append("\r\n");
                    }
                    reader.close();
                    content = response.toString();

                    Log.e("正文",content);

                } catch (MalformedURLException e) {
                    Log.e("返回值", String.valueOf(responseCode));
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                mHandler.sendEmptyMessage(0);

            }

        }).start();
    }
}
