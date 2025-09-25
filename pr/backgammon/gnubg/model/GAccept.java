package pr.backgammon.gnubg.model;

import pr.backgammon.model.Match;

public class GAccept extends GnuCmd {

    @Deprecated
    public void runOn(Match m) {
        if (m.isCubeOffered()) {
            m.take();
        } else if (m.anyResignOffered()) {
            m.acceptResign();
        }
    }

    @Override
    protected void safeAppend(StringBuilder sb) {
        sb.append("accept");
    }
    
}
