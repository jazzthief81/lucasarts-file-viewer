package lucasarts.format.gamefile;

/**
 * Created by Lars on 15-04-2017.
 */
public class BoxMatrixRowRange {

    private int fMinDestinationBoxIndex;
    private int fMaxDestinationBoxIndex;
    private int fNextBoxIndex;

    public BoxMatrixRowRange(int aMinDestinationBoxIndex, int aMaxDestinationBoxIndex, int aNextBoxIndex) {
        fMinDestinationBoxIndex = aMinDestinationBoxIndex;
        fMaxDestinationBoxIndex = aMaxDestinationBoxIndex;

        fNextBoxIndex = aNextBoxIndex;
    }

    public int getMinDestinationBoxIndex() {
        return fMinDestinationBoxIndex;
    }

    public int getMaxDestinationBoxIndex() {
        return fMaxDestinationBoxIndex;
    }

    public int getNextBoxIndex() {
        return fNextBoxIndex;
    }
}
