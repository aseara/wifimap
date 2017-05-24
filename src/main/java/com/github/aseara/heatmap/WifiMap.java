package com.github.aseara.heatmap;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

/**
 * Created by qiujingde on 2017/5/22.
 *
 */
public class WifiMap {

    /**
     * saves the image in the provided buffer to the destination.
     *
     * @param buff
     *            buffer to be saved
     * @param dest
     *            destination to save at
     */
    private void saveImage(final BufferedImage buff, final String dest) {
        try {
            final File outputfile = new File(dest);
            ImageIO.write(buff, "png", outputfile);
        } catch (final IOException e) {
            print("error saving the image: " + dest + ": " + e);
        }
    }

    private BufferedImage getWifiImage(int w, int h, int cx, int cy, int radius) {
        BufferedImage image = new BufferedImage(w, h, BufferedImage.TYPE_4BYTE_ABGR);
        for (int i = 0; i < w; i++) {
            for (int j = 0; j < h; j++) {
                image.setRGB(i, j, getRgb(cx, cy, i, j, radius));
            }
        }
        return image;
    }

    private int getRgb(int cx, int cy, int x, int y, int radius) {
        double d = Math.sqrt(Math.pow(x - cx, 2) + Math.pow(y - cy, 2));
        d = d / radius;
        if (d > 1) {
            d = 1;
        }
        d = d * Math.PI / 2;
        d = Math.cos(d);
        if (d > 1) {
            d = 1;
        } else if (d < 0) {
            d = 0;
        }
        int cs = (int)(d * 255);
        return new Color(0, 0, 0, cs).getRGB();
    }

    /**
     * prints string to sto.
     *
     * @param s
     *            string to print
     */
    private void print(final String s) {
        System.out.println(s);
    }

    public static void main(String[] args) {
        final String outputFile = Main.class.getResource("/heatmap/wifi.png").getPath();
        WifiMap map = new WifiMap();
        int w = 800;
        int h = 800;
        int cx = w / 2;
        int cy = h / 2;
        int radius = 400;
        BufferedImage image = map.getWifiImage(w, h, cx, cy, radius);
        map.saveImage(image, outputFile);
    }

}
