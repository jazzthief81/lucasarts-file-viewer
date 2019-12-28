package lucasarts.format.gamefile;

/**
 * Created by Lars on 15-04-2017.
 */
public class Box  {

    private short[] xs;
    private short[] ys;
    private short fScaleTableIndex;

    public Box(short[] aXs, short[] aYs, short aScaleTableIndex) {
        xs = aXs;
        ys = aYs;
        fScaleTableIndex = aScaleTableIndex;
    }

    public short[] getXs() {
        return xs;
    }

    public short[] getYs() {
        return ys;
    }

    public short getScaleTableIndex() {
        return fScaleTableIndex;
    }

}
