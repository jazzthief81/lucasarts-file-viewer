package lucasarts.format.gamefile;

import java.awt.*;
import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Lars on 08-04-2017.
 */
public class GameFileDecoder {

    private static final String[] ZP_KEYWORDS = new String[]{
        "ZP01", "ZP02", "ZP03", "ZP04"
    };
    private static final String[] IM_KEYWORDS = new String[]{
        "IM01", "IM02", "IM03", "IM04", "IM05", "IM06", "IM07", "IM08",
        "IM09", "IM0A", "IM0B", "IM0C", "IM0D", "IM0E", "IM0F"
    };
    private DecryptingInputStream fDecryptingInputStream;
    private DataInputStream fDataInputStream;
    private Clut roomClut;

    public GameFile decode(String aPath) throws IOException {
        fDecryptingInputStream = new DecryptingInputStream(new BufferedInputStream(new FileInputStream(aPath)));
        fDataInputStream = new DataInputStream(fDecryptingInputStream);

        try {
            // Read LECF section.
            String lecfTag = GameFileDecoderUtil.readTag(fDataInputStream);
            if (!"LECF".equals(lecfTag)) {
                throw new IOException("Expected LECF tag not found.");
            }
            int fileSize = fDataInputStream.readInt();

            // Read LOFF section.
            int loffStartPosition = fDecryptingInputStream.getPosition();
            String loffTag = GameFileDecoderUtil.readTag(fDataInputStream);
            if (!"LOFF".equals(loffTag)) {
                throw new IOException("Expected LOFF tag not found.");
            }
            int loffSectionSize = fDataInputStream.readInt();

            int roomCount = fDataInputStream.read();
            List<RoomPosition> roomPositions = new ArrayList<RoomPosition>(roomCount);
            for (int roomIndex = 0; roomIndex < roomCount; roomIndex++) {
                int roomId = fDataInputStream.read();
                int roomOffset = GameFileDecoderUtil.readIntLittleEndian(fDataInputStream);
                roomPositions.add(new RoomPosition(roomId, roomOffset));
            }

            if (fDecryptingInputStream.getPosition() - loffStartPosition != loffSectionSize) {
                throw new IOException("LOFF section does not have expected size.");
            }

            LoffSection loffSection = new LoffSection(loffStartPosition, loffSectionSize, roomPositions);

            // Read LFLF sections.
            List<LflfSection> lflfSections = new ArrayList<LflfSection>();
            while (fDecryptingInputStream.getPosition() < fileSize) {
                int lflfStartPosition = fDecryptingInputStream.getPosition();
                String lflfTag = GameFileDecoderUtil.readTag(fDataInputStream);
                if (!"LFLF".equals(lflfTag)) {
                    throw new IOException("Expected LFLF tag not found.");
                }
                int lflfSectionSize = fDataInputStream.readInt();

                ArrayList<Resource> resources = new ArrayList<Resource>();
                while (fDecryptingInputStream.getPosition() < lflfStartPosition + lflfSectionSize) {
                    int resourcePosition = fDecryptingInputStream.getPosition();
                    String resourceTag = GameFileDecoderUtil.readTag(fDataInputStream);
                    int resourceSize = fDataInputStream.readInt();
                    Resource resource = readResource(resourceTag, resourcePosition, resourceSize);

                    if (fDecryptingInputStream.getPosition() != resourcePosition + resourceSize) {
                        throw new IOException(resourceTag + " section does not have expected size.");
                    }
                    resources.add(resource);
                }
                if (fDecryptingInputStream.getPosition() != lflfStartPosition + lflfSectionSize) {
                    throw new IOException("LFLF section does not have expected size.");
                }

                LflfSection lflfSection = new LflfSection(lflfStartPosition, lflfSectionSize, resources);
                lflfSections.add(lflfSection);
            }

            if (fDecryptingInputStream.getPosition() != fileSize) {
                throw new IOException("LECF section does not have expected size.");
            }

            return new GameFile(loffSection, lflfSections);
        }
        catch (IOException e) {
            e.printStackTrace();
        }
        finally {
            fDataInputStream.close();
            fDataInputStream = null;
            fDecryptingInputStream = null;
        }
        return null;
    }

