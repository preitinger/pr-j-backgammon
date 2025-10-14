package pr.backgammon.spin;

import java.awt.image.BufferedImage;
import java.awt.image.Raster;
import java.util.ArrayList;

import javax.swing.SwingWorker;

import pr.backgammon.gnubg.model.GnuCmd;
import pr.backgammon.model.Field;
import pr.backgammon.spin.control.BoardSearchers;
import pr.backgammon.spin.control.CalibrationForSpin;
import pr.backgammon.spin.control.FastChequerSearch;
import pr.backgammon.spin.control.SpinRolls;

@Deprecated
public class TrackBoard extends SwingWorker<Void, TrackBoardMsg> {
    private final CalibrationForSpin calWhite, calBlack;
    private FastChequerSearch fastChequerSearch;
    private BoardSearchers s;
    private SpinRolls spinRolls;
    private final ArrayList<GnuCmd> commands = new ArrayList<>();
    private Field fieldOwn = new Field(), fieldOpp = new Field();
    private Field lastFieldOwn = new Field(), lastFieldOpp = new Field();
    private int die1 = 0, die2 = 0;
    private BufferedImage board = null;
    private Raster boardRaster = null;
    private int pendingRedouble = 0;

    public TrackBoard(CalibrationForSpin calWhite, CalibrationForSpin calBlack) {
        if (calWhite != null && !calWhite.ownWhite) {
            throw new IllegalArgumentException("calWhite is for black!");
        }
        if (calBlack != null && calBlack.ownWhite) {
            throw new IllegalArgumentException("calBlack is for white!");
        }

        this.calWhite = calWhite;
        this.calBlack = calBlack;
    }

