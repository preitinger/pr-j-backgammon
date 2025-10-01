package pr.backgammon.spin;

import pr.backgammon.spin.control.BoardSearchers;
import pr.control.MyWorker;

/**
 * @deprecated
 * Fuer die Situation, wenn der eigene Spieler am Zug ist und folgende Optionen
 * hat:
 * 1. Zug machen, d.h. Steine bewegen
 * 2. Aufgeben
 * 3. Auf Aufgabe des Gegners reagieren
 */
@Deprecated
public abstract class WaitForOwnMove extends MyWorker<WaitForOwnMoveRes, Void> {
    private final BoardSearchers s;

    public WaitForOwnMove(BoardSearchers s) {
        this.s = s;
    }

    @Override
    public WaitForOwnMoveRes doIt() throws Exception {

        // Point dlgCornerPos = null;
        // Roll newRoll = null;
        // Raster boardRaster = null;

        // do {
        //     Thread.sleep(100);
        //     BufferedImage boardShot = s.boardShot();
        //     boardRaster = boardShot.getRaster();
        //     dlgCornerPos = s.dlgCorner.run(boardRaster);
        //     if (dlgCornerPos == null) {
        //         s.spinRolls.detectFromBoardShot(boardRaster);
        //         if (s.spinRolls.isOppDice()) {
        //             newRoll = new Roll(s.spinRolls.die1(), s.spinRolls.die2());
        //         }
        //     }
        // } while (!isCancelled() && dlgCornerPos == null && newRoll == null);

        // if (dlgCornerPos != null) {
        //     return new WaitForOwnMoveRes(WaitForOwnMoveResType.DLG, null);
        // } else {
        //     // TODO detect which move has been done using chequers, findAllMoves whatsoever
        //     throw new RuntimeException("nyi");
        // }
        throw new RuntimeException("nyi because deprecated");
    }
}
