package pr.backgammon.gnubg.model;

import pr.backgammon.control.Move;
import pr.backgammon.model.Match;
import pr.model.MutableIntArray;

public class GMove extends GnuCmd {
    private final MutableIntArray move;

    /**
     * @param move - is copied
     */
    public GMove(MutableIntArray move) {
        this.move = new MutableIntArray(8);
        this.move.set(move);
    }

    @Deprecated
    public void runOn(Match m) {
        Move.run(m, move);
    }

    @Override
    protected void safeAppend(StringBuilder sb) {
        sb.append("move");
        int n = move.length();

        for (int i = 0; i < n; ++i) {
            sb.append(' ').append(move.at(i));
        }
    }

}
