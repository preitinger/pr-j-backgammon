package pr.backgammon.gnubg.model;

import pr.backgammon.model.Match;

public class GSetCubeValue extends GnuCmd {
    private final int value;

    public GSetCubeValue(int value) {
        this.value = value;
    }

    // @Override
    public void runOn(Match m) {
        m.cube.value = value;
    }

    @Override
    protected void safeAppend(StringBuilder sb) {
        sb.append("set cube value ").append(value);
    }
    
}
