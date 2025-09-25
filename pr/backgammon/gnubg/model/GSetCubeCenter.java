package pr.backgammon.gnubg.model;

import pr.backgammon.model.Match;

public class GSetCubeCenter extends GnuCmd {

    // @Override
    public void runOn(Match m) {
        m.cube.owner = -1;
    }

    @Override
    protected void safeAppend(StringBuilder sb) {
        sb.append("set cube center");
    }
    
}
