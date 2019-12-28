package lucasarts.format.directory;

import lucasarts.format.gamefile.DecryptingInputStream;
import lucasarts.format.gamefile.GameFileDecoderUtil;

import java.io.DataInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.List;

/**
 * Created by Lars on 28-05-2018.
 */
public class DirectoryFileDecoder {

    public DirectoryFile decode(String aPath) throws IOException {
        DirectoryFile directoryFile = new DirectoryFile();
        DecryptingInputStream decryptingInputStream = new DecryptingInputStream(new FileInputStream(aPath));
        DataInputStream inputStream = new DataInputStream(decryptingInputStream);

        // Read RNAM section.
        byte[] rnamPayload = readPayloadForTag(decryptingInputStream, inputStream, "RNAM");
        directoryFile.setRnamPayload(rnamPayload);

        // Read MAXS section.
        byte[] maxsPayload = readPayloadForTag(decryptingInputStream,inputStream,"MAXS");
        directoryFile.setMaxsPayload(maxsPayload);

        // Read DROO section.
        int drooStartPosition = decryptingInputStream.getPosition();
        String drooTag = GameFileDecoderUtil.readTag(inputStream);
        if (!"DROO".equals(drooTag)) {
            throw new IOException("Expected DROO tag not found.");
        }
        int drooSize = inputStream.readInt();
        short roomCount = GameFileDecoderUtil.readShortLittleEndian(inputStream);
        for(int roomIndex=0;roomIndex<roomCount;roomIndex++) {
            int fileId = inputStream.read();
            RoomDirectoryEntry roomDirectoryEntry = new RoomDirectoryEntry(fileId,0);
            directoryFile.getRoomDirectoryEntries().add(roomDirectoryEntry);
        }
        for(int roomIndex=0;roomIndex<roomCount;roomIndex++) {
            int offset = GameFileDecoderUtil.readIntLittleEndian(inputStream);
            directoryFile.getRoomDirectoryEntries().get(roomIndex).setOffset(offset);
        }
        if (decryptingInputStream.getPosition() != drooStartPosition + drooSize) {
            throw new IOException("DROO section does not have expected size.");
        }

        // Read DSCR section.
        readDirectoryForTag(decryptingInputStream, inputStream, "DSCR", directoryFile.getScriptDirectoryEntries());

        // Read DSOU section.
        readDirectoryForTag(decryptingInputStream, inputStream, "DSOU", directoryFile.getSoundDirectoryEntries());

        // Read DCOS section.
        readDirectoryForTag(decryptingInputStream, inputStream, "DCOS", directoryFile.getCustomeDirectoryEntries());

        // Read DCHR section.
        readDirectoryForTag(decryptingInputStream, inputStream, "DCHR", directoryFile.getCharacterSetDirectoryEntries());

        // Read DOBJ section.
        byte[] dobjPayload = readPayloadForTag(decryptingInputStream, inputStream, "DOBJ");
        directoryFile.setDobjPayload(dobjPayload);

        inputStream.close();

        return directoryFile;
    }

    private byte[] readPayloadForTag(DecryptingInputStream aDecryptingInputStream, DataInputStream aInputStream, String aTag) throws IOException {
        int payloadStartPosition = aDecryptingInputStream.getPosition();
        String payloadTag = GameFileDecoderUtil.readTag(aInputStream);
        if (!aTag.equals(payloadTag)) {
            throw new IOException("Expected " + aTag + " tag not found.");
        }
        int payloadSize = aInputStream.readInt();
        byte[] payload = new byte[payloadSize - 8];
        aInputStream.read(payload);
        if (aDecryptingInputStream.getPosition() != payloadStartPosition + payloadSize) {
            throw new IOException(aTag +" section does not have expected size.");
        }
        return payload;
    }

    private void readDirectoryForTag(DecryptingInputStream aDecryptingInputStream, DataInputStream aInputStream, String aTag, List<ResourceDirectoryEntry> aDirectoryEntries) throws IOException {
        int dirStartPosition = aDecryptingInputStream.getPosition();
        String dirTag = GameFileDecoderUtil.readTag(aInputStream);
        if (!aTag.equals(dirTag)) {
            throw new IOException("Expected " + aTag + " tag not found.");
        }
        int dirSize = aInputStream.readInt();
        readResourceDirectoryEntries(aInputStream, aDirectoryEntries);
        if (aDecryptingInputStream.getPosition() != dirStartPosition + dirSize) {
            throw new IOException(aTag +" section does not have expected size.");
        }
    }

    private void readResourceDirectoryEntries(DataInputStream aInputStream, List<ResourceDirectoryEntry> aDirectoryEntries) throws IOException {
        short entryCount = GameFileDecoderUtil.readShortLittleEndian(aInputStream);
        for(int entryIndex=0;entryIndex<entryCount;entryIndex++) {
            int roomId = aInputStream.read();
            ResourceDirectoryEntry resourceDirectoryEntry = new ResourceDirectoryEntry(roomId,0);
            aDirectoryEntries.add(resourceDirectoryEntry);
        }
        for(int entryIndex=0;entryIndex<entryCount;entryIndex++) {
            int offset = GameFileDecoderUtil.readIntLittleEndian(aInputStream);
            aDirectoryEntries.get(entryIndex).setOffset(offset);
        }
    }

    public static void main(String[] args) throws IOException {
        DirectoryFile directoryFile = new DirectoryFileDecoder().decode(args[0]);
        System.out.println(directoryFile);
    }

}
