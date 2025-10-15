package pr.backgammon.spin.control.workers;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;

import pr.backgammon.control.AllMoves;
import pr.backgammon.control.Move;
import pr.backgammon.model.Field;
import pr.backgammon.model.Match;
import pr.backgammon.spin.control.BoardSearchers;
import pr.backgammon.spin.control.CalibrationForSpin;
import pr.backgammon.spin.control.FastChequerSearch;
import pr.backgammon.spin.control.MatchWorker;
import pr.backgammon.spin.control.SpinRolls;
import pr.backgammon.spin.control.TemplateSearchers;
import pr.backgammon.spin.model.WaitForOppMoveBug;
import pr.backgammon.spin.model.WorkerState;
import pr.control.Tools;
import pr.model.MutableIntArray;

public abstract class WaitForOppMove extends MatchWorker<WaitForOppMoveRes> {
    private final CalibrationForSpin cal;
    private final BoardSearchers bs;
    private final TemplateSearchers ts;
    private final FastChequerSearch chequers;
    private final SpinRolls spinRolls;
    private int savedFiles = 0;
    private boolean startWithSleep;

    public WaitForOppMove(CalibrationForSpin cal, BoardSearchers bs, TemplateSearchers ts, FastChequerSearch chequers, SpinRolls spinRolls, boolean startWithSleep) {
        this.cal = cal;
        this.bs = bs;
        this.ts = ts;
        this.chequers = chequers;
        this.spinRolls = spinRolls;
        this.startWithSleep = startWithSleep;
    }

