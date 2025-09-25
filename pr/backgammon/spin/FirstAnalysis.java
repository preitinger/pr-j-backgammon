package pr.backgammon.spin;

import java.awt.image.Raster;

import pr.backgammon.spin.control.BoardSearchers;
import pr.backgammon.spin.control.FastChequerSearch;
import pr.backgammon.spin.control.SpinRolls;
import pr.control.MyWorker;

public abstract class FirstAnalysis extends MyWorker<FirstAnalysisRes, Void> {
    public FirstAnalysis(BoardSearchers searchers, FastChequerSearch chequers, SpinRolls spinRolls, java.awt.image.Raster raster) {
        this.s = searchers;
        this.chequers = chequers;
        this.spinRolls = spinRolls;
        this.raster = raster;
    }

    private final BoardSearchers s;
    private final Raster raster;
    private final FastChequerSearch chequers;
    private final SpinRolls spinRolls;

    public static FirstAnalysisRes doIt(BoardSearchers s, FastChequerSearch chequers, SpinRolls spinRolls, Raster raster) {
        return null;
        
        // Searcher[][] simpleCases = {
        //         { s.enterWhite, s.enterBlack },
        //         { s.bereit },
        //         { s.dlgCorner },
        //         { s.sideVerlassen },

        // };
        // FirstAnalysisRes.Type[] simpleTypes = {
        //         FirstAnalysisRes.Type.CAN_ENTER,
        //         FirstAnalysisRes.Type.CAN_READY,
        //         FirstAnalysisRes.Type.DIALOG_OPEN,
        //         FirstAnalysisRes.Type.CAN_LEAVE,
        // };

        // assert (simpleCases.length == simpleTypes.length);

        // Point top12Found = s.top12.run(raster);
        // Point top24Found = s.top24.run(raster);

        // boolean calWhite = false;

        // if (top12Found == null) {
        //     if (top24Found == null) {
        //         return new FirstAnalysisRes(FirstAnalysisRes.Type.BOARD_NOT_FOUND, calWhite);
        //     } else {
        //         calWhite = false;
        //     }
        // } else {
        //     if (top24Found != null) {
        //         calWhite = top12Found.y < top24Found.y;
        //     } else {
        //         calWhite = true;
        //     }
        // }

        // for (int i = 0; i < simpleCases.length; ++i) {
        //     for (Searcher searcher : simpleCases[i]) {
        //         Point found = searcher.run(raster);
        //         if (searcher == s.sideVerlassen) {
        //             System.out.println("found for sideVerlassen: " + found);
        //         }
        //         if (found != null) {
        //             return new FirstAnalysisRes(simpleTypes[i], calWhite);
        //         }
        //     }
        // }

        // int[] chequersAsForMatchDebug = null;
        // Roll initialRoll = null;
        // Roll ownRoll = null;
        // Roll oppRoll = null;
        // int cubeVal = 1;

        // if (s.cal.ownWhite == calWhite) {
        //     // Kalibrierung passt fuer erkanntes Brett
        //     chequers.init(raster);
        //     chequersAsForMatchDebug = new int[26 * 2];
        //     for (int field = 24; field >= 1; --field) {
        //         pr.backgammon.spin.Player player = chequers.playerOnField(field);
        //         chequersAsForMatchDebug[field * 2] = field;
        //         chequersAsForMatchDebug[field * 2 + 1] = chequers.numChequersOnField(field)
        //                 * (player == pr.backgammon.spin.Player.OTHER ? -1 : 1);
        //     }

        //     spinRolls.detectFromBoardShot(raster);
        //     initialRoll = spinRolls.isInitialDice() ? new Roll(spinRolls.die1(), spinRolls.die2()) : null;
        //     ownRoll = spinRolls.isOwnDice() ? new Roll(spinRolls.die1(), spinRolls.die2()) : null;
        //     oppRoll = spinRolls.isOppDice() ? new Roll(spinRolls.die1(), spinRolls.die2()) : null;
        //     int midY = chequers.midY();

        //     Searcher[] cubeSearchers = { s.cube2, s.cube4, s.cube8, s.cube16, s.cube32 };
        //     for (int i = 0; i < cubeSearchers.length; ++i) {
        //         Point pos = cubeSearchers[i].run(raster);
        //         int baseVal = 2 << i;
        //         if (pos != null) {
        //             if (pos.y < midY) {
        //                 cubeVal = -baseVal;
        //             } else {
        //                 cubeVal = baseVal;
        //             }
        //             break;
        //         }
        //     }

        // }

        // // check if own match
        // if (s.sideAufgeben.run(raster) != null) {
        //     // Button "Aufgeben am rechten Rand sichtbar, d.h. wir spielen selber".
        //     // Falls wir zuschauen wuerden, waere der Button nicht sichtbar
        //     return new FirstAnalysisRes(FirstAnalysisRes.Type.OWN_MATCH, calWhite, chequersAsForMatchDebug, initialRoll,
        //             ownRoll,
        //             oppRoll,
        //             cubeVal);
        // }

        // return new FirstAnalysisRes(FirstAnalysisRes.Type.OTHERS_MATCH, calWhite, chequersAsForMatchDebug, initialRoll,
        //         ownRoll,
        //         oppRoll,
        //         cubeVal);

    }

    @Override
    public FirstAnalysisRes doIt() throws Exception {
        return doIt(s, chequers, spinRolls, raster);
    }

}
