package pr.backgammon.gnubg.model;

import pr.backgammon.model.Match;

public class GDouble extends GnuCmd {

    @Deprecated
    public void runOn(Match m) {
        m.offerDouble(m.getActivePlayer());
    }

    @Override
    protected void safeAppend(StringBuilder sb) {
        sb.append("double");
    }

}
