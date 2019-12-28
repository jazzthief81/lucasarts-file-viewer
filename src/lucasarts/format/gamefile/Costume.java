package lucasarts.format.gamefile;

/**
 * Created by Lars on 09-04-2017.
 */
public class Costume extends DefaultGameFileSection implements Resource {

    private CostumeFrame[] costumeFrames;
    private int[] fPixel2ClutMap;

    public Costume(int aOffset, int aSize, int[] aPixel2ClutMap, CostumeFrame[] aCostumeFrames) {
        super(aOffset, aSize);
        fPixel2ClutMap = aPixel2ClutMap;
        costumeFrames = aCostumeFrames;
    }

    @Override
    public String getKeyword() {
        return "COST";
    }

    public CostumeFrame[] getCostumeFrames() {
        return costumeFrames;
    }

    public int[] getPixel2ClutMap() {
        return fPixel2ClutMap;
    }
}
