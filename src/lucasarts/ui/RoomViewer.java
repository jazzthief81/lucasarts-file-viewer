package lucasarts.ui;

import lucasarts.format.gamefile.*;
import lucasarts.format.gamefile.Box;

import javax.swing.*;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeCellRenderer;
import javax.swing.tree.DefaultTreeModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by Lars on 09-04-2017.
 */
public class RoomViewer extends JPanel {

    public static final int SCALE_FACTOR = 2;
    private final JTree fTree;
    private final JPanel fObjectList;
    private final JLabel fImageViewer;
    private final ClutViewer fClutViewer;
    private BitmapRenderer bitmapRenderer = new BitmapRenderer();
    private Room fRoom;
    private List<GameFileSection> fEnabledSections = new ArrayList<>();
    private boolean fClutEnabled;
    private boolean fBoxdEnabled;
    private boolean fBoxMatrixEnabled;
    private boolean fCycleEnabled;
    private Color[] fColors;
    private int[] fColorCycleCounters;
    private final Timer fCycleTimer;

    public RoomViewer() {
        super(new BorderLayout());
        fImageViewer = new JLabel();
        fImageViewer.setHorizontalAlignment(SwingConstants.CENTER);
        fClutViewer = new ClutViewer();
        fTree = new JTree();
        fTree.setModel(null);
        fTree.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) {
                    DefaultMutableTreeNode node = (DefaultMutableTreeNode) fTree.getLastSelectedPathComponent();
                    Object userObject = node.getUserObject();
                    if (userObject instanceof GameFileSection) {
                        if (fEnabledSections.contains(userObject)) {
                            fEnabledSections.remove(userObject);
                        }
                        else {
                            fEnabledSections.add((GameFileSection) userObject);
                        }
                        fClutEnabled = fEnabledSections.contains(fRoom.getClut());
                        fBoxdEnabled = fEnabledSections.contains(fRoom.getBoxd());
                        fBoxMatrixEnabled = fEnabledSections.contains(fRoom.getBoxMatrix());
                        fCycleEnabled = fEnabledSections.contains(fRoom.getColorCycle());
                        fTree.repaint();
                        drawImage();
                    }
                }
            }
        });
        fCycleTimer = new Timer(20, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if(fCycleEnabled && fRoom != null) {
                    cycleColors();
                }
            }
        });
        fCycleTimer.setRepeats(true);
        fCycleTimer.start();
        JPanel roomDetailPanel = new JPanel(new GridLayout(2, 1));
        fObjectList = new JPanel(new GridBagLayout());
        roomDetailPanel.add(new JScrollPane(fObjectList));
        JSplitPane splitPane = new JSplitPane();
        splitPane.setDividerLocation(200);
