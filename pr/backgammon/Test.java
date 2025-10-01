package pr.backgammon;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Rectangle;
import java.awt.Robot;
import java.awt.Toolkit;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.lang.reflect.InvocationTargetException;

import javax.json.Json;
import javax.json.JsonArray;
import javax.json.JsonObject;
import javax.json.JsonObjectBuilder;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

import pr.backgammon.control.AllMoves;
import pr.backgammon.control.CreateIndexOfPublishedMatches;
import pr.backgammon.control.Move;
import pr.backgammon.control.OwnMove;
import pr.backgammon.control.OwnMoveCb;
import pr.backgammon.gnubg.model.GSetBoardSimple;
import pr.backgammon.jokers.control.AllJokers;
import pr.backgammon.model.Match;
import pr.backgammon.model.Roll;
import pr.backgammon.spin.SpinTracking;
import pr.backgammon.spin.control.BoardSearchers;
import pr.backgammon.spin.control.CalibrationForSpin;
import pr.backgammon.spin.control.FastChequerSearch;
import pr.backgammon.spin.control.MatchControl;
import pr.backgammon.spin.control.Rolls;
import pr.backgammon.spin.control.SpinRolls;
import pr.backgammon.spin.control.workers.WaitForOppMove;
import pr.backgammon.spin.control.workers.WaitForOppMoveRes;
import pr.backgammon.spin.model.WaitForOppMoveBug;
import pr.backgammon.spin.model.WorkerState;
import pr.backgammon.spin.view.SpinTrackFrame;
import pr.backgammon.view.MatchView;
import pr.control.MyWorker;
import pr.control.Tools;
import pr.http.HttpSessionClient;
import pr.http.HttpUtil;
import pr.model.MutableIntArray;

@SuppressWarnings("unused")
public class Test {
    private static MutableIntArray testMove(int... m) {
        MutableIntArray a = new MutableIntArray(8);
        for (int i = 0; i < m.length; ++i) {
            a.add(m[i]);
        }
        return a;
    }

    private static void paintField1() throws InterruptedException {
        Match m = new Match(new String[] { "Gegner", "Ich" }, 1, 3, 0, 0, false);
        m.initialRoll(1, 6, 5);
        pr.backgammon.control.Move.run(m, testMove(24, 18, 18, 13));
        m.offerDouble(0);
        m.take();
        m.roll(3, 1);
        pr.backgammon.control.Move.run(m, testMove(8, 5, 6, 5));
        m.offerDouble(1);
        m.take();
        m.offerResign(1, 2);
        Utils.showMatch(m);
        m.acceptResign();
        Utils.showMatch(m);
    }

    private static void paintField2() {
        Match m = new Match(new String[] { "Gegner", "Ich" }, 1, 3, 0, 0, false);
        m.initialRoll(1, 6, 2);
        pr.backgammon.control.Move.run(m, testMove(24, 18, 13, 11));
        m.roll(4, 6);
        pr.backgammon.control.Move.run(m, testMove(24, 18, 18, 14));

        m.roll(1, 1);
        // move.clear();
        // move.add(new PartMove(25, 24, false));
        // move.add(new PartMove(24, 23, false));
        // move.add(new PartMove(23, 22, false));
        // move.add(new PartMove(22, 21, false));
        pr.backgammon.control.Move.run(m, testMove(25, 24, 24, 23, 23, 22, 22, 21));

        m.roll(2, 3);
        pr.backgammon.control.Move.run(m, testMove(6, 4, 4, 1));

        MatchView v = new MatchView(m, true, false);
        v.setSize(800, 600);
        JFrame f = new JFrame("paintField");
        f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        f.add(v);
        f.setSize(850, 650);
        f.setVisible(true);
    }

