package com.hamusuke.threadr.client;

import com.hamusuke.threadr.logging.LogUtil;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.swing.*;
import javax.swing.plaf.FontUIResource;
import java.awt.*;
import java.awt.geom.AffineTransform;

public class MainClient {
    private static final Logger LOGGER = LogManager.getLogger();

    public static void main(String[] args) {
        var is = MainClient.class.getResourceAsStream("/font.otf");
        if (is != null) {
            try {
                var font = Font.createFont(Font.TRUETYPE_FONT, is).deriveFont(20.0F).deriveFont(AffineTransform.getTranslateInstance(0.0D, 4.5D));
                SwingUtilities.invokeLater(() -> setUIFont(new FontUIResource(font)));
                GraphicsEnvironment.getLocalGraphicsEnvironment().registerFont(font);
            } catch (Exception e) {
                LOGGER.warn("Error occurred while loading font", e);
            }
        } else {
            LOGGER.warn("Failed to load font.otf");
        }

        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception ignored) {
        }

        for (var arg : args) {
            if (arg.equals("debug")) {
                LogUtil.setLogLevel(Level.TRACE);
            }
        }

        var client = new ThreadRainbowClient();
        client.run();
    }

    public static void setUIFont(FontUIResource f) {
        var keys = UIManager.getDefaults().keys();
        while (keys.hasMoreElements()) {
            var key = keys.nextElement();
            var value = UIManager.get(key);
            if (value instanceof FontUIResource) {
                UIManager.put(key, f);
            }
        }
    }
}