    @Override
    public WaitForOppMoveRes doIt() throws Exception {
        System.out.println("\n***** WAIT FOR OPP MOVE\n");
        WaitForOppMoveRes res = new WaitForOppMoveRes();
        // res.cal = cal;
        // spinRolls = spinRolls;
        // res.chequers = new FastChequerSearch(cal, ts);

        System.out.println("match.active vor AllMoves.find: " + state.match.active);
        AllMoves.find(state.match, state.allMoves, state.findTaskArray);
        System.out.println("match.active nach AllMoves.find: " + state.match.active);
        Match match = state.match;
        Match matchCopy = state.matchCopy;
        matchCopy.set(match);

        if (match.closedOut(match.own)) {
            return waitForResignOrAnyMovePosition(res);
        } else {
            return waitForResignOrNewRollOrNewGameOrMatchEnd(res);
        }

        // // BEGIN neu

        // // Wegen der Möglichkeit von Fehlinterpretationen von Zwischenständen beim
        // // Rausspielen als Endstände, ist auch folgendes nicht immer korrekt.
        // // Benötigt wird also eine Fallunterscheidung, ob Verschwenden möglich ist
        // (dann
        // // ist Closeout unmöglich) oder umgekehrt. Je nach Fall die dann
        // // immer korrekte Variante. Bei weder Closeout noch Verschwendung, das
        // // schnellere, und das wäre dass man erst nur auf einen anderen Wurf oder
        // Ende
        // // des Matches wartet

        // // NEIN, folgendes ist nicht immer korrekt. Es kann sein, dass der eigene
        // // Spieler "closed out" ist und dann unmittelbar darauf wieder ein
        // gegnerischer
        // // Wurf kommt, evtl. sogar genau der gleiche.
        // // Also muss ungeachtet des Wurfs geprüft werden, ob ein Dialog sichtbar ist
        // und
        // // wenn nicht ob einer der gültigen Züge mittlerweile gespielt ist,
        // // moeglicherweise auch als letzten Zug im Spiel oder sogar im Match.
        // // Dazu das sleep stark reduzieren, am besten auf 50.

        // do {
        // Thread.sleep(50);
        // board = bs.boardShot().getRaster();

        // if (bs.dlgCorner.run(board) != null) {
        // int resign = bs.searchOppResign(board);
        // if (resign > 0) {
        // match.offerResign(1 - match.own, resign);
        // return res;
        // } else {
        // res.error = "Unerwarteter Dialog";
        // return res;
        // }
        // }

        // res.chequers.init(board);
        // res.chequers.getFields(newOwn, newOpp);
        // int oldOwnOff = matchCopy.getPlayer(match.own).field.getChequers(0);

        // if (oldOwnOff >= Field.NUM_ALL_CHEQUERS - 2 && newOwn.getChequers(0) == 0) {
        // // Gegner hat alle raus gespielt und nun Brett der naechsten Runde sichtbar.
        // // Suche zufaelligen Zug aus allMoves, der alle Steine raus spielt.
        // var winningMove = findWinningMove(matchCopy);
        // if (winningMove == null) {
        // throw new IllegalStateException(
        // "Feld erkannt, das nur in neuem Spiel auftreten kann, aber kein Gewinnzug
        // gefunden?!");
        // }

        // res.move = winningMove;
        // return res;
        // }

        // if (match.active != 1 - match.own) {
        // throw new IllegalStateException("Gegner gar nicht am Zug! (match.active=" +
        // match.active + ")");
        // }

        // res.chequers.init(board);
        // res.chequers.getFields(newOwn, newOpp);

        // int numMoves = match.allMoves.length();

        // for (int i = 0; i < numMoves; ++i) {
        // match.set(matchCopy);
        // MutableIntArray move = match.allMoves.at(i);
        // Move.run(match, move);

        // if (match.getPlayer(match.own).field.equals(newOwn)
        // && match.getPlayer(1 - match.own).field.equals(newOpp)) {
        // res.move = move;
        // System.out.println(move.append(new StringBuilder("Zug des Gegner erkannt:
        // ")));

        // // Falls mit Autowurf eigener Wurf bereits sichtbar: diesen im match setzen
        // if (spinRolls.isOwnDice()) {
        // res.match.roll(spinRolls.die1(), spinRolls.die2());
        // }

        // return res;
        // }
        // match.set(matchCopy);
        // }

        // // Wenn hier, dann ist es noch kein vollständiger gültiger Zug, warte weiter.

        // } while (true);

        // // END neu

        // // // BEGIN alt

        // // // Warten bis Bereit sichtbar, oder gegn. Wuerfel weg oder Dialog
        // sichtbar,
        // // oder
        // // // Initialpos erkannt. Dann einfach irgend einen Zug auswaehlen, der alle
        // // // restlichen Steine raus spielt.
        // // do {
        // // Thread.sleep(50);
        // // board = bs.boardShot().getRaster();
        // // if (bs.dlgCorner.run(board) != null) {
        // // int resign = bs.searchOppResign(board);
        // // if (resign > 0) {
        // // match.offerResign(1 - match.own, resign);
        // // return res;
        // // } else {
        // // res.error = "Unerwarteter Dialog";
        // // return res;
        // // }
        // // }

        // // if (bs.bereit.run(board) != null) {
        // // // Button Bereit sichtbar, d.h. Match zuende
        // // System.out.println("Button Bereit sichtbar, d.h. Match zuende");
        // // break;
        // // }

        // // res.chequers.init(board);
        // // res.chequers.getFields(newOwn, newOpp);
        // // int oldOwnOff = matchCopy.getPlayer(match.own).field.getChequers(0);

        // // if (oldOwnOff >= Field.NUM_ALL_CHEQUERS - 2 && newOwn.getChequers(0) == 0)
        // {
        // // // Gegner hat alle raus gespielt und nun Brett der naechsten Runde
        // sichtbar.
        // // // Suche zufaelligen Zug aus allMoves, der alle Steine raus spielt.
        // // var winningMove = findWinningMove(matchCopy);
        // // if (winningMove == null) {
        // // throw new IllegalStateException(
        // // "Feld erkannt, das nur in neuem Spiel auftreten kann, aber kein Gewinnzug
        // // gefunden?!");
        // // }

        // // res.move = winningMove;
        // // return res;
        // // }

        // // spinRolls.detectFromBoardShot(board);
        // // if ((!match.autoroll && !spinRolls.isOppDice() &&
        // !spinRolls.isInitialDice())
        // // || (match.autoroll && spinRolls.isOwnDice())) {
        // // System.out.println("verlasse do-while weil nicht mehr gegn. Wuerfel
        // // erkannt");
        // // break;
        // // }

        // // } while (true);
        // // // END alt

        // // if (match.active != 1 - match.own) {
        // // throw new IllegalStateException("Gegner gar nicht am Zug!");
        // // }

        // // res.chequers.init(board);
        // // res.chequers.getFields(newOwn, newOpp);

        // // int numMoves = allMoves.length();

        // // for (int i = 0; i < numMoves; ++i) {
        // // match.set(matchCopy);
        // // MutableIntArray move = allMoves.at(i);
        // // Move.run(match, move);

        // // if (match.getPlayer(match.own).field.equals(newOwn)
        // // && match.getPlayer(1 - match.own).field.equals(newOpp)) {
        // // res.move = move;
        // // System.out.println(move.append(new StringBuilder("Zug des Gegner erkannt:
        // // ")));

        // // // Falls mit Autowurf eigener Wurf bereits sichtbar: diesen im match
        // setzen
        // // if (spinRolls.isOwnDice()) {
        // // res.match.roll(spinRolls.die1(), spinRolls.die2());
        // // }

        // // return res;
        // // }
        // // }

        // // match.set(matchCopy);

        // // System.out.println("Zug des Gegners noch nicht erkannt");
        // // res.error = "Zug des Gegners nicht erkannt?!";
        // // return res;
    }