    private static void paint4OnOwnBar() {
        Match m = new Match(new String[] { "Gegner", "Ich" }, 1, 3, 0, 0, false);
        m.initialRoll(1, 1, 2);
        // Move move = new Move();
        // move.add(new PartMove(6, 5, false));
        // move.add(new PartMove(6, 4, false));
        pr.backgammon.control.Move.run(m, testMove(6, 5, 6, 4));

        m.roll(1, 2);
        // move.clear();
        // move.add(new PartMove(6, 5, false));
        // move.add(new PartMove(6, 4, false));
        pr.backgammon.control.Move.run(m, testMove(6, 5, 6, 4));

        m.roll(3, 4);
        // move.clear();
        // move.add(new PartMove(6, 3, false));
        // move.add(new PartMove(6, 2, false));
        pr.backgammon.control.Move.run(m, testMove(6, 3, 6, 2));

        m.roll(1, 1);
        // move.clear();
        // move.add(new PartMove(24, 23, true));
        // move.add(new PartMove(23, 22, true));
        // move.add(new PartMove(22, 21, true));
        // move.add(new PartMove(21, 20, true));
        pr.backgammon.control.Move.run(m, testMove(24, 23, 23, 22, 22, 21, 21, 20));

        m.roll(4, 6);
        // move.clear();
        // move.add(new PartMove(25, 21, true));
        pr.backgammon.control.Move.run(m, testMove(25, 21));

        m.roll(6, 1);
        // move.clear();
        // move.add(new PartMove(25, 19, true));
        // move.add(new PartMove(5, 4, true));
        pr.backgammon.control.Move.run(m, testMove(25, 19, 5, 4));

        m.roll(6, 4);
        // move.clear();
        // move.add(new PartMove(25, 21, true));
        pr.backgammon.control.Move.run(m, testMove(25, 21));

        MatchView v = new MatchView(m, true, true);
        v.setSize(800, 600);
        JFrame f = new JFrame("paintField");
        f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        f.add(v);
        f.setSize(850, 650);
        f.setVisible(true);
    }

    private static void paint4OnOppBar() {
        Match m = new Match(new String[] { "Gegner", "Ich" }, 1, 3, 0, 0, false);
        m.initialRoll(0, 1, 2);
        // Move move = new Move();
        // move.add(new PartMove(6, 5, false));
        // move.add(new PartMove(6, 4, false));
        pr.backgammon.control.Move.run(m, testMove(6, 5, 6, 4));

        m.roll(1, 2);
        // move.clear();
        // move.add(new PartMove(6, 5, false));
        // move.add(new PartMove(6, 4, false));
        pr.backgammon.control.Move.run(m, testMove(6, 5, 6, 4));

        m.roll(3, 4);
        // move.clear();
        // move.add(new PartMove(6, 3, false));
        // move.add(new PartMove(6, 2, false));
        pr.backgammon.control.Move.run(m, testMove(6, 3, 6, 2));

        m.roll(1, 1);
        // move.clear();
        // move.add(new PartMove(24, 23, true));
        // move.add(new PartMove(23, 22, true));
        // move.add(new PartMove(22, 21, true));
        // move.add(new PartMove(21, 20, true));
        pr.backgammon.control.Move.run(m, testMove(24, 23, 23, 22, 22, 21, 21, 20));

        m.roll(4, 6);
        // move.clear();
        // move.add(new PartMove(25, 21, true));
        pr.backgammon.control.Move.run(m, testMove(25, 21));

        m.roll(6, 1);
        // move.clear();
        // move.add(new PartMove(25, 19, true));
        // move.add(new PartMove(5, 4, true));
        pr.backgammon.control.Move.run(m, testMove(25, 19, 5, 4));

        m.roll(6, 4);
        // move.clear();
        // move.add(new PartMove(25, 21, true));
        pr.backgammon.control.Move.run(m, testMove(25, 21));

        MatchView v = new MatchView(m, true, false);
        v.setSize(800, 600);
        JFrame f = new JFrame("paintField");
        f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        f.add(v);
        f.setSize(850, 650);
        f.setVisible(true);
    }

    private static void testResign() throws Exception {
        Match m = new Match(new String[] { "Gegner", "Ich" }, 1, 3, 0, 0, false);
        m.initialRoll(0, 1, 2);
        // Move move = new Move();
        // move.add(new PartMove(6, 5, false));
        // move.add(new PartMove(6, 4, false));
        pr.backgammon.control.Move.run(m, testMove(6, 5, 6, 4));
        m.offerResign(0, 1);
        Utils.showMatch(m);
        m.acceptResign();
        Utils.showMatch(m);

        m.initialRoll(1, 2, 4);
        Utils.showMatch(m);
        // move.clear();
        // move.add(8, 4, false);
        // move.add(6, 4, false);
        pr.backgammon.control.Move.run(m, testMove(8, 4, 6, 4));
        Utils.showMatch(m);
    }

