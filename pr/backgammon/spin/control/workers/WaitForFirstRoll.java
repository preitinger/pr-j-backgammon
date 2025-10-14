package pr.backgammon.spin.control.workers;

import pr.backgammon.model.Match;
import pr.backgammon.spin.control.BoardSearchers;
import pr.backgammon.spin.control.FastChequerSearch;
import pr.backgammon.spin.control.MatchWorker;
import pr.backgammon.spin.control.SpinRolls;
import pr.backgammon.spin.control.TemplateSearchers;

public abstract class WaitForFirstRoll extends MatchWorker<Void> {
    private final BoardSearchers bs;
    private final TemplateSearchers ts;
    private final SpinRolls spinRolls;

    public WaitForFirstRoll(BoardSearchers bs, TemplateSearchers ts, SpinRolls spinRolls) {
        this.bs = bs;
        this.ts = ts;
        this.spinRolls = spinRolls;

    }

    @Override
    public Void doIt() throws Exception {
        System.out.println("\n***** WAIT FOR FIRST ROLL\n");
        Match match = state.match;
        FastChequerSearch chequers = new FastChequerSearch(bs.cal, ts);

        if (match.active != -1) {
            throw new IllegalStateException("Shall wait for first roll in a new game, but active not -1?!");
        }

        do {
            Thread.sleep(100);

            var board = bs.boardShot();
            // var boardRaster = board.getRaster();
            
            if (ts.visible(ts.dlgCorner, board)) {
                int resign = ts.searchOppResign(board);
                if (resign > 0) {
                    match.offerResign(1 - match.own, resign);
                    return null;
                } else {
                    throw new IllegalStateException("Unerwarteter Dialog");
                }

            }

            spinRolls.detectFromBoardShot(board);
            int die1 = spinRolls.die1();
            int die2 = spinRolls.die2();

            // TODO vor jedem "return" pruefen ob auch wirklich initialposition vorhanden
            // bzw. eine, die nicht schon Steine abgetragen hat

            if (spinRolls.isInitialDice()) {
                chequers.init(board);
                chequers.getFields(state.newOpp, state.newOwn);
                if (!(state.newOpp.numChequersAsExpected() && state.newOwn.numChequersAsExpected())) {
                    // Temporarily, illegal board states are possible (too few or too many chequers
                    // painted).
                    // Then, we simply take a new shot.
                    continue;
                }
                if (/* state.newOpp.getChequers(0) > 0 || state.newOwn.getChequers(0) > 0 */ !(/* state.newOpp.isInitial()
                        && */ state.newOwn.isInitial())) {
                    System.out.println("Got an old board shot, continue in the loop...");
                    continue;
                }
                if (die1 > die2) {
                    match.active = 1 - match.own;
                } else {
                    match.active = match.own;
                }

                // { // TODO BEGIN DEBUG
                //     Match testMatch = new Match();
                //     testMatch.getPlayer(0).field.set(state.newOpp);
                //     testMatch.getPlayer(1).field.set(state.newOwn);
                //     Test.testShowMatchView(new MatchView(testMatch, true, false));
                // } // END DEBUG

                match.roll.die1 = die1;
                match.roll.die2 = die2;
                match.initialRoll = true;
                return null;
            } else if (spinRolls.isOppDice()) {
                chequers.init(board);
                chequers.getFields(state.newOpp, state.newOwn);
                if (!(state.newOpp.numChequersAsExpected() && state.newOwn.numChequersAsExpected())) {
                    // Temporarily, illegal board states are possible (too few or too many chequers
                    // painted).
                    // Then, we simply take a new shot.
                    continue;
                }
                // Wenn Gegner Bot ist oder einfach so sehr schnell zieht, kann das gegnerische Brett ruhig nicht mehr initial sein. Hauptsache das eigene ist es.
                if (/* state.newOpp.getChequers(0) > 0 || state.newOwn.getChequers(0) > 0 */ !(/* state.newOpp.isInitial()
                        && */ state.newOwn.isInitial())) {
                    System.out.println("Got an old board shot, continue in the loop...");
                    continue;
                }

                // { // TODO BEGIN DEBUG
                //     Match testMatch = new Match();
                //     testMatch.getPlayer(0).field.set(state.newOpp);
                //     testMatch.getPlayer(1).field.set(state.newOwn);
                //     Test.testShowMatchView(new MatchView(testMatch, true, false));
                // } // END DEBUG

                match.active = 1 - match.own;
                match.roll.die1 = die1;
                match.roll.die2 = die2;
                return null;
            } else if (spinRolls.isOwnDice()) {
                chequers.init(board);
                chequers.getFields(state.newOpp, state.newOwn);
                if (!(state.newOpp.numChequersAsExpected() && state.newOwn.numChequersAsExpected())) {
                    // Temporarily, illegal board states are possible (too few or too many chequers
                    // painted).
                    // Then, we simply take a new shot.
                    continue;
                }
                if (/* state.newOpp.getChequers(0) > 0 || state.newOwn.getChequers(0) > 0 */ !(state.newOpp.isInitial()
                        && state.newOwn.isInitial())) {
                    System.out.println("Got an old board shot, continue in the loop...");
                    continue;
                }

                // { // TODO BEGIN DEBUG
                //     Match testMatch = new Match();
                //     testMatch.getPlayer(0).field.set(state.newOpp);
                //     testMatch.getPlayer(1).field.set(state.newOwn);
                //     Test.testShowMatchView(new MatchView(testMatch, true, false));
                // } // END DEBUG

                match.active = match.own;
                match.roll.die1 = die1;
                match.roll.die2 = die2;
                return null;
            }
        } while (true);
    }
}
