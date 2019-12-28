package lucasarts.ui;

import lucasarts.format.gamefile.*;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;

/**
 * Created by Lars on 03-06-2018.
 */
public class CostumeViewer extends JPanel {

    public static final int SCALE_FACTOR = 2;
    private final BitmapRenderer bitmapRenderer = new BitmapRenderer();
    private final JPanel fImageViewer;
    private Costume fCostume;

    public CostumeViewer() {
        super(new BorderLayout());
        fImageViewer = new JPanel();
        add(new JScrollPane(fImageViewer), BorderLayout.CENTER);
    }

    public Costume getCostume() {
        return fCostume;
    }

    public void setCostume(Costume aCostume) {
        fCostume = aCostume;
        drawImage();
    }

    private void drawImage() {
        fImageViewer.removeAll();
        if (fCostume != null) {
            fImageViewer.setLayout(new FlowLayout());
            for (int costumeIndex = 0; costumeIndex < fCostume.getCostumeFrames().length;costumeIndex++){
                CostumeFrame costumeFrame = fCostume.getCostumeFrames()[costumeIndex];
                if(costumeFrame!=null && costumeFrame.getImageWidth() != 0 && costumeFrame.getImageHeight() != 0) {
                    BufferedImage iconImage = new BufferedImage(costumeFrame.getImageWidth() * SCALE_FACTOR, costumeFrame.getImageHeight() * SCALE_FACTOR, BufferedImage.TYPE_INT_ARGB);
                    Graphics2D graphics = (Graphics2D) iconImage.getGraphics();
                    graphics.scale(SCALE_FACTOR, SCALE_FACTOR);

                    java.awt.Image renderedCostumeFrameImage =
                        bitmapRenderer.render(costumeFrame.getFrameImage(),
                            costumeFrame.getImageWidth(), costumeFrame.getImageHeight(), costumeFrame.getClut().getColors(),
                            new CostumePixel2ClutMapper(fCostume.getPixel2ClutMap()),!costumeFrame.getClut().isAmigaColors());
                    graphics.drawImage(renderedCostumeFrameImage, 0, 0, null);

                    JLabel imageLabel = new JLabel(new ImageIcon(iconImage));
                    imageLabel.setBorder(BorderFactory.createLineBorder(Color.RED, 1));
                    fImageViewer.add(imageLabel);
                }
            }
        }
        fImageViewer.revalidate();
        fImageViewer.repaint();
    }
}
