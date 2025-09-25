package pr.backgammon.gnubg.model;

import pr.backgammon.model.Field;
import pr.backgammon.model.Match;

public class GSetBoardSimple extends GnuCmd {
    private final int[] board;
    
    /**
     * Spieler am Wurf spielt von 24 nach 1.
     * 
     * Werte in board:
     * 
     * 0: Bar des Spielers am Wurf
     * 1-24: positiv falls Steine des Spielers am Wurf auf diesem Feld; negativ, falls Steine des Spielers nicht am Wurf auf diesem Feld.
     * 25: Bar des Spielers nicht am Wurf
     * 
     * @param board - is copied
     */
    public GSetBoardSimple(int[] board) {
        this.board = board.clone();
    }

    @Override
    public void safeAppend(StringBuilder sb) {
        sb.append("set board simple");
        for (int i = 0; i < board.length; ++i) {
            sb.append(' ').append(board[i]);
        }
    }

    @Deprecated
    public void runOn(Match m) {
        Field active = m.getPlayer(m.getActivePlayer()).getField();
        Field other = m.getPlayer(1 - m.getActivePlayer()).getField();

        active.set(25, board[0]);
        other.set(25, board[25]);

        for (int i = 1; i <= 24; ++i) {
            if (board[i] < 0) {
                other.set(25 - i, -board[i]);
                active.set(i, 0);
            } else {
                other.set(25 - i, 0);
                active.set(i, board[i]);
            }
        }
    }
}
