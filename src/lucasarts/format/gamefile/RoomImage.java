package lucasarts.format.gamefile;

/**
 * Created by Lars on 09-04-2017.
 */
public class RoomImage extends DefaultGameFileSection implements GameFileSection {

    private Image image;
    private short zpCount;

    public RoomImage(int aOffset, int aSize, Image aImage, short aZpCount) {
        super(aOffset, aSize);
        image = aImage;
        zpCount = aZpCount;
    }

    @Override
    public String getKeyword() {
        return "RMIM";
    }

    public Image getImage() {
        return image;
    }

    public short getZpCount() {
        return zpCount;
    }

}
