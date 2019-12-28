package lucasarts.format.gamefile;

/**
 * Created by Lars on 09-04-2017.
 */
public class RoomPosition {

    private final int fRoomId;
    private final int fRoomOffset;

    public RoomPosition(int aRoomId, int aRoomOffset) {
        fRoomId = aRoomId;
        fRoomOffset = aRoomOffset;
    }

    public int getRoomId() {
        return fRoomId;
    }

    public int getRoomOffset() {
        return fRoomOffset;
    }

}