    private Resource readResource(String aResourceTag, int aResourcePosition, int aResourceSize) throws IOException {
        if (aResourceTag.equals("ROOM")) {
            Room room = readRoom(aResourcePosition, aResourceSize);
            roomClut = room.getClut();
            return room;
        }
        else if (aResourceTag.equals("SCRP")) {
            fDataInputStream.skip(aResourceSize - 8);
            return new Script(aResourcePosition, aResourceSize);
        }
        else if (aResourceTag.equals("SOUN")) {
            fDataInputStream.skip(aResourceSize - 8);
            return new Sound(aResourcePosition, aResourceSize);
        }
        else if (aResourceTag.equals("COST")) {
            return readCostume(aResourcePosition, aResourceSize);
        }
        else if (aResourceTag.equals("CHAR")) {
            fDataInputStream.skip(aResourceSize - 8);
            return new CharacterSet(aResourcePosition, aResourceSize);
        }
        else {
            throw new IOException("Unknown resource type: " + aResourceTag);
        }
    }

    private Resource readCostume(int aResourcePosition, int aResourceSize) throws IOException {
        fDataInputStream.mark(aResourceSize - 8);

        int costumeFileOffset = fDecryptingInputStream.getPosition() - 6;

        fDataInputStream.read();
        int sizeMarker = fDataInputStream.read();
        int clutSize = 16;
        if ((sizeMarker & 0x7f) == 0x59) {
            clutSize = 32;
        }
        int[] pixel2ClutMap = new int[clutSize];
        for(int clutIndex=0;clutIndex<clutSize;clutIndex++){
            pixel2ClutMap[clutIndex] = fDataInputStream.read();
        }

//        System.out.println("Index table offset:");
        int indexTableOffset = GameFileDecoderUtil.readUnsignedShortLittleEndian(fDataInputStream);
//        System.out.println(Integer.toHexString(indexTableOffset));

        int[] costumeFrameOffsetOffsets = new int[16];
        System.out.println("Costume offset offsets:");
        for (int i = 0; i < costumeFrameOffsetOffsets.length; i++) {
            costumeFrameOffsetOffsets[i] = GameFileDecoderUtil.readUnsignedShortLittleEndian(fDataInputStream);
            System.out.println(Integer.toHexString(costumeFrameOffsetOffsets[i]));
        }

        fDataInputStream.skipBytes(costumeFrameOffsetOffsets[0] - (fDecryptingInputStream.getPosition() - costumeFileOffset));

        int firstCostumeFrameOffset = 0;
        int trailingFrames = -1;
        while (firstCostumeFrameOffset == 0) {
            firstCostumeFrameOffset = GameFileDecoderUtil.readUnsignedShortLittleEndian(fDataInputStream);
            trailingFrames++;
        }
        int frameCount = (firstCostumeFrameOffset - costumeFrameOffsetOffsets[0]) / 2;
        System.out.println("Frame count: " + frameCount);
        int[] frameOffsets = new int[frameCount];

//        System.out.println("Frame offsets:");
//        for (int frameIndex = 0; frameIndex < trailingFrames; frameIndex++) {
//            System.out.println(0);
//        }
        frameOffsets[trailingFrames] = firstCostumeFrameOffset;
//        System.out.println(Integer.toHexString(frameOffsets[trailingFrames]));

        for (int frameIndex = trailingFrames+1; frameIndex < frameCount; frameIndex++) {
            frameOffsets[frameIndex] = GameFileDecoderUtil.readUnsignedShortLittleEndian(fDataInputStream);
//            System.out.println(Integer.toHexString(frameOffsets[frameIndex]));
        }

        CostumeFrame costumeFrames[] = new CostumeFrame[frameOffsets.length];

        for (int frameIndex = 0; frameIndex < frameOffsets.length; frameIndex++) {
            int frameOffset = frameOffsets[frameIndex];
            if (frameOffset != 0) {
//                System.out.println("Reading frame at offset: " + Integer.toHexString(frameOffset));

                fDataInputStream.reset();
                fDataInputStream.mark(aResourceSize * 2);
                fDataInputStream.skipBytes(frameOffset - 6);

                short frameWidth = GameFileDecoderUtil.readShortLittleEndian(fDataInputStream);
                short frameHeight = GameFileDecoderUtil.readShortLittleEndian(fDataInputStream);
                short frameOffsetX = GameFileDecoderUtil.readShortLittleEndian(fDataInputStream);
                short frameOffsetY = GameFileDecoderUtil.readShortLittleEndian(fDataInputStream);
                System.out.println("Frame size: "+frameWidth+" x "+frameHeight);
                GameFileDecoderUtil.readShortLittleEndian(fDataInputStream);
                GameFileDecoderUtil.readShortLittleEndian(fDataInputStream);

                int[] pixelValues = new int[frameWidth * frameHeight];
                int pixelIndex = 0;
                while (pixelIndex < pixelValues.length) {
                    int pixelAndLength = fDataInputStream.read();
                    int pixel;
                    int length;
                    if (clutSize <= 16) {
                        pixel = pixelAndLength >> 4;
                        length = pixelAndLength & 0xf;
                    }
                    else {
                        pixel = pixelAndLength >> 3;
                        length = pixelAndLength & 0x7;
                    }
                    if (length == 0) {
                        length = fDataInputStream.read();
                    }
                    for (int repeat = 0; repeat < length /*&& pixelIndex < pixelValues.length*/; repeat++) {
                        pixelValues[pixelIndex++] = pixel;
                    }
                }
//                if (pixelIndex != pixelValues.length) {
//                    System.out.println("Overran section for frame.");
//                }
                costumeFrames[frameIndex] = new CostumeFrame(
                    frameWidth,
                    frameHeight,
                    frameOffsetX,
                    frameOffsetY,
                    pixelValues,
                    roomClut);
            }
            else {
                costumeFrames[frameIndex] = null;
            }
        }

        fDataInputStream.reset();
        fDataInputStream.skip(aResourceSize - 8);
        return new Costume(aResourcePosition, aResourceSize, pixel2ClutMap, costumeFrames);
    }

