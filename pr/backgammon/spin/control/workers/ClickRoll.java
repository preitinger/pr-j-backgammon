package pr.backgammon.spin.control.workers;

import java.awt.image.Raster;

import pr.backgammon.model.Match;
import pr.backgammon.spin.control.BoardSearchers;
import pr.backgammon.spin.control.MatchWorker;
import pr.backgammon.spin.control.SpinRolls;
import pr.control.MyRobot;

/**
 * Click to trigger the own roll and set the dice that the server rolls for us
 * in match
 */
public abstract class ClickRoll extends MatchWorker<Void> {
    private final BoardSearchers bs;
    private final SpinRolls spinRolls;
    private final boolean withWaitUntilNoOwnRollVisible;

    public ClickRoll(BoardSearchers bs, SpinRolls spinRolls, boolean withWaitUntilNoOwnRollVisible) {
        this.bs = bs;
        this.spinRolls = spinRolls;
        this.withWaitUntilNoOwnRollVisible = withWaitUntilNoOwnRollVisible;
    }

    @Override
    public Void doIt() throws Exception {
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
                var board = bs.boardShot().getRaster();
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
            var board = bs.boardShot().getRaster();
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
            var board = bs.boardShot().getRaster();
            if (handleDialog(board)) {
                return true;
            }

            spinRolls.detectFromBoardShot(board);

            if (!spinRolls.isOppDice()) {
                return false;
            }
        } while (true);
    }

    private boolean handleDialog(Raster board) {
        Match match = state.match;

        if (bs.dlgCorner.run(board) != null) {
            int resign = bs.searchOppResign(board);
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
