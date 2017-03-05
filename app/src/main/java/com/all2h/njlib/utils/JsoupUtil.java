package com.all2h.njlib.utils;

import android.util.Log;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by lazyeo on 13/02/2017.
 */

public class JsoupUtil {




    public static List<ArrayList<String>> loadResult(final String word){

        String resultNumText;

        List<ArrayList<String>> data = new ArrayList<>();

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


        return data;
    }

}