    private WaitForOppMoveRes waitForResignOrAnyMovePosition(WaitForOppMoveRes res) throws InterruptedException {
        Match match = state.match;
        Match matchCopy = state.matchCopy;
        Field newOwn = state.newOwn;
        Field newOpp = state.newOpp;

        do {
            Thread.sleep(50);
            var board = bs.boardShot();
            // var boardRaster = board.getRaster();

            if (ts.visible(ts.dlgCorner, board)) {
                int resign = ts.searchOppResign(board);
                if (resign > 0) {
                    match.offerResign(1 - match.own, resign);
                    return res;
                } else {
                    res.error = "Unerwarteter Dialog";
                    return res;
                }
            }

            chequers.init(board);
            chequers.getFields(newOwn, newOpp);

            int nMoves = state.allMoves.length();

            for (int i = 0; i < nMoves; ++i) {
                var move = state.allMoves.at(i);
                Move.run(match, move);
                if (match.getPlayer(match.own).field.equals(newOwn)
                        && match.getPlayer(1 - match.own).field.equals(newOpp)) {
                    res.move = move;
                    System.out.println(move.append(new StringBuilder("Zug des Gegner erkannt: ")));

                    // Falls mit Autowurf eigener Wurf bereits sichtbar: diesen im match setzen
                    if (spinRolls.isOwnDice()) {
                        match.roll(spinRolls.die1(), spinRolls.die2());
                    }

                    return res;
                } else {
                    match.set(matchCopy);
                }
            }

        } while (true);
    }

    private WaitForOppMoveRes waitForResignOrNewRollOrNewGameOrMatchEnd(WaitForOppMoveRes res)
            throws InterruptedException {

        // besserer Algorithmus wohl so:
        // 1. eine zeit lang warten

        Match match = state.match;
        Field newOwn = state.newOwn;
        Field newOpp = state.newOpp;
        if (match.active != 1 - match.own) {
            throw new IllegalStateException("WaitForOppMove executed, but match.active=" + match.active);
        }
        int oldOppOff = match.getPlayer(1 - match.own).field.getChequers(0);
        // TODO zeitaufwaendig, also erst nach warten auf eigenen wurf findWinningMove()
        // ausfuehren!
        var winningMove = findWinningMove();
        System.out.println("winningMove: " + (winningMove == null ? "null" : winningMove.append(null)));

        do {
            // so lange duerfte man nur warten, wenn sicher waere, dass es fuer jeden
            // moeglichen zug des gegners keinen
            // eigenen wurf danach gibt, bei dem wir nicht ziehen koennen. Sonst besteht
            // Risiko, dass eigener wurf waehrend
            // dieses sleeps verpasst wird!
            Thread.sleep(50); // das ist hier vor allem zu lang! ;-) - solange
            var board = bs.boardShot();
            // var boardRaster = board.getRaster();

            if (ts.visible(ts.dlgCorner, board)) {
                int resign = ts.searchOppResign(board);
                if (resign > 0) {
                    match.offerResign(1 - match.own, resign);
                    return res;
                } else {
                    res.error = "Unerwarteter Dialog";
                    return res;
                }
            }

            if (winningMove != null) {
                chequers.init(board);
                chequers.getFields(newOwn, newOpp);

                if (!(newOwn.numChequersAsExpected() && newOpp.numChequersAsExpected())) {
                    // Because the board is not updated atomically by Spin, there can be too many or
                    // too less chequers in a screenshot.
                    // This is rare, but possible. Then we simply take a new shot.
                    continue;
                }

                if (oldOppOff >= Field.NUM_ALL_CHEQUERS - 4 && newOpp.getChequers(0) == 0) {
                    // Gegner hat alle raus gespielt und nun Brett der naechsten Runde sichtbar.
                    // Suche zufaelligen Zug aus allMoves, der alle Steine raus spielt.

                    res.move = winningMove;
                    Move.run(match, winningMove);
                    return res;
                }
            }

            // TODO das zeitaufwendige bs.sideVerlassen.run() darf nur nach einer gewissen
            // zeit vorgenommen werden, wenn wirklich kein eigener wurf mehr kommt.
            // Sonst besteht Risiko, dass eigener Wurf verpasst wird, waehrend
            // bs.sideVerlassen.run läuft.

            spinRolls.detectFromBoardShot(board);
            if (spinRolls.isOwnDice() || (!spinRolls.isOppDice() && !spinRolls.isInitialDice())
                    || ts.visible(ts.bVerlassen, board)) {

                chequers.init(board);
                chequers.getFields(newOwn, newOpp);
                System.out.println("vor findMove: match.active  " + match.active);
                var foundMove = findMove(res);
                if (foundMove == null) {
                    // try again
                    System.out.println("no move found, try again");
                    continue;
                }

                res.move = foundMove;
                System.out.println(foundMove.append(new StringBuilder("Zug des Gegner erkannt: ")));

                // Falls mit Autowurf eigener Wurf bereits sichtbar: diesen im match setzen
                if (spinRolls.isOwnDice()) {
                    match.roll(spinRolls.die1(), spinRolls.die2());
                }

                return res;

            }

        } while (true);
    }

