package lucasarts.format.gamefile;

import java.io.IOException;
import java.util.List;

/**
 * Created by Lars on 08-04-2017.
 */
public class GameFileDecoderTest {

    public static void main(String[] args) throws IOException {
        if (args.length < 1) {
            System.err.println("No file specified.");
            System.exit(0);
        }

        GameFileDecoder gameFileDecoder = new GameFileDecoder();
        GameFile gameFile = gameFileDecoder.decode(args[0]);

        LoffSection loffSection = gameFile.getLoffSection();
        System.out.println(loffSection.getKeyword());
        List<RoomPosition> roomPositions = loffSection.getRoomPositions();
        System.out.println(" Room count: " + roomPositions.size());
        for (RoomPosition roomPosition : roomPositions) {
            System.out.println(" Room ID: " + roomPosition.getRoomId());
            System.out.println(" Room offset: " + roomPosition.getRoomOffset());
        }

        List<LflfSection> lflfSections = gameFile.getLflfSections();
        for (LflfSection lflfSection : lflfSections) {
            System.out.println(lflfSection.getKeyword());
            List<Resource> resources = lflfSection.getResources();
            for (Resource resource : resources) {
                System.out.println(" " + resource.getKeyword());
                if (resource instanceof Room) {
                    printRoom((Room) resource);
                }
            }
        }
    }

    private static void printRoom(Room aRoom) {
        System.out.println("  " + "RMHD");
        System.out.println("   Room width = " + aRoom.getRoomWidth());
        System.out.println("   Room height = " + aRoom.getRoomHeight());
        System.out.println("   Room object count = " + aRoom.getObjectImages().size());

        System.out.println("  CYCL");

        System.out.println("  TRNS");
        System.out.println("   Transparent color index = " + aRoom.getTransparentColorIndex());

        System.out.println("  EPAL");

        System.out.println("  BOXD");
        List<Box> boxes = aRoom.getBoxd().getBoxes();
        System.out.println("   Box count = " + boxes.size());
        for (int boxIndex = 0; boxIndex < boxes.size(); boxIndex++) {
            Box box = boxes.get(boxIndex);
            System.out.println("   Box " + (boxIndex + 1));
            System.out.print("    Box corners:");
            for (int boxCornerIndex = 0; boxCornerIndex < box.getXs().length; boxCornerIndex++) {
                System.out.print(" (" + box.getXs()[boxCornerIndex] + "," + box.getYs()[boxCornerIndex] + ")");
            }
            System.out.println();
            if ((box.getScaleTableIndex() & 0x8000) != 0) {
                System.out.println("    Scale table index = " + ((box.getScaleTableIndex() & 0x7fff) + 1));
            }
        }

        System.out.println("  BOXM");
        List<BoxMatrixRow> boxMatrixRows = aRoom.getBoxMatrix().getBoxMatrixRows();
        for (int boxIndex = 0; boxIndex < boxMatrixRows.size(); boxIndex++) {
            BoxMatrixRow boxMatrixRow = boxMatrixRows.get(boxIndex);
            System.out.println("   Box " + (boxIndex + 1));
            List<BoxMatrixRowRange> ranges = boxMatrixRow.getBoxMatrixRowRanges();
            for (BoxMatrixRowRange range : ranges) {
                System.out.println("    " + range.getMinDestinationBoxIndex() + " - " + range.getMaxDestinationBoxIndex() + " => " + range.getNextBoxIndex());
            }
        }

        System.out.println("  CLUT");

        System.out.println("  SCAL");
        List<ScaleTable> scaleTables = aRoom.getScaleTables();
        for (int scaleTableIndex = 0; scaleTableIndex < scaleTables.size(); scaleTableIndex++) {
            ScaleTable scaleTable = scaleTables.get(scaleTableIndex);
            System.out.println("   Scale table " + (scaleTableIndex + 1));
            System.out.println("    Scale1 = " + scaleTable.getScale1());
            System.out.println("    Y1 = " + scaleTable.getY1());
            System.out.println("    Scale2 = " + scaleTable.getScale2());
            System.out.println("    Y2 = " + scaleTable.getY2());
        }

        RoomImage roomImage = aRoom.getRoomImage();
        System.out.println("  " + roomImage.getKeyword());
        System.out.println("   RMIH");
        System.out.println("    ZP count = " + roomImage.getZpCount());

        Image roomImageImage = roomImage.getImage();
        printImage(roomImageImage);

        List<ObjectImage> objectImages = aRoom.getObjectImages();
        for (ObjectImage objectImage : objectImages) {
            System.out.println("  " + objectImage.getKeyword());
            System.out.println("   IMHD");
            System.out.println("    Object identifier = " + objectImage.getObjectIdentifier());
            System.out.println("    Image count = " + objectImage.getImages().size());
            int objectImageZpCount = objectImage.getImages().isEmpty() ? 0 : objectImage.getImages().get(0).getZp().size();
            System.out.println("    ZP count = " + objectImageZpCount);
//            System.out.println("    Image var2 = " + imageVar2);
            System.out.println("    Image width = " + objectImage.getImageWidth());
            System.out.println("    Image height = " + objectImage.getImageHeight());
            List<Image> objectImageImages = objectImage.getImages();
            for (Image objectImageImage : objectImageImages) {
                printImage(objectImageImage);
            }
        }
        List<ObjectCode> objectCodes = aRoom.getObjectCodes();
        for (ObjectCode objectCode : objectCodes) {
            System.out.println("  " + objectCode.getKeyword());
            System.out.println("   CDHD");
            System.out.println("    Object identifier = " + objectCode.getObjectIdentifier());
            System.out.println("   OBNA");
            System.out.println("    Object name = " + objectCode.getObjectName());
        }

        System.out.println("  EXCD");
        System.out.println("  ENCD");
        System.out.println("  NLSC");
        List<LocalScript> localScripts = aRoom.getLocalScripts();
        System.out.println("   Room script count = " + localScripts.size());
        for (LocalScript localScript : localScripts) {
            System.out.println("  " + localScript.getKeyword());
        }
    }

    private static void printImage(Image aImage) {
        System.out.println("   " + aImage.getKeyword());
        Smap smap = aImage.getSmap();
        System.out.println("    " + smap.getKeyword());
        List<Zp> zps = aImage.getZp();
        for (Zp zp : zps) {
            System.out.println("    " + zp.getKeyword());
        }
    }

}
