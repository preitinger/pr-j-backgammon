package pr.backgammon.spin;

import pr.backgammon.Move;

public class WaitForOwnMoveRes {
    public final WaitForOwnMoveResType type;
    public final Move move;

    public WaitForOwnMoveRes(WaitForOwnMoveResType type, Move move) {
        this.type = type;
        this.move = move;
    }

}
