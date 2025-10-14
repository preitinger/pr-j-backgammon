package pr.backgammon.spin.control;

import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.io.IOException;

import pr.control.MyRobot;
import pr.control.Searcher;

public class BoardSearchers {
    public final CalibrationForSpin cal;
    private final Rectangle boardScreenshotRect;
    public final Searcher 
    // bereit, dlgCorner, resignFromOwn1, resignFromOwn2, resignFromOwn3, resignFromOpp1,
    //         resignFromOppLong1,
    //         resignFromOpp2, resignFromOppLong2,
    //         resignFromOpp3, resignFromOppLong3, ja, nein, // ja, nein als runtergedrueckte Buttons
    //         annehmen, verdoppeln, aufgeben, // als runtergedrueckte Buttons
    //         jaUnpressed, neinUnpressed/* , annehmenUnpressed */, verdoppelnUnpressed/* , aufgebenUnpressed */,
    //         verdoppelnOpp, verdoppelnOppLong, // in Ueberschrift, wenn der Gegner doppelt (oder zurueckdoppelt).
    //         // cubeEmpty, cube2, cube4, cube8, cube16, cube32, cube64,
    //         sideVerlassen, sideAufgeben,
    //         // enterWhite, enterBlack,
            top12, // means ownWhite
            top24, // means ownBlack
            // playerBoxWhite, playerBoxBlack, playerBoxWhiteReady, playerBoxBlackReady,
            autorollSelected, autorollDeselected,
            statusHasRejected
            // , resignFromOwn1Unpressed, resignFromOwn2Unpressed, resignFromOwn3Unpressed
            ;

    // public final FastChequerSearch chequers;
    // public final SpinRolls spinRolls;
    // public final CalibrationForSpin cal;