    private static void testBearoff() throws Exception {
        Match m = new Match(new String[] { "Gegner", "Ich" }, 1, 3, 0, 0, true);
        m.initialRoll(0, 6, 6);
        // Utils.showMatch(m);
        // Move move = new Move();
        // move.add(new PartMove(6, 0, false));
        // move.add(new PartMove(6, 0, false));
        pr.backgammon.control.Move.run(m, testMove(6, 0, 6, 0));
        // Utils.showMatch(m);

        m.initialRoll(0, 1, 2);
        // move.clear();
        // move.add(6, 5, false);
        // move.add(6, 4, false);
        pr.backgammon.control.Move.run(m, testMove(6, 5, 6, 4));
        // Utils.showMatch(m);
        m.offerDouble(1);
        // Utils.showMatch(m);
        m.take();
        // Utils.showMatch(m);

        m.roll(5, 3);
        // move.clear();
        // move.add(6, 3, false);
        // move.add(6, 1, false);
        pr.backgammon.control.Move.run(m, testMove(6, 3, 6, 1));
        // Utils.showMatch(m);

        m.roll(4, 1);
        // move.clear();
        // move.add(5, 1, false);
        // move.add(1, 0, false);
        pr.backgammon.control.Move.run(m, testMove(5, 1, 1, 0));

        m.roll(5, 2);
        // Utils.showMatch(m);
        // move.clear();
        // move.add(3, 0, false);
        // move.add(1, 0, false);
        pr.backgammon.control.Move.run(m, testMove(3, 0, 1, 0));
        Utils.showMatch(m);

    }

    private static void testDrop() throws Exception {
        Match m = new Match(new String[] { "Gegner", "Ich" }, 1, 3, 0, 0, true);
        m.initialRoll(0, 1, 2);
        // Move move = new Move();
        // move.add(new PartMove(6, 5, false));
        // move.add(new PartMove(6, 4, false));
        pr.backgammon.control.Move.run(m, testMove(6, 5, 6, 4));

        m.offerDouble(1);
        Utils.showMatch(m);

        m.drop();
        Utils.showMatch(m);

        m.initialRoll(0, 1, 2);
        // move.clear();
        // move.add(new PartMove(6, 5, false));
        // move.add(new PartMove(6, 4, false));
        pr.backgammon.control.Move.run(m, testMove(6, 5, 6, 4));

        m.offerDouble(1);
        Utils.showMatch(m);

        m.drop();
        Utils.showMatch(m);

        m.initialRoll(1, 1, 2);
        // move.clear();
        // move.add(new PartMove(6, 5, false));
        // move.add(new PartMove(6, 4, false));
        pr.backgammon.control.Move.run(m, testMove(6, 5, 6, 4));

        m.offerResign(1, 1);
        Utils.showMatch(m);
        m.acceptResign();
        Utils.showMatch(m);

        m.initialRoll(1, 1, 2);
        // move.clear();
        // move.add(new PartMove(6, 5, false));
        // move.add(new PartMove(6, 4, false));
        pr.backgammon.control.Move.run(m, testMove(6, 5, 6, 4));

        Utils.showMatch(m);

        m.offerDouble(0);
        Utils.showMatch(m);

        m.drop();
        Utils.showMatch(m);
    }

    private static void testCrawford() throws Exception {
        Match m = new Match(new String[] { "Gegner", "Ich" }, 1, 3, 0, 0, true);
        m.initialRoll(0, 1, 2);
        // Move move = new Move();
        // move.add(new PartMove(6, 5, false));
        // move.add(new PartMove(6, 4, false));
        pr.backgammon.control.Move.run(m, testMove(6, 5, 6, 4));

        m.offerDouble(1);
        // Utils.showMatch(m);

        m.drop();
        // Utils.showMatch(m);

        m.initialRoll(0, 1, 2);
        // move.clear();
        // move.add(new PartMove(6, 5, false));
        // move.add(new PartMove(6, 4, false));
        pr.backgammon.control.Move.run(m, testMove(6, 5, 6, 4));

        m.offerDouble(1);
        Utils.showMatch(m);

        m.drop();
        Utils.showMatch(m);

        // Now, crawford round.

        m.initialRoll(1, 1, 2);
        // move.clear();
        // move.add(new PartMove(6, 5, false));
        // move.add(new PartMove(6, 4, false));
        pr.backgammon.control.Move.run(m, testMove(6, 5, 6, 4));
        Utils.showMatch(m);

        try {
            // expected to throw IllegalStateException:
            m.offerDouble(0);
            // not expected to reach this line, because exception expected
            throw new Exception("Expected IllegalStateException missing");
        } catch (IllegalStateException ex) {
            // expected, ignore
        }
        // Utils.showMatch(m);

        m.offerResign(1, 1);
        m.acceptResign();
        // Utils.showMatch(m);

        // Now, post-crawford.

        m.initialRoll(1, 1, 2);
        // move.clear();
        // move.add(new PartMove(6, 5, false));
        // move.add(new PartMove(6, 4, false));
        pr.backgammon.control.Move.run(m, testMove(6, 5, 6, 4));

        Utils.showMatch(m);

        m.offerDouble(0); // must not throw because of post-crawford round.
        Utils.showMatch(m);

        m.take();
        Utils.showMatch(m);

        m.roll(4, 4);
        Utils.showMatch(m);
        // move.clear();
        // move.add(6, 2, false);
        // move.add(6, 2, false);
        // move.add(2, 0, false);
        // move.add(2, 0, false);
        pr.backgammon.control.Move.run(m, testMove(6, 2, 6, 2, 2, 0, 2, 0));
        Utils.showMatch(m);

    }

