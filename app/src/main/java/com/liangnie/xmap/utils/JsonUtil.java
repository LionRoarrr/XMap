package com.liangnie.xmap.utils;

import com.liangnie.xmap.bean.HistorySearch;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class JsonUtil {
    public static JSONArray hsList2Json(List<HistorySearch> list) {
        JSONArray array = new JSONArray();
        for (HistorySearch item: list) {
            JSONObject jo = new JSONObject();
            try {
                jo.put("content", item.getContent());
            } catch (JSONException e) {
                e.printStackTrace();
            }
            array.put(jo);
        }
        return array;
    }

    public static List<HistorySearch> json2HsList(JSONArray array) {
        List<HistorySearch> list = new ArrayList<>();
        for (int i = 0; i < array.length(); i++) {
            try {
                JSONObject jo = array.getJSONObject(i);
                HistorySearch search = new HistorySearch();
                search.setContent(jo.getString("content"));
                list.add(search);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return list;
    }
}
