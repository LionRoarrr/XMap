package com.liangnie.xmap.utils;

import android.content.Context;
import android.location.LocationManager;

import com.amap.api.maps.model.LatLng;
import com.amap.api.services.core.LatLonPoint;
import com.amap.api.services.route.BusPath;
import com.amap.api.services.route.BusStep;
import com.amap.api.services.route.RouteBusLineItem;
import com.amap.api.services.route.RouteRailwayItem;
import com.liangnie.xmap.R;

import java.text.DecimalFormat;
import java.util.List;

public class MapUtil {
    /*
    * LatLonPoint转换为LatLng
    * */
    public static LatLng convertToLatLng(LatLonPoint point) {
        return new LatLng(point.getLatitude(), point.getLongitude());
    }

    /*
    * 转换时间为天时分秒字符串
    * */
    public static String getFriendlyTime(int second) {
        if (second > 3600) {
            int hour = second / 3600;
            int minute = (second % 3600) / 60;
            return hour + "小时" + minute + "分钟";
        }
        if (second >= 60) {
            int miniate = second / 60;
            return miniate + "分钟";
        }
        return second + "秒";
    }

    /*
    * 转换距离为友好距离
    * */
    public static String getFriendlyLength(int lenMeter) {
        if (lenMeter > 10000) // 10 km
        {
            int dis = lenMeter / 1000;
            return dis + "公里";
        }

        if (lenMeter > 1000) {
            float dis = (float) lenMeter / 1000;
            DecimalFormat fnum = new DecimalFormat("##0.0");
            String dstr = fnum.format(dis);
            return dstr + "公里";
        }

        if (lenMeter > 100) {
            int dis = lenMeter / 50 * 50;
            return dis + "米";
        }

        int dis = lenMeter / 10 * 10;
        if (dis == 0) {
            dis = 10;
        }

        return dis + "米";
    }

    public static String getFriendlyPoiType(String typeDes) {
        String[] strings = typeDes.split(";");
        return strings[1];
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

    /*
    * 格式化BusPath，输出格式换乘信息
    * */
    public static String getBusPathTitle(BusPath busPath) {
        if (busPath == null) {
            return "";
        }
        List<BusStep> busSteps = busPath.getSteps();
        if (busSteps == null) {
            return "";
        }
        StringBuffer sb = new StringBuffer();
        for (BusStep busStep : busSteps) {
            StringBuffer title = new StringBuffer();
            if (!busStep.getBusLines().isEmpty()) {
                for (RouteBusLineItem busline : busStep.getBusLines()) {
                    if (busline == null) {
                        continue;
                    }

                    String buslineName = getSimpleBusLineName(busline.getBusLineName());
                    title.append(buslineName);
                    title.append(" / ");
                }

                sb.append(title.substring(0, title.length() - 3));
                sb.append(" > ");
            }
            if (busStep.getRailway() != null) {
                RouteRailwayItem railway = busStep.getRailway();
                sb.append(railway.getTrip()).append("(").append(railway.getDeparturestop().getName()).append(" - ").append(railway.getArrivalstop().getName()).append(")");
                sb.append(" > ");
            }
        }
        return sb.substring(0, sb.length() - 3);
    }

    /*
    * 格式化BusPath，输出该Path下的总距离，时间和步行
    * */
    public static String getBusPathDes(BusPath busPath) {
        if (busPath == null) {
            return "";
        }

        String cost = busPath.getCost() + "元";
        String startStation = busPath.getSteps().get(0)
                .getBusLines().get(0)
                .getDepartureBusStation().getBusStationName() + "上车";
        String time = getFriendlyTime((int) busPath.getDuration());
        String walkDist = getFriendlyLength((int) busPath.getWalkDistance());

        return time + "\n" + cost + "·步行" + walkDist + "·" + startStation;
    }

    public static String getSimpleBusLineName(String busLineName) {
        if (busLineName == null) {
            return "";
        }
        return busLineName.replaceAll("\\(.*?--.*?\\)", "");
    }

    public static boolean isOPenGps(final Context context) {
        LocationManager locationManager
                = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
        // 通过GPS卫星定位，定位级别可以精确到街（通过24颗卫星定位，在室外和空旷的地方定位准确、速度快）
        boolean gps = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
        // 通过WLAN或移动网络(3G/2G)确定的位置（也称作AGPS，辅助GPS定位。主要用于在室内或遮盖物（建筑群或茂密的深林等）密集的地方定位）
        boolean network = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
        return gps || network;
    }
}