    private Room readRoom(int aResourcePosition, int aResourceSize) throws IOException {
        // Read RMHD section.
        int rmhdStartPosition = fDecryptingInputStream.getPosition();
        String rmhdTag = GameFileDecoderUtil.readTag(fDataInputStream);
        if (!"RMHD".equals(rmhdTag)) {
            throw new IOException("Expected RMHD tag not found.");
        }
        int rmhdSize = fDataInputStream.readInt();
        short roomWidth = GameFileDecoderUtil.readShortLittleEndian(fDataInputStream);
        short roomHeight = GameFileDecoderUtil.readShortLittleEndian(fDataInputStream);
        short roomObjectCount = GameFileDecoderUtil.readShortLittleEndian(fDataInputStream);
        if (fDecryptingInputStream.getPosition() != rmhdStartPosition + rmhdSize) {
            throw new IOException("RMHD section does not have expected size.");
        }

        // Read CYCL section.
        int cyclStartPosition = fDecryptingInputStream.getPosition();
        String cyclTag = GameFileDecoderUtil.readTag(fDataInputStream);
        if (!"CYCL".equals(cyclTag)) {
            throw new IOException("Expected CYCL tag not found.");
        }
        int cyclSize = fDataInputStream.readInt();
        ColorCycle colorCycle = new ColorCycle(cyclStartPosition, cyclSize);
        while(true) {
            int cycleIndex = fDataInputStream.readUnsignedByte();
            if(cycleIndex==0){
                if((fDecryptingInputStream.getPosition() - cyclStartPosition)%2==1) {
                    fDataInputStream.skipBytes(1);
                }
                break;
            }
            fDataInputStream.skipBytes(2);
            short cycleSpeed = fDataInputStream.readShort();
            short cycleDirection = fDataInputStream.readShort();
            int cycleStartIndex = fDataInputStream.readUnsignedByte();
            int cycleStopIndex = fDataInputStream.readUnsignedByte();
            colorCycle.entries.put(cycleIndex, new ColorCycleEntry(cycleSpeed, cycleDirection,
                cycleStartIndex, cycleStopIndex));
        }
        if (fDecryptingInputStream.getPosition() != cyclStartPosition + cyclSize) {
            throw new IOException("CYCL section does not have expected size.");
        }

        // Read TRNS section.
        int trnsStartPosition = fDecryptingInputStream.getPosition();
        String trnsTag = GameFileDecoderUtil.readTag(fDataInputStream);
        if (!"TRNS".equals(trnsTag)) {
            throw new IOException("Expected TRNS tag not found.");
        }
        int trnsSize = fDataInputStream.readInt();
        short transparentColorIndex = GameFileDecoderUtil.readShortLittleEndian(fDataInputStream);
        if (fDecryptingInputStream.getPosition() != trnsStartPosition + trnsSize) {
            throw new IOException("TRNS section does not have expected size.");
        }

        skipSection("EPAL");

        // Read BOXD section.
        int boxdStartPosition = fDecryptingInputStream.getPosition();
        String boxdTag = GameFileDecoderUtil.readTag(fDataInputStream);
        if (!"BOXD".equals(boxdTag)) {
            throw new IOException("Expected BOXD tag not found.");
        }
        int boxdSize = fDataInputStream.readInt();
        short boxCount = GameFileDecoderUtil.readShortLittleEndian(fDataInputStream);

        List<Box> boxes = new ArrayList<>(boxCount);
        for (int boxIndex = 0; boxIndex < boxCount; boxIndex++) {
            short x1 = GameFileDecoderUtil.readShortLittleEndian(fDataInputStream);
            short y1 = GameFileDecoderUtil.readShortLittleEndian(fDataInputStream);
            short x2 = GameFileDecoderUtil.readShortLittleEndian(fDataInputStream);
            short y2 = GameFileDecoderUtil.readShortLittleEndian(fDataInputStream);
            short x3 = GameFileDecoderUtil.readShortLittleEndian(fDataInputStream);
            short y3 = GameFileDecoderUtil.readShortLittleEndian(fDataInputStream);
            short x4 = GameFileDecoderUtil.readShortLittleEndian(fDataInputStream);
            short y4 = GameFileDecoderUtil.readShortLittleEndian(fDataInputStream);
            int boxVar1 = fDataInputStream.read();
            int boxVar2 = fDataInputStream.read();
            short scaleTableIndex = GameFileDecoderUtil.readShortLittleEndian(fDataInputStream);

            Box box = new Box(new short[]{x1, x2, x3, x4}, new short[]{y1, y2, y3, y4}, scaleTableIndex);
            boxes.add(box);
        }
        Boxd boxd = new Boxd(boxdStartPosition, boxdSize, boxes);

        if (fDecryptingInputStream.getPosition() != boxdStartPosition + boxdSize) {
            throw new IOException("BOXD section does not have expected size.");
        }

        // Read BOXM section.
        int boxmStartPosition = fDecryptingInputStream.getPosition();
        String boxmTag = GameFileDecoderUtil.readTag(fDataInputStream);
        if (!"BOXM".equals(boxmTag)) {
            throw new IOException("Expected BOXM tag not found.");
        }
        int boxmSize = fDataInputStream.readInt();

        List<BoxMatrixRow> boxMatrixRows = new ArrayList<>();
        for (int boxIndex = 0; boxIndex < boxCount; boxIndex++) {
            List<BoxMatrixRowRange> boxMatrixRowRanges = new ArrayList<>();
            while (true) {
                fDataInputStream.mark(1);
                int endBoxMatrixRowMarker = fDataInputStream.read();
                if (endBoxMatrixRowMarker == 0xff) {
                    break;
                }
                else {
                    fDataInputStream.reset();
                    BoxMatrixRowRange boxMatrixRowRange = new BoxMatrixRowRange(
                        fDataInputStream.read(),
                        fDataInputStream.read(),
                        fDataInputStream.read());
                    boxMatrixRowRanges.add(boxMatrixRowRange);
                }
            }
            BoxMatrixRow boxMatrixRow = new BoxMatrixRow(boxMatrixRowRanges);
            boxMatrixRows.add(boxMatrixRow);

        }
        BoxMatrix boxMatrix = new BoxMatrix(boxmStartPosition, boxmSize, boxMatrixRows);

        // Skip odd byte.
        if ((fDecryptingInputStream.getPosition() - boxmStartPosition) % 2 == 1) {
            fDataInputStream.skipBytes(1);
        }

        if (fDecryptingInputStream.getPosition() != boxmStartPosition + boxmSize) {
            throw new IOException("BOXM section does not have expected size.");
        }

        // Read CLUT section.
        int clutStartPosition = fDecryptingInputStream.getPosition();
        String clutTag = GameFileDecoderUtil.readTag(fDataInputStream);
        if (!"CLUT".equals(clutTag)) {
            throw new IOException("Expected CLUT tag not found.");
        }
        int clutSize = fDataInputStream.readInt();
        Color[] colors = GameFileDecoderUtil.readClut(fDataInputStream, 256);
        if (fDecryptingInputStream.getPosition() != clutStartPosition + clutSize) {
            throw new IOException("CLUT section does not have expected size.");
        }
        Clut clut = new Clut(clutStartPosition, clutSize, colors);

        // Read SCAL section.
        int scalStartPosition = fDecryptingInputStream.getPosition();
        String scalTag = GameFileDecoderUtil.readTag(fDataInputStream);
        if (!"SCAL".equals(scalTag)) {
            throw new IOException("Expected SCAL tag not found.");
        }
        int scalSize = fDataInputStream.readInt();
        List<ScaleTable> scaleTables = new ArrayList<>();
        for (int scaleTableIndex = 0; scaleTableIndex < 4; scaleTableIndex++) {
            short scaleTableScale1 = GameFileDecoderUtil.readShortLittleEndian(fDataInputStream);
            short scaleTableY1 = GameFileDecoderUtil.readShortLittleEndian(fDataInputStream);
            short scaleTableScale2 = GameFileDecoderUtil.readShortLittleEndian(fDataInputStream);
            short scaleTableY2 = GameFileDecoderUtil.readShortLittleEndian(fDataInputStream);
            ScaleTable scaleTable = new ScaleTable(scaleTableScale1, scaleTableY1, scaleTableScale2, scaleTableY2);
            scaleTables.add(scaleTable);
        }

        if (fDecryptingInputStream.getPosition() != scalStartPosition + scalSize) {
            throw new IOException("SCAL section does not have expected size.");
        }

        // Read RMIM section.
        RoomImage roomImage = readRoomImage(roomWidth, roomHeight, clut, transparentColorIndex);

        // Read OBIM sections.
        List<ObjectImage> objectImages = new ArrayList<ObjectImage>();
        for (int roomObjectIndex = 0; roomObjectIndex < roomObjectCount; roomObjectIndex++) {
            ObjectImage objectImage = readObjectImage(clut, transparentColorIndex);
            objectImages.add(objectImage);
        }

        // Read OBCD sections.
        List<ObjectCode> objectCodes = new ArrayList<ObjectCode>();
        for (int roomObjectIndex = 0; roomObjectIndex < roomObjectCount; roomObjectIndex++) {
            ObjectCode objectCode = readObjectCode();
            objectCodes.add(objectCode);
        }

        skipSection("EXCD");
        skipSection("ENCD");

        // Read NLSC section.
        int nlscStartPosition = fDecryptingInputStream.getPosition();
        String nlscTag = GameFileDecoderUtil.readTag(fDataInputStream);
        if (!"NLSC".equals(nlscTag)) {
            throw new IOException("Expected NLSC tag not found.");
        }
        int nlscSize = fDataInputStream.readInt();
        short roomScriptCount = GameFileDecoderUtil.readShortLittleEndian(fDataInputStream);
        if (fDecryptingInputStream.getPosition() != nlscStartPosition + nlscSize) {
            throw new IOException("NLSC section does not have expected size.");
        }

        // Read LSCR sections.
        List<LocalScript> localScripts = new ArrayList<LocalScript>();
        for (int roomScriptIndex = 0; roomScriptIndex < roomScriptCount; roomScriptIndex++) {
            int lscrStartPosition = fDecryptingInputStream.getPosition();
            int lscrSize = skipSection("LSCR");
            localScripts.add(new LocalScript(lscrStartPosition, lscrSize));
        }

        return new Room(aResourcePosition, aResourceSize, roomWidth, roomHeight, colorCycle, transparentColorIndex, boxd, boxMatrix, clut, scaleTables, roomImage, objectImages, objectCodes, localScripts);
    }

