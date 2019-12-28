package lucasarts.format.directory;

import lucasarts.format.gamefile.EncryptingOutputStream;
import lucasarts.format.gamefile.GameFileDecoderUtil;

import java.io.DataOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;

/**
 * Created by Lars on 27-05-2018.
 */
public class DirectoryFileEncoder {

    public void encode(DirectoryFile aDirectoryFile, String aPath) throws IOException {
        DataOutputStream outputStream = new DataOutputStream(new EncryptingOutputStream(new FileOutputStream(aPath)));

        // Encode RNAM section.
        outputStream.writeBytes("RNAM");
        outputStream.writeInt(aDirectoryFile.getRnamPayload().length + 8);
        outputStream.write(aDirectoryFile.getRnamPayload());

        // Encode MAXS section.
        outputStream.writeBytes("MAXS");
        outputStream.writeInt(aDirectoryFile.getMaxsPayload().length + 8);
        outputStream.write(aDirectoryFile.getMaxsPayload());

        // Encode room directory.
        outputStream.writeBytes("DROO");
        List<RoomDirectoryEntry> roomDirectoryEntries = aDirectoryFile.getRoomDirectoryEntries();
        outputStream.writeInt(10 + 5 * roomDirectoryEntries.size());
        GameFileDecoderUtil.writeShortLittleEndian(outputStream, (short) roomDirectoryEntries.size());
        for (RoomDirectoryEntry roomDirectoryEntry : roomDirectoryEntries) {
            outputStream.writeByte(roomDirectoryEntry.getFileId());
        }
        for (RoomDirectoryEntry roomDirectoryEntry : roomDirectoryEntries) {
            outputStream.writeInt(roomDirectoryEntry.getOffset());
        }

        // Encode script directory.
        outputStream.writeBytes("DSCR");
        List<ResourceDirectoryEntry> scriptDirectoryEntries = aDirectoryFile.getScriptDirectoryEntries();
        encodeResourceDirectory(scriptDirectoryEntries, aDirectoryFile, outputStream);

        // Encode sound directory.
        outputStream.writeBytes("DSOU");
        List<ResourceDirectoryEntry> soundDirectoryEntries = aDirectoryFile.getSoundDirectoryEntries();
        encodeResourceDirectory(soundDirectoryEntries, aDirectoryFile, outputStream);

        // Encode custome directory.
        outputStream.writeBytes("DCOS");
        List<ResourceDirectoryEntry> customeDirectoryEntries = aDirectoryFile.getCustomeDirectoryEntries();
        encodeResourceDirectory(customeDirectoryEntries, aDirectoryFile, outputStream);

        // Encode character set directory.
        outputStream.writeBytes("DCHR");
        List<ResourceDirectoryEntry> characterSetDirectoryEntries = aDirectoryFile.getCharacterSetDirectoryEntries();
        encodeResourceDirectory(characterSetDirectoryEntries, aDirectoryFile, outputStream);

        // Encode DOBJ section.
        outputStream.writeBytes("DOBJ");
        outputStream.writeInt(aDirectoryFile.getDobjPayload().length + 8);
        outputStream.write(aDirectoryFile.getDobjPayload());

        outputStream.close();
    }

    private void encodeResourceDirectory(List<ResourceDirectoryEntry> resourceDirectoryEntries, DirectoryFile aDirectoryFile, DataOutputStream aOutputStream) throws IOException {
        aOutputStream.writeInt(10 + 5 * resourceDirectoryEntries.size());
        GameFileDecoderUtil.writeShortLittleEndian(aOutputStream, (short) resourceDirectoryEntries.size());
        for (ResourceDirectoryEntry resourceDirectoryEntry : resourceDirectoryEntries) {
            aOutputStream.writeByte(resourceDirectoryEntry.getRoomId());
        }
        for (ResourceDirectoryEntry resourceDirectoryEntry : resourceDirectoryEntries) {
            GameFileDecoderUtil.writeIntLittleEndian(aOutputStream, resourceDirectoryEntry.getOffset());
        }
    }

}
