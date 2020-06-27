package com.liangnie.xmap.bean;

import com.amap.api.services.core.LatLonPoint;

import java.util.Locale;

public class MyPoiItem {
    private final String title;
    private String snippet;
    private float distance;
    private LatLonPoint latLonPoint;

    public MyPoiItem(String title) {
        this.title = title;
    }

    public MyPoiItem(String title, LatLonPoint latLonPoint) {
        this.title = title;
        this.latLonPoint = latLonPoint;
    }

    public MyPoiItem(String title, String snippet, float distance, LatLonPoint latLonPoint) {
        this.title = title;
        this.snippet = snippet;
        this.distance = distance;
        this.latLonPoint = latLonPoint;
    }

    public String getTitle() {
        return title;
    }

    public String getSnippet() {
        return snippet;
    }

    public String getDistance() {
        float compDistance;
        String result;

        if (distance > 1000.0) {
            compDistance = (float) (distance / 1000.0);
            result = String.format(Locale.CHINA,"%.2f km", compDistance);
        } else {
            result = String.format(Locale.CHINA,"%.0f m", distance);
        }
        return result;
    }

    public LatLonPoint getLatLonPoint() {
        return latLonPoint;
    }
}
