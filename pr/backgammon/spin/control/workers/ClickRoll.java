package pr.backgammon.spin.control.workers;

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

    public ClickRoll(BoardSearchers bs, SpinRolls spinRolls) {
        this.bs = bs;
        this.spinRolls = spinRolls;
    }

    @Override
    public Void doIt() throws Exception {
        Match match = state.match;

        // Pruefen, ob nicht schon gewuerfelt
        var board = bs.boardShot().getRaster();

        if (bs.dlgCorner.run(board) != null) {
            int resign = bs.searchOppResign(board);
            if (resign > 0) {
                match.offerResign(1 - match.own, resign);
                return null;
            } else {
                throw new IllegalStateException("Unerwarteter Dialog");
            }

        }

        spinRolls.detectFromBoardShot(board);

        if (spinRolls.isInitialDice()) {
            throw new IllegalStateException("Initial roll when own player shall roll?!");
        }

        if (spinRolls.isOppDice()) {
            throw new IllegalStateException("Opponent's roll when own player shall roll?!");
        }

        if (spinRolls.isOwnDice()) {
            System.out.println("do not click to roll because already rolled");
            setOwnDice();
            return null;
        }

        // einfach in die mitte der rechten bretthaelfte klicken, um das Wuerfeln zu
        // triggern
        MyRobot.click(bs.cal.left + (bs.cal.right - bs.cal.left) * 3 / 4, (bs.cal.top + bs.cal.bottom) / 2, 5, 5);

        // Wait until the own roll has appeared, then scan it and set it in the match

        do {
            Thread.sleep(100);

            board = bs.boardShot().getRaster();

            if (bs.dlgCorner.run(board) != null) {
                int resign = bs.searchOppResign(board);
                if (resign > 0) {
                    match.offerResign(1 - match.own, resign);
                    return null;
                } else {
                    throw new IllegalStateException("Unerwarteter Dialog");
                }

            }

            spinRolls.detectFromBoardShot(board);

            if (spinRolls.isOwnDice()) {
                setOwnDice();
                return null;
            }

            if (spinRolls.isOppDice()) {
                // throw new IllegalStateException("Opponent's roll when own player shall
                // roll?!");
                System.out.println("Ignore opp dice, hopefully old...");
                MyRobot.click(bs.cal.left + (bs.cal.right - bs.cal.left) * 3 / 4, (bs.cal.top + bs.cal.bottom) / 2, 5,
                        5);
            }

            if (spinRolls.isInitialDice()) {
                throw new IllegalStateException("Initial roll when own player shall roll?!");
            }

        } while (true);
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
