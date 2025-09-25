package pr.backgammon.spin.model;

import pr.backgammon.model.Field;
import pr.backgammon.model.FindTaskArray;
import pr.backgammon.model.Match;
import pr.backgammon.model.OngoingMove;
import pr.model.Mutable;
import pr.model.MutableArray;
import pr.model.MutableIntArray;

public class WorkerState implements Mutable<WorkerState> {
    private static final long serialVersionUID = 2L;

    public final Match match = new Match();
    public OngoingMove ongoingMove = new OngoingMove();
    public final MutableArray<MutableIntArray> allMoves = new MutableArray<MutableIntArray>(
            15 * 15 * 15 * 15) {
        @Override
        protected MutableIntArray createInstance() {
            return new MutableIntArray(8);
        }
    };
    public final FindTaskArray findTaskArray = new FindTaskArray(15 * 15 * 15 * 15);
    public final Match matchCopy = new Match();
    public final Field newOwn = new Field();
    public final Field newOpp = new Field();
    // Currently, only used for Move.aggregate(), but can be extended, later.
    public final MutableArray<MutableIntArray> tmp = new MutableArray<>(1) {
        @Override
        protected MutableIntArray createInstance() {
            return new MutableIntArray(8);
        }
    };
    /**
     * neu in version 2
     */
    public int game;

    @Override
    public void set(WorkerState other) {
        match.set(other.match);
        ongoingMove.set(other.ongoingMove);
        allMoves.set(other.allMoves);
        findTaskArray.set(other.findTaskArray);
        matchCopy.set(other.matchCopy);
        newOwn.set(other.newOwn);
        newOpp.set(other.newOpp);
        game = other.game;
    }
}
