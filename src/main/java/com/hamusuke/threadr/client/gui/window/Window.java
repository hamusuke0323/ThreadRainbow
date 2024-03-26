package com.hamusuke.threadr.client.gui.window;

import com.hamusuke.threadr.client.ThreadRainbowClient;

import javax.annotation.Nullable;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;

public abstract class Window extends JFrame implements ActionListener, WindowListener {
    protected ThreadRainbowClient client = ThreadRainbowClient.getInstance();
    protected Runnable onDisposed = () -> {
    };

    protected Window(String title) {
        this.setTitle(title);
        var bar = this.createMenuBar();
        if (bar != null) {
            this.add(bar, BorderLayout.NORTH);
        }
        this.addWindowListener(this);
        this.setDefaultCloseOperation(HIDE_ON_CLOSE);
    }

    @Nullable
    protected JMenuBar createMenuBar() {
        return null;
    }

    public static void addButton(Container owner, Component component, GridBagLayout layout, int x, int y, int w, int h, double wx, double wy) {
        GridBagConstraints constraints = new GridBagConstraints();
        constraints.fill = GridBagConstraints.BOTH;
        constraints.gridx = x;
        constraints.gridy = y;
        constraints.insets = new Insets(1, 1, 1, 1);
        constraints.gridwidth = w;
        constraints.gridheight = h;
        constraints.weightx = wx;
        constraints.weighty = wy;
        layout.setConstraints(component, constraints);
        owner.add(component);
    }

    public static void addButton(Container owner, Component component, GridBagLayout layout, int x, int y, int w, int h, double wh) {
        addButton(owner, component, layout, x, y, w, h, 1.0D, wh);
    }

    public void init() {
        this.client = ThreadRainbowClient.getInstance();
        this.setBackground(Color.WHITE);
    }

    public void tick() {
    }

    protected void onOpen() {
    }

    protected void onClose() {
        this.dispose();
    }

    protected void onHide() {
    }

    protected void onDisposed() {
        this.onDisposed.run();
    }

    @Override
    public void dispose() {
        this.dispose(() -> {
        });
    }

    public void dispose(Runnable after) {
        this.onDisposed = after;
        super.dispose();
    }

    @Override
    public void actionPerformed(ActionEvent e) {
    }

    @Override
    public void windowOpened(WindowEvent e) {
    }

    @Override
    public void windowClosing(WindowEvent e) {
        this.onClose();
    }

    @Override
    public void windowClosed(WindowEvent e) {
        this.onDisposed();
    }

    @Override
    public void windowIconified(WindowEvent e) {
    }

    @Override
    public void windowDeiconified(WindowEvent e) {
    }

    @Override
    public void windowActivated(WindowEvent e) {
        this.onOpen();
    }

    @Override
    public void windowDeactivated(WindowEvent e) {
        this.onHide();
    }
}
