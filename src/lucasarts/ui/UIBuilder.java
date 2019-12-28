package lucasarts.ui;

import javax.swing.*;
import java.awt.*;

/**
 * Created by Lars on 08-04-2017.
 */
public class UIBuilder {

    public UI build() {
        JFrame frame = new JFrame("LucasArts File Viewer");
        frame.setSize(1200, 800);

        JSplitPane splitPane = new JSplitPane();
        splitPane.setDividerLocation(200);
        frame.getContentPane().add(splitPane);

        GameFileTree gameFileTree = new GameFileTree();
        splitPane.setLeftComponent(new JScrollPane(gameFileTree));

        CardLayout cardLayout = new CardLayout();
        JPanel viewer = new JPanel(cardLayout);
        RoomViewer roomViewer = new RoomViewer();
        viewer.add(roomViewer, "room");
        CostumeViewer costumeViewer = new CostumeViewer();
        viewer.add(costumeViewer, "costume");
        splitPane.setRightComponent(viewer);

        return new UI(frame, gameFileTree, viewer, roomViewer, costumeViewer);
    }

}
