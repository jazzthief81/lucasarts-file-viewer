package lucasarts.format.gamefile;

import java.util.List;

/**
 * Created by Lars on 18-06-2018.
 */
public class Boxd extends DefaultGameFileSection{
    public List<Box> fBoxes;

    public Boxd(int aOffset, int aSize, List<Box> aBoxes) {
        super(aOffset, aSize);
        fBoxes = aBoxes;
    }

    public List<Box> getBoxes() {
        return fBoxes;
    }

    @Override
    public String getKeyword() {
        return "BOXD";
    }
}
