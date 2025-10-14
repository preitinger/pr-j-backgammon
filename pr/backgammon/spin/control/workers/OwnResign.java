package pr.backgammon.spin.control.workers;

import java.awt.Point;
import java.awt.image.BufferedImage;
import java.awt.image.Raster;

import pr.backgammon.model.Match;
import pr.backgammon.spin.control.BoardSearchers;
import pr.backgammon.spin.control.FastChequerSearch;
import pr.backgammon.spin.control.MatchWorker;
import pr.backgammon.spin.control.TemplateSearchers;

public abstract class OwnResign extends MatchWorker<Void> {
    private final BoardSearchers bs;
    private final TemplateSearchers ts;
    private final int resign;
    private BufferedImage board;
    private Raster boardRaster;
    private FastChequerSearch chequers;

    public OwnResign(BoardSearchers bs, TemplateSearchers ts, int resign) {
        this.bs = bs;
        this.ts = ts;
        chequers = new FastChequerSearch(bs.cal, ts);
        this.resign = resign;
    }

    private Point pos;

    @Override
    public Void doIt() throws Exception {
        System.out.println("\n***** OWN RESIGN\n");
        Match m = state.match;
        if (m.getPlayer(0).field.isInitial() && m.getPlayer(1).field.isInitial()) {
            throw new IllegalStateException("OwnResign darf nicht in der Initialposition ausgefuehrt werden!");
        }
        m.offerResign(m.own, resign);

        Thread.sleep(200);
        board = bs.boardShot()/* .getRaster() */;
        boardRaster = board.getRaster();
        ts.waitAndClick(bs, ts.bAufgeben);
        // bs.sideAufgeben.runAndClick(boardRaster, bs.boardRect().x, bs.boardRect().y);
        // MyRobot.click(bs.boardRect().x + pos.x + 18, bs.boardRect().y + pos.y + 13,
        // 63 - 18, 25 - 13);
        System.out.println("Auf Aufgeben geklickt");

        Thread.sleep(300);
        board = bs.boardShot();
        boardRaster = board.getRaster();

        switch (resign) {
            case 1:
                ts.waitAndClick(bs, ts.dlgResignFromOwn1);
                break;
            case 2:
                ts.waitAndClick(bs, ts.dlgResignFromOwn2);
                break;
            case 3:
                ts.waitAndClick(bs, ts.dlgResignFromOwn3);
                break;
            default:
                throw new IllegalStateException("resign=" + resign);
        }

        System.out.println("Auf Aufgabewert geklickt");

        do {
            Thread.sleep(500);
            board = bs.boardShot();
            boardRaster = board.getRaster();

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
        Point found = ts.bVerlassen.run(board, pos, false);
        if (pos == null && found != null) {
            pos = found;
        }
        return found != null;
    }

    private boolean isHasRejectedVisible() {
        return ts.visible(ts.statusHasRejected, board);
        // return bs.statusHasRejected.run(boardRaster) != null;
    }
}
