package lucasarts.format.gamefile;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.IOException;

/**
 * Created by Lars on 11-04-2017.
 */
public class ZpDecoder {

    public int[] decode(byte[] aZp, short aWidth, short aHeight) throws IOException {
        // Read strip offsets.
        int stripCount = aWidth / 8;
        int[] stripOffsets = new int[stripCount];
        ByteArrayInputStream streamForOffsets = new ByteArrayInputStream(aZp);
        streamForOffsets.skip(8);
        for (int stripIndex = 0; stripIndex < stripCount; stripIndex++) {
            stripOffsets[stripIndex] = GameFileDecoderUtil.readShortLittleEndian(streamForOffsets);
        }
        streamForOffsets.close();

        // Decode strips.
        int[] pixelValues = new int[aHeight * aWidth];
        for (int stripIndex = 0; stripIndex < stripCount; stripIndex++) {
            ByteArrayInputStream streamForStrip = new ByteArrayInputStream(aZp);
            streamForStrip.skip(stripOffsets[stripIndex]);
            int rowsDecoded = 0;
            while (rowsDecoded < aHeight) {
//                System.out.println("Strip " + stripIndex + " (offset = " + stripOffsets[stripIndex] + ")");
                int typeAndRowCount = streamForStrip.read();
                int rowCount = typeAndRowCount & 0x7f;
                int type = typeAndRowCount & 0x80;
                if (type == 0) {
//                    System.out.print(" Sequence row count: " + rowCount + ", masks: ");
                    for (int row = 0; row < rowCount; row++) {
                        int mask = streamForStrip.read();
//                        System.out.print(mask + " ");
                        int pixelMask = 0x80;
                        for (int x = 0; x < 8; x++) {
                            if ((mask & pixelMask) == 0) {
                                pixelValues[x + stripIndex * 8 + (row + rowsDecoded) * aWidth] = 0;
                            }
                            else {
                                pixelValues[x + stripIndex * 8 + (row + rowsDecoded) * aWidth] = 1;
                            }
                            pixelMask >>= 1;
                        }
                    }
//                    System.out.println();
                }
                else {
                    int mask = streamForStrip.read();
//                    System.out.println(" Row count: " + rowCount + ", mask: " + mask);
                    for (int row = 0; row < rowCount; row++) {
                        int pixelMask = 0x80;
                        for (int x = 0; x < 8; x++) {
                            if ((mask & pixelMask) == 0) {
                                pixelValues[x + stripIndex * 8 + (row + rowsDecoded) * aWidth] = 0;
                            }
                            else {
                                pixelValues[x + stripIndex * 8 + (row + rowsDecoded) * aWidth] = 1;
                            }
                            pixelMask >>= 1;
                        }
                    }
                }
                rowsDecoded += rowCount;
            }

        }
        return pixelValues;
    }

}
