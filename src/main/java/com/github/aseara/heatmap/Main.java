package com.github.aseara.heatmap;

import java.awt.Point;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Main {

    /**
     * @param args args
     */
    public static void main(final String[] args) {
        final List<Point> points = new ArrayList<>();

        Random random = new Random(47);

        for (int i = 0; i < 100; i++) {
            final int x = 400 + random.nextInt(200);
            final int y = 400 + random.nextInt(200);
            final Point p = new Point(x, y);
            points.add(p);
        }

        final String outputFile = Main.class.getResource("/heatmap/check.png").getPath();
        final String originalImage = Main.class.getResource("/heatmap/123.png").getPath();
        final HeatMap myMap = new HeatMap(points, outputFile, originalImage);
        myMap.createHeatMap(1f);
    }

}