    private RoomImage readRoomImage(short aRoomWidth, short aRoomHeight, Clut aClut, short aTransparentColorIndex) throws IOException {
        // Read RMIM section.
        int rmimStartPosition = fDecryptingInputStream.getPosition();
        String rmimTag = GameFileDecoderUtil.readTag(fDataInputStream);
        if (!"RMIM".equals(rmimTag)) {
            throw new IOException("Expected RMIM tag not found.");
        }
        int rmimSize = fDataInputStream.readInt();

        // Read RMIH section.
        int rmihStartPosition = fDecryptingInputStream.getPosition();
        String rmihTag = GameFileDecoderUtil.readTag(fDataInputStream);
        if (!"RMIH".equals(rmihTag)) {
            throw new IOException("Expected RMIH tag not found.");
        }
        int rmihSize = fDataInputStream.readInt();
        short zpCount = GameFileDecoderUtil.readShortLittleEndian(fDataInputStream);

        if (fDecryptingInputStream.getPosition() != rmihStartPosition + rmihSize) {
            throw new IOException("RMIH section does not have expected size.");
        }

        // Read IM00 section.
        Image image = readImage("IM00", aRoomWidth, aRoomHeight, aClut, zpCount, aTransparentColorIndex);

        if (fDecryptingInputStream.getPosition() != rmimStartPosition + rmimSize) {
            throw new IOException("RMIM section does not have expected size.");
        }

        return new RoomImage(rmimStartPosition, rmimSize, image, zpCount);
    }

