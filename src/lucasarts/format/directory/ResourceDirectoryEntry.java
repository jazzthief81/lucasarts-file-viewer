package lucasarts.format.directory;

/**
 * Created by Lars on 27-05-2018.
 */
public class ResourceDirectoryEntry {

    private int roomId;
    private int offset;

    public ResourceDirectoryEntry(int aRoomId, int aOffset) {
        roomId = aRoomId;
        offset = aOffset;
    }

    public int getRoomId() {
        return roomId;
    }

    public int getOffset() {
        return offset;
    }

    public void setRoomId(int aRoomId) {
        roomId = aRoomId;
    }

    public void setOffset(int aOffset) {
        offset = aOffset;
    }
}
