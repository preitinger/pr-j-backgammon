package pr.backgammon.spin.control.workers;

import pr.backgammon.model.Match;
import pr.backgammon.spin.control.BoardSearchers;
import pr.backgammon.spin.control.MatchWorker;
import pr.backgammon.spin.control.SpinRolls;

public abstract class OppRollOrDouble extends MatchWorker<Void> {
    private final BoardSearchers bs;
    private final SpinRolls spinRolls;

    public OppRollOrDouble(BoardSearchers bs, SpinRolls spinRolls) {
        this.bs = bs;
        this.spinRolls = spinRolls;
    }

    @Override
    public Void doIt() throws Exception {
        Match match = state.match;
        
        if (match.active != 1 - match.own) {
            throw new IllegalStateException("Gegner nicht am Wurf?!");
        }

        // Warten bis Aufgabedialog oder Doppeldialog oder Wurf des Gegners

        do {
            Thread.sleep(100);
            var board = bs.boardShot().getRaster();

            if (bs.dlgCorner.run(board) != null) {
                System.out.println("dialog erkannt");
                int resign = bs.searchOppResign(board);
                if (resign > 0) {
                    match.offerResign(1 - match.own, resign);
                    System.out.println("resign erkannt");
                    return null;
                } else {
                    if (bs.verdoppelnOpp.run(board) != null || bs.verdoppelnOppLong.run(board) != null) {
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
                    return null;
                }

                if (spinRolls.isInitialDice()) {
                    System.out.println("initial dice erkannt");
                    throw new IllegalStateException("expected opp roll, but initial dice?!");
                }

                // if (spinRolls.isOwnDice()) {
                //     // Wahrscheinlich war die Erkennung zu schnell und es ist noch der alte eigene Wurf.
                // }
            }
        } while (true);
    }
}