    @Override
    protected Void doInBackground() throws Exception {
        return null;

        // try {
        //     String[] names = { null, null };
        //     CalibrationForSpin cal = calWhite == null ? calBlack : calWhite;
        //     commands.clear();

        //     // TODO sowas aehnliches hier ausfuehren:
        //     // String fileName =
        //     // DateTimeFormatter.ofPattern("yyyy-MM-dd_HH-mm-ss").format(ZonedDateTime.now())
        //     // + "__"
        //     // + frame.getNameOpp() + "__" + frame.getNameOwn() + ".mat";
        //     // try {
        //     // fileWriter = new BufferedWriter(new FileWriter(fileName));
        //     // } catch (Exception ex) {
        //     // ex.printStackTrace();
        //     // JOptionPane.showMessageDialog(frame, "Konnte Datei nicht erzeugen: " +
        //     // fileName);
        //     // return;
        //     // }

        //     if (cal == null) {
        //         publish(TrackBoardMsg.msg("FEHLER: Keine Kalibrierung"));
        //         return null;
        //     }

        //     fastChequerSearch = new FastChequerSearch(cal);
        //     var tmpRect = new Rectangle();
        //     fastChequerSearch.boardScreenshotRect(tmpRect);
        //     s = new BoardSearchers(cal, tmpRect);
        //     spinRolls = new SpinRolls(cal, tmpRect);
        //     FirstAnalysisRes firstAnalysis = null;

        //     Thread.sleep(100);
        //     boardShot();
        //     firstAnalysis = FirstAnalysis.doIt(s, boardRaster);
        //     publish(TrackBoardMsg.msg("firstAnalysis returned " + firstAnalysis.type));

        //     // // BEGIN debug
        //     // ReadPlayerNames.runIt(s, boardRaster, names);
        //     // System.out.println("detected player names: white " + names[0] + " black " +
        //     // names[1]);
        //     // // END debug

        //     while (firstAnalysis.type == FirstAnalysisRes.Type.BOARD_NOT_FOUND
        //             || firstAnalysis.type == FirstAnalysisRes.Type.OTHERS_MATCH) {
        //         Thread.sleep(1000);
        //         boardShot();
        //         firstAnalysis = FirstAnalysis.doIt(s, boardRaster);
        //     }

        //     TrackBoardMsg.msg("final analysis: " + firstAnalysis.append(null));
        //     Point posBereit = null, posSideVerlassen = null;
        //     Point posResign = null;

        //     do {
        //         waitForCanLeave();

        //         boolean ownWhite = s.top12.run(boardRaster) != null;
        //         if (!ownWhite) {
        //             if (s.top24.run(boardRaster) == null) {
        //                 throw new IllegalStateException();
        //             }
        //         }
        //         System.out.println("ownWhite " + ownWhite);

        //         if ((ownWhite && cal != calWhite) || (!ownWhite && cal != calBlack)) {
        //             if (ownWhite) {
        //                 if (calWhite == null) {
        //                     throw new RuntimeException("Not calibrated for white, but needed now!");
        //                 }
        //                 cal = calWhite;
        //                 System.out.println("switched to white calibration");
        //             } else {
        //                 if (calBlack == null) {
        //                     throw new RuntimeException("Not calibrated for black, but needed now!");
        //                 }
        //                 cal = calBlack;
        //                 System.out.println("switched to black calibration");
        //             }

        //             fastChequerSearch = new FastChequerSearch(cal);
        //             tmpRect = new Rectangle();
        //             fastChequerSearch.boardScreenshotRect(tmpRect);
        //             s = new BoardSearchers(cal, tmpRect);
        //             spinRolls = new SpinRolls(cal, tmpRect);
        //         }

        //         // wait for CAN_LEAVE & !CAN_READY or !CAN_LEAVE

        //         do {
        //             Thread.sleep(1000);
        //             boardShot();
        //             posSideVerlassen = s.sideVerlassen.run(boardRaster);
        //             if (posSideVerlassen == null)
        //                 break;
        //             posBereit = s.bereit.run(boardRaster);
        //             if (posBereit == null)
        //                 break;
        //         } while (true);

        //         posResign = s.aufgeben.run(boardRaster);
        //         System.out.println("big do-while (end of body): posResign " + posResign + "  posSideVerlassen "
        //                 + posSideVerlassen + "  posBereit (evtl gar nicht berechnet) " + posBereit);
        //     } while (posSideVerlassen == null && posResign == null);

        //     System.out.println(
        //             "big do-while (just left): posResign " + posResign + "  posSideVerlassen " + posSideVerlassen
        //                     + "  posBereit (evtl gar nicht berechnet) " + posBereit);

        //     while (posResign == null && posSideVerlassen != null) {
        //         Thread.sleep(100);
        //         boardShot();
        //         posResign = s.sideAufgeben.run(boardRaster);
        //         posSideVerlassen = s.sideVerlassen.run(boardRaster);
        //     }

        //     System.out.println("after last while");

        //     spinRolls.detectFromBoardShot(boardRaster);
        //     spinRolls.dump();
        //     if (!spinRolls.isInitialDice()) {
        //         throw new IllegalStateException();
        //     }
        //     fields();
        //     if (!(fieldOwn.isInitial() && fieldOpp.isInitial())) {
        //         StringBuilder sb = new StringBuilder();
        //         sb.append("Own field:\n");
        //         fieldOwn.appendPosition(sb);
        //         sb.append("Opp field:\n");
        //         fieldOpp.appendPosition(sb);
        //     }
        //     die1 = spinRolls.die1();
        //     die2 = spinRolls.die2();
        //     System.out.println("detected initial board and initial roll " + die1 + " " + die2);
        //     System.out.println("UEBERNEHME MAUS in 2 s!");
        //     Thread.sleep(2000);
        //     ReadPlayerNames.runIt(s, boardRaster, names);
        //     String nameWhite = names[0];
        //     String nameBlack = names[1];
        //     String nameOwn = cal.ownWhite ? nameWhite : nameBlack;
        //     String nameOpp = cal.ownWhite ? nameBlack : nameWhite;
        //     System.out.println("detected player names: white " + names[0] + "   black " + names[1]);
        //     String chatText = ChatTextViaClipboard.runIt(100);
        //     msg("read chatText: '" + chatText + "'");
        //     Integer matchLen = searchMatchLen(chatText);
        //     addCmd(new GNewMatch(matchLen));
        //     if (nameOpp != null) {
        //         addCmd(new GSetPlayerName(0, nameOpp)); // in gnu commands ist immer player 0 der Gegner und player 1
        //                                                 // der eigene Spieler!
        //     }
        //     if (nameOwn != null) {
        //         addCmd(new GSetPlayerName(1, nameOwn));
        //     }
        //     addCmd(new GSetTurn(die1 > die2 ? 0 : 1));
        //     addCmd(new GSetBoardSimple(Field.getBoardSimple(fieldOwn, fieldOpp, null)));
        //     addCmd(new GSetDice(die1, die2));

        //     if (die2 > die1) {
        //         ownMove(true);
        //     } else {
        //         oppMove(true);
        //     }

        //     return null;
        // } catch (Exception ex) {
        //     ex.printStackTrace();
        //     throw ex;
        // }
    }

    private void fields() {
        fastChequerSearch.init(board);
        fastChequerSearch.getFields(fieldOwn, fieldOpp);
    }

    private void lastFields() {
        Field tmp = lastFieldOwn;
        lastFieldOwn = fieldOwn;
        fieldOwn = tmp;
        tmp = lastFieldOpp;
        lastFieldOpp = fieldOpp;
        fieldOpp = tmp;
    }