    public BoardSearchers(CalibrationForSpin cal, Rectangle boardScreenshotRect) throws IOException {
        this.cal = cal;
        this.boardScreenshotRect = new Rectangle(boardScreenshotRect);
        System.out.println("boardScreenshotRect:  " + boardScreenshotRect);
        // Rectangle r = cal.boardScreenshotRect(null);
        // System.out.println("r " + r);
        // boardScreenshotRect: java.awt.Rectangle[x=401,y=295,width=1088,height=574]

        // bereit = Searcher.create("bereit", null,
        //         new Rectangle(10, 10, boardScreenshotRect.width - 20, boardScreenshotRect.height - 20), 2);

        // // TODO clipToSearchIn
        // dlgCorner = Searcher.create("dlgCorner", null, null, 2);

        // // TODO clipToSearchIn
        // resignFromOwn1 = Searcher.create("resignFromOwn1", null, null, 2);

        // // TODO clipToSearchIn
        // resignFromOwn2 = Searcher.create("resignFromOwn2", null, null, 2);

        // // TODO clipToSearchIn
        // resignFromOwn3 = Searcher.create("resignFromOwn3", null, null, 2);

        // // TODO clipToSearchIn
        // resignFromOpp1 = Searcher.create("resignFromOpp1", null, null, 2);
        // resignFromOppLong1 = Searcher.create("resignFromOppLong1", null, null, 2);

        // // TODO clipToSearchIn
        // resignFromOpp2 = Searcher.create("resignFromOpp2", null, null, 2);
        // resignFromOppLong2 = Searcher.create("resignFromOppLong2", null, null, 2);

        // // TODO clipToSearchIn
        // resignFromOpp3 = Searcher.create("resignFromOpp3", null, null, 2);
        // resignFromOppLong3 = Searcher.create("resignFromOppLong3", null, null, 2);

        // // TODO clipToSearchIn
        // ja = Searcher.create("ja", null, new Rectangle(353, 318, 13, 14), 2);

        // // TODO clipToSearchIn
        // nein = Searcher.create("nein", null, new Rectangle(431, 318, 30, 14), 2);

        // // TODO clipToSearchIn
        // annehmen = Searcher.create("annehmen", null, null, 2);

        // // TODO clipToSearchIn
        // verdoppeln = Searcher.create("verdoppeln", null, null, 2);

        // // TODO clipToSearchIn
        // aufgeben = Searcher.create("aufgeben", null, null, 2);

        // jaUnpressed = Searcher.create("jaUnpressed", null, null, 2);
        // neinUnpressed = Searcher.create("neinUnpressed", null, null, 2);
        // // annehmenUnpressed = Searcher.create("annehmenUnpressed", null, null, 2);
        // verdoppelnUnpressed = Searcher.create("verdoppelnUnpressed", null, null, 2);
        // // aufgebenUnpressed = Searcher.create("aufgebenUnpressed", null, null, 2);

        // // TODO clipToSearchIn
        // verdoppelnOpp = Searcher.create("verdoppelnOpp", null, null, 2);
        // verdoppelnOppLong = Searcher.create("verdoppelnOppLong", null, null, 2);

        // // cubeEmpty = Searcher.create("cubeEmpty", null, null, 2);

        // // // TODO clipToSearchIn
        // // cube2 = Searcher.create("cube2", null, null, 2);

        // // // TODO clipToSearchIn
        // // cube4 = Searcher.create("cube4", null, null, 2);

        // // // TODO clipToSearchIn
        // // cube8 = Searcher.create("cube8", null, null, 2);

        // // // TODO clipToSearchIn
        // // cube16 = Searcher.create("cube16", null, null, 2);

        // // // TODO clipToSearchIn
        // // cube32 = Searcher.create("cube32", null, null, 2);

        // // cube64 = Searcher.create("cube64", null, null, 2);

        // sideVerlassen = Searcher.create("sideVerlassen", null, null, 2);

        // sideAufgeben = Searcher.create("sideAufgeben", null,
        //         /* new Rectangle(1340 - 401, 609 - 295, 1390 - 1340, 690 - 609) */ null, 2);

        // enterWhite = Searcher.create("enterWhite", null, null, 2);

        // enterBlack = Searcher.create("enterWhite", null, null, 2);

        top12 = Searcher.create("top12", null, null, 2);

        top24 = Searcher.create("top24", null, null, 2);

        // playerBoxWhite = Searcher.create("playerBoxWhite", null,
        //         new Rectangle(1337 - 401, 304 - 295, 19, 44), 2);
        // playerBoxBlack = Searcher.create("playerBoxBlack", null,
        //         new Rectangle(1337 - 401, 304 - 295, 19, 44), 2);
        // // playerBoxWhiteReady = Searcher.create("playerBoxWhiteReady", null,
        // // new Rectangle(1337 - boardScreenshotRect.x, 304 - boardScreenshotRect.y, 19,
        // // 44), 2);
        // // playerBoxBlackReady = Searcher.create("playerBoxBlackReady", null,
        // // new Rectangle(1337 - boardScreenshotRect.x, 304 - boardScreenshotRect.y, 19,
        // // 44), 2);
        // playerBoxWhiteReady = Searcher.create("playerBoxWhiteReady", null,
        //         /* new Rectangle(934, 6, 20 + 8, 40 + 8) */ null, 2);
        // playerBoxBlackReady = Searcher.create("playerBoxBlackReady", null,
        //         /* new Rectangle(934, 6, 20 + 8, 40 + 10) */ null, 2);

        autorollSelected = Searcher.create("autorollSelected", null, null, 2);
        autorollDeselected = Searcher.create("autorollDeselected", null, null, 2);
        statusHasRejected = Searcher.create("statusHasRejected", null,
                new Rectangle(828 - 401, 885 - 295, 1228 - 828, 907 - 885), 2);
        // resignFromOwn1Unpressed = Searcher.create("resignFromOwn1Unpressed", null,
        //         /* new Rectangle(2790 - 1920 - 401, 550 - 295, 2839 - 2790, 573 - 550) */ null, 2);
        // resignFromOwn2Unpressed = Searcher.create("resignFromOwn2Unpressed", null,
        //         /* new Rectangle(2806 - 1920 - 401, 577 - 295, 2863 - 2806, 594 - 577) */ null, 2);
        // resignFromOwn3Unpressed = Searcher.create("resignFromOwn3Unpressed", null,
        //         /* new Rectangle(2795 - 1920 - 401, 598 - 295, 2858 - 2795, 614 - 598) */ null, 2);

    }

    // public int searchOppResign(Raster board) {
    //     if (this.resignFromOpp1.run(board) != null || this.resignFromOppLong1.run(board) != null) {
    //         return 1;
    //     } else if (this.resignFromOpp2.run(board) != null || this.resignFromOppLong2.run(board) != null) {
    //         return 2;
    //     } else if (this.resignFromOpp3.run(board) != null || this.resignFromOppLong3.run(board) != null) {
    //         return 3;
    //     }

