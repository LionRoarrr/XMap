package com.liangnie.xmap.bean;

import com.amap.api.services.route.BusStep;

public class SchemeBusStep extends BusStep {
    private boolean mIsWalk;
    private boolean mIsBus;
    private boolean mIsRailway;
    private boolean mIsTaxi;

    public SchemeBusStep(BusStep step) {
        if (step != null) {
            this.setBusLines(step.getBusLines());
            this.setWalk(step.getWalk());
            this.setRailway(step.getRailway());
            this.setTaxi(step.getTaxi());
        }
    }

    public boolean isWalk() {
        return mIsWalk;
    }

    public boolean isBus() {
        return mIsBus;
    }

    public boolean isRailway() {
        return mIsRailway;
    }

    public boolean isTaxi() {
        return mIsTaxi;
    }

    public void setIsWalk(boolean mIsWalk) {
        this.mIsWalk = mIsWalk;
    }

    public void setIsBus(boolean mIsBus) {
        this.mIsBus = mIsBus;
    }

    public void setIsRailway(boolean mIsRailway) {
        this.mIsRailway = mIsRailway;
    }

    public void setIsTaxi(boolean mIsTaxi) {
        this.mIsTaxi = mIsTaxi;
    }
}
