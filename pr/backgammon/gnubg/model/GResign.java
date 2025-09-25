package pr.backgammon.gnubg.model;

public class GResign extends GnuCmd {
    /**
     * 1 to 3
     */
    private final int resign;

    public GResign(int resign) {
        this.resign = resign;
    }

    @Override
    protected void safeAppend(StringBuilder sb) {
        sb.append("resign ").append(resign);
    }
}
