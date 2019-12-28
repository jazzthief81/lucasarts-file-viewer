package lucasarts.format.gamefile;

import java.util.List;

/**
 * Created by Lars on 09-04-2017.
 */
public class ObjectImage extends DefaultGameFileSection implements GameFileSection {

    private short fObjectIdentifier;
    private final List<Image> fImages;
    private final short fImageWidth;
    private final short fImageHeight;
    private final short fImageX;
    private final short fImageY;

    public ObjectImage(int aOffset, int aSize, short aObjectIdentifier, List<Image> aImages, short aImageWidth, short aImageHeight, short aImageX, short aImageY) {
        super(aOffset, aSize);
        fObjectIdentifier = aObjectIdentifier;
        fImages = aImages;
        fImageWidth = aImageWidth;
        fImageHeight = aImageHeight;
        fImageX = aImageX;
        fImageY = aImageY;
    }

    @Override
    public String getKeyword() {
        return "OBIM";
    }

    public short getObjectIdentifier() {
        return fObjectIdentifier;
    }

    public List<Image> getImages() {
        return fImages;
    }

    public short getImageWidth() {
        return fImageWidth;
    }

    public short getImageHeight() {
        return fImageHeight;
    }

    public short getImageX() {
        return fImageX;
    }

    public short getImageY() {
        return fImageY;
    }

}
