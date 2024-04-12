package com.hamusuke.threadr.client.gui.component.list;

import com.hamusuke.threadr.Constants;
import com.hamusuke.threadr.client.ThreadRainbowClient;
import com.hamusuke.threadr.game.card.LocalCard;
import com.hamusuke.threadr.game.card.NumberCard;
import com.hamusuke.threadr.network.protocol.packet.serverbound.play.MoveCardReq;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.dnd.*;
import java.awt.image.BufferedImage;
import java.io.IOException;

public class NumberCardList extends JList<NumberCard> implements DragGestureListener, DragSourceListener, Transferable {
    private static final Color LINE_COLOR = new Color(0x64_64_FF);
    private static final String NAME = "test";
    private static final String MIME_TYPE = DataFlavor.javaJVMLocalObjectMimeType;
    private static final DataFlavor FLAVOR = new DataFlavor(MIME_TYPE, NAME);
    private final Rectangle targetLine = new Rectangle();
    protected int draggedIndex = -1;
    protected int targetIndex = -1;
    protected final BufferedImage card;
    protected final ThreadRainbowClient client;
    private final boolean locked;

    private NumberCardList(ThreadRainbowClient client, boolean locked) {
        this.client = client;
        this.locked = locked;
        this.setCellRenderer(null);
        new DropTarget(this, DnDConstants.ACTION_COPY_OR_MOVE, new ItemDropTargetListener(), true);
        DragSource.getDefaultDragSource().createDefaultDragGestureRecognizer(this, DnDConstants.ACTION_COPY_OR_MOVE, this);

        this.setLayoutOrientation(JList.HORIZONTAL_WRAP);
        this.setVisibleRowCount(1);
        this.setFixedCellWidth(Constants.CARD_WIDTH);
        this.setFixedCellHeight(Constants.CARD_HEIGHT);

        BufferedImage card;
        var is = NumberCardList.class.getResourceAsStream("/card.jpg");
        if (is == null) {
            card = null;
        } else {
            try {
                card = ImageIO.read(is);
            } catch (IOException e) {
                card = null;
            }
        }

        this.card = card;
    }

    public static NumberCardList play(ThreadRainbowClient client) {
        return new NumberCardList(client, false);
    }

    public static NumberCardList result(ThreadRainbowClient client) {
        return new NumberCardList(client, true);
    }

    private void drawCardImage(Graphics g, int x) {
        if (this.card != null) {
            g.drawImage(this.card, x, 0, null);
        }
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        for (int i = 0; i < this.getModel().getSize(); i++) {
            var card = this.getModel().getElementAt(i);
            if (card instanceof LocalCard) {
                if (card.isUncovered()) {
                    g.drawString("あなたのカード", i * this.getFixedCellWidth(), 20);
                    g.drawString("" + card.getNumber(), i * this.getFixedCellWidth() + this.getFixedCellWidth() / 2 - 4, this.getFixedCellHeight() / 2 - 4);
                } else {
                    this.drawCardImage(g, i * this.getFixedCellWidth());
                    g.drawString("あなたのカード: " + card.getNumber(), i * this.getFixedCellWidth(), 20);
                }
            } else {
                if (card.isUncovered()) {
                    g.drawString("" + card.getNumber(), i * this.getFixedCellWidth() + this.getFixedCellWidth() / 2 - 4, this.getFixedCellHeight() / 2 - 4);
                } else {
                    this.drawCardImage(g, i * this.getFixedCellWidth());
                }

                g.drawString(card.getOwner().getName(), i * this.getFixedCellWidth(), 20);
            }
        }

        if (this.targetIndex >= 0) {
            var g2 = (Graphics2D) g.create();
            g2.setPaint(LINE_COLOR);
            g2.fill(this.targetLine);
            g2.dispose();
        }
    }

