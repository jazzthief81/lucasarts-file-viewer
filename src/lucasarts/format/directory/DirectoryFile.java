package lucasarts.format.directory;

import lucasarts.format.gamefile.Resource;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Lars on 27-05-2018.
 */
public class DirectoryFile {

    private byte[] fRnamPayload;
    private byte[] fMaxsPayload;
    private List<RoomDirectoryEntry> fRoomDirectoryEntries = new ArrayList<>();
    private List<ResourceDirectoryEntry> fScriptDirectoryEntries = new ArrayList<>();
    private List<ResourceDirectoryEntry> fSoundDirectoryEntries = new ArrayList<>();
    private List<ResourceDirectoryEntry> fCustomeDirectoryEntries = new ArrayList<>();
    private List<ResourceDirectoryEntry> fCharacterSetDirectoryEntries = new ArrayList<>();
    private byte[] fDobjPayload;

    public byte[] getRnamPayload() {
        return fRnamPayload;
    }

    public void setRnamPayload(byte[] aRnamPayload) {
        fRnamPayload = aRnamPayload;
    }

    public byte[] getMaxsPayload() {
        return fMaxsPayload;
    }

    public void setMaxsPayload(byte[] aMaxsPayload) {
        fMaxsPayload = aMaxsPayload;
    }

    public List<RoomDirectoryEntry> getRoomDirectoryEntries() {
        return fRoomDirectoryEntries;
    }

    public List<ResourceDirectoryEntry> getScriptDirectoryEntries() {
        return fScriptDirectoryEntries;
    }

    public List<ResourceDirectoryEntry> getSoundDirectoryEntries() {
        return fSoundDirectoryEntries;
    }

    public List<ResourceDirectoryEntry> getCustomeDirectoryEntries() {
        return fCustomeDirectoryEntries;
    }

    public List<ResourceDirectoryEntry> getCharacterSetDirectoryEntries() {
        return fCharacterSetDirectoryEntries;
    }

    public byte[] getDobjPayload() {
        return fDobjPayload;
    }

    public void setDobjPayload(byte[] aDobjPayload) {
        fDobjPayload = aDobjPayload;
    }

}
