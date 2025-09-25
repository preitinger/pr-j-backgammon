package pr.backgammon.spin.control.workers;

import java.awt.Point;
import java.awt.image.Raster;

import pr.backgammon.model.Match;
import pr.backgammon.spin.control.BoardSearchers;
import pr.backgammon.spin.control.FastChequerSearch;
import pr.backgammon.spin.control.MatchWorker;
import pr.control.MyRobot;

public abstract class OwnResign extends MatchWorker<Void> {
    private final BoardSearchers bs;
    private final int resign;
    private Raster board;
    private FastChequerSearch chequers;

    public OwnResign(BoardSearchers bs, int resign) {
        this.bs = bs;
        chequers = new FastChequerSearch(bs.cal);
        this.resign = resign;
    }

    @Override
    public Void doIt() throws Exception {
        Match m = state.match;
        if (m.getPlayer(0).field.isInitial() && m.getPlayer(1).field.isInitial()) {
            throw new IllegalStateException("OwnResign darf nicht in der Initialposition ausgefuehrt werden!");
        }
        m.offerResign(m.own, resign);

        Thread.sleep(200);
        board = bs.boardShot().getRaster();
        Point pos = bs.sideAufgeben.run(board);
        if (pos == null) {
            throw new IllegalStateException("Button Aufgeben nicht gefunden!");
        }
        MyRobot.click(bs.boardRect().x + pos.x + 18, bs.boardRect().y + pos.y + 13, 63 - 18, 25 - 13);
        System.out.println("Auf Aufgeben geklickt");

        Thread.sleep(300);
        board = bs.boardShot().getRaster();

        switch (resign) {
            case 1:
                if (!bs.resignFromOwn1Unpressed.runAndClick(board, bs.boardRect().x, bs.boardRect().y)) {
                    throw new IllegalStateException("Button Aufgabe einfach nicht gefunden!");
                }
                break;
            case 2:
                if (!bs.resignFromOwn2Unpressed.runAndClick(board, bs.boardRect().x, bs.boardRect().y)) {
                    throw new IllegalStateException("Button Aufgabe Gammon nicht gefunden!");
                }
                break;
            case 3:
                if (!bs.resignFromOwn3Unpressed.runAndClick(board, bs.boardRect().x, bs.boardRect().y)) {
                    throw new IllegalStateException("Button Aufgabe Backgammon nicht gefunden!");
                }
                break;
            default:
                throw new IllegalStateException("resign=" + resign);
        }

        System.out.println("Auf Aufgabewert geklickt");

        do {
            Thread.sleep(500);
            board = bs.boardShot().getRaster();

            // Fall 1 - initiales Brett und ein Wurf
            // bedeutet Aufgabe angenommen und Match noch nicht zuende

            // Fall 2 - bs.sideVerlassen sichtbar
            // bedeutet Aufgabe angenommen und Match zuende

            // Fall 3 - bs.hasRejected sichtbar
            // bedeutet hat Aufgabe abgelehnt

            if (isInitialBoard()) { // Ist ok, da Initialposition fuer OwnResign ausgeschlossen wird.
                // Fall 1
                m.acceptResign();
                return null;
            }

            if (isLeaveButtonVisible()) {
                // Fall 2
                m.acceptResign();
                return null;
            }

            if (isHasRejectedVisible()) {
                // Fall 3
                m.resetResign();
                return null;
            }

        } while (true);

    }

    private boolean isInitialBoard() {
        chequers.init(board);
        chequers.getFields(state.newOwn, state.newOpp);
        return state.newOwn.isInitial() && state.newOpp.isInitial();
    }

    private boolean isLeaveButtonVisible() {
        return bs.sideVerlassen.run(board) != null;
    }

    private boolean isHasRejectedVisible() {
        return bs.statusHasRejected.run(board) != null;
    }
}
