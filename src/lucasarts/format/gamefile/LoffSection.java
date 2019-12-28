package lucasarts.format.gamefile;

import java.util.List;

/**
 * Created by Lars on 09-04-2017.
 */
public class LoffSection extends DefaultGameFileSection implements GameFileSection {

    private List<RoomPosition> fRoomPositions;

    @Override
    public String getKeyword() {
        return "LOFF";
    }

    public LoffSection(int aOffset, int aSize, List<RoomPosition> aRoomPositions) {
        super(aOffset, aSize);
        fRoomPositions = aRoomPositions;
    }

    public List<RoomPosition> getRoomPositions() {
        return fRoomPositions;
    }

}
