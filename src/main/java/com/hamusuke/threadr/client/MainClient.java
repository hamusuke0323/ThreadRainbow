package com.hamusuke.threadr.client;

import com.hamusuke.threadr.client.gui.window.ListWindow;

import javax.swing.plaf.FontUIResource;
import java.awt.*;
import java.awt.geom.AffineTransform;
import java.io.IOException;

import static com.hamusuke.threadr.client.gui.window.ListWindow.setUIFont;

public class MainClient {
    public static void main(String[] args) throws IOException, FontFormatException {
        var is = ListWindow.class.getResourceAsStream("/font.otf");
        if (is != null) {
            var font = Font.createFont(Font.TRUETYPE_FONT, is).deriveFont(20.0F).deriveFont(AffineTransform.getTranslateInstance(0.0D, 4.5D));
            EventQueue.invokeLater(() -> setUIFont(new FontUIResource(font)));
        }

        var client = new ThreadRainbowClient();
        client.run();
        client.stop();
    }
}
