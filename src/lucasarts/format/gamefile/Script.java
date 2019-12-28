package lucasarts.format.gamefile;

/**
 * Created by Lars on 09-04-2017.
 */
public class Script extends DefaultGameFileSection implements Resource {

    public Script(int aOffset, int aSize) {
        super(aOffset, aSize);
    }

    @Override
    public String getKeyword() {
        return "SCRP";
    }

}
