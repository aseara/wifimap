package com.github.aseara.wifimap;

/**
 * Created by qiujingde on 2017/5/24.
 *
 * AP相关信息强度计算参数。
 */
public class WifiApParam {

    /**
     * 信号频率，单位MHz。
     */
    private double frequency;

    /**
     * AP坐标点横坐标。
     */
    private int pointX;

    /**
     * AP坐标点纵坐标。
     */
    private int pointY;

    /**
     * 起始场强值。
     */
    private double fieldValue;

    /**
     * 固定衰减值。
     */
    private double reduceValue;

    /**
     * 频率相关参数。
     */
    private double frequencyPara;

    /**
     * 距离相关参数。
     */
    private double pPara;

    /**
     * 固定增益值。
     */
    private double gain;

    public double getFrequency() {
        return frequency;
    }

    public void setFrequency(double frequency) {
        this.frequency = frequency;
    }

    public int getPointX() {
        return pointX;
    }

    public void setPointX(int pointX) {
        this.pointX = pointX;
    }

    public int getPointY() {
        return pointY;
    }

    public void setPointY(int pointY) {
        this.pointY = pointY;
    }

    public double getFieldValue() {
        return fieldValue;
    }

    public void setFieldValue(double fieldValue) {
        this.fieldValue = fieldValue;
    }

    public double getReduceValue() {
        return reduceValue;
    }

    public void setReduceValue(double reduceValue) {
        this.reduceValue = reduceValue;
    }

    public double getFrequencyPara() {
        return frequencyPara;
    }

    public void setFrequencyPara(double frequencyPara) {
        this.frequencyPara = frequencyPara;
    }

    public double getpPara() {
        return pPara;
    }

    public void setpPara(double pPara) {
        this.pPara = pPara;
    }

    public double getGain() {
        return gain;
    }

    public void setGain(double gain) {
        this.gain = gain;
    }
}
