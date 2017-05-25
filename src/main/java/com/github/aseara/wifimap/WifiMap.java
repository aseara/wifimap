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

    /**
     * gradient image.
     */
    private BufferedImage gradientImg;

    /**
     * wifi image.
     */
    private BufferedImage wifiImg;

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
    private double fixValue;


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
        initImage();
        computeFixValue();
        computeGradientImg();

        addImage(wifiImg, gradientImg);

        return wifiImg;
    }

    private void initColorGradient() {
        try {
            colorGradient = ImageIO.read(WifiMap.class.getResourceAsStream("/heatmap/colors.png"));
        } catch (Exception e) {

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

    /**
     * 获取热点梯度图。
     */
    private void initImage() {
        maxX = origin.getWidth();
        maxY = origin.getHeight();

        wifiImg = new BufferedImage(maxX, maxY, BufferedImage.TYPE_4BYTE_ABGR);
        addImage(wifiImg, origin);
    }

    private void computeGradientImg() {
        gradientImg = new BufferedImage(maxX, maxY, BufferedImage.TYPE_4BYTE_ABGR);

        Graphics2D g2 = gradientImg.createGraphics();
        g2.setColor(new Color(0, 0, 0, 0));
        g2.fillRect(0, 0, maxX, maxY);
        g2.dispose();

        WifiApParam apParam = param.getApParam();
        double pPara = apParam.getpPara();

        int cx = param.getApParam().getPointX();
        int cy = param.getApParam().getPointY();

        for (int i = 0; i < maxX; i++) {
            for (int j = 0; j < maxY; j++) {
                if (i == cx && j == cy) {
                    gradientImg.setRGB(i, j, getGradientColor(20.0));
                } else {
                    if (inBoundsWall(i, j)) {
                        double d = getDistance(i, j);
                        double value = fixValue + pPara * Math.log10(d);
                        value += shiftThroughWalls(i, j);
                        gradientImg.setRGB(i, j, getGradientColor(value));
                    }
                }
            }
        }
    }

    private void computeFixValue() {
        // 信号强度值的计算公式为：fieldValue - reduceValue + frequencyPara * lg F + pPara * lg D + gain
        // 其中F的单位为MHz, D的单位为Km。
        // 除pPara * lgD外，对于某一AP来说，其他值都为固定的，因此可首先算该固定值(设为fixValue)。
        // fixValue = fieldValue - reduceValue + frequencyPara * lg F + gain
        fixValue = 0;
        WifiApParam apParam = param.getApParam();
        fixValue += apParam.getFieldValue();
        fixValue -= apParam.getReduceValue();
        // fixValue += apParam.getFrequencyPara() * Math.log10(apParam.getFrequency());
        fixValue += apParam.getGain();
    }

    private double getDistance(int x, int y) {
        int cx = param.getApParam().getPointX();
        int cy = param.getApParam().getPointY();
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
        int cx = param.getApParam().getPointX();
        int cy = param.getApParam().getPointY();

        Line2D line = new Line2D.Double(x, y, cx, cy);
        for (WifiWallParam wall : boundWalls) {
            if (line.intersectsLine(wall.getStartX(), wall.getStartY(), wall.getEndX(), wall.getEndY())) {
                return false;
            }
        }

        return true;
    }

    private double shiftThroughWalls(int x, int y) {
        double shift = 0;
        int cx = param.getApParam().getPointX();
        int cy = param.getApParam().getPointY();

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
