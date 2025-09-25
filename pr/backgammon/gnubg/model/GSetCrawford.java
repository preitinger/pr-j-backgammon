package pr.backgammon.gnubg.model;

import pr.backgammon.model.Match;

public class GSetCrawford extends GnuCmd {
    private final boolean crawford;

    public GSetCrawford(boolean crawford) {
        this.crawford = crawford;
    }

    @Deprecated
    public void runOn(Match m) {
        m.crawfordRound = crawford;
    }

    @Override
    protected void safeAppend(StringBuilder sb) {
        sb.append("set crawford ").append(crawford ? "on" : "off");
    }
    
}
