package pr.backgammon;

import pr.model.Mutable;

@Deprecated
public class MutablePartMove implements Mutable<MutablePartMove> {
    public int from, to;
    public boolean hit;

    /**
     * deep copy
     */
    public void set(MutablePartMove other) {
        from = other.from;
        to = other.to;
        hit = other.hit;
    }

    public StringBuilder append(StringBuilder sb) {
        if (sb == null) {
            sb = new StringBuilder();
        }
        
        sb.append(from).append('/').append(to);
        if (hit)
            sb.append('*');

        return sb;
    }
}
