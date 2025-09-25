package pr.backgammon.gnubg.model;

import pr.backgammon.model.Match;

public class GSetCubeOwner extends GnuCmd {
    private final int owner;

    public GSetCubeOwner(int owner) {
        if (owner < 0 || owner > 1) {
            throw new IllegalArgumentException();
        }
        this.owner = owner;
    }

    // @Override
    public void runOn(Match m) {
        m.cube.owner = owner;
    }

    @Override
    protected void safeAppend(StringBuilder sb) {
        sb.append("set cube owner ").append(owner);
    }
    
}
