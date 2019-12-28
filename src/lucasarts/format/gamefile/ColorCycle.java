package lucasarts.format.gamefile;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Lars on 17-06-2018.
 */
public class ColorCycle extends DefaultGameFileSection {

    public Map<Integer, ColorCycleEntry> entries = new HashMap<>();

    public ColorCycle(int aOffset, int aSize) {
        super(aOffset, aSize);
    }

    @Override
    public String getKeyword() {
        return "CYCL";
    }
}
