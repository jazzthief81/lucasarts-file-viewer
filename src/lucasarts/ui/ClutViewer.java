package lucasarts.ui;

import lucasarts.format.gamefile.Clut;
import lucasarts.format.gamefile.DecryptingInputStream;
import lucasarts.format.gamefile.GameFileDecoderUtil;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.io.*;
import java.util.*;

/**
 * Created by Lars on 29-03-2017.
 */
public class ClutViewer extends JPanel {

    private static java.util.List<Clut> cluts = new ArrayList<Clut>();
    private static int currentClutIndex = 0;
    private static ClutViewer sClutPanel;
    private static JLabel clutIndexLabel;

    public static void main(String[] args) throws IOException {
        File inputFile = new File(args[0]);
        File[] inputFiles = GameFileDecoderUtil.getInputFiles(inputFile);

        for (File file : inputFiles) {
            System.out.println(file.getName());
            DecryptingInputStream decryptingInputStream =
                new DecryptingInputStream(new BufferedInputStream(new FileInputStream(file)));
            DataInputStream stream =
                    new DataInputStream(decryptingInputStream);

            while (GameFileDecoderUtil.skipToSection(stream, "CLUT")) {
                System.out.println("CLUT found");
                int size = stream.readInt();
                int position = decryptingInputStream.getPosition();
                System.out.println(size);
                int clutSize = (size - 8) / 3;
                Color[] colors = GameFileDecoderUtil.readClut(stream, clutSize);
                Clut clut = new Clut(position-4, size, colors);
                cluts.add(clut);
            }
            stream.close();
        }

        JFrame frame = new JFrame("CLUT Viewer");
        sClutPanel = new ClutViewer();
        JToolBar toolbar = new JToolBar(JToolBar.HORIZONTAL);
        toolbar.add(new PreviousClutAction());
        clutIndexLabel = new JLabel();
        toolbar.add(clutIndexLabel);
        toolbar.add(new NextClutAction());
        toolbar.setFloatable(false);
        frame.getContentPane().add(toolbar, BorderLayout.NORTH);
        frame.getContentPane().add(sClutPanel, BorderLayout.CENTER);
        updateClutPanel();
        frame.pack();
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setVisible(true);
    }

    private static void updateClutPanel(){
        sClutPanel.setClut(cluts.get(currentClutIndex).getColors());
        clutIndexLabel.setText(" " + (currentClutIndex + 1) + " / " + cluts.size() + " ");
    }

    private static class PreviousClutAction extends AbstractAction {
        public PreviousClutAction() {
            super(" < ");
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            if (currentClutIndex > 0) {
                currentClutIndex--;
                updateClutPanel();
            }
        }
    }

    private static class NextClutAction extends AbstractAction {
        public NextClutAction() {
            super(" >" );
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            if (currentClutIndex < cluts.size() - 1) {
                currentClutIndex++;
                updateClutPanel();
            }
        }
    }

    public ClutViewer() {
        super(new GridLayout(16, 16, 1, 1));
    }

    public void setClut(Color[] aColors) {
        removeAll();
        if(aColors!=null) {
            for (Color color : aColors) {
                JLabel label = new JLabel();
                label.setPreferredSize(new Dimension(12, 12));
                label.setBackground(color);
                label.setOpaque(true);
                add(label);
            }
        }
        revalidate();
        repaint();
    }

}