    private ObjectImage readObjectImage(Clut aClut, short aTransparentColorIndex) throws IOException {
        // Read OBIM section.
        int obimStartPosition = fDecryptingInputStream.getPosition();
        String obimTag = GameFileDecoderUtil.readTag(fDataInputStream);
        if (!"OBIM".equals(obimTag)) {
            throw new IOException("Expected OBIM tag not found.");
        }
        int obimSize = fDataInputStream.readInt();
//        System.out.println("  OBIM");

        // Read IMHD section.
        int imhdStartPosition = fDecryptingInputStream.getPosition();
        String imhdTag = GameFileDecoderUtil.readTag(fDataInputStream);
        if (!"IMHD".equals(imhdTag)) {
            throw new IOException("Expected IMHD tag not found.");
        }
        int imhdSize = fDataInputStream.readInt();
//        System.out.println("   IMHD");
        short objectIdentifier = GameFileDecoderUtil.readShortLittleEndian(fDataInputStream);
        short imageCount = GameFileDecoderUtil.readShortLittleEndian(fDataInputStream);
        short zpCount = GameFileDecoderUtil.readShortLittleEndian(fDataInputStream);
        short imageVar2 = GameFileDecoderUtil.readShortLittleEndian(fDataInputStream);
        short imageX = GameFileDecoderUtil.readShortLittleEndian(fDataInputStream);
        short imageY = GameFileDecoderUtil.readShortLittleEndian(fDataInputStream);
        short imageWidth = GameFileDecoderUtil.readShortLittleEndian(fDataInputStream);
        short imageHeight = GameFileDecoderUtil.readShortLittleEndian(fDataInputStream);
//        System.out.println("    Image var1 = " + imageVar1);
//        System.out.println("    Image count = " + imageCount);
//        System.out.println("    ZP count = " + zpCount);
//        System.out.println("    Image var2 = " + imageVar2);
//        System.out.println("    Image width = " + imageWidth);
//        System.out.println("    Image height = " + imageHeight);

        if (fDecryptingInputStream.getPosition() != imhdStartPosition + imhdSize) {
            throw new IOException("IMHD section does not have expected size.");
        }

        // Read IMxx section.
        List<Image> images = new ArrayList<Image>();
        for (int imageIndex = 0; imageIndex < imageCount; imageIndex++) {
            Image image = readImage(IM_KEYWORDS[imageIndex], imageWidth, imageHeight, aClut, zpCount, aTransparentColorIndex);
            images.add(image);
        }

        if (fDecryptingInputStream.getPosition() != obimStartPosition + obimSize) {
            throw new IOException("OBIM section does not have expected size.");
        }

        return new ObjectImage(obimStartPosition, obimSize, objectIdentifier, images, imageWidth, imageHeight, imageX, imageY);
    }

