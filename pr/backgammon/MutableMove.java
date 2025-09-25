package pr.backgammon;

import pr.backgammon.model.MovePrio;
import pr.model.MutableArray;

@Deprecated
public class MutableMove extends MutableArray<MutablePartMove> {

    public MutableMove() {
        super(4);
    }

    @Override
    protected MutablePartMove createInstance() {
        return new MutablePartMove();
    }

    public MovePrio getPrio(int smallDie) {
        int n = length();
        if (n > 1) {
            return MovePrio.MAX;
        }
        if (n == 0) {
            return MovePrio.EMPTY;
        }
        MutablePartMove pm = at(0);
        if (pm.to - pm.from > smallDie) {
            return MovePrio.BIG;
        }
        return MovePrio.SMALL;
    }

    public static MovePrio getPrio(MutableArray<MutablePartMove> move, int smallDie) {
        int n = move.length();
        if (n > 1) {
            return MovePrio.MAX;
        }
        if (n == 0) {
            return MovePrio.EMPTY;
        }
        MutablePartMove pm = move.at(0);
        if (pm.to - pm.from > smallDie) {
            return MovePrio.BIG;
        }
        return MovePrio.SMALL;

    }
}
