package com.github.aseara.wifimap;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.geom.Line2D;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by qiujingde on 2017/5/22.
 *
 */
public class WifiMap {

    private BufferedImage colorGradient;
    private final BufferedImage origin;
    private List<WifiWallParam> boundWalls;
    private List<WifiWallParam> normalWalls;

    private final WifiMapParam param;
    /**
     * 横坐标最大值。
     */
    private int maxX;
    /**
     * 纵坐标最大值。
     */
    private int maxY;

    /**
     * 计算信号强度时，除距离相关计算值外的固定值。
     */
    private List<Double> fixValues = new ArrayList<>();


    public WifiMap(BufferedImage origin, WifiMapParam param) {
        this.origin = origin;
        this.param = param;
        initColorGradient();
        initWallParam(param);
    }

    /**
     * 计算处理wifi
     */
    public BufferedImage compute() {

        computeFixValue();

        BufferedImage wifiImg = initImage();
        BufferedImage gradientImg = computeGradientImg();

        addImage(wifiImg, gradientImg);

        return wifiImg;
    }

    private void initColorGradient() {
        try {
            colorGradient = ImageIO.read(WifiMap.class.getResourceAsStream("/heatmap/colors.png"));
        } catch (Exception e) {
            // ignore
        }
    }

    private void initWallParam(WifiMapParam param) {
        boundWalls = new ArrayList<>();
        normalWalls = new ArrayList<>();
        if (param.getWallParams() != null) {
            for (WifiWallParam wallParam : param.getWallParams()) {

                if (wallParam.getWallType() == 4 || wallParam.getWallType() == 5) {
                    boundWalls.add(wallParam);
                } else if (wallParam.getWallType() == 2 || wallParam.getWallType() == 3){
                    normalWalls.add(wallParam);
                } else {
                    throw new RuntimeException("衰减物线类型不正确！");
                }

            }
        }
    }


    private void computeFixValue() {
        // 信号强度值的计算公式为：fieldValue - reduceValue + frequencyPara * lg F + pPara * lg D + gain
        // 其中F的单位为MHz, D的单位为Km。
        // 除pPara * lgD外，对于某一AP来说，其他值都为固定的，因此可首先算该固定值(设为fixValue)。
        // fixValue = fieldValue - reduceValue + frequencyPara * lg F + gain

        for (WifiApParam apParam : param.getApParams()) {
            double fixValue = 0;
            fixValue += apParam.getFieldValue();
            fixValue -= apParam.getReduceValue();
            fixValue += apParam.getFrequencyPara() * Math.log10(apParam.getFrequency());
            fixValue += apParam.getGain();

            fixValues.add(fixValue);
        }
    }

    /**
     * 获取背景图。
     * @return 背景图
     */
    private BufferedImage initImage() {
        maxX = origin.getWidth();
        maxY = origin.getHeight();

        BufferedImage wifiImg = new BufferedImage(maxX, maxY, BufferedImage.TYPE_4BYTE_ABGR);
        addImage(wifiImg, origin);

        return wifiImg;
    }

    /**
     * 获取信号强度图
     * @return 信号强度梯度图
     */
    private BufferedImage computeGradientImg() {
        BufferedImage gradientImg = new BufferedImage(maxX, maxY, BufferedImage.TYPE_4BYTE_ABGR);

        Graphics2D g2 = gradientImg.createGraphics();
        g2.setColor(new Color(0, 0, 0, 0));
        g2.fillRect(0, 0, maxX, maxY);
        g2.dispose();

        for (int i = 0; i < maxX; i++) {
            for (int j = 0; j < maxY; j++) {
                if (inBoundsWall(i, j)) {
                    double value = computeFieldStrength(i, j);
                    gradientImg.setRGB(i, j, getGradientColor(value));
                }
            }
        }

        return gradientImg;
    }


    private double computeFieldStrength(int x, int y) {
        double strength = 1000;
        for (int i = 0; i < param.getApParams().size(); i++) {
            double fixValue = fixValues.get(i);
            WifiApParam apParam = param.getApParams().get(i);

            double pPara = apParam.getpPara();

            double d = getDistance(apParam, x, y);

            double crtStrength = fixValue + pPara * Math.log10(d);
            crtStrength += shiftThroughWalls(apParam, x, y);

            strength = Math.min(strength, crtStrength);
        }
        return strength;
    }

    private double getDistance(WifiApParam apParam, int x, int y) {
        int cx = apParam.getPointX();
        int cy = apParam.getPointY();
        double pixelD = Math.sqrt(Math.pow(x - cx, 2) + Math.pow(y - cy, 2));

        // 获取以千米为单位的距离值
        return pixelD * param.getScale() / 1000;
    }

    private int getGradientColor(double value) {
        if (value > 100) {
            value = 100;
        }
        if (value < 20) {
            value = 20;
        }
        final int gradientHeight = colorGradient.getHeight() - 1;
        final double colorY = (100 - value) / 80 * gradientHeight;

        final double alpha = (100 - value) / 80 * 255;

        Color color = new Color(colorGradient.getRGB(0, (int)colorY));
        Color color2 = new Color(color.getRed(), color.getGreen(), color.getBlue(), (int)alpha);
        return color2.getRGB();
    }

    /**
     * prints the contents of buff2 on buff1 with the given opaque value.
     *
     * @param buff1
     *            buffer
     * @param buff2
     *            buffer
     */
    private void addImage(final BufferedImage buff1, final BufferedImage buff2) {
        final Graphics2D g2d = buff1.createGraphics();
        g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1f));
        g2d.drawImage(buff2, 0, 0, null);
        g2d.dispose();
    }

    private boolean inBoundsWall(int x, int y) {
        // 所有ap源的计算都认为该点在边界外，返回false
        // 否则返回true
        for (WifiApParam apParam : param.getApParams()) {
            int cx = apParam.getPointX();
            int cy = apParam.getPointY();
            if (x == cx && y == cy) {
                return true;
            }
            boolean out = false;
            Line2D line = new Line2D.Double(x, y, cx, cy);
            for (WifiWallParam wall : boundWalls) {
                if (line.intersectsLine(wall.getStartX(), wall.getStartY(), wall.getEndX(), wall.getEndY())) {
                    out = true;
                    break;
                }
            }
            if (!out) {
                return true;
            }
        }
        return false;
    }

    private double shiftThroughWalls(WifiApParam apParam, int x, int y) {
        double shift = 0;
        int cx = apParam.getPointX();
        int cy = apParam.getPointY();

        Line2D line = new Line2D.Double(x, y, cx, cy);
        for (WifiWallParam wall : normalWalls) {
            if (line.intersectsLine(wall.getStartX(), wall.getStartY(), wall.getEndX(), wall.getEndY())) {
                shift += shiftThroughWall(wall.getWallType());
            }
        }

        return shift;
    }

    private double shiftThroughWall(int wallType) {
        double shift = 0.0;
        switch (wallType) {
            case 2:
                shift = 2.0;
                break;
            case 3:
                shift = 1.0;
                break;
            default:
                break;
        }
        return shift;
    }

}
