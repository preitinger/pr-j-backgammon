package pr.backgammon.gnubg.model;

import pr.backgammon.model.Match;

public class GSetTurn extends GnuCmd {
    private int player;

    public GSetTurn(int player) {
        this.player = player;
    }

    // @Override
    public void runOn(Match m) {
        m.setTurn(player);
    }

    @Override
    protected void safeAppend(StringBuilder sb) {
        sb.append("set turn ").append(player);
    }
    
}