    //     return 0;
    // }

    // public Searcher cubeSearcher(int cubeVal) {
    //     switch (cubeVal) {
    //         case 1:
    //             return cubeEmpty;
    //         case 2:
    //             return cube2;
    //         case 4:
    //             return cube4;
    //         case 8:
    //             return cube8;
    //         case 16:
    //             return cube16;
    //         case 32:
    //             return cube32;
    //         default:
    //             return null;
    //     }
    // }

    /**
     * Absolutely forbidden to change the returned rectangle.
     * For efficiency, it is not copied.
     */
    public Rectangle boardRect() {
        return boardScreenshotRect;
    }

    // public static class FirstAnalysisRes {
    // public static enum Type {
    // /**
    // * weder top12 noch top24 gefunden
    // */
    // BOARD_NOT_FOUND,

    // /**
    // * Can enter as white or black or any of them, i.e. at least one of the enter
    // * chequers is visible.
    // */
    // CAN_ENTER,

    // /**
    // * Ready button is visible and can be clicked
    // */
    // CAN_READY,

    // /**
    // * Irgendein Spieldialog ist gerade offen (Aufgabe oder Doppeln, hier egal).
    // * Das heißt nämlich, dass das Brett noch nicht analysiert werden kann, da
    // * Chequer verdeckt sein können.
    // */
    // DIALOG_OPEN,

    // /**
    // * Button `Verlassen` visible, d.h. zwar bereit, aber Gegner noch nicht.
    // * Sonst haette CAN_READY Vorrang vor CAN_LEAVE
    // */
    // CAN_LEAVE,

    // /**
    // * Match aktiv, und wir spielen mit.
    // */
    // OWN_MATCH,

    // /**
    // * Wenn weder der Button `Verlassen` noch `Aufgeben` noch der Farbdialog
    // * sichtbar ist, bleibt nur
    // * die letzte Möglichkeit, dass 2 andere spielen und wir nur zusehen.
    // */
    // OTHERS_MATCH
    // }

    // public final Type type;
    // /**
    // * if true, calibration for white is needed to continue, otherwise calibration
    // * for black
    // */
    // public final boolean calWhite;
    // public final int[] chequersAsForMatchDebug;
    // public final Roll initialRoll, ownRoll, oppRoll;
    // /**
    // * Initial: 1. Im eigenen Besitz: > 1. Im gegnerischen Besitz: < -1.
    // */
    // public final int cubeVal;

    // FirstAnalysisRes(Type type, boolean calWhite) {
    // this.type = type;
    // this.calWhite = calWhite;
    // chequersAsForMatchDebug = null;
    // this.initialRoll = null;
    // this.ownRoll = null;
    // this.oppRoll = null;
    // this.cubeVal = 1;
    // }

    // public FirstAnalysisRes(Type type, boolean calWhite, int[]
    // chequersAsForMatchDebug, Roll initialRoll,
    // Roll ownRoll,
    // Roll oppRoll,
    // int cubeVal) {
    // this.type = type;
    // this.calWhite = calWhite;
    // this.chequersAsForMatchDebug = chequersAsForMatchDebug;
    // this.initialRoll = initialRoll;
    // this.ownRoll = ownRoll;
    // this.oppRoll = oppRoll;
    // this.cubeVal = cubeVal;
    // }

    // public StringBuilder append(StringBuilder sb) {
    // if (sb == null) {
    // sb = new StringBuilder();
    // }
    // sb.append(type.toString()).append('\n');
    // if (chequersAsForMatchDebug != null) {
    // Field tmpField = new Field();
    // tmpField.debugField(chequersAsForMatchDebug);
    // tmpField.appendPosition(sb);
    // }
    // appendRoll(sb, "initial roll", initialRoll);
    // appendRoll(sb, "opp roll", oppRoll);
    // appendRoll(sb, "own roll", ownRoll);
    // return sb;
    // }

    // private void appendRoll(StringBuilder sb, String name, Roll roll) {
    // if (roll != null) {
    // sb.append(name).append("
    // ").append(roll.die1()).append(roll.die2()).append('\n');
    // }
    // }
    // }