    private ObjectCode readObjectCode() throws IOException {
        int objectCodeStartPosition = fDecryptingInputStream.getPosition();
        String objectCodeTag = GameFileDecoderUtil.readTag(fDataInputStream);
        if (!"OBCD".equals(objectCodeTag)) {
            throw new IOException("Expected OBCD tag not found.");
        }
        int objectCodeSize = fDataInputStream.readInt();

        // Read CDHD section.
        int codeHeaderStartPosition = fDecryptingInputStream.getPosition();
        String codeHeaderTag = GameFileDecoderUtil.readTag(fDataInputStream);
        if (!"CDHD".equals(codeHeaderTag)) {
            throw new IOException("Expected CDHD tag not found.");
        }
        int codeHeaderSize = fDataInputStream.readInt();

        short objectIdentifier = GameFileDecoderUtil.readShortLittleEndian(fDataInputStream);
        fDataInputStream.read();
        fDataInputStream.read();
        fDataInputStream.read();
        fDataInputStream.read();
        fDataInputStream.read();
        fDataInputStream.read();
        GameFileDecoderUtil.readShortLittleEndian(fDataInputStream);
        GameFileDecoderUtil.readShortLittleEndian(fDataInputStream);
        fDataInputStream.read();

        if (fDecryptingInputStream.getPosition() != codeHeaderStartPosition + codeHeaderSize) {
            throw new IOException("CDHD section does not have expected size.");
        }

        // Read VERB section.
        int verbStartPosition = fDecryptingInputStream.getPosition();
        String verbTag = GameFileDecoderUtil.readTag(fDataInputStream);
        if (!"VERB".equals(verbTag)) {
            throw new IOException("Expected VERB tag not found.");
        }
        int verbSize = fDataInputStream.readInt();

        fDecryptingInputStream.skip(verbSize - 8);

        if (fDecryptingInputStream.getPosition() != verbStartPosition + verbSize) {
            throw new IOException("VERB section does not have expected size.");
        }
        // Read OBNA section.
        int objectNameStartPosition = fDecryptingInputStream.getPosition();
        String objectNameTag = GameFileDecoderUtil.readTag(fDataInputStream);
        if (!"OBNA".equals(objectNameTag)) {
            throw new IOException("Expected OBNA tag not found.");
        }
        int objectNameSize = fDataInputStream.readInt();

        StringBuilder objectNameBuilder = new StringBuilder();
        int nextChar;
        while ((nextChar = fDataInputStream.readByte()) != 0) {
            objectNameBuilder.append((char) nextChar);
        }
        String objectName = objectNameBuilder.toString();

        if (fDecryptingInputStream.getPosition() != objectNameStartPosition + objectNameSize) {
            throw new IOException("OBNA section does not have expected size.");
        }

        if (fDecryptingInputStream.getPosition() != objectCodeStartPosition + objectCodeSize) {
            throw new IOException("OBCD section does not have expected size.");
        }

        return new ObjectCode(objectCodeStartPosition, objectCodeSize, objectIdentifier, objectName);
    }

