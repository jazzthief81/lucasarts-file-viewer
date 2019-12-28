package lucasarts.format.gamefile;

import java.awt.*;
import java.io.*;
import java.util.Arrays;

/**
 * Created by Lars on 02-04-2017.
 */
public class GameFileDecoderUtil {

    public static File[] getInputFiles(File aInputFile) {
        File[] files;
        if (aInputFile.isDirectory()) {
            files = aInputFile.listFiles(new FilenameFilter() {
                @Override
                public boolean accept(File dir, String name) {
                    return name.matches(".*\\.[0-9]{3}");
                }
            });
        }
        else {
            files = new File[]{aInputFile};
        }
        return files;
    }

    public static boolean skipToSection(InputStream aStream, String aSectionKeyword) throws IOException {
        byte[] sectionKeywordBytes = aSectionKeyword.getBytes();
        byte[] buffer = new byte[sectionKeywordBytes.length];
        aStream.read(buffer);
        while (!Arrays.equals(buffer, sectionKeywordBytes)) {
            int nextByte = aStream.read();
            if (nextByte == -1) {
                return false;
            }
            else {
                System.arraycopy(buffer, 1, buffer, 0, buffer.length - 1);
                buffer[buffer.length - 1] = (byte) nextByte;
            }
        }
        return true;
    }

    public static String readTag(InputStream aStream) throws IOException {
        byte[] tagChars = new byte[4];
        aStream.read(tagChars);
        return new String(tagChars);
    }

    public static Color[] readClut(InputStream aStream, int aSize) throws IOException {
        Color[] colors = new Color[aSize];
        for (int index = 0; index < aSize; index++) {
            Color color = new Color(aStream.read(), aStream.read(), aStream.read());
            colors[index] = color;
        }
        return colors;
    }

    public static short readShortLittleEndian(InputStream aStream) throws IOException {
        int ch1 = aStream.read();
        int ch2 = aStream.read();
        if ((ch1 | ch2) < 0)
            throw new EOFException();
        return (short) ((ch1 << 0) + (ch2 << 8));
    }

    public static int readUnsignedShortLittleEndian(InputStream aStream) throws IOException {
        int ch1 = aStream.read();
        int ch2 = aStream.read();
        if ((ch1 | ch2) < 0)
            throw new EOFException();
        return (ch1 << 0) + (ch2 << 8);
    }

    public static final int readIntLittleEndian(InputStream aStream) throws IOException {
        int ch1 = aStream.read();
        int ch2 = aStream.read();
        int ch3 = aStream.read();
        int ch4 = aStream.read();
        if ((ch1 | ch2 | ch3 | ch4) < 0)
            throw new EOFException();
        return ((ch1 << 0) + (ch2 << 8) + (ch3 << 16) + (ch4 << 24));
    }

    public static final void writeShortLittleEndian(OutputStream aStream, short aInt) throws IOException {
        aStream.write(aInt & 0xff);
        aStream.write((aInt>>8) & 0xff);
    }

    public static final void writeIntLittleEndian(OutputStream aStream, int aInt) throws IOException {
        aStream.write(aInt & 0xff);
        aStream.write((aInt>>8) & 0xff);
        aStream.write((aInt>>16) & 0xff);
        aStream.write((aInt>>24) & 0xff);
    }

}
