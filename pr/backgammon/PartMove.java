package pr.backgammon;

/**
 * immutable, so no copy constructor because no copying
 */
public class PartMove {
    /**
     * 25 is bar
     * 24-1 are regular fields
     * 0 is off
     */
    public PartMove(int from, int to, boolean hit) {
        if (from > 25 || from < 1)
            throw new IllegalArgumentException("from is " + from);
        if (to > 24 || to < 0)
            throw new IllegalArgumentException("to is " + to);

        this.from = from;
        this.to = to;
        this.hit = hit;
    }

    public void append(StringBuilder sb) {
        sb.append(from).append('/').append(to);
        if (hit)
            sb.append('*');
    }

    public final int from, to;
    public final boolean hit;
}
