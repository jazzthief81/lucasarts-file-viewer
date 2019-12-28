package lucasarts.format.gamefile;

/**
 * Created by Lars on 09-04-2017.
 */
public class Sound extends DefaultGameFileSection implements Resource {

    public Sound(int aOffset, int aSize) {
        super(aOffset, aSize);
    }

    @Override
    public String getKeyword() {
        return "SOUN";
    }

}
