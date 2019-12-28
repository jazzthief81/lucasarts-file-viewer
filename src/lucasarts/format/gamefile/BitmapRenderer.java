package lucasarts.format.gamefile;

import java.awt.*;
import java.awt.image.BufferedImage;

/**
 * Created by Lars on 17-04-2017.
 */
public class BitmapRenderer {

    public BufferedImage render(int[] aPixelValues, int aBitmapWidth, int aBitmapHeight, Color[] aColors, Pixel2ClutMapper aClutMapper, boolean rotated) {
        BufferedImage image = new BufferedImage(aBitmapWidth, aBitmapHeight, BufferedImage.TYPE_INT_ARGB);
        for (int x = 0; x < aBitmapWidth; x++) {
            for (int y = 0; y < aBitmapHeight; y++) {
                int pixelValue;
                if(rotated){
                    pixelValue = aPixelValues[y + x * aBitmapHeight];
                }
                else {
                    pixelValue = aPixelValues[x + y * aBitmapWidth];
                }
                int rgb;
                if (pixelValue == -1) {
                    rgb = 0;
                }
                else {
                    int clutValue = aClutMapper.pixel2ClutValue(pixelValue);
                    Color color = aColors[clutValue];
                    rgb = color.getRGB();
                }
                image.setRGB(x, y, rgb);
            }
        }
        return image;
    }

}