    // /**
    // * raster muss von einem Screenshot fuer das Rechteck von
    // * CalibrationForSpin.boardScreenshotRect() sein
    // */
    // FirstAnalysisRes firstAnalysis(java.awt.image.Raster raster) {
    // Searcher[][] simpleCases = {
    // { enterWhite, enterBlack },
    // { bereit },
    // { dlgCorner },
    // { sideVerlassen },

    // };
    // FirstAnalysisRes.Type[] simpleTypes = {
    // FirstAnalysisRes.Type.CAN_ENTER,
    // FirstAnalysisRes.Type.CAN_READY,
    // FirstAnalysisRes.Type.DIALOG_OPEN,
    // FirstAnalysisRes.Type.CAN_LEAVE,
    // };

    // assert (simpleCases.length == simpleTypes.length);

    // Point top12Found = top12.run(raster);
    // Point top24Found = top24.run(raster);

    // boolean calWhite = false;

    // if (top12Found == null) {
    // if (top24Found == null) {
    // return new FirstAnalysisRes(FirstAnalysisRes.Type.BOARD_NOT_FOUND, calWhite);
    // } else {
    // calWhite = false;
    // }
    // } else {
    // if (top24Found != null) {
    // calWhite = top12Found.y < top24Found.y;
    // } else {
    // calWhite = true;
    // }
    // }

    // for (int i = 0; i < simpleCases.length; ++i) {
    // for (Searcher searcher : simpleCases[i]) {
    // Point found = searcher.run(raster);
    // if (searcher == sideVerlassen) {
    // System.out.println("found for sideVerlassen: " + found);
    // }
    // if (found != null) {
    // return new FirstAnalysisRes(simpleTypes[i], calWhite);
    // }
    // }
    // }

    // int[] chequersAsForMatchDebug = null;
    // Roll initialRoll = null;
    // Roll ownRoll = null;
    // Roll oppRoll = null;
    // int cubeVal = 1;

    // if (this.cal.ownWhite == calWhite) {
    // // Kalibrierung passt fuer erkanntes Brett
    // chequers.init(raster);
    // chequersAsForMatchDebug = new int[26 * 2];
    // for (int field = 24; field >= 1; --field) {
    // pr.backgammon.spin.Player player = chequers.playerOnField(field);
    // chequersAsForMatchDebug[field * 2] = field;
    // chequersAsForMatchDebug[field * 2 + 1] = chequers.numChequersOnField(field)
    // * (player == pr.backgammon.spin.Player.OTHER ? -1 : 1);
    // }

    // spinRolls.detectFromBoardShot(raster);
    // initialRoll = spinRolls.isInitialDice() ? new Roll(spinRolls.die1(),
    // spinRolls.die2()) : null;
    // ownRoll = spinRolls.isOwnDice() ? new Roll(spinRolls.die1(),
    // spinRolls.die2()) : null;
    // oppRoll = spinRolls.isOppDice() ? new Roll(spinRolls.die1(),
    // spinRolls.die2()) : null;
    // int midY = chequers.midY();

    // Searcher[] cubeSearchers = { cube2, cube4, cube8, cube16, cube32 };
    // for (int i = 0; i < cubeSearchers.length; ++i) {
    // Point pos = cubeSearchers[i].run(raster);
    // int baseVal = 2 << i;
    // if (pos != null) {
    // if (pos.y < midY) {
    // cubeVal = -baseVal;
    // } else {
    // cubeVal = baseVal;
    // }
    // break;
    // }
    // }

    // }

    // // check if own match
    // if (sideAufgeben.run(raster) != null) {
    // // Button "Aufgeben am rechten Rand sichtbar, d.h. wir spielen selber".
    // // Falls wir zuschauen wuerden, waere der Button nicht sichtbar
    // return new FirstAnalysisRes(FirstAnalysisRes.Type.OWN_MATCH, calWhite,
    // chequersAsForMatchDebug, initialRoll,
    // ownRoll,
    // oppRoll,
    // cubeVal);
    // }

    // return new FirstAnalysisRes(FirstAnalysisRes.Type.OTHERS_MATCH, calWhite,
    // chequersAsForMatchDebug, initialRoll,
    // ownRoll,
    // oppRoll,
    // cubeVal);
    // }

    public static enum ResignResponse {
        TOO_FAST,
        YES,
        NO
    }

