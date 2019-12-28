package lucasarts.format.gamefile;

import java.awt.*;
import java.util.List;

/**
 * Created by Lars on 09-04-2017.
 */
public class Room extends DefaultGameFileSection implements Resource {

    private final short fRoomWidth;
    private final short fRoomHeight;
    private final ColorCycle fColorCycle;
    private short fTransparentColorIndex;
    private Boxd fBoxd;
    private BoxMatrix fBoxMatrix;
    private Clut fClut;
    private List<ScaleTable> fScaleTables;
    private final RoomImage fRoomImage;
    private final List<ObjectImage> fObjectImages;
    private final List<ObjectCode> fObjectCodes;
    private final List<LocalScript> fLocalScripts;

    public Room(int aOffset, int aSize, short aRoomWidth, short aRoomHeight, ColorCycle aColorCycle, short aTransparentColorIndex, Boxd aBoxd, BoxMatrix aBoxMatrix, Clut aClut, List<ScaleTable> aScaleTables, RoomImage aRoomImage, List<ObjectImage> aObjectImages,
                List<ObjectCode> aObjectCodes, List<LocalScript> aLocalScripts) {
        super(aOffset, aSize);
        fRoomWidth = aRoomWidth;
        fRoomHeight = aRoomHeight;
        fColorCycle = aColorCycle;
        fTransparentColorIndex = aTransparentColorIndex;
        fBoxd = aBoxd;
        fBoxMatrix = aBoxMatrix;
        fClut = aClut;
        fScaleTables = aScaleTables;
        fRoomImage = aRoomImage;
        fObjectImages = aObjectImages;
        fObjectCodes = aObjectCodes;
        fLocalScripts = aLocalScripts;
    }

    public short getRoomWidth() {
        return fRoomWidth;
    }

    public short getRoomHeight() {
        return fRoomHeight;
    }

    public ColorCycle getColorCycle() {
        return fColorCycle;
    }

    public short getTransparentColorIndex() {
        return fTransparentColorIndex;
    }

    public Boxd getBoxd() {
        return fBoxd;
    }

    public BoxMatrix getBoxMatrix() {
        return fBoxMatrix;
    }

    public Clut getClut() {
        return fClut;
    }

    public List<ScaleTable> getScaleTables() {
        return fScaleTables;
    }

    public RoomImage getRoomImage() {
        return fRoomImage;
    }

    public List<ObjectImage> getObjectImages() {
        return fObjectImages;
    }

    public List<ObjectCode> getObjectCodes() {
        return fObjectCodes;
    }

    public List<LocalScript> getLocalScripts() {
        return fLocalScripts;
    }

    @Override
    public String getKeyword() {
        return "ROOM";
    }

}
