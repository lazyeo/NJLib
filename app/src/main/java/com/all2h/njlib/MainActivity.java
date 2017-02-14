package com.all2h.njlib;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.google.firebase.analytics.FirebaseAnalytics;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;


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

    public void toActivity(android.view.View button){
        startActivity(SearchActivity.class);

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

    public void test(View button){


        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    //从一个URL加载一个Document对象。
                    Document doc = Jsoup.connect("http://app.jslib.org.cn/n_xj/xj.php?id=NL00036421").get();
                    //选择“单本书”所在节点
                    Element elements = doc.getElementById("MessageTitle");
                    //选择
                    Elements elements1 = doc.select("ul.wxlist");
                    int num = elements1.size();

                    Log.i("mytag",elements1.select("td.t2").get(0).select("input").attr("value") );
                    Log.i("mytag","clicked");
                }catch(Exception e) {
                    Log.i("mytag", e.toString());
                }
            }
        }).start();

    }
}