    /**
     * Sucht in state.allMoves nach einem Zug, der zu den Feldern state.newOwn bzw.
     * state.newOpp führt.
     * Falls so einer existiert, wird er auf state.match ausgeführt und in res.move
     * gesetzt.
     * Falls so ein Zug nicht existiert, enthält state.match nach der Funktion den
     * gleichen Zustand wie davor.
     * Temporär kann sich der Zustand von state.match aber auch dann ändern.
     * 
     * @return null, falls kein Zug gefunden; sonst der gefundene Zug
     */
    private MutableIntArray findMove(WaitForOppMoveRes res) {
        Match match = state.match;
        Match matchCopy = state.matchCopy;
        Field newOwn = state.newOwn;
        Field newOpp = state.newOpp;
        int nMoves = state.allMoves.length();
        matchCopy.set(match);

        for (int i = 0; i < nMoves; ++i) {
            var move = state.allMoves.at(i);
            Move.run(match, move);
            if (match.getPlayer(match.own).field.equals(newOwn)
                    && match.getPlayer(1 - match.own).field.equals(newOpp)) {
                return move;
            } else {
                match.set(matchCopy);
            }
        }

        if (savedFiles++ < 10) {
            WaitForOppMoveBug bug = new WaitForOppMoveBug();
            bug.match.set(match);
            bug.matchCopy.set(matchCopy);
            bug.newOpp.set(newOpp);
            bug.newOwn.set(newOwn);

            try {
                String bugFile = "WaitForOppMoveBug_" + Tools.dateTimeString();
                System.out.println("Save " + bugFile);
                ObjectOutputStream os = new ObjectOutputStream(
                        new FileOutputStream(bugFile));
                os.writeObject(bug);
                os.close();
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        } else {
            System.out.println("Max. of 10 files reached, no more files saved.");
        }

        return null;
        // throw new IllegalStateException("Aktuelle Position kann nicht mit gültigem
        // Zug erreicht werden?!");

    }

    public static MutableIntArray findWinningMove(WorkerState state) {
        Match match = state.match;
        Match matchCopy = state.matchCopy;
        int n = state.allMoves.length();
        matchCopy.set(match);

        for (int i = 0; i < n; ++i) {
            var move = state.allMoves.at(i);
            try {
                Move.run(match, move);
                if (match.finished() || match.gameStarting()) {
                    return move;
                }
            } finally {
                match.set(matchCopy);
            }
        }

        return null;

    }

    /**
     * returns match in original state, even though it does temporary changes.
     */
    private MutableIntArray findWinningMove() {
        Match match = state.match;
        Match matchCopy = state.matchCopy;
        int n = state.allMoves.length();
        matchCopy.set(match);

        for (int i = 0; i < n; ++i) {
            var move = state.allMoves.at(i);
            try {
                Move.run(match, move);
                if (match.finished() || match.gameStarting()) {
                    return move;
                }
            } finally {
                match.set(matchCopy);
            }
        }

        return null;
    }

}
