package pr.backgammon.gnubg.model;

public class GNewGame extends GnuCmd {
    @Override
    protected void safeAppend(StringBuilder sb) {
        sb.append("new game");
    }
}
