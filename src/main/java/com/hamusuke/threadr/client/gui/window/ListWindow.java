package com.hamusuke.threadr.client.gui.window;

import com.google.common.collect.Lists;
import com.hamusuke.threadr.Constants;
import com.hamusuke.threadr.client.gui.component.ImageLabel;
import com.hamusuke.threadr.client.gui.component.list.NumberCardList;
import com.hamusuke.threadr.client.network.spider.LocalSpider;
import com.hamusuke.threadr.client.network.spider.RemoteSpider;
import com.hamusuke.threadr.game.card.LocalCard;
import com.hamusuke.threadr.game.card.NumberCard;
import com.hamusuke.threadr.game.card.RemoteCard;

import javax.swing.*;
import javax.swing.plaf.FontUIResource;
import java.awt.*;
import java.awt.geom.AffineTransform;
import java.io.IOException;

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

        var image = new ImageLabel("/zero.jpg");
        image.setPreferredSize(new Dimension(Constants.CARD_WIDTH, Constants.CARD_HEIGHT));

        var list = new NumberCardList();
        var model = new DefaultListModel<NumberCard>();
        model.addElement(new LocalCard(new LocalSpider("あああ"), (byte) 8));
        model.addAll(Lists.newArrayList(1, 5, 10, 15, 22, 24).stream().map(integer -> new RemoteCard(new RemoteSpider("nanashi" + integer))).toList());
        list.setModel(model);

        var p = new JPanel();
        p.setLayout(new BoxLayout(p, BoxLayout.X_AXIS));
        p.add(image);
        p.add(new JScrollPane(list));
        //p.setPreferredSize(new Dimension(list.getFixedCellWidth() * MathHelper.clamp(model.getSize(), 0, 7) + 10, list.getFixedCellHeight() + 10));

        this.add(p);
        this.pack();
        this.setLocationRelativeTo(null);
    }

    @Override
    protected void onClose() {
        System.exit(0);
    }
}
