package com.github.aseara.wifimap;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by qiujingde on 2017/5/22.
 *
 */
public class WifiMap {

    /**
     * 信号衰减的最大有效值，超过该值的衰减量可以认为信号无效。
     */
    private static final double maxValid = 120;

    private BufferedImage colorGradient;

    private final BufferedImage origin;

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
     * 信号强度的最大值。
     */
    private double maxValue;

    /**
     * 信号强度最小值。
     */
    private double minValue;

    /**
     * 计算信号强度时，除距离相关计算值外的固定值。
     */
    private double fixValue;
    /**
     * 图上的坐标点对应信号强度的map。<br />
     * 用"x,y"代表对应的坐标点。
     */
    private Map<String, Double> valueMap = new HashMap<>();



    public WifiMap(BufferedImage origin, WifiMapParam param) {
        this.origin = origin;
        this.param = param;

        try {
            colorGradient = ImageIO.read(WifiMap.class.getResourceAsStream("/heatmap/colors.png"));
        } catch (Exception e) {

        }
    }

    /**
     * 计算处理wifi
     */
    public BufferedImage compute() {
        initImage();

        // 信号强度值的计算公式为：fieldValue - reduceValue + frequencyPara * lg F + pPara * lg D + gain
        // 其中F的单位为MHz, D的单位为Km。
        // 除pPara * lgD外，对于某一AP来说，其他值都为固定的，因此可首先算该固定值(设为fixValue)。
        computeFixValue();

        fillValueMap();
        gradientMap();

        addGradient();
        negateImage();
        remap();

        addImage(wifiImg, gradientImg, 0.4f);

        return wifiImg;
    }

    /**
     * 获取热点梯度图。
     * @return 热点梯度图
     */
    private void initImage() {
        maxX = origin.getWidth();
        maxY = origin.getHeight();
        gradientImg = new BufferedImage(maxX, maxY, BufferedImage.TYPE_4BYTE_ABGR);

        Graphics2D g2 = gradientImg.createGraphics();
        g2.setColor(new Color(0, 0, 0, 0));
        g2.fillRect(0, 0, maxX, maxY);
        g2.dispose();

        wifiImg = new BufferedImage(maxX, maxY, BufferedImage.TYPE_4BYTE_ABGR);
        addImage(wifiImg, origin, 1);
    }

    private void fillValueMap() {
        WifiApParam apParam = param.getApParam();
        double pPara = apParam.getpPara();
        for (int i = 0; i < maxX; i++) {
            for (int j = 0; j < maxY; j++) {

                double d = getDistance(i, j);
                if (d != 0) {
                    double value = fixValue + pPara * Math.log10(d);
                    valueMap.put(i+","+j, value);

                    if (value > maxValue) {
                        maxValue = value;
                    } else if (value < minValue) {
                        minValue = value;
                    }
                }
            }
        }

        valueMap.put(apParam.getPointX()+","+apParam.getPointY(), minValue);
    }

    private void computeFixValue() {
        // fixValue = fieldValue - reduceValue + frequencyPara * lg F + gain
        fixValue = 0;
        WifiApParam apParam = param.getApParam();
        fixValue += apParam.getFieldValue();
        fixValue -= apParam.getReduceValue();
        fixValue += apParam.getFrequencyPara() * Math.log10(apParam.getFrequency());
        fixValue += apParam.getGain();

        minValue = fixValue;
    }

    private double getDistance(int x, int y) {
        int cx = param.getApParam().getPointX();
        int cy = param.getApParam().getPointY();
        double pixelD = Math.sqrt(Math.pow(x - cx, 2) + Math.pow(y - cy, 2));

        // 获取以千米为单位的距离值
        return pixelD * param.getScale() / 1000;
    }

    private void gradientMap() {
        for (int i = 0; i < maxX; i++) {
            for (int j = 0; j < maxY; j++) {

                gradientImg.setRGB(i, j, new Color(0, 0, 0, getGradient(i, j)).getRGB());

            }
        }
    }

    private int getGradient(int x, int y) {
        double value = valueMap.get(x + "," + y);

        if (maxValid < value) {
            return 0;
        }

        double pow = 4;
        double gradient = (value - minValue) / (maxValid - minValue);
        gradient = 1 - Math.pow(gradient, pow);
        gradient = gradient * 255;

        return (int) gradient;
    }

    /**
     * returns a negated version of this image.
     */
    private void negateImage() {
        for (int x = 0; x < maxX; x++) {
            for (int y = 0; y < maxY; y++) {
                int rGB = gradientImg.getRGB(x, y);
                int r = Math.abs(((rGB >>> 16) & 0xff) - 255);
                int g = Math.abs(((rGB >>> 8) & 0xff) - 255);
                int b = Math.abs((rGB & 0xff) - 255);

                gradientImg.setRGB(x, y, (r << 16) | (g << 8) | b);
            }
        }
    }

    /**
     * remaps black and white picture with colors. It uses the colors from
     * SPECTRUMPIC. The whiter a pixel is, the more it will get a color from the
     * bottom of it. Black will stay black.
     *
     */
    private void remap() {
        final int gradientHeight = colorGradient.getHeight() - 1;
        for (int i = 0; i < maxX; i++) {
            for (int j = 0; j < maxY; j++) {

                // get heatMapBW color values:
                final int rGB = gradientImg.getRGB(i, j);

                // calculate multiplier to be applied to height of gradiant.
                float multiplier = rGB & 0xff; // blue
                multiplier *= ((rGB >>> 8)) & 0xff; // green
                multiplier *= (rGB >>> 16) & 0xff; // red
                multiplier /= 16581375; // 255f * 255f * 255f

                // apply multiplier
                final int y = (int) (multiplier * gradientHeight);

                // remap values
                // calculate new value based on whitenes of heatMap
                // (the whiter, the more a color from the top of colorGradiant
                // will be chosen.
                final int mapedRGB = colorGradient.getRGB(0, y);
                // set new value
                gradientImg.setRGB(i, j, mapedRGB);
            }
        }
    }

    /**
     * prints the contents of buff2 on buff1 with the given opaque value.
     *
     * @param buff1
     *            buffer
     * @param buff2
     *            buffer
     * @param opaque
     *            how opaque the second buffer should be drawn
     * @param x
     *            x position where the second buffer should be drawn
     * @param y
     *            y position where the second buffer should be drawn
     */
    private void addImage(final BufferedImage buff1, final BufferedImage buff2,
                          final float opaque, final int x, final int y) {
        final Graphics2D g2d = buff1.createGraphics();
        g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER,
                opaque));
        g2d.drawImage(buff2, x, y, null);
        g2d.dispose();
    }

    /**
     * prints the contents of buff2 on buff1 with the given opaque value
     * starting at position 0, 0.
     *
     * @param buff1
     *            buffer
     * @param buff2
     *            buffer to add to buff1
     * @param opaque
     *            opacity
     */
    private void addImage(final BufferedImage buff1, final BufferedImage buff2,
                          final float opaque) {
        addImage(buff1, buff2, opaque, 0, 0);
    }

    private void addGradient() {
        BufferedImage oldGradient = gradientImg;

        gradientImg = new BufferedImage(maxX, maxY, BufferedImage.TYPE_4BYTE_ABGR);
        Graphics2D g2 = gradientImg.createGraphics();
        g2.setColor(Color.white);
        g2.fillRect(0, 0, maxX, maxY);
        g2.dispose();

        addImage(gradientImg, oldGradient, 1);
    }


}
