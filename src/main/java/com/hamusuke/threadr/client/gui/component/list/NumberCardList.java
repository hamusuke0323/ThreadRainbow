package com.hamusuke.threadr.client.gui.component.list;

import com.hamusuke.threadr.Constants;
import com.hamusuke.threadr.game.card.LocalCard;
import com.hamusuke.threadr.game.card.NumberCard;
import com.hamusuke.threadr.game.card.RemoteCard;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.dnd.*;
import java.awt.image.BufferedImage;
import java.io.IOException;

public class NumberCardList extends JList<NumberCard> implements DragGestureListener, DragSourceListener, Transferable {
    private static final Logger LOGGER = LogManager.getLogger();
    private static final Color LINE_COLOR = new Color(0x64_64_FF);
    private static final String NAME = "test";
    private static final String MIME_TYPE = DataFlavor.javaJVMLocalObjectMimeType;
    private static final DataFlavor FLAVOR = new DataFlavor(MIME_TYPE, NAME);
    private static final Color EVEN_BGC = new Color(0xF0_F0_F0);
    private final Rectangle targetLine = new Rectangle();
    protected int draggedIndex = -1;
    protected int targetIndex = -1;
    protected final BufferedImage card;

    public NumberCardList() {
        super();
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

    @Override
    public void updateUI() {
        setCellRenderer(null);
        super.updateUI();
        var renderer = getCellRenderer();
        setCellRenderer((list, value, index, isSelected, cellHasFocus) -> {
            var c = renderer.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
            if (isSelected) {
                c.setForeground(list.getSelectionForeground());
                c.setBackground(list.getSelectionBackground());
            } else {
                c.setForeground(list.getForeground());
                c.setBackground(index % 2 == 0 ? EVEN_BGC : list.getBackground());
            }
            return c;
        });
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        if (this.card != null) {
            for (int i = 0; i < this.getModel().getSize(); i++) {
                g.drawImage(this.card, i * this.getFixedCellWidth(), 0, null);
                var card = this.getModel().getElementAt(i);
                if (card instanceof LocalCard localCard) {
                    g.drawString("あなたのカード: " + localCard.getNumber(), i * this.getFixedCellWidth(), this.getFixedCellHeight() / 2 - 3);
                } else if (card instanceof RemoteCard remoteCard) {
                    g.drawString(remoteCard.getOwner().getName(), i * this.getFixedCellWidth(), this.getFixedCellHeight() / 2 - 3);
                }
            }
        }

        if (targetIndex >= 0) {
            var g2 = (Graphics2D) g.create();
            g2.setPaint(LINE_COLOR);
            g2.fill(targetLine);
            g2.dispose();
        }
    }

    protected void initTargetLine(Point p) {
        var rect = getCellBounds(0, 0);
        int width = rect.width;
        int lineHeight = rect.height;
        int modelSize = getModel().getSize();
        targetIndex = -1;
        targetLine.setSize(2, lineHeight);
        for (int i = 0; i < modelSize; i++) {
            rect.setLocation(width * i - width / 2, 0);
            if (rect.contains(p)) {
                targetIndex = i;
                targetLine.setLocation(i * width, 0);
                break;
            }
        }
        if (targetIndex < 0) {
            targetIndex = modelSize;
            targetLine.setLocation(targetIndex * width, 0);
        }
    }

    @Override
    public void dragGestureRecognized(DragGestureEvent e) {
        boolean oneOrMore = getSelectedIndices().length > 1;
        draggedIndex = locationToIndex(e.getDragOrigin());
        if (oneOrMore || draggedIndex < 0) {
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
            if (isDragAcceptable(e)) {
                e.acceptDrag(e.getDropAction());
            } else {
                e.rejectDrag();
            }
        }

        @Override
        public void dragOver(DropTargetDragEvent e) {
            if (isDragAcceptable(e)) {
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
            var model = (DefaultListModel<NumberCard>) getModel();
            if (isDropAcceptable(e) && targetIndex >= 0 && draggedIndex != targetIndex - 1) {
                var str = model.get(draggedIndex);
                if (targetIndex == draggedIndex) {
                    setSelectedIndex(targetIndex);
                } else if (targetIndex < draggedIndex) {
                    LOGGER.info("左に動かした");
                    LOGGER.info("{}から{}へ", draggedIndex, targetIndex);
                    model.remove(draggedIndex);
                    model.add(targetIndex, str);
                    setSelectedIndex(targetIndex);
                } else {
                    LOGGER.info("右に動かした");
                    LOGGER.info("{}から{}へ", draggedIndex, targetIndex - 1);
                    model.add(targetIndex, str);
                    model.remove(draggedIndex);
                    setSelectedIndex(targetIndex - 1);
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
            return isDataFlavorSupported(e.getCurrentDataFlavors()[0]);
        }

        private boolean isDropAcceptable(DropTargetDropEvent e) {
            return isDataFlavorSupported(e.getTransferable().getTransferDataFlavors()[0]);
        }
    }
}
