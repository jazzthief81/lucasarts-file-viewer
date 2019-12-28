package lucasarts.format.gamefile;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;

/**
 * Created by Lars on 09-04-2017.
 */
public class SmapDecoder {

    private static int sBitOffset;
    private static int sCurrentByte;
//    private static int sBitsRead;

    public int[] decode(byte[] aSmap, short aWidth, short aHeight, int aTransparentColorIndex) throws IOException {
        // Read strip offsets.
        int stripCount = aWidth / 8;
        int[] stripOffsets = new int[stripCount];
        ByteArrayInputStream streamForOffsets = new ByteArrayInputStream(aSmap);
        streamForOffsets.skip(8);
        for (int stripIndex = 0; stripIndex < stripCount; stripIndex++) {
            stripOffsets[stripIndex] = GameFileDecoderUtil.readIntLittleEndian(streamForOffsets);
        }
        streamForOffsets.close();

        // Decode strips.
        int[] stripContent = new int[aHeight * 8];
        int[] pixelValues = new int[aHeight * aWidth];
        for (int stripIndex = 0; stripIndex < stripCount; stripIndex++) {
//            System.out.println("Strip " + stripIndex + " (offset = " + stripOffsets[stripIndex] + ")");

            ByteArrayInputStream streamForStrip = new ByteArrayInputStream(aSmap);
            streamForStrip.skip(stripOffsets[stripIndex]);
            int stripType = streamForStrip.read();
//            System.out.println("Strip type: " + stripType);

            Arrays.fill(stripContent, 0);
            if (stripType >= 14 && stripType <= 18) {
                decodeStripType1x2x3x4x(streamForStrip, stripType - 10, aHeight, stripContent);
                copyStripVertically( stripContent, stripIndex, pixelValues, aWidth, aHeight, aTransparentColorIndex);
            }
            else if (stripType >= 24 && stripType <= 28) {
                decodeStripType1x2x3x4x(streamForStrip, stripType - 20, aHeight, stripContent);
                copyStripHorizontally(stripContent, stripIndex, pixelValues, aWidth, aHeight, aTransparentColorIndex);
            }
            else if (stripType >= 34 && stripType <= 38) {
                decodeStripType1x2x3x4x(streamForStrip, stripType - 30, aHeight, stripContent);
                copyStripVertically( stripContent, stripIndex, pixelValues, aWidth, aHeight, aTransparentColorIndex);
            }
            else if (stripType >= 44 && stripType <= 48) {
                decodeStripType1x2x3x4x(streamForStrip, stripType - 40, aHeight, stripContent);
                copyStripHorizontally(stripContent, stripIndex, pixelValues, aWidth, aHeight, aTransparentColorIndex);
            }
            else if (stripType >= 64 && stripType <= 68) {
                decodeStripType6x8x(streamForStrip, stripType - 60, aHeight, stripContent);
                copyStripHorizontally(stripContent, stripIndex, pixelValues, aWidth, aHeight, aTransparentColorIndex);
            }
            else if (stripType >= 84 && stripType <= 88) {
                decodeStripType6x8x(streamForStrip, stripType - 80, aHeight, stripContent);
                copyStripHorizontally(stripContent, stripIndex, pixelValues, aWidth, aHeight, aTransparentColorIndex);
            }
            else {
                System.err.println("Unsupported strip type: " + stripType);
            }

            streamForStrip.close();
        }
        return pixelValues;
    }

    private static void copyStripHorizontally(int[] aStripContent, int aStripIndex, int[] aImageContent, int aImageWidth, int aImageHeight, int aTransparentColorIndex) {
        for (int i = 0; i < aStripContent.length; i++) {
            int pixelValue = aStripContent[i];
            if(pixelValue == aTransparentColorIndex){
                pixelValue = -1;
            }
            aImageContent[aStripIndex * 8 + i % 8 + (i / 8) * aImageWidth] = pixelValue;
        }
    }

    private static void copyStripVertically(int[] aStripContent, int aStripIndex, int[] aImageContent, int aImageWidth, int aImageHeight, int aTransparentColorIndex) {
        for (int i = 0; i < aStripContent.length; i++) {
            int pixelValue = aStripContent[i];
            if(pixelValue == aTransparentColorIndex){
                pixelValue = -1;
            }
            aImageContent[aStripIndex * 8 + i / aImageHeight + (i % aImageHeight) * aImageWidth] = pixelValue;
        }
    }

    private void decodeStripType1x2x3x4x(InputStream aStreamForStrip, int aBitCount, short aHeight, int[] aStripContent) throws IOException {
        sCurrentByte = aStreamForStrip.read();
        sBitOffset = 0;
//        sBitsRead = 8; // for the strip type
        int pixelValueOffset = 1;
        int pixelValue = readBits(aStreamForStrip, 8);
        int valueIndex = 0;
//        System.out.print("Values: ");
        for (int y = 0; y < aHeight; y++) {
            for (int x = 0; x < 8; x++) {
//                System.out.print(pixelValue + " ");
                aStripContent[valueIndex++] = pixelValue;
                int controlBit = readBits(aStreamForStrip, 1);
                if (controlBit != 0) { // stop sequence
                    controlBit = readBits(aStreamForStrip, 1);
                    if (controlBit == 0) { // read new value
                        pixelValue = readBits(aStreamForStrip, aBitCount);
                        pixelValueOffset = 1;
                    }
                    else { // derive new value from current value
                        controlBit = readBits(aStreamForStrip, 1);
                        if (controlBit == 1) {
                            pixelValueOffset = -pixelValueOffset;
                        }
                        pixelValue = pixelValue - pixelValueOffset;
                    }
                }
            }
        }
//        System.out.println();
//        System.out.println("Bytes read: " + ((sBitsRead + 7) / 8));
    }

    private static void decodeStripType6x8x(InputStream aStreamForStrip, int aBitCount, short aHeight, int[] aStripContent) throws IOException {
        sCurrentByte = aStreamForStrip.read();
        sBitOffset = 0;
//        sBitsRead = 8; // for the strip type
        int pixelValue = readBits(aStreamForStrip, 8);
        int valueIndex = 0;
//        System.out.print("Values: ");
        for (int y = 0; y < aHeight; y++) {
            for (int x = 0; x < 8; x++) {
//                System.out.print(pixelValue + " ");
                aStripContent[valueIndex++] = pixelValue;
                int controlBit = readBits(aStreamForStrip, 1);
                if (controlBit != 0) { // stop sequence
                    controlBit = readBits(aStreamForStrip, 1);
                    if (controlBit == 0) { // read new value
                        pixelValue = readBits(aStreamForStrip, aBitCount);
                    }
                    else { // derive new value from current value
                        int pixelValueOffset = readBits(aStreamForStrip, 3) - 4;
                        pixelValue += pixelValueOffset;
                    }
                }
            }
        }
//        System.out.println();
//        System.out.println("Bytes read: " + ((sBitsRead + 7) / 8));
    }

    private static int readBits(InputStream aStreamForStrip, int aBitCount) throws IOException {
        int value = 0;
        int bit = 1;
        for (int bitIndex = 0; bitIndex < aBitCount; bitIndex++) {
            if ((sCurrentByte & 1) != 0) {
                value += bit;
            }
            bit = bit << 1;
            sCurrentByte = sCurrentByte >> 1;
            sBitOffset++;
//            sBitsRead++;
            if (sBitOffset == 8) {
                sCurrentByte = aStreamForStrip.read();
                sBitOffset = 0;
            }
        }
        return value;
    }

}
