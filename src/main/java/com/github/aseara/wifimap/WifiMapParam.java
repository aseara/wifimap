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
    private WifiApParam apParam;

    /**
     * 标尺，单位：米/像素。
     */
    private double scale;

    /**
     * 墙相关参数。
     */
    private List<WifiWallParam> wallParams;


    public WifiApParam getApParam() {
        return apParam;
    }

    public void setApParam(WifiApParam apParam) {
        this.apParam = apParam;
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
