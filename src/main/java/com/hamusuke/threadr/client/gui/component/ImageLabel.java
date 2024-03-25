package com.hamusuke.threadr.client.gui.component;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.annotation.Nullable;
import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;

public class ImageLabel extends JPanel {
    private static final Logger LOGGER = LogManager.getLogger();
    @Nullable
    protected final BufferedImage image;

    public ImageLabel(String name) {
        this.image = read(name);
    }

    @Nullable
    private static BufferedImage read(String name) {
        var is = ImageLabel.class.getResourceAsStream(name);
        if (is == null) {
            return null;
        }

        try {
            return ImageIO.read(is);
        } catch (Exception e) {
            LOGGER.warn("Failed to read the image", e);
        }

        return null;
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        if (this.image != null) {
            g.drawImage(this.image, this.getX(), this.getY(), null);
        }
    }
}
