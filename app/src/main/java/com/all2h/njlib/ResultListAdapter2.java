package com.all2h.njlib;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.othershe.baseadapter.ViewHolder;
import com.othershe.baseadapter.base.CommonBaseAdapter;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by lazyeo on 03/02/2017.
 */


public  class ResultListAdapter2 extends CommonBaseAdapter<ArrayList<String>> {

        //声明一个数组，用来存放需要展示的数据


    public ResultListAdapter2(Context context, ArrayList<ArrayList<String>> datas, boolean isLoadMore) {
        super(context, datas, isLoadMore);
    }



    @Override
    protected void convert(ViewHolder holder, ArrayList<String> data) {
        Log.i("全套",data.toString());
        Log.i("书名",data.get(0));
        Log.i("作者",data.get(1));
        Log.i("出版社",data.get(3));
        Log.i("出版日期",data.get(4));
        Log.i("馆藏信息",data.get(5));
        Log.i("馆藏信息",data.get(6));

        holder.setText(R.id.book_name,data.get(0));
        holder.setText(R.id.book_autor,data.get(1));
        holder.setText(R.id.pub_house,data.get(3));
        holder.setText(R.id.pub_year,data.get(4));
        holder.setText(R.id.lib_nums,data.get(5));
    }

    @Override
    protected int getItemLayoutId() {
        return R.layout.result_list_item;
    }


}

//public class ResultListAdapter2 extends RecyclerView.Adapter<ResultListAdapter2.ViewHolder>{
//    //声明一个数组，用来存放需要展示的数据
//    private ArrayList<ArrayList<String>> mData;
//
//    //声明自身，用于传递数据
//
//    public ResultListAdapter2(ArrayList<ArrayList<String>> data) {
//        this.mData = data;
//    }
//
//    //更新数据方法未使用？
//    public void updateData(ArrayList<ArrayList<String>> data) {
//        this.mData = data;
//        notifyDataSetChanged();
//    }
//
//    @Override
//    //此处未使用多个viewtype
//    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
//        // 实例化展示的view
//        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.result_list_item, parent, false);
//        // 实例化viewholder
//        return new ViewHolder(v);
//    }
//
//    @Override
//    public void onBindViewHolder(ViewHolder holder, int position) {
//        // 绑定数据
//        holder.bookName.setText(mData.get(position).get(0));
//        holder.author.setText(mData.get(position).get(1));
//        holder.pubHouse.setText(mData.get(position).get(3));
//        holder.pubYear.setText(mData.get(position).get(4));
//        holder.libNums.setText(mData.get(position).get(5));
//    }
//
//    //获取条目数，以确定list长度
//    @Override
//    public int getItemCount() {
//        return mData == null ? 0 : mData.size();
//    }
//
//
//    //创建了一个viewholder
//    public static class ViewHolder extends RecyclerView.ViewHolder {
//
//        ImageView bookCover;
//
//        TextView bookName,author,pubYear,pubHouse,libNums;
//
//        public ViewHolder(View itemView) {
//            super(itemView);
//            bookCover = (ImageView)itemView.findViewById(R.id.book_cover);
//            bookName = (TextView) itemView.findViewById(R.id.book_name);
//            author = (TextView) itemView.findViewById(R.id.book_autor);
//            pubYear = (TextView) itemView.findViewById(R.id.pub_year);
//            pubHouse = (TextView) itemView.findViewById(R.id.pub_house);
//            libNums = (TextView) itemView.findViewById(R.id.lib_nums);
//
//        }
//    }
//}