    // /**
    //  * Für die Situation, wenn der Gegner dem eigenen Spieler Aufgabe anbietet,
    //  * warten bis entweder `ja` erkannt, oder `nein` erkannt oder verschwundenes
    //  * `dlgCorner` erkannt
    //  */
    // public ResignResponse waitForResignResponse() throws InterruptedException {
    //     Rectangle origBoardShot = boardScreenshotRect;
    //     Rectangle shotRect = new Rectangle(origBoardShot.x + 328, origBoardShot.y + 217, 133, 115); // Rechteck
    //                                                                                                 // optimiert, da es
    //                                                                                                 // hier wirklich
    //                                                                                                 // schnell gehen
    //     // sollte, damit der Klick auf Ja oder Nein nicht
    //     // verpasst wird!

    //     Rectangle clipDlgCorner = new Rectangle(328 - (shotRect.x - origBoardShot.x),
    //             217 - (shotRect.y - origBoardShot.y), 5, 4);
    //     Rectangle clipJa = new Rectangle(353 - (shotRect.x - origBoardShot.x), 318 - (shotRect.y - origBoardShot.y), 13,
    //             14);
    //     Rectangle clipNein = new Rectangle(431 - (shotRect.x - origBoardShot.x), 318 - (shotRect.y - origBoardShot.y),
    //             30, 14);
    //     BufferedImage shot = null;
    //     java.awt.image.Raster raster = null;

    //     {
    //         // only for debugging
    //         shot = MyRobot.shot(shotRect);
    //         raster = shot.getRaster();
    //         Point found = resignFromOpp1.run(raster);
    //         int resign = 0;
    //         if (found != null) {
    //             resign = 1;
    //         } else if (resignFromOpp2.run(raster) != null) {
    //             resign = 2;
    //         } else if (resignFromOpp3.run(raster) != null) {
    //             resign = 3;
    //         }
    //         System.out.println("Btw, resign is " + resign);
    //     }

    //     while (true) {
    //         Thread.sleep(10);
    //         // dlgCorner.run
    //         // dlgCorner bei jeder Aufgabevariante: 328, 217, 5x4
    //         // ja:

    //         /*
    //          * ja: 353,318, 13x14
    //          * nein: 431,318, 30x14
    //          */

    //         // bounding screenshot: 328, 217 461, 332 (133, 115)
    //         shot = MyRobot.shot(shotRect);
    //         raster = shot.getRaster();
    //         Point foundJa = ja.run(raster, clipJa);
    //         if (foundJa != null)
    //             return ResignResponse.YES;

    //         Point foundNein = nein.run(raster, clipNein);
    //         if (foundNein != null)
    //             return ResignResponse.NO;

    //         Point foundDlgCorner = dlgCorner.run(raster, clipDlgCorner);
    //         if (foundDlgCorner == null)
    //             return ResignResponse.TOO_FAST;

    //     }
    // }

    public static enum AnalyzeDialogRes {
        /**
         * dialog disappeared without seeing which button was clicked
         */
        TOO_FAST,
        TAKE,
        REDOUBLE,
        DROP,
        RESIGN_OWN_1,
        RESIGN_OWN_2,
        RESIGN_OWN_3,
        RESIGN_OPP_REJECTED,
        RESIGN_OPP_1_ACCEPTED,
        RESIGN_OPP_2_ACCEPTED,
        RESIGN_OPP_3_ACCEPTED,
    }

    // public AnalyzeDialogRes analyzeDialog(Raster boardRaster) {
    //     int resign = 0;
    //     Point pos = resignFromOpp1.run(boardRaster);
    //     if (pos != null) {
    //         resign = -1;
    //     } else {
    //         pos = resignFromOpp2.run(boardRaster);
    //         if (pos != null) {
    //             resign = -2;
    //         } else {
    //             pos = resignFromOpp3.run(boardRaster);
    //             if (pos != null) {
    //                 resign = -3;
    //             }
    //         }
    //     }
    //     if (resign != 0) {
    //         // warten auf Klick auf Ja oder Nein
    //     } else {
    //         // pruefen ob verdoppelnOpp sichtbar
    //         // Dann schnell warten auf annehmen oder verdoppeln oder aufgeben
    //         // schon implementiert: waitForResignResponse(r)

    //         // Sonst ausgehen von eigener Aufgabe und schnell warten bis resignFromOwn1 bis
    //         // resignFromOwn3
    //     }

    //     throw new RuntimeException("nyi");

    // }

    public BufferedImage boardShot() {
        return MyRobot.shot(boardScreenshotRect);

    }
}