//        splitPane.setLeftComponent(roomDetailPanel);
        splitPane.setLeftComponent(new JScrollPane(fTree));
        JPanel imageViewerPanel = new JPanel(new FlowLayout());
        imageViewerPanel.add(fImageViewer);
        imageViewerPanel.add(fClutViewer);
        splitPane.setRightComponent(new JScrollPane(imageViewerPanel));
        add(splitPane, BorderLayout.CENTER);
    }

    public Room getRoom() {
        return fRoom;
    }

    public void setRoom(Room aRoom) {
        fRoom = aRoom;
        fEnabledSections.clear();
        fCycleEnabled = false;
        fTree.setModel(null);
        fObjectList.removeAll();

        if (fRoom != null) {
            DefaultMutableTreeNode roomNode = new DefaultMutableTreeNode(aRoom);

            roomNode.add(new DefaultMutableTreeNode(aRoom.getColorCycle()));
            roomNode.add(new DefaultMutableTreeNode(aRoom.getBoxd()));
            roomNode.add(new DefaultMutableTreeNode(aRoom.getBoxMatrix()));
            roomNode.add(new DefaultMutableTreeNode(aRoom.getClut()));

            DefaultMutableTreeNode roomImageNode = new DefaultMutableTreeNode(aRoom.getRoomImage());
            roomNode.add(roomImageNode);

            lucasarts.format.gamefile.Image roomImageImage = aRoom.getRoomImage().getImage();
            DefaultMutableTreeNode roomImageImageNode = new DefaultMutableTreeNode(roomImageImage);
            roomImageNode.add(roomImageImageNode);
            roomImageImageNode.add(new DefaultMutableTreeNode(roomImageImage.getSmap()));
            List<Zp> roomImageImageZpImages = roomImageImage.getZp();
            for (Zp roomImageImageZpImage : roomImageImageZpImages) {
                roomImageImageNode.add(new DefaultMutableTreeNode(roomImageImageZpImage));
            }

            List<ObjectImage> objectImages = fRoom.getObjectImages();
            for (ObjectImage objectImage : objectImages) {
                DefaultMutableTreeNode objectImageTreeNode = new DefaultMutableTreeNode(objectImage);
                List<lucasarts.format.gamefile.Image> images = objectImage.getImages();
                for (lucasarts.format.gamefile.Image image : images) {
                    DefaultMutableTreeNode imageTreeNode = new DefaultMutableTreeNode(image);
                    objectImageTreeNode.add(imageTreeNode);
                    imageTreeNode.add(new DefaultMutableTreeNode(image.getSmap()));
                    for (Zp zp : image.getZp()) {
                        imageTreeNode.add(new DefaultMutableTreeNode(zp));
                    }
                }
                roomNode.add(objectImageTreeNode);
            }

            DefaultTreeModel newModel = new DefaultTreeModel(roomNode);

            fTree.setModel(newModel);
            fTree.setCellRenderer(new DefaultTreeCellRenderer() {
                @Override
                public Component getTreeCellRendererComponent(JTree tree, Object value, boolean sel, boolean expanded, boolean leaf, int row, boolean hasFocus) {
                    DefaultMutableTreeNode node = (DefaultMutableTreeNode) value;
                    Object userObject = node.getUserObject();
                    if (userObject instanceof GameFileSection) {
                        GameFileSection gameFileSection = (GameFileSection) userObject;
                        Component treeCellRendererComponent = super.getTreeCellRendererComponent(tree, gameFileSection.getKeyword(), sel, expanded, leaf, row, hasFocus);
                        if (fEnabledSections.contains(gameFileSection)) {
                            treeCellRendererComponent.setFont(treeCellRendererComponent.getFont().deriveFont(Font.BOLD));
                        }
                        else {
                            treeCellRendererComponent.setFont(treeCellRendererComponent.getFont().deriveFont(Font.PLAIN));
                        }
                        return treeCellRendererComponent;
                    }
                    else {
                        return super.getTreeCellRendererComponent(tree, userObject, sel, expanded, leaf, row, hasFocus);
                    }
                }
            });

            for (int row = 0; row < fTree.getRowCount(); row++) {
                fTree.expandRow(row);
            }

            // Update list.
            List<ObjectCode> objectCodes = fRoom.getObjectCodes();

            GridBagConstraints c = new GridBagConstraints();
            c.gridx = GridBagConstraints.REMAINDER;
            c.gridy = GridBagConstraints.RELATIVE;
            c.weightx = 1;
            c.weighty = 0;
            c.anchor = GridBagConstraints.WEST;
            for (ObjectCode objectCode : objectCodes) {
                String objectName = objectCode.getObjectName();
                if (objectName.isEmpty()) {
                    objectName = "<No name>";
                }
                JCheckBox objectCheckBox = new JCheckBox(objectName);
                ObjectImage objectImageForObjectCode = null;
                for (ObjectImage objectImage : objectImages) {
                    if (objectImage.getObjectIdentifier() == objectCode.getObjectIdentifier()) {
                        objectImageForObjectCode = objectImage;
                        break;
                    }
                }

                objectCheckBox.addActionListener(new ToggleRoomObjectVisibilityListener(objectImageForObjectCode));
                fObjectList.add(objectCheckBox, c);
            }
            c.weighty = 1;
            fObjectList.add(new JPanel(), c);

            fEnabledSections.add(fRoom.getRoomImage().getImage().getSmap());

            Color[] roomColors = fRoom.getClut().getColors();
            fColors = new Color[roomColors.length];
            System.arraycopy(roomColors, 0, fColors, 0, roomColors.length);

            fColorCycleCounters = new int[fRoom.getColorCycle().entries.size()];
            for (Map.Entry<Integer, ColorCycleEntry> entry : fRoom.getColorCycle().entries.entrySet()) {
                int countDown = 0x4000 / entry.getValue().getCycleSpeed();
                fColorCycleCounters[entry.getKey()-1] = countDown;
            }
        }

        fObjectList.revalidate();
        fObjectList.repaint();
        drawImage();
    }

    private void drawImage() {
        if (fRoom != null) {
            BufferedImage iconImage = new BufferedImage(fRoom.getRoomWidth() * SCALE_FACTOR, fRoom.getRoomHeight() * SCALE_FACTOR, BufferedImage.TYPE_INT_ARGB);
            Graphics2D graphics = (Graphics2D) iconImage.getGraphics();
            graphics.scale(SCALE_FACTOR, SCALE_FACTOR);

            if (fEnabledSections.contains(fRoom.getRoomImage().getImage().getSmap())) {
                java.awt.Image renderedRoomSmapImage =
                        bitmapRenderer.render(fRoom.getRoomImage().getImage().getSmap().getPixelValues(),
                                fRoom.getRoomWidth(), fRoom.getRoomHeight(), fColors,
                                new RoomPixel2ClutMapper(fRoom.getClut().isAmigaColors()), false);
                graphics.drawImage(renderedRoomSmapImage, 0, 0, null);
            }

            List<Zp> roomZps = fRoom.getRoomImage().getImage().getZp();
            for (Zp zp : roomZps) {
                if (fEnabledSections.contains(zp)) {
                    java.awt.Image renderedRoomZpImage =
                            bitmapRenderer.render(zp.getPixelValues(),
                                    fRoom.getRoomWidth(), fRoom.getRoomHeight(), new Color[]{Color.BLACK, Color.WHITE},
                                    new RoomPixel2ClutMapper(false), false);
                    graphics.drawImage(renderedRoomZpImage, 0, 0, null);
                }
            }

            List<ObjectImage> objectImages = fRoom.getObjectImages();
            for (ObjectImage objectImage : objectImages) {
                List<lucasarts.format.gamefile.Image> images = objectImage.getImages();
                for (lucasarts.format.gamefile.Image image : images) {
                    if (fEnabledSections.contains(image.getSmap())) {
                        java.awt.Image renderedObjectSmapImage =
                                bitmapRenderer.render(image.getSmap().getPixelValues(),
                                        objectImage.getImageWidth(), objectImage.getImageHeight(), fColors,
                                        new RoomPixel2ClutMapper(fRoom.getClut().isAmigaColors()), false);
                        graphics.drawImage(renderedObjectSmapImage, objectImage.getImageX(), objectImage.getImageY(), null);
                    }
                    List<Zp> objectZps = image.getZp();
                    for (Zp zp : objectZps) {
                        if (fEnabledSections.contains(zp)) {
                            java.awt.Image renderedObjectZpImage =
                                    bitmapRenderer.render(zp.getPixelValues(),
                                            objectImage.getImageWidth(), objectImage.getImageHeight(), new Color[]{Color.BLACK, Color.WHITE},
                                            new RoomPixel2ClutMapper(false), false);
                            graphics.drawImage(renderedObjectZpImage, objectImage.getImageX(), objectImage.getImageY(), null);
                        }
                    }
                }
            }

            if (fClutEnabled) {
                fClutViewer.setClut(fColors);
            }
            else {
                fClutViewer.setClut(null);
            }

            if(fBoxdEnabled) {
                graphics.setColor(Color.RED);
                List<Box> boxes = fRoom.getBoxd().getBoxes();
                for (Box box : boxes) {
                    Polygon polygon = getPolygon(box);
                    graphics.drawPolygon(polygon);
                }
            }

            if(fBoxMatrixEnabled) {
                graphics.setColor(Color.YELLOW);
                List<Box> boxes = fRoom.getBoxd().getBoxes();
                List<BoxMatrixRow> boxMatrixRows = fRoom.getBoxMatrix().getBoxMatrixRows();
                for (int boxMatrixRowIndex = 0; boxMatrixRowIndex < boxMatrixRows.size(); boxMatrixRowIndex++) {
                    BoxMatrixRow boxMatrixRow = boxMatrixRows.get(boxMatrixRowIndex);
                    List<BoxMatrixRowRange> boxMatrixRowRanges = boxMatrixRow.getBoxMatrixRowRanges();
                    Point sourcePoint = getBoxCenter(boxes.get(boxMatrixRowIndex));
                    for (BoxMatrixRowRange boxMatrixRowRange : boxMatrixRowRanges) {
                        int nextBoxIndex = boxMatrixRowRange.getNextBoxIndex();
                        Point nextPoint = getBoxCenter(boxes.get(nextBoxIndex));
                        graphics.drawLine(sourcePoint.x, sourcePoint.y, nextPoint.x, nextPoint.y);
                    }
                }
            }

            fImageViewer.setIcon(new ImageIcon(iconImage));
        }
        else {
            fImageViewer.setIcon(null);
        }
    }

    private void cycleColors(){
        boolean redraw = false;
        Map<Integer, ColorCycleEntry> colorCycleEntries = fRoom.getColorCycle().entries;
        for (Map.Entry<Integer, ColorCycleEntry> colorCycleEntryEntry : colorCycleEntries.entrySet()) {
            Integer colorCycleEntryIndex = colorCycleEntryEntry.getKey()-1;
            fColorCycleCounters[colorCycleEntryIndex]--;
            if (fColorCycleCounters[colorCycleEntryIndex] == 0) {
                ColorCycleEntry colorCycleEntry = colorCycleEntryEntry.getValue();
                if((colorCycleEntry.getCycleDirection() & 0x2) == 0){
                    Color stopIndex = fColors[colorCycleEntry.getCycleStopIndex()];
                    for (int colorIndex = colorCycleEntry.getCycleStopIndex(); colorIndex > colorCycleEntry.getCycleStartIndex(); colorIndex--) {
                        fColors[colorIndex] = fColors[colorIndex - 1];
                    }
                    fColors[colorCycleEntry.getCycleStartIndex()] = stopIndex;
                }
                else{
                    Color startIndex = fColors[colorCycleEntry.getCycleStartIndex()];
                    for (int colorIndex = colorCycleEntry.getCycleStartIndex(); colorIndex < colorCycleEntry.getCycleStopIndex(); colorIndex++) {
                        fColors[colorIndex] = fColors[colorIndex + 1];
                    }
                    fColors[colorCycleEntry.getCycleStopIndex()] = startIndex;
                }

                fColorCycleCounters[colorCycleEntryIndex] = 0x4000 / colorCycleEntry.getCycleSpeed();
                redraw = true;
            }
        }
        if (redraw) {
            drawImage();
        }
    }

    private Polygon getPolygon(Box box) {
        Polygon polygon = new Polygon();
        for (int pointIndex = 0; pointIndex < box.getXs().length; pointIndex++) {
            polygon.addPoint(box.getXs()[pointIndex], box.getYs()[pointIndex]);
        }
        return polygon;
    }

    private Point getBoxCenter(Box box) {
        double x = 0;
        double y = 0;
        for (int pointIndex = 0; pointIndex < box.getXs().length; pointIndex++) {
            x += box.getXs()[pointIndex];
            y += box.getYs()[pointIndex];
        }
        x /= box.getXs().length;
        y /= box.getYs().length;
        return new Point((int) x, (int) y);
    }


    private class ToggleRoomObjectVisibilityListener implements ActionListener {

        private ObjectImage fObjectImage;

        public ToggleRoomObjectVisibilityListener(ObjectImage aObjectImage) {
            fObjectImage = aObjectImage;
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            if (((JCheckBox) e.getSource()).isSelected()) {
            }
            else {
            }
        }

    }

}
