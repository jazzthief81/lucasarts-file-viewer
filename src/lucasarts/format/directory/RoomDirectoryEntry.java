package lucasarts.format.directory;

/**
 * Created by Lars on 27-05-2018.
 */
public class RoomDirectoryEntry {

    private int fileId;
    private int offset;

    public RoomDirectoryEntry(int aFileId, int aOffset) {
        fileId = aFileId;
        offset = aOffset;
    }

    public int getFileId() {
        return fileId;
    }

    public int getOffset() {
        return offset;
    }

    public void setFileId(int aFileId) {
        fileId = aFileId;
    }

    public void setOffset(int aOffset) {
        offset = aOffset;
    }

}
