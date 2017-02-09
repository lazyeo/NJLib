package com.all2h.njlib;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.EditText;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Objects;

public class SearchActivity extends AppCompatActivity {
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;
    private SharedPreferences history;
    private SharedPreferences.Editor editor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        history = getSharedPreferences("search_history",Activity.MODE_PRIVATE);

        editor = history.edit();
        initData();
        initView();
    }

    public void search(View button){
        //把搜索框的文字保存进历史记录
        EditText searchText = (EditText) findViewById(R.id.searchText);
        insertWord(searchText.getText().toString());

        startSearch(searchText.getText().toString());
        //刷新历史记录list
        initData();
        initView();


        //此处应当跳转activity并把搜索框的文字带过去
    }

    public void clear(View button){
        editor.clear();
        editor.apply();
        initData();
        initView();
        clearSearchText();
    }

    public void clearSearchText(){
        EditText searchText = (EditText) findViewById(R.id.searchText);
        searchText.setText("");
    }

    //将输入的词汇插入历史记录中保存
    public void insertWord(String word){
        //有搜索记录就从xml中取出记录字段
        String historyList = history.getString("search_history", "");
        //按照","把记录字段进行分割
        String[] historyArray = historyList.split(",");

        //声明一个新的数组
        ArrayList<String> tempList = new ArrayList<>();

        //比对输入的词是否已经在历史记录中存在,若存在则去掉,其余加入新数组
        for (int i=0;i<historyArray.length;i++){

            if (!Objects.equals(historyArray[i], word)){
                tempList.add(historyArray[i]);
            }
        }

        //把新的数组转化为字符串，去掉空格和数组前后的[]
        historyList = tempList.toString().replace(" ","");
        historyList = historyList.substring(1);
        historyList = historyList.substring(0,historyList.length()-1);

        //把新的历史记录存进配置xml
        editor.putString("search_history",word+","+historyList);
        editor.apply();

    }



    private void initData() {
        mLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);

        //初始化数据

        mAdapter = new HistoryListAdapter(getData());
    }

    private void initView() {
        RecyclerView mRecyclerView = (RecyclerView) findViewById(R.id.search_history_list);
        // 设置布局管理器
        mRecyclerView.setLayoutManager(mLayoutManager);
        // 设置adapter
        mRecyclerView.setAdapter(mAdapter);
    }

    private ArrayList<String> getData() {
        ArrayList<String> data = new ArrayList<>();


        //判断是否有搜索记录
        if(Objects.equals(history.getString("search_history", ""), "")){
            //没有搜索记录就默认输出"暂无历史搜索"
            data.add("暂无历史搜索记录");
        }else{
            //有搜索记录就从xml中取出记录字段
            String historyList = history.getString("search_history", "");
            //按照","把记录字段进行分割
            String[] historyArray = historyList.split(",");
            //把分割的记录数组添加进data
            Collections.addAll(data, historyArray);
        }


        return data;
    }


    private void startSearch(String word) {
        Intent intent = new Intent(SearchActivity.this,ResultListActivity.class);

        intent.putExtra("keyWord",word);
        startActivity(intent);

        //finish();
    }
}
