package lucasarts.format.gamefile;

import lucasarts.format.directory.*;

import java.io.*;
import java.util.List;

/**
 * Created by Lars on 21-05-2018.
 */
public class Splicer {

    public static final int AMIGA_ROOM_ID = 1;
    public static final int PC_ROOM_ID = 1;
    public static final String AMIGA_FILE_EXTENSION = "002";
    public static final int AMIGA_FILE_ID = 2;

    public static void main(String[] args) throws IOException {
//        new Splicer().splice(
//            "C:\\Users\\Lars\\Documents\\Amiga\\harddisk\\atlantisamiga\\atlantis256." + AMIGA_FILE_EXTENSION,
//            "C:\\Users\\Lars\\Documents\\Amiga\\harddisk\\atlantispc\\atlantis.000", AMIGA_ROOM_ID,
//            PC_ROOM_ID,
//            "C:\\Users\\Lars\\Documents\\Amiga\\harddisk\\atlantisamiga\\atlantis256.000", AMIGA_FILE_ID,
//            "RMHD",
//            new String[]{"LFLF", "ROOM"},
//            new String[]{"OBCD", "EXCD"});
        new Splicer().splice(
            "C:\\Users\\Lars\\Documents\\Amiga\\harddisk\\atlantisamiga\\atlantis256.001",
            "C:\\Users\\Lars\\Documents\\Amiga\\harddisk\\atlantispc\\atlantis.000",
            2, 9,
            "C:\\Users\\Lars\\Documents\\Amiga\\harddisk\\atlantisamiga\\atlantis256.000", 1,
            "COST",
            new String[]{"LFLF"},
            new String[]{"COST", "LFLF"});
    }

    public void splice(String aInputAmigaFile, String aInputPCFile, int aAmigaRoomId, int aPCRoomId, String aAmigaDirectoryFile, int aFileId, String aStartMarker, String[] aParentTags, String[] aEndMarkers) throws IOException {
        DecryptingInputStream inputAmiga = new DecryptingInputStream(
            new BufferedInputStream(
                new FileInputStream(aInputAmigaFile)));
        DecryptingInputStream inputPC = new DecryptingInputStream(
            new BufferedInputStream(
                new FileInputStream(aInputPCFile)));
        EncryptingOutputStream outputAmiga = new EncryptingOutputStream(new FileOutputStream(aInputAmigaFile + ".tmp"));

        // PASS 1: replace the room image from the Amiga file with the corresponding room image in the PC file

        // Forward to point in Amiga file.
        int[] parentOffsets = new int[aParentTags.length];
        int[] roomOffsets = new int[1];
        int oldSectionOffset = skipUntilStart(inputAmiga, outputAmiga, new String[]{aStartMarker}, aAmigaRoomId, aParentTags, parentOffsets, roomOffsets);

        // Forward to point in PC file.
        skipUntilStart(inputPC, null, new String[]{aStartMarker}, aPCRoomId, null, null, null);

        int oldSectionSize = skipUntilStart(inputAmiga, null, aEndMarkers, 1, null, null, null);
        int newSectionSize = skipUntilStart(inputPC, outputAmiga, aEndMarkers, 1, null, null, null);

        skipUntilStart(inputAmiga, outputAmiga, null, 0, null, null, null);

        inputAmiga.close();
        inputPC.close();
        outputAmiga.close();

        // PASS 2a: Correct the size of the LECF section.

        DataInputStream inputAmiga256 =
            new DataInputStream(
                new DecryptingInputStream(
                    new BufferedInputStream(
                        new FileInputStream(aInputAmigaFile + ".tmp"))));
        DataOutputStream outputAmiga256 =
            new DataOutputStream(
                new EncryptingOutputStream(new FileOutputStream(aInputAmigaFile)));
        int bytesRead = 0;

        outputAmiga256.writeInt(inputAmiga256.readInt());
        outputAmiga256.writeInt(inputAmiga256.readInt() + newSectionSize - oldSectionSize);

        bytesRead += 8;

        // PASS 2b: Correct the room offsets in the LOFF section.

        outputAmiga256.writeInt(inputAmiga256.readInt());
        outputAmiga256.writeInt(inputAmiga256.readInt());
        bytesRead += 8;

        int affectedRoomId = -1;
        int affectedSectionOffset = -1;
        int roomCount = inputAmiga256.read();
        outputAmiga256.writeByte(roomCount);
        bytesRead++;

        for (int roomIndex = 0; roomIndex < roomCount; roomIndex++) {
            int roomId = inputAmiga256.read();
            outputAmiga256.write(roomId);
            int roomOffset = GameFileDecoderUtil.readIntLittleEndian(inputAmiga256);
            if (roomOffset == roomOffsets[0]) {
                affectedRoomId = roomId;
                affectedSectionOffset = oldSectionOffset - roomOffset;
            }
            if (roomOffset > oldSectionOffset) {
                roomOffset += newSectionSize - oldSectionSize;
            }
            GameFileDecoderUtil.writeIntLittleEndian(outputAmiga256, roomOffset);
            bytesRead += 5;
        }

        // PASS 2c: Correct sizes of all parent sections.

        int byteRead;
        int tagIndex = 0;
        do {
            if (tagIndex < parentOffsets.length && bytesRead == parentOffsets[tagIndex]) {
                byte[] tag = new byte[4];
                inputAmiga256.read(tag);
                outputAmiga256.write(tag);
                int length = inputAmiga256.readInt();
                int newLength = length + newSectionSize - oldSectionSize;
                outputAmiga256.writeInt(newLength);
                bytesRead += 8;
                tagIndex++;
                byteRead = (byte) length & 0xff;
            }
            else {
                byteRead = inputAmiga256.read();
                if (byteRead != -1) {
                    outputAmiga256.write(byteRead);
                    bytesRead++;
                }
            }
        }
        while (byteRead != -1);

        inputAmiga256.close();
        outputAmiga256.close();

        // PASS 3: Correct parentOffsets in directory if the affected file is the one occurring in the directory for this room.
        DirectoryFileDecoder directoryFileDecoder = new DirectoryFileDecoder();
        DirectoryFile directoryFile = directoryFileDecoder.decode(aAmigaDirectoryFile);

        if (directoryFile.getRoomDirectoryEntries().get(affectedRoomId).getFileId() == aFileId) {
            shiftOffsets(directoryFile.getScriptDirectoryEntries(), affectedRoomId, affectedSectionOffset, newSectionSize - oldSectionSize);
            shiftOffsets(directoryFile.getCustomeDirectoryEntries(), affectedRoomId, affectedSectionOffset, newSectionSize - oldSectionSize);
            shiftOffsets(directoryFile.getSoundDirectoryEntries(), affectedRoomId, affectedSectionOffset, newSectionSize - oldSectionSize);
            shiftOffsets(directoryFile.getCharacterSetDirectoryEntries(), affectedRoomId,affectedSectionOffset,  newSectionSize - oldSectionSize);

            DirectoryFileEncoder directoryFileEncoder = new DirectoryFileEncoder();
            directoryFileEncoder.encode(directoryFile, aAmigaDirectoryFile);
            System.out.println("   Directory rewritten");
        }
        else{
            System.out.println("   Directory NOT rewritten");
        }
    }

