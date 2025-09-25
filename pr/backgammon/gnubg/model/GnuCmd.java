package pr.backgammon.gnubg.model;

public abstract class GnuCmd {
    public final StringBuilder append(StringBuilder sb) {
        if (sb == null) {
            sb = new StringBuilder();
        }
        safeAppend(sb);
        sb.append('\n');
        return sb;
    }

    // public abstract void runOn(Match m);

    /**
     * called from append(StringBuilder sb).
     * calling append() will add a new line at the end, implicitly.
     * @param sb - guaranteed to be not null
     */
    protected abstract void safeAppend(StringBuilder sb);

    public String toString() {
        return append(null).toString();
    }
}