    private Image readImage(String aTag, short aWidth, short aHeight, Clut aClut, short aZpCount, short aTransparentColorIndex) throws IOException {
        int imageStartPosition = fDecryptingInputStream.getPosition();
        String imageTag = GameFileDecoderUtil.readTag(fDataInputStream);
        if (!aTag.equals(imageTag)) {
            throw new IOException("Expected " + aTag + " tag not found.");
        }
        int imageSize = fDataInputStream.readInt();
//        System.out.println("   " + aTag);

        int smapStartPosition = fDecryptingInputStream.getPosition();
        String smapTag = GameFileDecoderUtil.readTag(fDataInputStream);
        if (!"SMAP".equals(smapTag)) {
            throw new IOException("Expected SMAP tag not found.");
        }
        int smapSize = fDataInputStream.readInt();
//        System.out.println("    SMAP");
        SmapDecoder smapDecoder = new SmapDecoder();
        byte[] smapBytes = new byte[smapSize];
        fDataInputStream.read(smapBytes, 8, smapSize - 8);
        int[] smapPixelValues = smapDecoder.decode(smapBytes, aWidth, aHeight, aTransparentColorIndex);
        Smap smap = new Smap(smapStartPosition, smapSize, "SMAP", smapPixelValues);

        if (fDecryptingInputStream.getPosition() != smapStartPosition + smapSize) {
            throw new IOException("SMAP section does not have expected size.");
        }

        ZpDecoder zpDecoder = new ZpDecoder();
        List<Zp> zps = new ArrayList<>();
        for (int zpIndex = 0; zpIndex < aZpCount; zpIndex++) {
            int zpStartPosition = fDecryptingInputStream.getPosition();
            String zpTag = GameFileDecoderUtil.readTag(fDataInputStream);
            String zpKeyword = ZP_KEYWORDS[zpIndex];
            if (!zpKeyword.equals(zpTag)) {
                throw new IOException("Expected " + zpTag + " tag not found.");
            }
            int zpSize = fDataInputStream.readInt();
//            System.out.println("    " + zpKeyword);

            byte[] zpBytes = new byte[zpSize];
            fDataInputStream.read(zpBytes, 8, zpSize - 8);
            int[] zpPixalValues = zpDecoder.decode(zpBytes, aWidth, aHeight);

            if (fDecryptingInputStream.getPosition() != zpStartPosition + zpSize) {
                throw new IOException(zpKeyword + " section does not have expected size.");
            }

            Zp zp = new Zp(zpStartPosition, zpSize, zpKeyword, zpPixalValues);
            zps.add(zp);
        }

        if (fDecryptingInputStream.getPosition() != imageStartPosition + imageSize) {
            throw new IOException(aTag + " section does not have expected size.");
        }

        return new Image(imageStartPosition, imageSize, aTag, smap, zps);
    }

    private int skipSection(String aTag) throws IOException {
        String tag = GameFileDecoderUtil.readTag(fDataInputStream);
        if (!tag.equals(aTag)) {
            throw new IOException("Expected " + aTag + " tag not found.");
        }
        int sectionSize = fDataInputStream.readInt();
        fDataInputStream.skipBytes(sectionSize - 8);
        return sectionSize;
    }

}
