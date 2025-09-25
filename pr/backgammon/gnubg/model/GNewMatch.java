package pr.backgammon.gnubg.model;

import pr.backgammon.model.Match;

public class GNewMatch extends GnuCmd {
    private final int len;

    public GNewMatch(int len) {
        this.len = len;
    }

    @Override
    protected void safeAppend(StringBuilder sb) {
        sb.append("new match ").append(len);
    }

    @Deprecated
    public void runOn(Match m) {
        m.reset(1, len);
    }
}
