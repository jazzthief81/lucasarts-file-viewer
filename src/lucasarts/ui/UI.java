package lucasarts.ui;

import javax.swing.*;
import java.awt.*;

/**
 * Created by Lars on 09-04-2017.
 */
public class UI {

    private JFrame fFrame;
    private GameFileTree fGameFileTree;
    private JPanel fViewer;
    private RoomViewer fRoomViewer;
    private CostumeViewer fCostumeViewer;

    UI(JFrame aFrame, GameFileTree aGameFileTree, JPanel aViewer, RoomViewer aRoomViewer, CostumeViewer aCostumeViewer) {
        fFrame = aFrame;
        fGameFileTree = aGameFileTree;
        fViewer = aViewer;
        fRoomViewer = aRoomViewer;
        fCostumeViewer = aCostumeViewer;
    }

    public JFrame getFrame() {
        return fFrame;
    }

    public GameFileTree getGameFileTree() {
        return fGameFileTree;
    }

    public RoomViewer getRoomViewer() {
        return fRoomViewer;
    }

    public CostumeViewer getCostumeViewer() {
        return fCostumeViewer;
    }

    public void showRoomViewer() {
        ((CardLayout) fViewer.getLayout()).show(fViewer, "room");
    }

    public void showCostumeViewer() {
        ((CardLayout) fViewer.getLayout()).show(fViewer, "costume");
    }
}
