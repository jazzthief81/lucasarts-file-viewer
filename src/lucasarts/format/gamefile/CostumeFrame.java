package lucasarts.format.gamefile;

/**
 * Created by Lars on 03-06-2018.
 */
public class CostumeFrame {
    private short imageWidth;
    private short imageHeight;
    private short imageOffsetX;
    private short imageOffsetY;
    private int[] frameImage;
    private Clut fClut;

    public CostumeFrame(short aImageWidth, short aImageHeight, short aImageOffsetX, short aImageOffsetY, int[] aFrameImage, Clut aClut) {
        imageWidth = aImageWidth;
        imageHeight = aImageHeight;
        imageOffsetX = aImageOffsetX;
        imageOffsetY = aImageOffsetY;
        frameImage = aFrameImage;
        fClut = aClut;
    }

    public short getImageWidth() {
        return imageWidth;
    }

    public short getImageHeight() {
        return imageHeight;
    }

    public short getImageOffsetX() {
        return imageOffsetX;
    }

    public short getImageOffsetY() {
        return imageOffsetY;
    }

    public int[] getFrameImage() {
        return frameImage;
    }

    public Clut getClut() {
        return fClut;
    }
}