    private static void testSpinTrackFrame() throws Exception {
        SpinTrackFrame f = new SpinTrackFrame(new SpinTrackFrame.Listener() {

            @Override
            public void calibrate() {
            }

            @Override
            public void startTracking() {
            }

            @Override
            public void trackNow() {
            }

            @Override
            public void testScan() {

            }

            @Override
            public void cancel() {
            }

            @Override
            public void screenshot() {
            }

            @Override
            public void debugAction() {

            }

            @Override
            public void abortMatch() {

            }

        }, new JPanel());
        // f.setSize(500, 800);
        f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        f.setVisible(true);
    }

    private static void testSpinTracking() throws Exception {
        Robot r = new Robot();
        SpinTracking tracking = new SpinTracking();
        tracking.run();
    }

    private static void testSpinRolls() throws Exception {
        System.out.println("Bitte initialen Wurf in spin.de aufstellen und dann Enter");
        Robot r = new Robot();
        BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
        reader.readLine();
        Rectangle screenRect = new Rectangle(0, 0);
        screenRect.setSize(Toolkit.getDefaultToolkit().getScreenSize());
        BufferedImage screen = r.createScreenCapture(screenRect);
        CalibrationForSpin cal = new CalibrationForSpin(screen);
        Rolls rolls = new Rolls(cal);
        FastChequerSearch fastChequerSearch = new FastChequerSearch(cal);
        Rectangle boardScreenshotRect = fastChequerSearch.boardScreenshotRect(null);
        SpinRolls spinRolls = new SpinRolls(cal, boardScreenshotRect);

        System.out.println("Bitte zu testenden Wurf bereitstellen und dann Enter");
        reader.readLine();

        screen = r.createScreenCapture(screenRect);
        spinRolls.debug(screen, rolls, fastChequerSearch);

        // shotsize war hier 868x574
    }

    private static void testSwingWorker() throws Exception {
        Object sync = new Object();
        boolean[] done = { false };
        MyWorker<Integer, Void> testWorker = new MyWorker<Integer, Void>() {
            @Override
            public Integer doIt() throws Exception {
                Thread.sleep(1000);
                return 42;
            }

            @Override
            public void resultOnEventDispatchThread(Integer result) {
                System.out.println("result of worker was " + result);
                synchronized (sync) {
                    done[0] = true;
                    sync.notifyAll();
                }
            }
        };
        testWorker.execute();
        Thread.sleep(100);
        boolean resCancel = testWorker.cancel(false);
        System.out.println("result of cancel " + resCancel);
        Thread.sleep(2000);

        synchronized (sync) {
            System.out.println(
                    "after sleep(2000): done[0] is expected still to be false because of the cancellation and is indeed: "
                            + done[0]);
        }
    }

    private static void testGSetBoardSimple() throws Exception {
        Match m = new Match(new String[] { "Gegner", "Ich" }, 1, 3, 0, 0, false);
        m.initialRoll(1, 3, 1);
        GSetBoardSimple cmd = new GSetBoardSimple(m.getBoardSimple(null));
        System.out.println("cmd: " + cmd.append(null).toString());
    }

    private static void testMatchControl() throws Exception {
        MatchControl matchControl = new MatchControl();
        matchControl.run();
        SpinTracking spinTracking = new SpinTracking();
        spinTracking.run();
    }

