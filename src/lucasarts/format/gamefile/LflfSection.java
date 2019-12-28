package lucasarts.format.gamefile;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Lars on 09-04-2017.
 */
public class LflfSection extends DefaultGameFileSection implements GameFileSection {

    private List<Resource> fResources;

    public LflfSection(int aOffset, int aSize, List<Resource> aResources) {
        super(aOffset, aSize);
        fResources = aResources;
    }

    @Override
    public String getKeyword() {
        return "LFLF";
    }

    public List<Resource> getResources() {
        return fResources;
    }

    public List<Room> getRooms() {
        return getResourcesByType(Room.class);
    }

    private List getResourcesByType(Class aType) {
        List resourcesByType = new ArrayList();
        for (Resource resource : fResources) {
            if (aType.isInstance(resource)) {
                resourcesByType.add(resource);
            }
        }
        return resourcesByType;
    }

}
