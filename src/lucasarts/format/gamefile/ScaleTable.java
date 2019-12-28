package lucasarts.format.gamefile;

/**
 * Created by Lars on 17-04-2017.
 */
public class ScaleTable {

    private final short fScale1;
    private final short fY1;
    private final short fScale2;
    private final short fY2;

    public ScaleTable(short aScale1, short aY1, short aScale2, short aY2) {
        fScale1 = aScale1;
        fY1 = aY1;
        fScale2 = aScale2;
        fY2 = aY2;
    }

    public short getScale1() {
        return fScale1;
    }

    public short getY1() {
        return fY1;
    }

    public short getScale2() {
        return fScale2;
    }

    public short getY2() {
        return fY2;
    }

}
