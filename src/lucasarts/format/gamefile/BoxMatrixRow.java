package lucasarts.format.gamefile;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Lars on 15-04-2017.
 */
public class BoxMatrixRow {

    private List<BoxMatrixRowRange> fBoxMatrixRowRanges = new ArrayList<BoxMatrixRowRange>();

    public BoxMatrixRow(List<BoxMatrixRowRange> aBoxMatrixRowRanges) {
        fBoxMatrixRowRanges = aBoxMatrixRowRanges;
    }

    public List<BoxMatrixRowRange> getBoxMatrixRowRanges() {
        return fBoxMatrixRowRanges;
    }

}
