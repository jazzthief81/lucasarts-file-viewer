package lucasarts.format.gamefile;

import java.awt.*;

/**
 * Created by Lars on 09-04-2017.
 */
public class Clut extends DefaultGameFileSection implements GameFileSection {

    private Color[] fColors;

    public Clut(int aOffset, int aSize, Color[] aColors) {
        super(aOffset, aSize);
        fColors = aColors;
    }

    @Override
    public String getKeyword() {
        return "CLUT";
    }

    public Color[] getColors() {
        return fColors;
    }

    public boolean isAmigaColors(){
        if(fColors.length>=192){
            Color color = fColors[80];
            for (int colorIndex = 81; colorIndex < 192; colorIndex++) {
                if (!color.equals(fColors[colorIndex])) {
                    return false;
                }
            }

            return true;
        }
        return false;
    }

}
