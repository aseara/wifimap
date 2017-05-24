package com.github.aseara.wifimap;

import com.github.aseara.heatmap.Main;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;

/**
 * Created by qiujingde on 2017/5/24.
 *
 */
public class MapTest {

    public static WifiMapParam getParam() {
        WifiMapParam param = new WifiMapParam();
        WifiApParam apParam = new WifiApParam();
        param.setApParam(apParam);

        apParam.setFrequency(2400);
        apParam.setFieldValue(92.40);
        apParam.setReduceValue(2.00);
        apParam.setGain(-2.67);
        apParam.setFrequencyPara(20.00);
        apParam.setpPara(20.00);

        apParam.setPointX(400);
        apParam.setPointY(400);

        param.setScale(0.0240870);

        return param;
    }

    public static void main(String[] args) throws Exception {
        BufferedImage origin = ImageIO.read(MapTest.class.getResourceAsStream("/heatmap/123.png"));

        WifiMapParam param = getParam();
        WifiMap mapGen = new WifiMap(origin, param);
        BufferedImage wifiMap = mapGen.compute();

        String outputFile = Main.class.getResource("/heatmap/wifi.png").getPath();
        ImageIO.write(wifiMap, "png", new File(outputFile));
    }

}
