package com.liangnie.xmap.utils;

import com.amap.api.maps.model.LatLng;
import com.amap.api.services.core.LatLonPoint;
import com.liangnie.xmap.R;

import java.util.Locale;

public class MapUtil {
    public static LatLng convertToLatLng(LatLonPoint point) {
        return new LatLng(point.getLatitude(), point.getLongitude());
    }

    /*
    * 转换时间为天时分秒字符串
    * */
    public static String convertToString(long duration) {
        long temp = duration;
        StringBuilder timeStr = new StringBuilder();

        if (duration >= (60 * 60 * 24)) {
            timeStr.append(duration / (60 * 60 * 24));
            timeStr.append("天");
        }

        duration %= (60 * 60 * 24);
        if (duration >= (60 * 60)) {
            timeStr.append(duration / (60 * 60));
            timeStr.append("小时");
        }

        duration %= (60 * 60);
        if (duration >= 60) {
            timeStr.append(duration / 60);
            timeStr.append("分钟");
        }

        duration %= 60;
        if (duration > 0 && duration == temp) {
            timeStr.append(duration);
            timeStr.append("秒");
        }

        return timeStr.toString();
    }

    /*
    * 转换距离为米和公里字符串
    * */
    public static String convertToString(float distance) {
        StringBuilder distanceStr = new StringBuilder();

        if (distance > 1000.0) {
            float compDistance = distance / 1000f;
            distanceStr.append(String.format(Locale.CHINA,"%.2f公里", compDistance));
        } else if (distance >= 0) {
            distanceStr.append(String.format(Locale.CHINA,"%.0f米", distance));
        }

        return distanceStr.toString();
    }

    /*
    * 路径规划方向指示和图片对应
    * */
    public static int getDriveActionID(String actionName) {
        if (actionName == null || actionName.equals("")) {
            return R.drawable.dir3;
        }
        if ("左转".equals(actionName)) {
            return R.drawable.dir2;
        }
        if ("右转".equals(actionName)) {
            return R.drawable.dir1;
        }
        if ("向左前方行驶".equals(actionName) || "靠左".equals(actionName)) {
            return R.drawable.dir6;
        }
        if ("向右前方行驶".equals(actionName) || "靠右".equals(actionName)) {
            return R.drawable.dir5;
        }
        if ("向左后方行驶".equals(actionName) || "左转调头".equals(actionName)) {
            return R.drawable.dir7;
        }
        if ("向右后方行驶".equals(actionName)) {
            return R.drawable.dir8;
        }
        if ("直行".equals(actionName)) {
            return R.drawable.dir3;
        }
        if ("减速行驶".equals(actionName)) {
            return R.drawable.dir4;
        }
        return R.drawable.dir3;
    }
}
