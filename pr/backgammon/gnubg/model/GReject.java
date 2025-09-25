package pr.backgammon.gnubg.model;

import pr.backgammon.model.Match;

public class GReject extends GnuCmd {

    @Deprecated
    public void runOn(Match m) {
        if (m.isCubeOffered()) {
            m.drop();
        } else if (m.anyResignOffered()) {
            m.resetResign();
        }

    }

    @Override
    protected void safeAppend(StringBuilder sb) {
        sb.append("reject");
    }
    
}
