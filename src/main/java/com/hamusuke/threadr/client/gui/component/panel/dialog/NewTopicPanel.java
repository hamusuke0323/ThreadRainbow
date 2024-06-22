package com.hamusuke.threadr.client.gui.component.panel.dialog;

import com.hamusuke.threadr.client.gui.component.panel.Panel;
import com.hamusuke.threadr.game.topic.Topic;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.function.Consumer;
import java.util.function.Function;

public class NewTopicPanel extends Panel {
    private static final Function<JTextField, KeyAdapter> LENGTH_CHECKER = jTextField -> new KeyAdapter() {
        @Override
        public void keyTyped(KeyEvent e) {
            if (jTextField.getText().length() >= Topic.MAX_TEXT_LENGTH) {
                e.consume();
            }
        }
    };
    private final Consumer<NewTopicPanel> callback;
    private boolean accepted;
    private JCheckBox twoLines;
    private JTextField mainText;
    private JTextField subText;
    private JTextField minDesc;
    private JTextField maxDesc;

    public NewTopicPanel(Consumer<NewTopicPanel> callback) {
        super(new GridBagLayout());
        this.callback = callback;
    }

    private static boolean invalidateStr(String text) {
        return text.isEmpty() || text.isBlank();
    }

    @Override
    public void init() {
        super.init();

        var mainText = new JLabel("タイトル", SwingConstants.CENTER);
        this.mainText = new JTextField("タイトル");
        this.mainText.addKeyListener(LENGTH_CHECKER.apply(this.mainText));

        this.twoLines = new JCheckBox("2行");
        this.twoLines.addActionListener(e -> this.subText.setEnabled(this.twoLines.isSelected()));

        var subTextLabel = new JLabel("サブタイトル");
        this.subText = new JTextField("...になって考えよう");
        this.subText.setEnabled(this.twoLines.isSelected());
        this.subText.addKeyListener(LENGTH_CHECKER.apply(this.subText));

        var minDescLabel = new JLabel("小さい数字の説明");
        this.minDesc = new JTextField("...しない");
        this.minDesc.addKeyListener(LENGTH_CHECKER.apply(this.minDesc));

        var maxDescLabel = new JLabel("大きい数字の説明");
        this.maxDesc = new JTextField("...する");
        this.maxDesc.addKeyListener(LENGTH_CHECKER.apply(this.maxDesc));

        var create = new JButton("作成する");
        create.addActionListener(e -> this.create());
        var cancel = new JButton("キャンセル");
        cancel.addActionListener(e -> this.onClose());

        var l = (GridBagLayout) getLayout();
        addButton(this, mainText, l, 0, 0, 1, 1, 0.0125D);
        addButton(this, this.mainText, l, 0, 1, 1, 1, 0.125D);
        addButton(this, this.twoLines, l, 0, 2, 1, 1, 0.125D);
        addButton(this, subTextLabel, l, 0, 3, 1, 1, 0.0125D);
        addButton(this, this.subText, l, 0, 4, 1, 1, 0.125D);
        addButton(this, minDescLabel, l, 0, 5, 1, 1, 0.0125D);
        addButton(this, this.minDesc, l, 0, 6, 1, 1, 0.125D);
        addButton(this, maxDescLabel, l, 0, 7, 1, 1, 0.0125D);
        addButton(this, this.maxDesc, l, 0, 8, 1, 1, 0.125D);
        addButton(this, create, l, 0, 9, 1, 1, 0.125D);
        addButton(this, cancel, l, 0, 10, 1, 1, 0.125D);
    }

    @Override
    public JMenuBar createMenuBar() {
        var jMenuBar = new JMenuBar();
        jMenuBar.add(this.createMenuMenu());
        jMenuBar.add(this.createChatMenu());
        jMenuBar.add(this.createNetworkMenu());
        return jMenuBar;
    }

    private void create() {
        var main = this.getMainText();
        if (invalidateStr(main)) {
            return;
        }

        var sub = this.getSubText();
        if (this.hasSubText() && invalidateStr(sub)) {
            return;
        }

        var min = this.getMinDesc();
        if (invalidateStr(min)) {
            return;
        }

        var max = this.getMaxDesc();
        if (invalidateStr(max)) {
            return;
        }

        this.accepted = true;
        this.onClose();
    }

    public String getMainText() {
        var mainTextText = this.mainText.getText();
        return mainTextText.substring(0, Math.min(mainTextText.length(), Topic.MAX_TEXT_LENGTH));
    }

    public String getSubText() {
        var subTextText = this.subText.getText();
        return subTextText.substring(0, Math.min(subTextText.length(), Topic.MAX_TEXT_LENGTH));
    }

    public String getMinDesc() {
        var minDescText = this.minDesc.getText();
        return minDescText.substring(0, Math.min(minDescText.length(), Topic.MAX_TEXT_LENGTH));
    }

    public String getMaxDesc() {
        var maxDescText = this.maxDesc.getText();
        return maxDescText.substring(0, Math.min(maxDescText.length(), Topic.MAX_TEXT_LENGTH));
    }

    public boolean hasSubText() {
        return this.twoLines.isSelected();
    }

    public boolean isAccepted() {
        return this.accepted;
    }

    @Override
    public void onClose() {
        this.callback.accept(this);
    }
}
