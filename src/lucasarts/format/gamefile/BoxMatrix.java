package lucasarts.format.gamefile;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Lars on 15-04-2017.
 */
public class BoxMatrix extends DefaultGameFileSection{

    private List<BoxMatrixRow> fBoxMatrixRows = new ArrayList<>();

    public BoxMatrix(int aOffset, int aSize, List<BoxMatrixRow> aBoxMatrixRows) {
        super(aOffset, aSize);
        fBoxMatrixRows = aBoxMatrixRows;
    }

    public List<BoxMatrixRow> getBoxMatrixRows() {
        return fBoxMatrixRows;
    }

    @Override
    public String getKeyword() {
        return "BOXM";
    }
}
