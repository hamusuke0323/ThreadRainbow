package com.hamusuke.threadr.client.gui.window;

import com.google.common.collect.Lists;
import com.hamusuke.threadr.Constants;
import com.hamusuke.threadr.client.gui.component.list.NumberCardList;
import com.hamusuke.threadr.util.MathHelper;

import javax.annotation.Nullable;
import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.plaf.FontUIResource;
import javax.swing.text.StyledEditorKit.FontSizeAction;
import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.Arrays;

public class ListWindow extends Window {
    @Nullable
    private final BufferedImage zero;

    public ListWindow() {
        super("ゲーム");

        BufferedImage zero;
        var is = ListWindow.class.getResourceAsStream("/zero.jpg");
        if (is == null) {
            zero = null;
        } else {
            try {
                zero = ImageIO.read(is);
            } catch (Exception ignored) {
                zero = null;
            }
        }

        this.zero = zero;
    }

    public static void main(String[] args) throws IOException, FontFormatException {
        var is = ListWindow.class.getResourceAsStream("/font.otf");
        if (is != null) {
            var font = Font.createFont(Font.TRUETYPE_FONT, is).deriveFont(20.0F).deriveFont(AffineTransform.getTranslateInstance(0.0D, 4.5D));
            EventQueue.invokeLater(() -> setUIFont(new FontUIResource(font)));
        } else {
            EventQueue.invokeLater(() -> setUIFont(new FontUIResource("Serif", Font.BOLD, 20)));
        }

        var window = new ListWindow();
        window.init();
        window.setVisible(true);
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

    @Override
    public void init() {
        super.init();

        var image = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);

                g.drawImage(zero, this.getX(), this.getY(), null);
            }
        };
        image.setPreferredSize(new Dimension(Constants.CARD_WIDTH, Constants.CARD_HEIGHT));

        var list = new NumberCardList<Integer>();
        var model = new DefaultListModel<Integer>();
        model.addAll(Lists.newArrayList(1, 5, 8, 10, 15, 22, 24, 40));
        list.setModel(model);

        var p = new JPanel(new BorderLayout());
        p.add(new JScrollPane(list));
        p.setPreferredSize(new Dimension(list.getFixedCellWidth() * MathHelper.clamp(model.getSize(), 0, 7) + 10, list.getFixedCellHeight() + 10));

        this.setLayout(new BoxLayout(this.getContentPane(), BoxLayout.X_AXIS));
        this.getContentPane().add(image);
        this.getContentPane().add(p);
        this.pack();
        this.setLocationRelativeTo(null);
    }

    @Override
    protected void onClose() {
        System.exit(0);
    }
}
