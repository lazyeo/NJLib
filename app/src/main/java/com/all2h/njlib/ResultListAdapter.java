package com.all2h.njlib;

import android.media.Image;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by lazyeo on 03/02/2017.
 */

public class ResultListAdapter extends RecyclerView.Adapter<ResultListAdapter.ViewHolder>{
    //声明一个数组，用来存放需要展示的数据
    private ArrayList<ArrayList<String>> mData;

    //声明自身，用于传递数据

    public ResultListAdapter(ArrayList<ArrayList<String>> data) {
        this.mData = data;
    }

    //更新数据方法未使用？
    public void updateData(ArrayList<ArrayList<String>> data) {
        this.mData = data;
        notifyDataSetChanged();
    }

    @Override
    //此处未使用多个viewtype
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        // 实例化展示的view
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.result_list_item, parent, false);
        // 实例化viewholder
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        // 绑定数据
        holder.bookName.setText(mData.get(position).get(0));
        holder.author.setText(mData.get(position).get(1));
        holder.pubHouse.setText(mData.get(position).get(3));
        holder.pubYear.setText(mData.get(position).get(4));
        holder.libNums.setText(mData.get(position).get(5));
    }

    //获取条目数，以确定list长度
    @Override
    public int getItemCount() {
        return mData == null ? 0 : mData.size();
    }


    //创建了一个viewholder
    public static class ViewHolder extends RecyclerView.ViewHolder {

        ImageView bookCover;

        TextView bookName,author,pubYear,pubHouse,libNums;

        public ViewHolder(View itemView) {
            super(itemView);
            bookCover = (ImageView)itemView.findViewById(R.id.book_cover);
            bookName = (TextView) itemView.findViewById(R.id.book_name);
            author = (TextView) itemView.findViewById(R.id.book_autor);
            pubYear = (TextView) itemView.findViewById(R.id.pub_year);
            pubHouse = (TextView) itemView.findViewById(R.id.pub_house);
            libNums = (TextView) itemView.findViewById(R.id.lib_nums);

        }
    }
}