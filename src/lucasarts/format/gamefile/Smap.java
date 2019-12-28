package lucasarts.format.gamefile;

/**
 * Created by Lars on 13-04-2017.
 */
public class Smap extends DefaultGameFileSection implements GameFileSection, Bitmap {

    private String fKeyword;
    private int[] fPixelValues;

    public Smap(int aOffset, int aSize, String aKeyword, int[] aPixelValues) {
        super(aOffset, aSize);
        fKeyword = aKeyword;
        fPixelValues = aPixelValues;
    }

    @Override
    public String getKeyword() {
        return fKeyword;
    }

    public int[] getPixelValues() {
        return fPixelValues;
    }

}
