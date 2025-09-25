package pr.backgammon.spin.control.workers;

import java.awt.Point;
import java.awt.image.Raster;

import pr.backgammon.spin.control.BoardSearchers;
import pr.control.MyRobot;
import pr.control.MyWorker;

public abstract class ReadPlayerNames extends MyWorker<Void, Void> {
    private final BoardSearchers s;
    private final Raster board;
    private final String[] playerNames;

    /**
     * @param outPlayerNames - length must be 2; white name will be set in 0 if any; black name in 1 if any.
     */
    public ReadPlayerNames(BoardSearchers s, Raster board, String[] outPlayerNames) {
        this.s = s;
        this.board = board;
        this.playerNames = outPlayerNames;
    }
    public static void runIt(BoardSearchers s, Raster board, String[] playerNames) throws InterruptedException {

        Point posWhite = s.playerBoxWhite.run(board);
        Point posBlack = s.playerBoxBlack.run(board);
        Point posWhiteReady = s.playerBoxWhiteReady.run(board);
        Point posBlackReady = s.playerBoxBlackReady.run(board);
        System.out.println("ReadPlayerNames: posWhite " + posWhite + "  posBlack " + posBlack + "  posWhiteReady " + posWhiteReady + "  posBlackReady " + posBlackReady);

        playerNames[0] = null;
        playerNames[1] = null;

        int dx = s.boardRect().x;
        int dy = s.boardRect().y;

        if (posWhite != null) {
            playerNames[0] = scanLine(posWhite.x + dx + 3, posWhite.y + dy + 3);
        }

        if (posWhiteReady != null) {
            playerNames[0] = scanLine(posWhiteReady.x + dx + 3, posWhiteReady.y + dy + 3);
        }
        if (posBlack != null) {
            playerNames[1] = scanLine(posBlack.x + dx, posBlack.y + dy);
        }
        if (posBlackReady != null) {
            playerNames[1] = scanLine(posBlackReady.x + dx, posBlackReady.y + dy);
        }
        
        // int x = 1348;
        // int downy = 316;
        // int upy = 343;

        // // suche playerboxes zwischen 1337, 304 und 1356, 348
        // MyRobot.move(x, downy);
        // sleep();
        // MyRobot.press();
        // sleep();
        // MyRobot.release();
        // sleep();
        // MyRobot.press();
        // sleep();
        // MyRobot.move(x, upy);
        // sleep();
        // MyRobot.release();
        // sleep();
        // Thread.sleep(500);
        // String text = CopyAndGetClipboardText.runIt();
        // int splitPos = text.indexOf('\n');
        // if (splitPos == -1) throw new IllegalStateException();
        // playerNames[0] = text.substring(0, splitPos).trim();
        // playerNames[1] = text.substring(splitPos + 1).trim();
        // System.out.println("From clipboard: '" + s + "'" + "   length " + text.length());
    }

    @Override
    public Void doIt() throws Exception {
        runIt(s, board, playerNames);
        return null;
    }

    private static void sleep() throws InterruptedException {
        Thread.sleep(100);
    }

    private static String scanLine(int x, int y) throws InterruptedException {
        MyRobot.move(x, y);
        sleep();
        MyRobot.press();
        sleep();
        MyRobot.release();
        sleep();
        MyRobot.press();
        sleep();
        MyRobot.move(x, y + 15);
        Thread.sleep(500);
        MyRobot.release();

        sleep();
        return firstLine(CopyAndGetClipboardText.runIt());
    }

    private static String firstLine(String s) {
        int pos = s.indexOf('\n');
        if (pos == -1) return s;
        return s.substring(0, pos);
    }
}
