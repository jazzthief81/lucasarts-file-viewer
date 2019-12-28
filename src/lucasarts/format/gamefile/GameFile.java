package lucasarts.format.gamefile;

import java.util.List;

/**
 * Created by Lars on 09-04-2017.
 */
public class GameFile {

    private LoffSection fLoffSection;
    private List<LflfSection> fLflfSections;

    public GameFile(LoffSection aLoffSection, List<LflfSection> aLflfSections) {
        fLoffSection = aLoffSection;
        fLflfSections = aLflfSections;
    }

    public LoffSection getLoffSection() {
        return fLoffSection;
    }

    public List<LflfSection> getLflfSections() {
        return fLflfSections;
    }

}
