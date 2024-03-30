package com.hamusuke.threadr.client;

import javax.swing.*;
import javax.swing.plaf.FontUIResource;
import java.awt.*;
import java.awt.geom.AffineTransform;
import java.io.IOException;

public class MainClient {
    public static void main(String[] args) throws IOException, FontFormatException {
        var is = MainClient.class.getResourceAsStream("/font.otf");
        if (is != null) {
            var font = Font.createFont(Font.TRUETYPE_FONT, is).deriveFont(20.0F).deriveFont(AffineTransform.getTranslateInstance(0.0D, 4.5D));
            EventQueue.invokeLater(() -> setUIFont(new FontUIResource(font)));
            GraphicsEnvironment.getLocalGraphicsEnvironment().registerFont(font);
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
