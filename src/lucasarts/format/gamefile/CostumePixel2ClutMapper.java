package lucasarts.format.gamefile;

import lucasarts.format.gamefile.Pixel2ClutMapper;

/**
 * Created by Lars on 04-06-2018.
 */
public class CostumePixel2ClutMapper implements Pixel2ClutMapper {

    private int[] pixel2ClutMap;

    public CostumePixel2ClutMapper(int[] aPixel2ClutMap) {
        pixel2ClutMap = aPixel2ClutMap;
    }

    @Override
    public int pixel2ClutValue(int aPixelValue) {
        return pixel2ClutMap[aPixelValue];
    }
}