    private void boardShot() {
        this.board = s.boardShot();
        this.boardRaster = board.getRaster();

    }

    private static enum OwnMoveRes {
        GAME_END,
        REGULAR,
    }

    private OwnMoveRes ownMove(boolean initialRoll) throws InterruptedException {
        if (die1 == 0 || die2 == 0) {
            waitForOwnRoll();
        }
        assert (die1 >= 1 && die1 <= 6 && die2 >= 1 && die2 <= 6);
        fastChequerSearch.init(board);
        fastChequerSearch.getFields(fieldOwn, fieldOpp);

        // Warten bis der initiale Wurf oder die eigenen Wuerfel nicht mehr sichtbar
        // sind
        WaitUntilNotRollRes notRollRes;
        boolean pendingDoubleTake = false;

        do {
            if (initialRoll) {
                notRollRes = waitUntilNotInitialRoll();
            } else {
                notRollRes = waitUntilNotOwnRoll();
            }

            switch (notRollRes) {
                case MATCH_END:
                    return OwnMoveRes.GAME_END;
                case DLG: {
                    // if (s.verdoppelnOpp.run(boardRaster) != null) {
                    //     // Gegner bietet wahrscheinlich mit Auto-Doppeln Verdopplung an, bevor wir
                    //     // wissen, was unser eigener Wurf war.
                    //     // Also erst unsere Antwort auf das Doppeln ermitteln und dann wenn der Dialog
                    //     // nicht mehr die Steine verdeckt
                    //     // unseren Zug vor dem Doppeln ermitteln und diesen senden und anschließend das
                    //     // Doppeln und unsere Antwort darauf
                    //     switch (observeDoubleDlg()) {
                    //         case TAKE:
                    //             // Warten bis Dialogecke weg, dann Feld ermitteln dann Zug ermitteln, dann
                    //             // eigenen Zug, Double und Take senden
                    //             pendingDoubleTake = true;
                    //             // in naechstem Schleifendurchlauf wird das Warten auf das Ende des Wurfs fortgesetzt
                    //             // und dann hoffentlich bei
                    //             // case REGULAR fortgesetzt.
                    //             break;
                    //         case REDOUBLE:
                    //             ++pendingRedouble;
                    //             // detectAndSendLastMove();
                    //             // Die Antwort auf unser redouble wird dann im folgenden aufruf von opp
                    //             throw new RuntimeException("nyi");
                    //         case DROP:
                    //             // Wenn der eigene Spieler das schnelle Doppel des Gegners ablehnt, wird danach
                    //             // gleich das Brett fuer
                    //             // die naechste Runde mit Initialposition angezeigt.
                    //             // Also Platzhaltermove mit Liste aller moeglichen Zuege senden.
                    //             // In der GUI wird dann ein Zug ausgewaehlt bevor der Move geschrieben wird.
                    //             throw new RuntimeException("nyi");
                    //     }
                    // } /* else if () */
                    break;
                }
                case REGULAR:
                    // detectAndSendLastMove();
                    break;
            }
        } while (true);
    }

    private static enum DoubleDlgRes {
        TAKE,
        REDOUBLE,
        DROP
    }

    /**
     * Ermittelt welcher Button gedrueckt wird; stellt nicht sicher, dass danach der
     * Dialog bereits wieder unsichtbar ist.
     */
    DoubleDlgRes observeDoubleDlg() throws InterruptedException {
        throw new RuntimeException("Unsupported");
        // do {
        //     Thread.sleep(10);
        //     boardShot(); // TODO ggf. Optimierung notwendig durch kleineren Screenshot und dann angabe
        //                  // eines passenden clips im run
        //     if (s.annehmen.run(boardRaster) != null) {
        //         return DoubleDlgRes.TAKE;
        //     }
        //     if (s.verdoppeln.run(boardRaster) != null) {
        //         return DoubleDlgRes.REDOUBLE;
        //     }
        //     if (s.aufgeben.run(boardRaster) != null) {
        //         return DoubleDlgRes.DROP;
        //     }
        // } while (true);
    }

    private static enum WaitUntilNotRollRes {
        /**
         * Wenn Zeit ablaeuft, ist das moeglich. Dann ist der Button "Verlassen"
         * sichtbar.
         */
        MATCH_END,
        /**
         * Warten abgebrochen durch Erscheinen eines Dialogs. Kann Doppel-Dialog sein,
         * wenn Gegner Auto-Doppeln gesetzt hat.
         * Oder kann Aufgabedialog sein.
         * Falls es eine Doppelanfrage ist, kann der letzte eigene Zug erst bestimmt
         * werden, wenn der Dialog geschlossen wurde.
         */
        DLG,
        /**
         * spinRolls enthaelt neuen Wurf oder keinen Wurf, wenn Gegner nicht automatisch
         * wuerfelt.
         */
        REGULAR,
    }

