package com.github.aseara.wifimap;

/**
 * Created by aseara on 2017/5/22.
 *
 */
public class WifiWallParam {

    /**
     * 类型。
     *  2  承重墙减弱固定值：2
     *  3  薄墙减弱固定值：1
     *  4  边界承重墙线外的点不渲染热力图
     *  5  边界薄墙线外的点不渲染热力图
     */
    private int wallType;

    private int startX;

    private int startY;

    private int endX;

    private int endY;

    public int getWallType() {
        return wallType;
    }

    public void setWallType(int wallType) {
        this.wallType = wallType;
    }

    public int getStartX() {
        return startX;
    }

    public void setStartX(int startX) {
        this.startX = startX;
    }

    public int getStartY() {
        return startY;
    }

    public void setStartY(int startY) {
        this.startY = startY;
    }

    public int getEndX() {
        return endX;
    }

    public void setEndX(int endX) {
        this.endX = endX;
    }

    public int getEndY() {
        return endY;
    }

    public void setEndY(int endY) {
        this.endY = endY;
    }
}
