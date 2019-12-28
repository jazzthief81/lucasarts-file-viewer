package lucasarts.format.gamefile;

import java.util.List;

/**
 * Created by Lars on 10-04-2017.
 */
public class Image extends DefaultGameFileSection implements GameFileSection {

    private String fKeyword;
    private Smap fSmap;
    private List<Zp> fZp;

    public Image(int aOffset, int aSize, String aKeyword, Smap aSmap, List<Zp> aZp) {
        super(aOffset, aSize);
        fKeyword = aKeyword;
        fSmap = aSmap;
        fZp = aZp;
    }

    @Override
    public String getKeyword() {
        return fKeyword;
    }

    public Smap getSmap() {
        return fSmap;
    }

    public List<Zp> getZp() {
        return fZp;
    }

}
