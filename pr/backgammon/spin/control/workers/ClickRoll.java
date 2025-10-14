package pr.backgammon.spin.control.workers;

import java.awt.Point;
import java.awt.image.BufferedImage;

import pr.backgammon.model.Match;
import pr.backgammon.spin.control.BoardSearchers;
import pr.backgammon.spin.control.MatchWorker;
import pr.backgammon.spin.control.SpinRolls;
import pr.backgammon.spin.control.TemplateSearchers;
import pr.control.MyRobot;
import pr.control.TemplateSearcher;

/**
 * Click to trigger the own roll and set the dice that the server rolls for us
 * in match
 */
public abstract class ClickRoll extends MatchWorker<Void> {
    private final BoardSearchers bs;
    private final TemplateSearchers ts;
    private final SpinRolls spinRolls;
    private final boolean withWaitUntilNoOwnRollVisible;

    public ClickRoll(BoardSearchers bs, TemplateSearchers ts, SpinRolls spinRolls, boolean withWaitUntilNoOwnRollVisible) {
        this.bs = bs;
        this.ts = ts;
        this.spinRolls = spinRolls;
        this.withWaitUntilNoOwnRollVisible = withWaitUntilNoOwnRollVisible;
    }

    @Override
    public Void doIt() throws Exception {
        System.out.println("\n***** CLICK ROLL\n");
        boolean dialogHandled;

        if (withWaitUntilNoOwnRollVisible) {
            dialogHandled = waitUntilLastOwnRollDisappeared();
            if (dialogHandled) {
                return null;
            }
        }

        do {

            dialogHandled = waitUntilNoOppRollVisible();
            if (dialogHandled) {
                return null;
            }

            System.out.println("Clicked for own roll");
            doRollClick();

            for (int i = 0; i < 40; ++i) {
                Thread.sleep(100);
                var board = bs.boardShot();
                // var boardRaster = board.getRaster();
                if (handleDialog(board)) {
                    return null;
                }
                spinRolls.detectFromBoardShot(board);
                if (spinRolls.isOwnDice()) {
                    setOwnDice();
                    return null;
                }

                if (spinRolls.isOppDice()) {
                    break;
                }
            }

        } while (true);

        // ////////////////////////////////////////////////////////////////////////

        // Match match = state.match;

        // // Pruefen, ob nicht schon gewuerfelt
        // var board = bs.boardShot().getRaster();

        // if (bs.dlgCorner.run(board) != null) {
        // int resign = bs.searchOppResign(board);
        // if (resign > 0) {
        // match.offerResign(1 - match.own, resign);
        // return null;
        // } else {
        // throw new IllegalStateException("Unerwarteter Dialog");
        // }

        // }

        // spinRolls.detectFromBoardShot(board);

        // if (spinRolls.isInitialDice()) {
        // throw new IllegalStateException("Initial roll when own player shall roll?!");
        // }

        // if (spinRolls.isOppDice()) {
        // throw new IllegalStateException("Opponent's roll when own player shall
        // roll?!");
        // }

        // if (spinRolls.isOwnDice()) {
        // System.out.println("do not click to roll because already rolled");
        // setOwnDice();
        // return null;
        // }

        // // einfach in die mitte der rechten bretthaelfte klicken, um das Wuerfeln zu
        // // triggern
        // doRollClick();

        // // Wait until the own roll has appeared, then scan it and set it in the match

        // do {
        // Thread.sleep(100);

        // board = bs.boardShot().getRaster();

        // if (bs.dlgCorner.run(board) != null) {
        // int resign = bs.searchOppResign(board);
        // if (resign > 0) {
        // match.offerResign(1 - match.own, resign);
        // return null;
        // } else {
        // throw new IllegalStateException("Unerwarteter Dialog");
        // }

        // }

        // spinRolls.detectFromBoardShot(board);

        // if (spinRolls.isOwnDice()) {
        // setOwnDice();
        // return null;
        // }

        // if (spinRolls.isOppDice()) {
        // // throw new IllegalStateException("Opponent's roll when own player shall
        // // roll?!");
        // System.out.println("Ignore opp dice, hopefully old...");
        // doRollClick();
        // }

        // if (spinRolls.isInitialDice()) {
        // throw new IllegalStateException("Initial roll when own player shall roll?!");
        // }

        // } while (true);
    }

    private void doRollClick() throws InterruptedException {
        MyRobot.click(bs.cal.left + (bs.cal.right - bs.cal.left) * 3 / 4, (bs.cal.top + bs.cal.bottom) / 2, 5,
                5);
    }

    /**
     * @return true iff a dialog has become visible, otherwise false.
     */
    private boolean waitUntilLastOwnRollDisappeared() throws InterruptedException {
        // When there is a closeout, this activity is started immediately after the
        // click of our move.
        // So it is possible that the roll for our last move is still visible.
        // Therefore, we wait here eventually until no own roll is visible.

        do {
            var board = bs.boardShot();
            // var boardRaster = board.getRaster();
            if (handleDialog(board)) {
                return true;
            }

            spinRolls.detectFromBoardShot(board);

            if (!spinRolls.isOwnDice()) {
                return false;
            }
            Thread.sleep(100);
        } while (true);
    }

    /**
     * @return true iff a dialog has become visible, otherwise false.
     */
    private boolean waitUntilNoOppRollVisible() throws InterruptedException {

        do {
            Thread.sleep(100);
            var board = bs.boardShot();
            // var boardRaster = board.getRaster();
            if (handleDialog(board)) {
                return true;
            }

            spinRolls.detectFromBoardShot(board);

            if (!spinRolls.isOppDice()) {
                return false;
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

    private boolean handleDialog(BufferedImage board) {
        Match match = state.match;

        if (visible(ts.dlgCorner, board)) {
            int resign = ts.searchOppResign(board);
            if (resign > 0) {
                match.offerResign(1 - match.own, resign);
                return true;
            } else {
                throw new IllegalStateException("Unerwarteter Dialog");
            }

        }

        return false;
    }

    private void setOwnDice() {
        Match match = state.match;

        if (match.active != match.own) {
            throw new IllegalStateException("It's not our roll in the match?!");
        }
        match.roll.die1 = spinRolls.die1();
        match.roll.die2 = spinRolls.die2();
    }
}
