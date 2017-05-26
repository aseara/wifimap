package com.github.aseara.wifimap;

import java.util.List;

/**
 * Created by qiujingde on 2017/5/24.
 *
 * wifi信号计算的参数。
 *
 */
public class WifiMapParam {

    /**
     * AP相关参数。
     */
    private List<WifiApParam> apParams;

    /**
     * 标尺，单位：米/像素。
     */
    private double scale;

    /**
     * 墙相关参数。
     */
    private List<WifiWallParam> wallParams;


    public List<WifiApParam> getApParams() {
        return apParams;
    }

    public void setApParams(List<WifiApParam> apParams) {
        this.apParams = apParams;
    }

    public double getScale() {
        return scale;
    }

    public void setScale(double scale) {
        this.scale = scale;
    }

    public List<WifiWallParam> getWallParams() {
        return wallParams;
    }

    public void setWallParams(List<WifiWallParam> wallParams) {
        this.wallParams = wallParams;
    }
}
