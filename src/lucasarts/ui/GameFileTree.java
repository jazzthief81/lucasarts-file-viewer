package lucasarts.ui;

import lucasarts.format.gamefile.GameFile;
import lucasarts.format.gamefile.GameFileSection;
import lucasarts.format.gamefile.LflfSection;
import lucasarts.format.gamefile.Resource;

import javax.swing.*;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeCellRenderer;
import javax.swing.tree.DefaultTreeModel;
import java.awt.*;
import java.util.List;

/**
 * Created by Lars on 10-04-2017.
 */
public class GameFileTree extends JTree {

    private GameFile fGameFile;

    public GameFileTree() {
        setModel(null);
        setCellRenderer(new DefaultTreeCellRenderer() {
            @Override
            public Component getTreeCellRendererComponent(JTree tree, Object value, boolean sel, boolean expanded, boolean leaf, int row, boolean hasFocus) {
                DefaultMutableTreeNode node = (DefaultMutableTreeNode) value;
                Object userObject = node.getUserObject();
                if (userObject instanceof GameFileSection) {
                    GameFileSection gameFileSection = (GameFileSection) userObject;
                    return super.getTreeCellRendererComponent(tree, gameFileSection.getKeyword(), sel, expanded, leaf, row, hasFocus);
                }
                else {
                    return super.getTreeCellRendererComponent(tree, userObject, sel, expanded, leaf, row, hasFocus);
                }
            }
        });
        setRootVisible(false);
    }

    public GameFile getGameFile() {
        return fGameFile;
    }

    public void setGameFile(GameFile aGameFile) {
        fGameFile = aGameFile;

        DefaultMutableTreeNode gameFileNode = new DefaultMutableTreeNode(fGameFile);
        List<LflfSection> lflfSections = fGameFile.getLflfSections();
        for (LflfSection lflfSection : lflfSections) {
            DefaultMutableTreeNode lflfNode = new DefaultMutableTreeNode(lflfSection);
            List<Resource> resources = lflfSection.getResources();
            for (Resource resource : resources) {
                DefaultMutableTreeNode resourceNode = new DefaultMutableTreeNode(resource);
                lflfNode.add(resourceNode);
            }
            gameFileNode.add(lflfNode);
        }

        setModel(new DefaultTreeModel(gameFileNode));

        for(int row=0;row< getRowCount();row++) {
            expandRow(row);
        }
    }
}
