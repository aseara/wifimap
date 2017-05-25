package com.github.aseara.wifimap;

import org.junit.Test;

import javax.imageio.ImageIO;
import java.awt.geom.Line2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by qiujingde on 2017/5/24.
 *
 */
public class MapTest {

    private WifiMapParam getParam() {
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

        param.setScale(0.0180652);

        List<WifiWallParam> walls = new ArrayList<>();
        param.setWallParams(walls);

        // 边界墙 start
        WifiWallParam wall = new WifiWallParam();
        wall.setWallType(4);
        wall.setStartX(81);
        wall.setStartY(165);
        wall.setEndX(541);
        wall.setEndY(166);
        walls.add(wall);

        wall = new WifiWallParam();
        wall.setWallType(4);
        wall.setStartX(541);
        wall.setStartY(166);
        wall.setEndX(540);
        wall.setEndY(255);
        walls.add(wall);

        wall = new WifiWallParam();
        wall.setWallType(4);
        wall.setStartX(540);
        wall.setStartY(255);
        wall.setEndX(605);
        wall.setEndY(258);
        walls.add(wall);

        wall = new WifiWallParam();
        wall.setWallType(4);
        wall.setStartX(605);
        wall.setStartY(258);
        wall.setEndX(608);
        wall.setEndY(305);
        walls.add(wall);

        wall = new WifiWallParam();
        wall.setWallType(4);
        wall.setStartX(608);
        wall.setStartY(305);
        wall.setEndX(738);
        wall.setEndY(306);
        walls.add(wall);

        wall = new WifiWallParam();
        wall.setWallType(4);
        wall.setStartX(738);
        wall.setStartY(306);
        wall.setEndX(738);
        wall.setEndY(558);
        walls.add(wall);

        wall = new WifiWallParam();
        wall.setWallType(4);
        wall.setStartX(738);
        wall.setStartY(558);
        wall.setEndX(543);
        wall.setEndY(558);
        walls.add(wall);

        wall = new WifiWallParam();
        wall.setWallType(4);
        wall.setStartX(543);
        wall.setStartY(558);
        wall.setEndX(543);
        wall.setEndY(635);
        walls.add(wall);

        wall = new WifiWallParam();
        wall.setWallType(4);
        wall.setStartX(543);
        wall.setStartY(635);
        wall.setEndX(322);
        wall.setEndY(635);
        walls.add(wall);

        wall = new WifiWallParam();
        wall.setWallType(4);
        wall.setStartX(322);
        wall.setStartY(635);
        wall.setEndX(322);
        wall.setEndY(595);
        walls.add(wall);

        wall = new WifiWallParam();
        wall.setWallType(4);
        wall.setStartX(322);
        wall.setStartY(595);
        wall.setEndX(245);
        wall.setEndY(595);
        walls.add(wall);

        wall = new WifiWallParam();
        wall.setWallType(4);
        wall.setStartX(245);
        wall.setStartY(595);
        wall.setEndX(245);
        wall.setEndY(567);
        walls.add(wall);

        wall = new WifiWallParam();
        wall.setWallType(4);
        wall.setStartX(245);
        wall.setStartY(567);
        wall.setEndX(146);
        wall.setEndY(567);
        walls.add(wall);

        wall = new WifiWallParam();
        wall.setWallType(4);
        wall.setStartX(146);
        wall.setStartY(567);
        wall.setEndX(146);
        wall.setEndY(397);
        walls.add(wall);

        wall = new WifiWallParam();
        wall.setWallType(4);
        wall.setStartX(146);
        wall.setStartY(397);
        wall.setEndX(68);
        wall.setEndY(397);
        walls.add(wall);

        wall = new WifiWallParam();
        wall.setWallType(4);
        wall.setStartX(68);
        wall.setStartY(397);
        wall.setEndX(81);
        wall.setEndY(156);
        walls.add(wall);
        // 边界墙 end

        wall = new WifiWallParam();
        wall.setWallType(2);
        wall.setStartX(325);
        wall.setStartY(370);
        wall.setEndX(324);
        wall.setEndY(557);
        walls.add(wall);

        return param;
    }

    @Test
    public void wifiMapTest() throws Exception {
        BufferedImage origin = ImageIO.read(MapTest.class.getResourceAsStream("/heatmap/123.png"));

        WifiMapParam param = getParam();
        WifiMap mapGen = new WifiMap(origin, param);
        BufferedImage wifiMap = mapGen.compute();

        String outputFile = MapTest.class.getResource("/").getPath() + "heatmap/wifi.png";
        ImageIO.write(wifiMap, "png", new File(outputFile));
    }

    @Test
    public void intersectTest() {
        Line2D line = new Line2D.Double(144, 59, 400, 400);

        System.out.println(line.intersects(63, 143, 406, 145));
    }

    @Test
    public void testResourcePath() {
        System.out.println(getClass().getResource("/").getPath() + "heatmap/no.png");
    }

    @Test
    public void someValueTest() {
        System.out.println(20 * Math.log10(2400));
    }

}
