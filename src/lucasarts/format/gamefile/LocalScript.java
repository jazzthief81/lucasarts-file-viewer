package lucasarts.format.gamefile;

/**
 * Created by Lars on 09-04-2017.
 */
public class LocalScript extends DefaultGameFileSection implements GameFileSection {

    public LocalScript(int aOffset, int aSize) {
        super(aOffset, aSize);
    }

    @Override
    public String getKeyword() {
        return "LSCR";
    }

}
