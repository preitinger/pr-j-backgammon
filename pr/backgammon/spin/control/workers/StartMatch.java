package pr.backgammon.spin.control.workers;

import java.awt.Point;
import java.awt.Rectangle;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.image.Raster;
import java.io.IOException;

import pr.backgammon.model.Match;
import pr.backgammon.spin.control.BoardSearchers;
import pr.backgammon.spin.control.CalibrationForSpin;
import pr.backgammon.spin.control.ScanUtils;
import pr.backgammon.spin.control.SpinRolls;
import pr.control.MyWorker;

public abstract class StartMatch extends MyWorker<StartMatchRes, Void> {
    private final CalibrationForSpin calWhite, calBlack;
    private final BoardSearchers s;

    public StartMatch(CalibrationForSpin calWhite, CalibrationForSpin calBlack, BoardSearchers s) {
        this.calWhite = calWhite;
        this.calBlack = calBlack;
        this.s = s;
    }

    public static StartMatchRes runIt(CalibrationForSpin calWhite, CalibrationForSpin calBlack, BoardSearchers s)
            throws InterruptedException, IOException {
        StartMatchRes res = new StartMatchRes();

        Thread.sleep(1000);

        final Rectangle boardRect = s.boardRect();

        int boardx = boardRect.x;
        int boardy = boardRect.y;
        Raster board = s.boardShot().getRaster();

        // Farbe des eigenen Spielers ermitteln

        if (s.top12.run(board) != null) {
            res.cal = calWhite;
        } else if (s.top24.run(board) != null) {
            res.cal = calBlack;
        } else {
            res.error = "Brett prüfen!";
            return res;
        }

        res.bs = new BoardSearchers(res.cal, boardRect);
        res.spinRolls = new SpinRolls(res.cal, boardRect);

        if (!s.bereit.runAndClick(board, boardx, boardy)) {
            try {
                scanMatch(s, res, board);
                res.match.active = -1;
                res.match.roll.setEmpty();
                return res;
            } catch (Exception ex) {
                ex.printStackTrace();
                res.error = ex.getMessage();
                return res;
            }
        }

        // Neuer Ablauf:
        // 1. Warten bis beide Spieler bereit sind.
        // 2. Eine halbe Sek. warten.
        // 3. Warten bis initialer Wurf vorliegt (andere einfach ignorieren)

        // BEGIN NEU

        do {
            Thread.sleep(500);
            board = s.boardShot().getRaster();
            Point posWhite = res.bs.playerBoxWhiteReady.run(board, null);
            Point posBlack = res.bs.playerBoxBlackReady.run(board, null);

            if (res.bs.playerBoxWhiteReady.run(board, null) != null
                    && res.bs.playerBoxBlackReady.run(board, null) != null) {
                System.out.println("posWhite  " + posWhite);
                System.out.println("posBlack  " + posBlack);
                break;
            }

            System.out.println("Not all players ready, wait on.");
        } while (true);

        do {
            Thread.sleep(500);
            board = s.boardShot().getRaster();
            res.spinRolls.detectFromBoardShot(board);
            if (res.spinRolls.isInitialDice()) {
                try {

                    scanMatch(s, res, board);

                    if (res.match.roll.die1 > res.match.roll.die2) {
                        res.match.active = 0;
                    } else {
                        res.match.active = 1;
                    }
                    res.match.initialRoll = true;
                    return res;
                } catch (Exception ex) {
                    ex.printStackTrace();
                    res.error = ex.getMessage();
                    return res;
                }
            }
        } while (true);

        // END NEU

        // // Warten, bis Gegner auch bereit ist und initialer Wurf vorliegt.

        // do {
        // Thread.sleep(500);
        // board = s.boardShot().getRaster();
        // res.spinRolls.detectFromBoardShot(board);
        // {
        // Point posWhite = res.bs.playerBoxWhiteReady.run(board, null);
        // Point posBlack = res.bs.playerBoxBlackReady.run(board, null);

        // if (res.bs.playerBoxWhiteReady.run(board, null) == null
        // || res.bs.playerBoxBlackReady.run(board, null) == null) {
        // System.out.println("Not all players ready, wait on.");
        // continue;
        // }

        // System.out.println("posWhite " + posWhite);
        // System.out.println("posBlack " + posBlack);
        // }

        // if (res.spinRolls.isInitialDice()) {
        // String[] playerNames = { null, null };
        // ReadPlayerNames.runIt(res.bs, board, playerNames);
        // if (res.cal.ownWhite) {
        // String tmp = playerNames[0];
        // playerNames[0] = playerNames[1];
        // playerNames[1] = tmp;
        // }
        // // playerNames now, [0]: opp, [1]: own
        // try {

        // String chatText = ChatTextViaClipboard.runIt(100);
        // Integer matchLen = searchMatchLen(chatText);
        // res.match = new Match(playerNames, 1, matchLen, 0, 0, false);
        // res.match.roll.die1 = res.spinRolls.die1();
        // res.match.roll.die2 = res.spinRolls.die2();
        // res.match.cube.used = s.cubeEmpty.run(board) != null;

        // if (res.match.roll.die1 > res.match.roll.die2) {
        // res.match.active = 0;
        // } else {
        // res.match.active = 1;
        // }
        // res.match.initialRoll = true;
        // res.match.autoroll = s.autorollSelected.run(board) != null;
        // System.out.println("autoroll " + res.match.autoroll);
        // return res;
        // } catch (Exception ex) {
        // ex.printStackTrace();
        // res.error = ex.getMessage();
        // return res;
        // }
        // } else if (res.spinRolls.isOppDice()) {
        // res.error = "Unerwarteter Weise Wurf nicht initial, sondern im Besitz des
        // Gegners!";
        // return res;
        // } else if (res.spinRolls.isOwnDice()) {
        // res.error = "Unerwarteter Weise Wurf nicht initial, sondern im Besitz des
        // eigenen Spielers!";
        // return res;
        // }
        // } while (true);
    }

    private static void scanMatch(BoardSearchers s, StartMatchRes res, Raster board)
            throws InterruptedException, UnsupportedFlavorException, IOException {
        String[] playerNames = { null, null };
        ReadPlayerNames.runIt(res.bs, board, playerNames);
        if (res.cal.ownWhite) {
            String tmp = playerNames[0];
            playerNames[0] = playerNames[1];
            playerNames[1] = tmp;
        }
        // playerNames now, [0]: opp, [1]: own

        String chatText = ChatTextViaClipboard.runIt(100);
        Integer matchLen = searchMatchLen(chatText);
        res.match = new Match(playerNames, 1, matchLen, 0, 0, false);
        res.match.roll.die1 = res.spinRolls.die1();
        res.match.roll.die2 = res.spinRolls.die2();
        ScanUtils.scanCube(board, res.match, s);
    }

    @Override
    public StartMatchRes doIt() throws Exception {
        return runIt(calWhite, calBlack, s);
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