    protected void initTargetLine(Point p) {
        var rect = getCellBounds(0, 0);
        int width = rect.width;
        int lineHeight = rect.height;
        int modelSize = this.getModel().getSize();
        this.targetIndex = -1;
        this.targetLine.setSize(2, lineHeight);
        for (int i = 0; i < modelSize; i++) {
            rect.setLocation(width * i - width / 2, 0);
            if (rect.contains(p)) {
                this.targetIndex = i;
                this.targetLine.setLocation(i * width, 0);
                break;
            }
        }
        if (this.targetIndex < 0) {
            this.targetIndex = modelSize;
            this.targetLine.setLocation(this.targetIndex * width, 0);
        }
    }

    private void onMoved(int from, int to) {
        this.client.getConnection().sendPacket(new MoveCardReq(from, to));
    }

    @Override
    public void dragGestureRecognized(DragGestureEvent e) {
        boolean oneOrMore = this.getSelectedIndices().length > 1;
        this.draggedIndex = this.locationToIndex(e.getDragOrigin());
        if (oneOrMore || this.draggedIndex < 0) {
            return;
        }
        try {
            e.startDrag(DragSource.DefaultMoveDrop, this, this);
        } catch (InvalidDnDOperationException ex) {
            throw new IllegalStateException(ex);
        }
    }

    @Override
    public void dragEnter(DragSourceDragEvent e) {
        e.getDragSourceContext().setCursor(DragSource.DefaultMoveDrop);
    }

    @Override
    public void dragExit(DragSourceEvent e) {
        e.getDragSourceContext().setCursor(DragSource.DefaultMoveNoDrop);
    }

    @Override
    public void dragOver(DragSourceDragEvent e) {
    }

    @Override
    public void dragDropEnd(DragSourceDropEvent e) {
    }

    @Override
    public void dropActionChanged(DragSourceDragEvent e) {
    }

    @Override
    public Object getTransferData(DataFlavor flavor) {
        return this;
    }

    @Override
    public DataFlavor[] getTransferDataFlavors() {
        return new DataFlavor[]{FLAVOR};
    }

    @Override
    public boolean isDataFlavorSupported(DataFlavor flavor) {
        return NAME.equals(flavor.getHumanPresentableName());
    }

    private final class ItemDropTargetListener implements DropTargetListener {
        @Override
        public void dragExit(DropTargetEvent e) {
            targetIndex = -1;
            repaint();
        }

        @Override
        public void dragEnter(DropTargetDragEvent e) {
            if (this.isDragAcceptable(e)) {
                e.acceptDrag(e.getDropAction());
            } else {
                e.rejectDrag();
            }
        }

        @Override
        public void dragOver(DropTargetDragEvent e) {
            if (this.isDragAcceptable(e)) {
                e.acceptDrag(e.getDropAction());
            } else {
                e.rejectDrag();
                return;
            }
            initTargetLine(e.getLocation());
            repaint();
        }

        @Override
        public void dropActionChanged(DropTargetDragEvent e) {
        }

        @Override
        public void drop(DropTargetDropEvent e) {
            if (this.isDropAcceptable(e) && targetIndex >= 0 && draggedIndex != targetIndex - 1) {
                if (targetIndex == draggedIndex) {
                    setSelectedIndex(targetIndex);
                } else if (targetIndex < draggedIndex) {
                    onMoved(draggedIndex, targetIndex);
                } else {
                    onMoved(draggedIndex, targetIndex - 1);
                }
                e.dropComplete(true);
            } else {
                e.dropComplete(false);
            }
            e.dropComplete(false);
            targetIndex = -1;
            repaint();
        }

        private boolean isDragAcceptable(DropTargetDragEvent e) {
            return !locked && isDataFlavorSupported(e.getCurrentDataFlavors()[0]);
        }

        private boolean isDropAcceptable(DropTargetDropEvent e) {
            return !locked && isDataFlavorSupported(e.getTransferable().getTransferDataFlavors()[0]);
        }
    }
}
