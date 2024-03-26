package com.hamusuke.threadr.client.gui.window;

import com.hamusuke.threadr.game.topic.TopicLoader;
import com.hamusuke.threadr.util.Util;

import javax.swing.*;
import javax.swing.plaf.FontUIResource;
import java.awt.*;
import java.awt.geom.AffineTransform;
import java.io.IOException;
import java.util.Random;

public class ListWindow extends Window {
    public ListWindow() {
        super("ゲーム");
    }

    public static void main(String[] args) throws IOException, FontFormatException {
        var is = ListWindow.class.getResourceAsStream("/font.otf");
        if (is != null) {
            var font = Font.createFont(Font.TRUETYPE_FONT, is).deriveFont(20.0F).deriveFont(AffineTransform.getTranslateInstance(0.0D, 4.5D));
            EventQueue.invokeLater(() -> setUIFont(new FontUIResource(font)));
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

        /*
        var image = new ImageLabel("/zero.jpg");
        image.setPreferredSize(new Dimension(Constants.CARD_WIDTH, Constants.CARD_HEIGHT));

        var list = new NumberCardList<Integer>();
        var model = new DefaultListModel<Integer>();
        model.addAll(Lists.newArrayList(1, 5, 8, 10, 15, 22, 24));
        list.setModel(model);

        var p = new JPanel(new BorderLayout());
        p.add(new JScrollPane(list));
        p.setPreferredSize(new Dimension(list.getFixedCellWidth() * MathHelper.clamp(model.getSize(), 0, 7) + 10, list.getFixedCellHeight() + 10));

        this.setLayout(new BoxLayout(this.getContentPane(), BoxLayout.X_AXIS));
        this.getContentPane().add(image);
        this.getContentPane().add(p);
         */
        var loader = new TopicLoader();
        loader.loadTopics();

        var topic = Util.chooseRandom(loader.getTopics(), new Random());

        this.getContentPane().add(topic.toPanel(), BorderLayout.CENTER);

        this.pack();
        this.setLocationRelativeTo(null);
    }

    @Override
    protected void onClose() {
        System.exit(0);
    }
}
