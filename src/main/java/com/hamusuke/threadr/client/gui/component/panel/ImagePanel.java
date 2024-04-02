package com.hamusuke.threadr.client.gui.component.panel;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.annotation.Nullable;
import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;

public class ImagePanel extends JPanel {
    private static final Logger LOGGER = LogManager.getLogger();
    @Nullable
    protected final BufferedImage image;
    protected final boolean centered;

    public ImagePanel(String name) {
        this(name, false);
    }

    public ImagePanel(String name, boolean centered) {
        this.image = read(name);
        this.centered = centered;
    }

    @Nullable
    private static BufferedImage read(String name) {
        var is = ImagePanel.class.getResourceAsStream(name);
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
            int x = this.centered ?
                    (int) (this.getX() + this.getWidth() / 2 - this.getPreferredSize().getWidth() / 2)
                    : this.getX();
            int y = this.centered ?
                    (int) (this.getY() + this.getHeight() / 2 - this.getPreferredSize().getHeight() / 2)
                    : this.getY();
            g.drawImage(this.image, x, y, null);
        }
    }
}
