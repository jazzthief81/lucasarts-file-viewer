package lucasarts;

import lucasarts.format.gamefile.Costume;
import lucasarts.format.gamefile.GameFile;
import lucasarts.format.gamefile.GameFileDecoder;
import lucasarts.format.gamefile.Room;
import lucasarts.ui.UI;
import lucasarts.ui.UIBuilder;

import javax.swing.*;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.DefaultMutableTreeNode;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.List;

/**
 * Created by Lars on 08-04-2017.
 */
public class LucasArtsFileViewer {

    private static UI ui;
    private static GameFile gameFile;

    public static void main(String[] args) throws InvocationTargetException, InterruptedException {
        SwingUtilities.invokeAndWait(new Runnable() {
            @Override
            public void run()  {
                try {
                    UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
                }
                catch (ClassNotFoundException | InstantiationException | UnsupportedLookAndFeelException | IllegalAccessException e) {
                    e.printStackTrace();
                }

                UIBuilder uiBuilder = new UIBuilder();
                ui = uiBuilder.build();
                ui.getFrame().setLocationRelativeTo(null);
                ui.getFrame().setVisible(true);
                ui.getFrame().setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

                ui.getGameFileTree().addTreeSelectionListener(new TreeSelectionListener() {
                    @Override
                    public void valueChanged(TreeSelectionEvent e) {
                        if(e.getNewLeadSelectionPath() == null){
                            ui.getRoomViewer().setRoom(null);
                            ui.getCostumeViewer().setCostume(null);
                            ui.showRoomViewer();
                        }
                        else {
                            DefaultMutableTreeNode node = (DefaultMutableTreeNode) e.getNewLeadSelectionPath().getLastPathComponent();
                            if (node.getUserObject() instanceof Room) {
                                ui.getRoomViewer().setRoom((Room) node.getUserObject());
                                ui.showRoomViewer();
                            }
                            else {
                                ui.getRoomViewer().setRoom(null);
                            }
                            if (node.getUserObject() instanceof Costume) {
                                ui.getCostumeViewer().setCostume((Costume) node.getUserObject());
                                ui.showCostumeViewer();
                            }
                            else {
                                ui.getCostumeViewer().setCostume(null);
                            }
                        }
                    }
                });

                ui.getFrame().setTransferHandler(new TransferHandler(){
                    @Override
                    public boolean importData(TransferSupport support) {
                        try {
                            Object transferData = support.getTransferable().getTransferData(DataFlavor.javaFileListFlavor);
                            List<File> files = (List<File>) transferData;
                            GameFileDecoder gameFileDecoder = new GameFileDecoder();
                            gameFile = gameFileDecoder.decode(files.get(0).getPath());
                            ui.getGameFileTree().setGameFile(gameFile);
                            ui.getRoomViewer().setRoom(null);
                            ui.getCostumeViewer().setCostume(null);
                            return true;
                        }
                        catch (Exception e) {
                            e.printStackTrace();
                            return true;
                        }
                    }

                    @Override
                    public boolean canImport(TransferSupport support) {
                        return support.getDataFlavors()[0].isFlavorJavaFileListType();
                    }
                });
            }
        });
    }

}
