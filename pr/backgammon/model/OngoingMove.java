package pr.backgammon.model;

import pr.model.Mutable;
import pr.model.MutableIntArray;

public class OngoingMove implements Mutable<OngoingMove> {
    private static final long serialVersionUID = 1L;

    /**
     * [2 * i] contains from field, and [2 * i + 1] contains to field.
     */
    public final MutableIntArray move = new MutableIntArray(8);
    /**
     * If a chequer has been hit in part move i, [i] contains 1, otherwise it contains 0.
     */
    public final MutableIntArray hits = new MutableIntArray(4);
    /**
     * Field 24 is the start for the own player in the corresponding MatchView
     */
    public int highlightedPip = -1;
    public int hoveredField = -1;

    public void set(OngoingMove other) {
        this.move.set(other.move);
        this.highlightedPip = other.highlightedPip;
    }
}
