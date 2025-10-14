package pr.backgammon.spin.control.workers;

import java.awt.Point;
import java.awt.image.BufferedImage;
import java.io.IOException;

import pr.backgammon.model.Match;
import pr.backgammon.spin.control.BoardSearchers;
import pr.backgammon.spin.control.MatchWorker;
import pr.backgammon.spin.control.SpinRolls;
import pr.backgammon.spin.control.TemplateSearchers;
import pr.control.MyRobot;

public abstract class OppDoubleReaction extends MatchWorker<Void> {
    private final BoardSearchers bs;
    private final SpinRolls spinRolls;
    private final TemplateSearchers ts;

    public OppDoubleReaction(BoardSearchers bs, SpinRolls spinRolls, TemplateSearchers ts) {
        this.bs = bs;
        this.spinRolls = spinRolls;
        this.ts = ts;
    }

    @Override
    public Void doIt() throws Exception {
        System.out.println("\n***** OPP DOUBLE REACTION\n");
        Match match = state.match;
        // Wait for either dialog with resign or dialog with redouble reaction or own
        // roll or only cube doubled and no own roll or end of match or starting game

        var taken = false;

        do {
            Thread.sleep(100);
            var board = bs.boardShot();
            // var boardRaster = board.getRaster();

            if (ts.visible(ts.dlgCorner, board)) {
                if (ts.visible(ts.dlgVerdoppelnOpp, board)) {
                    // Redouble from opponent, actually illegal in match, but it's spin you know?
                    // ;-)
                    match.cube.value <<= 1;
                    match.cube.owner = match.own;
                    match.cube.offered = true;
                    return null;
                } else {
                    int resign = ts.searchOppResign(board);
                    if (resign > 0) {
                        match.offerResign(1 - match.own, resign);
                        System.out.println("resign erkannt");
                        return null;
                    } else {
                        throw new IllegalStateException("Unknown dialog?!");
                    }
                }
            } else if (!taken) {
                if (ts.visible(ts.bVerlassen, board)) {
                    // Match zuende, hat sich doch ein Dead Cube eingeschlichen.
                    // Kann nur durch drop passiert sein
                    match.drop();
                    if (!match.finished()) {
                        throw new IllegalStateException("Match zuende, aber !match.finished?!");
                    }
                    return null;
                // } else if (bs.cubeSearcher(match.cube.value).run(boardRaster) != null) {
                } else if (searchCube(match.cube.value, board) != null) {
                    // Opp has taken, just wait for own roll
                    System.out.println("Detected that opp has taken the double");
                    match.take();
                    taken = true;

                    spinRolls.detectFromBoardShot(board);
                    if (spinRolls.isOwnDice()) {
                        match.roll.die1 = spinRolls.die1();
                        match.roll.die2 = spinRolls.die2();
                        System.out.println(match.roll.append(new StringBuilder("Own dice detected: ")));
                        return null;
                    } else {
                        System.out.println("Doing roll click because own dice not visible after the take.");
                        Thread.sleep(500); // For panic reason because there were freezes somewhere in this class, probably.
                        MyRobot.click(bs.cal.left + (bs.cal.right - bs.cal.left) * 3 / 4,
                                (bs.cal.top + bs.cal.bottom) / 2, 5, 5);
                    }
                } else {
                    // Nun pruefen, ob Wuerfel sichtbar. Wenn ja, muss der Gegner abgelehnt haben und ein neues Spiel mit initialem Wurf begonnen haben.
                    spinRolls.detectFromBoardShot(board);
                    if (spinRolls.isInitialDice() || spinRolls.isOwnDice() || spinRolls.isOppDice()) {
                        System.out.println("initial dice: " + spinRolls.isInitialDice());
                        System.out.println("own dice: " + spinRolls.isOwnDice());
                        System.out.println("opp dice: " + spinRolls.isOppDice());
                        System.out.println("Nehme daher an, dass Gegner Doppel abgelehnt hat.");
                        match.drop();
                        if (match.finished()) {
                            throw new IllegalStateException("Button Verlassen nicht sichtbar, aber match.finished?!");
                        }

                        return null; // match enthaelt noch nicht den wurf. Dieser wird anschließend in WaitForFirstRoll ermittelt.
                    }
                }
            } else {
                spinRolls.detectFromBoardShot(board);
                if (spinRolls.isOwnDice()) {
                    match.roll.die1 = spinRolls.die1();
                    match.roll.die2 = spinRolls.die2();
                    return null;
                }
            }
        } while (true);
    }

    private Point searchCube(int val, BufferedImage board) throws IOException {
        return ts.search(ts.cube(val), board);
    }
}