    private WaitUntilNotRollRes waitUntilNotInitialRoll() throws InterruptedException {
        throw new RuntimeException("Unsupported");

        // // muss auch mit abgelaufener zeit zurecht kommen, also auch auf verlassen
        // // testen

        // do {
        //     Thread.sleep(500);
        //     boardShot();
        //     if (s.sideVerlassen.run(boardRaster) != null) {
        //         return WaitUntilNotRollRes.MATCH_END;
        //     }
        //     if (s.dlgCorner.run(boardRaster) != null) {
        //         return WaitUntilNotRollRes.DLG;
        //     }
        //     spinRolls.detectFromBoardShot(board);
        //     if (!spinRolls.isInitialDice())
        //         return WaitUntilNotRollRes.REGULAR;
        // } while (true);
    }

    private WaitUntilNotRollRes waitUntilNotOwnRoll() throws InterruptedException {
        throw new RuntimeException("Unsupported");

        // // muss auch mit abgelaufener zeit zurecht kommen, also auch auf verlassen
        // // testen

        // do {
        //     Thread.sleep(500);
        //     boardShot();
        //     if (s.sideVerlassen.run(boardRaster) != null) {
        //         return WaitUntilNotRollRes.MATCH_END;
        //     }
        //     if (s.dlgCorner.run(boardRaster) != null) {
        //         return WaitUntilNotRollRes.DLG;
        //     }
        //     spinRolls.detectFromBoardShot(board);
        //     if (!spinRolls.isOwnDice())
        //         return WaitUntilNotRollRes.REGULAR;
        // } while (true);

    }

    // private WaitUntilNotRollRes waitUntilNotOppRoll() throws InterruptedException {

    //     // muss auch mit abgelaufener zeit zurecht kommen, also auch auf verlassen
    //     // testen

    //     do {
    //         Thread.sleep(500);
    //         boardShot();
    //         if (s.sideVerlassen.run(boardRaster) != null) {
    //             return WaitUntilNotRollRes.MATCH_END;
    //         }
    //         if (s.dlgCorner.run(boardRaster) != null) {
    //             return WaitUntilNotRollRes.DLG;
    //         }
    //         spinRolls.detectFromBoardShot(board);
    //         if (!spinRolls.isOppDice())
    //             return WaitUntilNotRollRes.REGULAR;
    //     } while (true);

    // }

    private void oppMove(boolean initialRoll) {
        if (pendingRedouble > 0) {
            // Gegner kann...
            // - noch mal verdoppeln
            // - annehmen und seinen Zug machen
            // - ablehnen

        }
        throw new RuntimeException("nyi");

    }

    private void waitForOwnRoll() {
        throw new RuntimeException("nyi");
    }

    private void addCmd(GnuCmd cmd) {
        System.out.println("cmd: " + cmd);
        commands.add(cmd);
        msg(cmd.toString());
    }

    private void waitForCanLeave() throws InterruptedException {
        throw new RuntimeException("Unsupported");

        // do {
        //     boardShot();
        //     Point p = s.sideVerlassen.run(boardRaster);
        //     if (p == null) {
        //         Thread.sleep(1000);
        //     } else {
        //         return;
        //     }
        // } while (true);

    }

    private void msg(String text) {
        publish(TrackBoardMsg.msg(text));
    }

    private static Integer searchMatchLen(String chatText) {
        int pos = -1;
        final String searchedTraining = "[server]: Trainingsspiel über ";
        final String searchedRank = "[server]: Ranglistenspiel über ";

        int lastTraining = chatText.lastIndexOf('\n' + searchedTraining);
        int lastRank = chatText.lastIndexOf('\n' + searchedRank);
        if (lastTraining > lastRank) {
            pos = lastTraining + 1 + searchedTraining.length();
        } else if (lastRank > lastTraining) {
            pos = lastRank + 1 + searchedRank.length();
        } else if (chatText.startsWith(searchedTraining)) {
            pos = searchedTraining.length();
        } else if (chatText.startsWith(searchedRank)) {
            pos = searchedRank.length();
        }

        if (pos >= 0) {
            int end = pos + 1;
            int c;
            do {
                c = chatText.charAt(end);
                if (!(c >= '0' && c <= '9')) {
                    break;
                }
                ++end;
            } while (true);

            return Integer.parseInt(chatText, pos, end, 10);
        }

        return null;
    }
}
