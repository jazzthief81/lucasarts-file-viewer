package lucasarts.format.gamefile;

/**
 * Created by Lars on 04-06-2018.
 */
public class RoomPixel2ClutMapper implements Pixel2ClutMapper {

    private boolean fAmigaPalette;

    public RoomPixel2ClutMapper(boolean aAmigaPalette) {
        fAmigaPalette = aAmigaPalette;
    }

    public int pixel2ClutValue(int aPixelValue ) {
        if (fAmigaPalette) {
            int clutValue = (aPixelValue + 16) % 256;
            return (clutValue - 16) % 64 + 16;
        }
        else {
            return aPixelValue;
        }
    }

}
