package lucasarts.format.gamefile;

/**
 * Created by Lars on 09-04-2017.
 */
public class ObjectCode extends DefaultGameFileSection implements GameFileSection {

    private final short fObjectIdentifier;
    private String fObjectName;

    public ObjectCode(int aOffset, int aSize, short aObjectIdentifier, String aObjectName) {
        super(aOffset, aSize);
        fObjectIdentifier = aObjectIdentifier;
        fObjectName = aObjectName;
    }

    @Override
    public String getKeyword() {
        return "OBCD";
    }

    public short getObjectIdentifier() {
        return fObjectIdentifier;
    }

    public String getObjectName() {
        return fObjectName;
    }

}