    private static void testMatchViewContainingField() throws Exception {
        Match match = new Match();
        MatchView[] matchView = { new MatchView(match, true, false) };
        int w = 800;
        int h = 600;
        matchView[0].setPreferredSize(new Dimension(w, h));

        JFrame frame = new JFrame("testMatchViewContainingField");
        SwingUtilities.invokeAndWait(new Runnable() {
            @Override
            public void run() {
                frame.setLayout(new BorderLayout());
                frame.add(matchView[0], BorderLayout.CENTER);
                frame.pack();
                frame.setVisible(true);
            }
        });
        Thread.sleep(500);

        final int gapw = 10;
        final int gaph = 10;
        int unit = (w - gapw - gapw) / 18 / 2;
        int unitH = (h - gaph - gaph) / 25;
        if (unitH < unit)
            unit = unitH;
        int unit2 = unit + unit;
        int boardx = (w - unit * 36) / 2;
        int boardy = (h - 26 * unit) / 2;

        int ytop = chequerYOnTop(boardy, unit);
        int ybottom = chequerYOnBottom(boardy, unit);
        testContainingField1(12, matchView[0], chequerXOnLeft(boardx, unit, 0), ytop);
        testContainingField1(11, matchView[0], chequerXOnLeft(boardx, unit, 1), ytop);
        testContainingField1(7, matchView[0], chequerXOnLeft(boardx, unit, 5), ytop);
        testContainingField1(13, matchView[0], chequerXOnLeft(boardx, unit, 0), ybottom);
        testContainingField1(14, matchView[0], chequerXOnLeft(boardx, unit, 1), ybottom);
        testContainingField1(18, matchView[0], chequerXOnLeft(boardx, unit, 5), ybottom);

        testContainingField1(6, matchView[0], chequerXOnRight(boardx, unit, 0), ytop);
        testContainingField1(5, matchView[0], chequerXOnRight(boardx, unit, 1), ytop);
        testContainingField1(1, matchView[0], chequerXOnRight(boardx, unit, 5), ytop);
        testContainingField1(19, matchView[0], chequerXOnRight(boardx, unit, 0), ybottom);
        testContainingField1(20, matchView[0], chequerXOnRight(boardx, unit, 1), ybottom);
        testContainingField1(24, matchView[0], chequerXOnRight(boardx, unit, 5), ybottom);

        SwingUtilities.invokeAndWait(new Runnable() {

            @Override
            public void run() {
                frame.remove(matchView[0]);
                matchView[0] = new MatchView(match, false, true);
                matchView[0].setPreferredSize(new Dimension(w, h));
                frame.add(matchView[0], BorderLayout.CENTER);
                frame.validate();
                frame.pack();
                frame.setVisible(true);
                frame.repaint();

            }
        });
        Thread.sleep(500);

        testContainingField1(24, matchView[0], chequerXOnLeft(boardx, unit, 0), ytop);
        testContainingField1(23, matchView[0], chequerXOnLeft(boardx, unit, 1), ytop);
        testContainingField1(19, matchView[0], chequerXOnLeft(boardx, unit, 5), ytop);
        testContainingField1(1, matchView[0], chequerXOnLeft(boardx, unit, 0), ybottom);
        testContainingField1(2, matchView[0], chequerXOnLeft(boardx, unit, 1), ybottom);
        testContainingField1(6, matchView[0], chequerXOnLeft(boardx, unit, 5), ybottom);

        testContainingField1(18, matchView[0], chequerXOnRight(boardx, unit, 0), ytop);
        testContainingField1(17, matchView[0], chequerXOnRight(boardx, unit, 1), ytop);
        testContainingField1(13, matchView[0], chequerXOnRight(boardx, unit, 5), ytop);
        testContainingField1(7, matchView[0], chequerXOnRight(boardx, unit, 0), ybottom);
        testContainingField1(8, matchView[0], chequerXOnRight(boardx, unit, 1), ybottom);
        testContainingField1(12, matchView[0], chequerXOnRight(boardx, unit, 5), ybottom);

        SwingUtilities.invokeAndWait(new Runnable() {

            @Override
            public void run() {
                frame.remove(matchView[0]);
                matchView[0] = new MatchView(match, false, false);
                matchView[0].setPreferredSize(new Dimension(w, h));
                frame.add(matchView[0], BorderLayout.CENTER);
                frame.validate();
                frame.pack();
                frame.setVisible(true);
                frame.repaint();

            }
        });
        Thread.sleep(500);

        testContainingField1(13, matchView[0], chequerXOnLeft(boardx, unit, 0), ytop);
        testContainingField1(14, matchView[0], chequerXOnLeft(boardx, unit, 1), ytop);
        testContainingField1(18, matchView[0], chequerXOnLeft(boardx, unit, 5), ytop);
        testContainingField1(12, matchView[0], chequerXOnLeft(boardx, unit, 0), ybottom);
        testContainingField1(11, matchView[0], chequerXOnLeft(boardx, unit, 1), ybottom);
        testContainingField1(7, matchView[0], chequerXOnLeft(boardx, unit, 5), ybottom);

        testContainingField1(19, matchView[0], chequerXOnRight(boardx, unit, 0), ytop);
        testContainingField1(20, matchView[0], chequerXOnRight(boardx, unit, 1), ytop);
        testContainingField1(24, matchView[0], chequerXOnRight(boardx, unit, 5), ytop);
        testContainingField1(6, matchView[0], chequerXOnRight(boardx, unit, 0), ybottom);
        testContainingField1(5, matchView[0], chequerXOnRight(boardx, unit, 1), ybottom);
        testContainingField1(1, matchView[0], chequerXOnRight(boardx, unit, 5), ybottom);

        SwingUtilities.invokeAndWait(new Runnable() {

            @Override
            public void run() {
                frame.remove(matchView[0]);
                matchView[0] = new MatchView(match, true, true);
                matchView[0].setPreferredSize(new Dimension(w, h));
                frame.add(matchView[0], BorderLayout.CENTER);
                frame.validate();
                frame.pack();
                frame.setVisible(true);
                frame.repaint();

            }
        });
        Thread.sleep(500);

        testContainingField1(1, matchView[0], chequerXOnLeft(boardx, unit, 0), ytop);
        testContainingField1(2, matchView[0], chequerXOnLeft(boardx, unit, 1), ytop);
        testContainingField1(6, matchView[0], chequerXOnLeft(boardx, unit, 5), ytop);
        testContainingField1(24, matchView[0], chequerXOnLeft(boardx, unit, 0), ybottom);
        testContainingField1(23, matchView[0], chequerXOnLeft(boardx, unit, 1), ybottom);
        testContainingField1(19, matchView[0], chequerXOnLeft(boardx, unit, 5), ybottom);

        testContainingField1(7, matchView[0], chequerXOnRight(boardx, unit, 0), ytop);
        testContainingField1(8, matchView[0], chequerXOnRight(boardx, unit, 1), ytop);
        testContainingField1(12, matchView[0], chequerXOnRight(boardx, unit, 5), ytop);
        testContainingField1(18, matchView[0], chequerXOnRight(boardx, unit, 0), ybottom);
        testContainingField1(17, matchView[0], chequerXOnRight(boardx, unit, 1), ybottom);
        testContainingField1(13, matchView[0], chequerXOnRight(boardx, unit, 5), ybottom);

        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                frame.setVisible(false);
                frame.dispose();

            }
        });

    }

    public static void testShowMatchView(MatchView v) throws InterruptedException, InvocationTargetException {
        SwingUtilities.invokeAndWait(new Runnable() {
            @Override
            public void run() {
                v.setPreferredSize(new Dimension(800, 600));
                JFrame frame = new JFrame();
                frame.setLayout(new BorderLayout());
                frame.add(v, BorderLayout.CENTER);
                frame.pack();
                frame.setVisible(true);
            }
        });

    }

    private static void testContainingField1(int expected, MatchView matchView, int x, int y) {
        int real = matchView.containingField(x, y);

        if (real != expected) {
            throw new IllegalStateException(real + ", but expected " + expected + " on " + x + "," + y);
        }
    }

    private static int chequerXOnLeft(int boardx, int unit, int i) {
        return boardx + unit * 2 * (2 + i) + unit;
    }

    private static int chequerXOnRight(int boardx, int unit, int i) {
        return boardx + (10 + i) * (unit * 2);
    }

    private static int chequerYOnTop(int boardy, int unit) {
        return boardy + unit * 2;
    }

    private static int chequerYOnBottom(int boardy, int unit) {
        return boardy + unit * 24;
    }

    private static void testOwnMove() throws Exception {
        WorkerState state = new WorkerState();
        Match match = state.match;
        match.getPlayer(1).field.set(25, 1);
        match.getPlayer(1).field.set(24, 1);
        match.initialRoll(1, 5, 6);
        MatchView matchView = new MatchView(match, true, false);
        testShowMatchView(matchView);
        OwnMove ownMove = new OwnMove(state, matchView, new OwnMoveCb() {
            @Override
            public void done() {
                System.out.println("OwnMove done");
            }
        });
    }

    private static void testAllMoves() {
        WorkerState state = new WorkerState();
        Match m = state.match;
        m.getPlayer(1).field.set(24, 0);
        m.getPlayer(1).field.set(25, 2);
        m.active = 1;
        m.roll.die1 = 5;
        m.roll.die2 = 6;
        AllMoves.find(m, state.allMoves, state.findTaskArray);
        System.out.println("all moves:");
        int n = state.allMoves.length();
        for (int i = 0; i < n; ++i) {
            var move = state.allMoves.at(i);
            System.out.println(move.append(null).toString());
        }

    }

    private static void testMoveRunLast() throws Exception {
        Match m = new Match();
        m.debug(new int[] {
                1, 1,
                24, -1,
                23, -2,
        }, null, null, new Roll(3, 4), 1, false);
        System.out.println(m.appendPosition(new StringBuilder("Testposition:\n")).toString());
        MutableIntArray move = new MutableIntArray(8);
        move.add(1);
        move.add(0);
        Move.run(m, move);
    }

    private static void testPaintOffChequers() throws Exception {
        Match m = new Match();

        var field = m.getPlayer(1).field;
        field.set(24, 1);
        field.set(0, 15);
        field = m.getPlayer(0).field;
        field.set(0, 3);
        // MatchView matchView = new MatchView(m, true, false);
        // testShowMatchView(matchView);

        for (int ownWhite = 0; ownWhite < 2; ++ownWhite) {
            boolean bownWhite = ownWhite != 0;
            for (int clockwise = 0; clockwise < 2; ++clockwise) {
                boolean bclockwise = clockwise != 0;
                MatchView matchView = new MatchView(m, bownWhite, bclockwise);
                testShowMatchView(matchView);
            }
        }
    }

    private static void testObjectStream() throws Exception {
        WaitForOppMoveBug obj = new WaitForOppMoveBug();
        String fileName;
        ObjectOutputStream os = new ObjectOutputStream(
                new FileOutputStream(fileName = Tools.dateTimeString() + "_tmpObjStream.bin"));
        System.out.println("fileName: '" + fileName + "'");
        try {
            os.writeObject(obj);
        } finally {
            os.close();
        }

        ObjectInputStream is = new ObjectInputStream(new FileInputStream(fileName));
        try {
            WaitForOppMoveBug obj2 = (WaitForOppMoveBug) is.readObject();
            System.out.println(obj2.match.appendPosition(null));
        } finally {
            is.close();
        }
    }

    private static void testMoveAggregate() throws Exception {
        WorkerState state = new WorkerState();
        Match m = state.match;
        m.initialRoll(1, 2, 2);
        var move = testMove(24, 22, 22, 20, 20, 18, 18, 16);
        var aggregatedMove = testMove();
        Move.aggregate(2, 2, move, m.getPlayer(0).field, aggregatedMove, state.tmp);
        StringBuilder sb = new StringBuilder();
        sb.append("move: ");
        move.append(sb);
        sb.append("\n aggregated move: ");
        aggregatedMove.append(sb).append("\n");
        System.out.println(sb);
    }

    private static void testBug20250917() throws Exception {
        WorkerState workerState = new WorkerState();

        workerState.match.debug(new int[] {
                20, -1,
                21, -1,
                5, 2,
                1, 1
        }, null, new Roll(3, 3), null, 1, false);
        Match m = workerState.match;
        System.out.println("m.active  " + m.active);
        m.active = 0;
        // AllMoves.find(workerState.match, workerState.allMoves,
        // workerState.findTaskArray);
        // {
        // StringBuilder sb = new StringBuilder("all moves:\n");
        // var n = workerState.allMoves.length();

        // for (var i = 0; i < n; ++i) {
        // workerState.allMoves.at(i).append(sb).append('\n');
        // }
        // System.out.println(sb);
        // }

        testShowMatchView(new MatchView(m, true, false));

        var winningMove = WaitForOppMove.findWinningMove(workerState);
        System.out.println("winningMove: " + winningMove);
        CalibrationForSpin cal = new CalibrationForSpin();
        FastChequerSearch chequers = new FastChequerSearch(cal);
        Rectangle boardRect = chequers.boardScreenshotRect(null);
        var bs = new BoardSearchers(cal, boardRect);
        var spinRolls = new SpinRolls(cal, boardRect);
        var worker = new WaitForOppMove(cal, bs, spinRolls) {
            @Override
            public void resultOnEventDispatchThread(WaitForOppMoveRes result) {
                System.out.println("result:");
                System.out.println("error: " + result.error);
                System.out.println("move: " + result.move.append(null).toString());
            }
        };
        worker.setState(workerState);
        try {
            worker.execute();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        Thread.sleep(10000);

        System.exit(0);
    }
    /*
    
    
    
    
    
     */

    private static void testLogin(HttpSessionClient client) throws Exception {
        String json = """
                {
                    "type": "login",
                    "req": { "email": "peter.reitinger@gmail.com", "passwd": "a", "force": true },
                    "version": { "main": 0, "sub": 20 }
                }
                                """;
        var resp = client.postJson("http://localhost:3001/api/countRolls/myUser", json);
        System.out.println("response of login:\n" + resp);
    }

    private static void testSendJokers() throws Exception {
        boolean manualCookieOverride = true;
        HttpSessionClient client = new HttpSessionClient(manualCookieOverride);
        testLogin(client);
        for (var cookie : client.getCookieStore().getCookies()) {
            System.out.println("cookie: " + cookie.getName() + "   -   " + cookie.getValue());
            System.out.println("getSecure(): " + cookie.getSecure());
        }
        String json = client.postJson("http://localhost:3001/api/countJokers/upload",
                """
                        {
                            "globalAccId": "68ac2854f60b95ed625d6f22",
                            "name1": "Test A",
                            "name2": "Test B",
                            "counts1": [
                                {
                                    "name": "Prime5FromBarEscape",
                                    "pos": 2,
                                    "neg": 38
                                },
                                {
                                    "name": "OtherForTesting",
                                    "pos": 10,
                                    "neg": 20
                                }
                            ],
                            "counts2": [
                                {
                                    "name": "Prime5FromBarEscape",
                                    "pos": 20,
                                    "neg": 380
                                },
                                {
                                    "name": "OtherForTesting",
                                    "pos": 100,
                                    "neg": 200
                                }
                            ],
                            "version": {
                                "main": 0,
                                "sub": 20
                            }
                        }
                                                                            """);

        System.out.println("Antwort:\n" + json);
        client.close();
    }

    private static String jokerUploadRequest(String globalAccId, String player1, String player2, AllJokers jokers1,
            AllJokers jokers2) {
        JsonArray jokers1Json = jokers1.toJson();
        JsonArray jokers2Json = jokers2.toJson();

        JsonObject json = addVersion(Json.createObjectBuilder()
                .add("globalAccId", globalAccId)
                .add("name1", player1)
                .add("name2", player2)
                .add("counts1", jokers1Json)
                .add("counts2", jokers2Json)).build();

        return json.toString();
    }

    private static JsonObjectBuilder addVersion(JsonObjectBuilder b) {
        b.add("version", Json.createObjectBuilder().add("main", 0).add("sub", 20));
        return b;
    }

    private static void testJokersJson() {
        try {
            var builder = Json.createObjectBuilder();
            var ownJokers = new AllJokers();
            var oppJokers = new AllJokers();
            var workerState = new WorkerState();
            Match match = workerState.match;
            match.active = 0;
            match.roll.die1 = 4;
            match.roll.die2 = 4;
            match.getPlayer(0).name = "Test A";
            match.getPlayer(1).name = "Test B";
            match.getPlayer(0).field.set(25, 1);
            ownJokers.count(match, workerState.tmp);
            oppJokers.count(match, workerState.tmp);
            JsonArray ownJokersJson = ownJokers.toJson();
            JsonArray oppJokersJson = oppJokers.toJson();
            var json = jokerUploadRequest("68ac2854f60b95ed625d6f22", "Test A", "Test B", ownJokers, oppJokers);
            System.out.println("json: '" + json + "'");

            // JsonObject json = builder.add("globalAccId", "68ac2854f60b95ed625d6f22")
            //         .add("name1", match.getPlayerName(match.own))
            //         .add("name2", match.getPlayerName(1 - match.own))
            //         .add("counts1", ownJokersJson)
            //         .add("counts2", oppJokersJson).build();

            // System.out.println("json: " + json);


            boolean manualCookieOverride = true;
            HttpSessionClient client = new HttpSessionClient(manualCookieOverride);
            testLogin(client);
            client.postJson("http://localhost:3001/api/countJokers/upload", json);

            client.close();

        } catch (Exception ex) {
            ex.printStackTrace();
        }

    }

    private static void testCreateIndexOfPublishedMatches() throws Exception {
        var o = new CreateIndexOfPublishedMatches("../pr-home/public/gnubg");
        o.run();
    }

    public static void main(String[] args) throws Exception {
        // paintField1();
        // paint4OnOwnBar();
        // paint4OnOppBar();
        // testResign();
        // testBearoff();
        // testDrop();
        // testCrawford();
        // testSpinTrackFrame();
        // testSpinTracking(); // future main
        // testSpinRolls();
        // testSwingWorker();
        // testGSetBoardSimple();
        testMatchControl();
        // testMatchViewContainingField();
        // testOwnMove();
        // testAllMoves();
        // testMoveRunLast();
        // testPaintOffChequers();
        // testObjectStream();
        // testMoveAggregate();
        // testBug20250917();
        // testSendJokers();
        // testJokersJson();
        // testCreateIndexOfPublishedMatches();
    }

}

class Utils {
    static void showMatch(Match m) throws InterruptedException {
        Object sync = new Object();
        boolean[] closed = { false };

        MatchView v = new MatchView(m, true, false);
        v.setSize(800, 600);
        JFrame f = new JFrame("paintField");
        f.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        f.add(v);
        f.setSize(850, 650);
        f.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosed(WindowEvent e) {
                synchronized (sync) {
                    closed[0] = true;
                    sync.notifyAll();
                }
            }
        });
        f.setVisible(true);

        synchronized (sync) {
            while (!closed[0]) {
                sync.wait();
            }
        }
    }

    public static MutableIntArray mkMove() {
        return new MutableIntArray(8);
    }
}