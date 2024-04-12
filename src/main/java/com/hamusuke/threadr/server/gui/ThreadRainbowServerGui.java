package com.hamusuke.threadr.server.gui;

import com.google.common.collect.Lists;
import com.hamusuke.threadr.logging.LogQueues;
import com.hamusuke.threadr.server.ThreadRainbowServer;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.swing.*;
import javax.swing.border.EtchedBorder;
import javax.swing.border.TitledBorder;
import javax.swing.text.BadLocationException;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.Collection;
import java.util.Objects;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicBoolean;

public class ThreadRainbowServerGui extends JComponent {
    private static final Font MONOSPACED = new Font("Monospaced", Font.PLAIN, 12);
    private static final Logger LOGGER = LogManager.getLogger();
    final AtomicBoolean isClosing = new AtomicBoolean();
    private final ThreadRainbowServer server;
    private final Collection<Runnable> finalizers = Lists.newArrayList();
    private final CountDownLatch latch = new CountDownLatch(1);
    private Thread logAppenderThread;

    private ThreadRainbowServerGui(ThreadRainbowServer server) {
        this.server = server;
        this.setPreferredSize(new Dimension(854, 480));
        this.setLayout(new BorderLayout());

        try {
            this.add(this.buildChatPanel(), "Center");
            this.add(this.buildInfoPanel(), "West");
        } catch (Exception e) {
            LOGGER.error("Couldn't build server GUI", e);
        }
    }

    public static ThreadRainbowServerGui showGuiFor(ThreadRainbowServer server) {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception ignored) {
        }

        var jframe = new JFrame("Thread Rainbow Server");
        var gui = new ThreadRainbowServerGui(server);
        jframe.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        jframe.add(gui);
        jframe.pack();
        jframe.setLocationRelativeTo(null);
        jframe.setVisible(true);
        jframe.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                if (!gui.isClosing.getAndSet(true)) {
                    jframe.setTitle("Thread Rainbow server - shutting down!");
                    server.stop(true);
                    gui.runFinalizers();
                }
            }
        });
        Objects.requireNonNull(jframe);
        gui.addFinalizer(jframe::dispose);
        gui.start();
        return gui;
    }

    public void addFinalizer(Runnable runnable) {
        this.finalizers.add(runnable);
    }

    private JComponent buildInfoPanel() {
        var jpanel = new JPanel(new BorderLayout());
        jpanel.add(this.buildSpiderPanel(), "Center");
        jpanel.setBorder(new TitledBorder(new EtchedBorder(), "Stats"));
        return jpanel;
    }

    private JComponent buildSpiderPanel() {
        var list = new SpiderList(this.server);
        var jscrollpane = new JScrollPane(list, ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS, ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        jscrollpane.setBorder(new TitledBorder(new EtchedBorder(), "Spiders"));
        return jscrollpane;
    }

    private JComponent buildChatPanel() {
        var jpanel = new JPanel(new BorderLayout());
        var jtextarea = new JTextArea();
        var jscrollpane = new JScrollPane(jtextarea, ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS, ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        jtextarea.setEditable(false);
        jtextarea.setFont(MONOSPACED);
        var jtextfield = new JTextField();
        jtextfield.addActionListener(e -> {
            var s = jtextfield.getText().trim();
            if (!s.isEmpty()) {
                if (s.startsWith("/")) {
                    server.enqueueCommand(s.substring(1));
                } else {
                    server.sendMessageToAll(s);
                }
            }

            jtextfield.setText("");
        });
        jpanel.add(jscrollpane, "Center");
        jpanel.add(jtextfield, "South");
        jpanel.setBorder(new TitledBorder(new EtchedBorder(), "Log and chat"));
        this.logAppenderThread = new Thread(() -> {
            String s;
            while ((s = LogQueues.getNextLogEvent("ServerGuiConsole")) != null) {
                this.print(jtextarea, jscrollpane, s);
            }
        });
        this.logAppenderThread.setUncaughtExceptionHandler((t, e) -> LOGGER.error("Caught exception", e));
        this.logAppenderThread.setDaemon(true);
        return jpanel;
    }

    public void start() {
        this.logAppenderThread.start();
        this.latch.countDown();
    }

    public void close() {
        if (!this.isClosing.getAndSet(true)) {
            this.runFinalizers();
        }
    }

    void runFinalizers() {
        this.finalizers.forEach(Runnable::run);
    }

    public void print(JTextArea area, JScrollPane pane, String s) {
        try {
            this.latch.await();
        } catch (InterruptedException ignored) {
        }

        if (!SwingUtilities.isEventDispatchThread()) {
            SwingUtilities.invokeLater(() -> this.print(area, pane, s));
        } else {
            var document = area.getDocument();
            var jscrollbar = pane.getVerticalScrollBar();
            boolean flag = false;
            if (pane.getViewport().getView() == area) {
                flag = (double) jscrollbar.getValue() + jscrollbar.getSize().getHeight() + (double) (MONOSPACED.getSize() * 4) > (double) jscrollbar.getMaximum();
            }

            try {
                document.insertString(document.getLength(), s, null);
            } catch (BadLocationException ignored) {
            }

            if (flag) {
                jscrollbar.setValue(Integer.MAX_VALUE);
            }
        }
    }
}
