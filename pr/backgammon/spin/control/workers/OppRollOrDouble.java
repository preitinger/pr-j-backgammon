package pr.backgammon.spin.control.workers;

import java.awt.Point;
import java.awt.image.BufferedImage;
import java.io.IOException;

import pr.backgammon.model.Match;
import pr.backgammon.spin.control.BoardSearchers;
import pr.backgammon.spin.control.MatchWorker;
import pr.backgammon.spin.control.SpinRolls;
import pr.backgammon.spin.control.TemplateSearchers;
import pr.control.TemplateSearcher;

public abstract class OppRollOrDouble extends MatchWorker<Void> {
    private final BoardSearchers bs;
    private final SpinRolls spinRolls;
    private final TemplateSearchers ts;
    private boolean startWithSleep;

    public OppRollOrDouble(BoardSearchers bs, SpinRolls spinRolls, TemplateSearchers ts, boolean startWithSleep) {
        this.bs = bs;
        this.spinRolls = spinRolls;
        this.ts = ts;
        this.startWithSleep = startWithSleep;
    }

    @Override
    public Void doIt() throws Exception {
        System.out.println("\n***** OPP ROLL OR DOUBLE\n");
        Match match = state.match;

        if (match.active != 1 - match.own) {
            throw new IllegalStateException("Gegner nicht am Wurf?!");
        }

        // Warten bis Aufgabedialog oder Doppeldialog oder Wurf des Gegners

        do {
            if (startWithSleep) {
                Thread.sleep(100);
            } else {
                startWithSleep = true;
            }
            var board = bs.boardShot();
            // var boardRaster = board.getRaster();

            if (visible(ts.dlgCorner, board)) {
                // if (bs.dlgCorner.run(boardRaster) != null) {
                System.out.println("dialog erkannt");
                int resign = ts.searchOppResign(board);
                if (resign > 0) {
                    match.offerResign(1 - match.own, resign);
                    System.out.println("resign erkannt");
                    return null;
                } else {
                    // if (bs.verdoppelnOpp.run(boardRaster) != null ||
                    // bs.verdoppelnOppLong.run(boardRaster) != null) {
                    if (searchVerdoppelnOpp(board) != null) {
                        System.out.println("doppeln vom gegner erkannt");
                        match.offerDouble(1 - match.own);
                        return null;
                    } else {
                        throw new IllegalStateException("Unerwarteter Dialog");
                    }
                }
            } else {
                spinRolls.detectFromBoardShot(board);

                if (spinRolls.isOppDice()) {
                    System.out.println("opp dice erkannt");
                    match.roll.die1 = spinRolls.die1();
                    match.roll.die2 = spinRolls.die2();
                    // spinRolls.storeImg(board, 1, true, !bs.cal.ownWhite, match.roll.die1);
                    // spinRolls.storeImg(board, 1, false, !bs.cal.ownWhite, match.roll.die2);
                    return null;
                }

                if (spinRolls.isInitialDice()) {
                    System.out.println("initial dice erkannt");
                    throw new IllegalStateException("expected opp roll, but initial dice?!");
                }

                // if (spinRolls.isOwnDice()) {
                // // Wahrscheinlich war die Erkennung zu schnell und es ist noch der alte
                // eigene Wurf.
                // }
            }
        } while (true);
    }

    private Point pos;

    private boolean visible(TemplateSearcher s, BufferedImage board) {
        Point found = s.run(board, pos, false);
        if (found != null) {
            pos = found;
            return true;
        }
        return false;
    }

    private Point searchVerdoppelnOpp(BufferedImage board) throws IOException {
        return ts.dlgVerdoppelnOpp.run(board, false);
    }
}
