package pr.backgammon.gnubg.model;

import pr.backgammon.model.Match;

public class GSetCubeUse extends GnuCmd {
    private final boolean cubeUse;

    public GSetCubeUse(boolean on) {
        this.cubeUse = on;
    }

    // @Override
    public void runOn(Match m) {
        // nothing yet in match for non-cube-use
    }

    @Override
    protected void safeAppend(StringBuilder sb) {
        sb.append("set cube use ").append(cubeUse ? "on" : "off");
    }
    
}
