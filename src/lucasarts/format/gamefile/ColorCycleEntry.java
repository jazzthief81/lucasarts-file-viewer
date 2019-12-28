package lucasarts.format.gamefile;

/**
 * Created by Lars on 17-06-2018.
 */
public class ColorCycleEntry {
    private final short fCycleSpeed;
    private final short fCycleDirection;
    private final int fCycleStartIndex;
    private final int fCycleStopIndex;

    public ColorCycleEntry(short aCycleSpeed, short aCycleDirection, int aCycleStartIndex, int aCycleStopIndex) {
        fCycleSpeed = aCycleSpeed;
        fCycleDirection = aCycleDirection;
        fCycleStartIndex = aCycleStartIndex;
        fCycleStopIndex = aCycleStopIndex;
    }

    public short getCycleSpeed() {
        return fCycleSpeed;
    }

    public short getCycleDirection() {
        return fCycleDirection;
    }

    public int getCycleStartIndex() {
        return fCycleStartIndex;
    }

    public int getCycleStopIndex() {
        return fCycleStopIndex;
    }
}
