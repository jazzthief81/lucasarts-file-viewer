package lucasarts.format.gamefile;

/**
 * Created by Lars on 27-05-2018.
 */
public abstract class DefaultGameFileSection implements GameFileSection {

    private int offset;
    private int size;

    public DefaultGameFileSection(int aOffset, int aSize) {
        offset = aOffset;
        size = aSize;
    }

    @Override
    public int getOffset() {
        return offset;
    }

    @Override
    public int getSize() {
        return size;
    }

}
