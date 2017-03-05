package com.all2h.njlib.utils;

import android.content.Context;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;

/**
 * Created by lazyeo on 4/27/16.
 */
public class JsonUtil {

    /**
     * 从ASSET获得并解析JSON文件
     *
     * @param ctx
     * @param file
     * @return
     * @throws IOException
     */
    public static JSONArray getDataFromAsset(Context ctx, String file) throws IOException {
        InputStream is = null;
        JSONArray obj = null;
        try {
            is = ctx.getResources().getAssets().open(file);
            byte[] buffer = new byte[is.available()];
            is.read(buffer);
            String result = new String(buffer, "utf-8");
            obj = new JSONArray(result);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        } finally {
            is.close();
        }
        return obj;
    }

    /**
     * 根据index获得数组元素
     * @param jsonArray
     * @param index
     * @return
     * @throws JSONException
     */
    public static JSONObject getDataForIndex(JSONArray jsonArray, int index) throws JSONException {
        JSONObject rs = null;
        for (int i = 0; i < jsonArray.length(); i++) {
            if (i == index) {
                rs = (JSONObject) jsonArray.get(i);
                }
        }
        return rs;
    }
}