    private static void shiftOffsets(List<ResourceDirectoryEntry> aScriptDirectoryEntries, int aAffectedRoomId, int aAffectedSectionOffset, int aOffsetDelta) {
        for (ResourceDirectoryEntry scriptDirectoryEntry : aScriptDirectoryEntries) {
            if (scriptDirectoryEntry.getRoomId() == aAffectedRoomId &&
                scriptDirectoryEntry.getOffset() > aAffectedSectionOffset) {
                scriptDirectoryEntry.setOffset(scriptDirectoryEntry.getOffset() + aOffsetDelta);
            }
        }
    }

    private static int skipUntilStart(DecryptingInputStream aInputAmiga, EncryptingOutputStream aOutputAmiga, String[] aTags, int aCount, String[] aParentTags, int[] aOffsets, int[] aRoomOffsets) throws IOException {
        byte[] tag = new byte[4];
        int count = 0;
        int bytesRead = 0;
        int byteRead;
        do {
            if(bytesRead>=4) {
                aInputAmiga.mark(4);
                aInputAmiga.read(tag);
                aInputAmiga.reset();
                String tagString = new String(tag);

                if (aParentTags != null) {
                    for (int parentIndex = 0; parentIndex < aParentTags.length; parentIndex++) {
                        String parentTag = aParentTags[parentIndex];
                        if (tagString.equals(parentTag)) {
                            aOffsets[parentIndex] = bytesRead;
                        }
                    }
                }
                if (aRoomOffsets != null) {
                    if (tagString.equals("ROOM")) {
                        aRoomOffsets[0] = bytesRead;
                    }
                }

                if (aTags != null) {
                    boolean tagFound = false;
                    for (String tagSearched : aTags) {
                        tagFound = tagString.equals(tagSearched);
                        if (tagFound) {
                            break;
                        }
                    }
                    if (tagFound) {
                        count++;
                        if (count == aCount) {
                            return bytesRead;
                        }
                    }
                }
            }
            byteRead = aInputAmiga.read();
            if (byteRead != -1) {
                bytesRead++;
                if (aOutputAmiga != null) {
                    aOutputAmiga.write(byteRead);
                }
            }
        }
        while (byteRead != -1);
        return bytesRead;
    }

}
