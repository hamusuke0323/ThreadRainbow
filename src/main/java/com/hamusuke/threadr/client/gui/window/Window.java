package com.hamusuke.threadr.client.gui.window;

import com.hamusuke.threadr.client.ThreadRainbowClient;

import javax.annotation.Nullable;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public abstract class Window extends JFrame implements ActionListener, WindowListener, ComponentListener {
    protected ThreadRainbowClient client = ThreadRainbowClient.getInstance();
    @Nullable
    protected JMenuBar menu;

    protected Window(String title) {
        this.setTitle(title);
        this.addWindowListener(this);
        this.addComponentListener(this);
        this.setDefaultCloseOperation(HIDE_ON_CLOSE);
    }

    @Nullable
    protected JMenuBar createMenuBar() {
        return null;
    }

    public void init() {
        this.client = ThreadRainbowClient.getInstance();
        this.setBackground(Color.WHITE);
    }

    public void tick() {
    }

    protected void onClose() {
        this.dispose();
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
    }

    @Override
    public void windowIconified(WindowEvent e) {
    }

    @Override
    public void windowDeiconified(WindowEvent e) {
    }

    @Override
    public void windowActivated(WindowEvent e) {
    }

    @Override
    public void windowDeactivated(WindowEvent e) {
    }

    @Override
    public void componentHidden(ComponentEvent e) {
    }

    @Override
    public void componentMoved(ComponentEvent e) {
    }

    @Override
    public void componentResized(ComponentEvent e) {
    }

    @Override
    public void componentShown(ComponentEvent e) {
    }
}
