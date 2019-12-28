package lucasarts.format.gamefile;

/**
 * Created by Lars on 09-04-2017.
 */
public class CharacterSet extends DefaultGameFileSection implements Resource {

    public CharacterSet(int aOffset, int aSize) {
        super(aOffset, aSize);
    }

    @Override
    public String getKeyword() {
        return "CHAR";
    }

}
