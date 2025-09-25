package pr.backgammon.gnubg.model;

public class GComment extends GnuCmd {
    private final String comment;

    public GComment(String comment) {
        this.comment = comment;
    }

    @Override
    protected void safeAppend(StringBuilder sb) {
        sb.append("# ").append(comment);
    }
}
