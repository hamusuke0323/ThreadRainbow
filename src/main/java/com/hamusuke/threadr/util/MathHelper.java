package com.hamusuke.threadr.util;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class MathHelper {
    public static int clamp(int value, int min, int max) {
        return value < min ? min : Math.min(value, max);
    }

    public static void main(String[] args) throws IOException {
        BufferedImage bufferedImage = new BufferedImage(1920, 1080, BufferedImage.TYPE_INT_ARGB);
        ImageIO.write(bufferedImage, "JPEG", new File("test.jpg"));
    }
}
