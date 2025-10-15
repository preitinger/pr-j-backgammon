package pr.backgammon;

import java.awt.AlphaComposite;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.GraphicsEnvironment;
import java.awt.Rectangle;
import java.awt.Robot;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;
import java.util.concurrent.ThreadLocalRandom;

import javax.json.Json;
import javax.json.JsonArray;
import javax.json.JsonObject;
import javax.json.JsonObjectBuilder;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.MatOfFloat;
import org.opencv.core.MatOfInt;
import org.opencv.core.Point;
import org.opencv.core.Scalar;
import org.opencv.core.Size;
import org.opencv.features2d.SimpleBlobDetector;
import org.opencv.features2d.SimpleBlobDetector_Params;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;

import pr.backgammon.control.AllMoves;
import pr.backgammon.control.CreateIndexOfPublishedMatches;
import pr.backgammon.control.Move;
import pr.backgammon.control.OwnMove;
import pr.backgammon.control.OwnMoveCb;
import pr.backgammon.gnubg.model.GSetBoardSimple;
import pr.backgammon.jokers.control.AllJokers;
import pr.backgammon.model.Field;
import pr.backgammon.model.LocAndVal;
import pr.backgammon.model.Match;
import pr.backgammon.model.Roll;
import pr.backgammon.spin.SpinTracking;
import pr.backgammon.spin.control.BoardSearchers;
import pr.backgammon.spin.control.CalibrationForSpin;
import pr.backgammon.spin.control.FastChequerSearch;
import pr.backgammon.spin.control.MatchControl;
import pr.backgammon.spin.control.ScreenSearchers;
import pr.backgammon.spin.control.SearchBearoffRects;
import pr.backgammon.spin.control.SearchChequerCircles;
import pr.backgammon.spin.control.SpinRolls;
import pr.backgammon.spin.control.TemplateSearchers;
import pr.backgammon.spin.control.workers.Calibrate;
import pr.backgammon.spin.control.workers.CalibrateRes;
import pr.backgammon.spin.control.workers.WaitForOppMove;
import pr.backgammon.spin.control.workers.WaitForOppMoveRes;
import pr.backgammon.spin.model.WaitForOppMoveBug;
import pr.backgammon.spin.model.WorkerState;
import pr.backgammon.spin.view.SpinTrackFrame;
import pr.backgammon.view.MatchView;
import pr.control.MyRobot;
import pr.control.MyWorker;
import pr.control.TemplateSearcherOld;
import pr.control.Tools;
import pr.cv.BufferedImageToMat;
import pr.cv.MatToBufferedImage;
import pr.http.HttpSessionClient;
import pr.model.MutableIntArray;
import pr.view.ImgAndMousePosFrame;
import pr.view.MousePosView;

@SuppressWarnings("unused")
public class Test {

    static {
    }

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

        try {
            initOpenCV();

            // ScreenSearchers s;
            TemplateSearchers ts;
            Rectangle screenRect;
            {
                // We assume that all screens are horizontally aligned and sum up all widths
                int wsum = 0, hmax = 0;
                var devices = GraphicsEnvironment.getLocalGraphicsEnvironment().getScreenDevices();
                for (var device : devices) {
                    var dm = device.getDisplayMode();
                    wsum += dm.getWidth();
                    hmax = Math.max(hmax, dm.getHeight());
                }
                screenRect = new Rectangle(0, 0, wsum, hmax);
                // s = new ScreenSearchers(screenRect);
                ts = new TemplateSearchers(screenRect);

                Calibrate calibrate = new Calibrate(ts) {
                    @Override
                    public void resultOnEventDispatchThread(CalibrateRes res) {
                        System.out.println("\n\n\n******* result!!!");
                        BoardSearchers bs;
                        if (res.error != null) {
                            System.out.println(res.error);
                        } else {
                            if (res.calWhite.ownWhite && !res.calBlack.ownWhite) {
                                CalibrationForSpin cal = res.calBlack; // TODO adapt for specific test
                                FastChequerSearch chequers = new FastChequerSearch(cal, ts);
                                try {
                                    Rectangle boardScreenshotRect = chequers.boardScreenshotRect(null);
                                    bs = new BoardSearchers(cal, boardScreenshotRect);

                                    System.out.println("Bitte zu testenden Wurf bereitstellen und dann Enter");
                                    BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
                                    in.readLine();
                                    SpinRolls spinRolls = new SpinRolls(cal, boardScreenshotRect);
                                    var board = bs.boardShot();
                                    var f = new ImgAndMousePosFrame("board");
                                    f.setImg(board);
                                    f.setVisible(true);
                                    // Tools.showImg("board", board, false);
                                    // var boardRaster = board.getRaster();
                                    spinRolls.detectFromBoardShot(board);
                                    spinRolls.dump();
                                    chequers.init(board);
                                    // chequers.chequersOffWhite()
                                } catch (IOException ex) {
                                    ex.printStackTrace();
                                } catch (Exception ex) {
                                    ex.printStackTrace();
                                }
                            }
                        }
                    }
                };
                calibrate.execute();
                do {
                    Thread.sleep(100000);
                } while (true);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
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
        initOpenCV();

        MatchControl matchControl = new MatchControl();
        matchControl.run();
        SpinTracking spinTracking = new SpinTracking();
        spinTracking.run();
    }

    private static void initOpenCV() {
        try {
            // lädt libopencv_java412.so über java.library.path/LD_LIBRARY_PATH
            System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
        } catch (UnsatisfiedLinkError e) {
            // Fallback: absoluter Pfad, wenn du keinen Pfad setzen willst
            System.load("/home/peter/opencv_4.12/opencv/build/lib/libopencv_java4130.so");
        }
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
                var mousePosView = new MousePosView(v);
                mousePosView.setPreferredSize(new Dimension(200, 100));
                mousePosView.start();
                frame.add(mousePosView, BorderLayout.EAST);
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
        // match.getPlayer(1).field.set(25, 1);
        // match.getPlayer(1).field.set(24, 1);
        match.initialRoll(1, 3, 3);
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
        TemplateSearchers ts = new TemplateSearchers(new Rectangle(0, 0, 1600, 1000));
        FastChequerSearch chequers = new FastChequerSearch(cal, ts);
        Rectangle boardRect = chequers.boardScreenshotRect(null);
        var bs = new BoardSearchers(cal, boardRect);
        var spinRolls = new SpinRolls(cal, boardRect);
        var worker = new WaitForOppMove(cal, bs, ts, chequers, spinRolls, true) {
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
            // .add("name1", match.getPlayerName(match.own))
            // .add("name2", match.getPlayerName(1 - match.own))
            // .add("counts1", ownJokersJson)
            // .add("counts2", oppJokersJson).build();

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

    private static void testOpenCv() throws Exception {
        initOpenCV();

        System.out.println("OpenCV: " + Core.getVersionString());
        // Mat img = Imgcodecs.imread("pr/res/whiteChequer.png");
        Mat img = BufferedImageToMat.toBgrMat(MyRobot.shot(new Rectangle(0, 0, 1600, 1000)));

        System.out.println("Gelesen: " + (!img.empty()) + ", Größe: " + img.size());
        Mat gray = new Mat();
        Imgproc.cvtColor(img, gray, Imgproc.COLOR_BGR2GRAY);

        // 2) Rauschen reduzieren (Median- oder Gaussian-Blur)
        {
            double sigmaX = 1.2;
            double sigmaY = 1.2;
            int borderType = Core.BORDER_CONSTANT;
            Imgproc.GaussianBlur(gray, gray, new Size(9, 9), sigmaX, sigmaY, borderType);
            Tools.showImg("GaussianBlur", MatToBufferedImage.matToBufferedImage(gray), false);
        }

        Mat circles = new Mat();
        int method = Imgproc.HOUGH_GRADIENT;
        // double dp = 1.0;
        // double minDist = 44;
        // double param1 = 100;
        // double param2 = 10; // 30
        // int minRadius = 11;
        // int maxRadius = 13;

        // Recht gut:
        // double dp = 1.0;
        // double minDist = 44;
        // double param1 = 150;
        // double param2 = 10; // 30
        // int minRadius = 11;
        // int maxRadius = 13;

        double dp = 1.0;
        double minDist = 44;
        double param1 = 100;
        double param2 = 20; // 30
        int minRadius = 11;
        int maxRadius = 13;
        Imgproc.HoughCircles(gray, circles, method, dp, minDist, param1, param2, minRadius, maxRadius);
        System.out.println("Gefundene Kreise: " + circles.cols());
        System.out.println("Gefundene Kreise: " + circles);
        System.out.println("rows: " + circles.rows());
        Mat result = new Mat();
        img.copyTo(result);

        int numCircles = circles.cols();
        Point p = new Point();
        float[] data = new float[3];
        int blue = 0;
        int green = 0;
        int red = 255;
        Scalar color = new Scalar(blue, green, red);
        for (int i = 0; i < numCircles; ++i) {
            int getRes = circles.get(0, i, data);
            System.out.println("getRes " + getRes);
            p.x = Math.round(data[0]);
            p.y = Math.round(data[1]);
            int radius = Math.round(data[2]);
            Imgproc.circle(result, p, radius, color);
        }

        var resultImg = MatToBufferedImage.matToBufferedImage(result);
        Tools.showImg("Kreise", resultImg, true);
    }

    private static void testSearchChequerCircles() {
        initOpenCV();

        SearchChequerCircles s = new SearchChequerCircles();
        float[] circles = new float[3 * 50];
        int n = s.run(MyRobot.shot(new Rectangle(0, 0, 1600, 950)), circles);
        System.out.println("n=" + n);
        for (int i = 0; i < n; ++i) {
            float x = circles[i * 3];
            float y = circles[i * 3 + 1];
            float radius = circles[i * 3 + 2];
            System.out.println("x " + x + "  y " + y + "  radius " + radius);
        }
    }

    private static void testSearchBearoffRects() throws Exception {
        initOpenCV();

        SearchBearoffRects s = new SearchBearoffRects();
        BufferedImage bufferedImg = Tools.loadImg(new File("screenshots/bearoffChequers.png"));
        Mat img = BufferedImageToMat.toBgrMat(bufferedImg);
        Imgproc.GaussianBlur(img, img, new Size(9, 9), 0);
        Mat img8bit = new Mat();
        Imgproc.cvtColor(img, img8bit, Imgproc.COLOR_BGR2GRAY);
        Tools.showImg("CV_8S", MatToBufferedImage.matToBufferedImage(img8bit), true);
        s.run(img8bit);
    }

    private static void testDieCircles() throws Exception {
        initOpenCV();

        double blackQuotSum = 0, blackQuotNum = 0;
        double whiteQuotSum = 0, whiteQuotNum = 0;

        String[] colors = { "black", "white" };

        final double pixPerDieWhite = 14.273148148148145;
        final double pixPerDieBlack = 3.9434210526315794; // 3.989102564102564; //4.03333333333333;

        for (String dieColor : colors) {
            boolean whiteDie = dieColor.equals("white");
            final int threshold = whiteDie ? 100 : 185;
            System.out.println("Arbeitsverzeichnis bei Farbe " + dieColor + ": " + System.getProperty("user.dir"));
            File dir = new File("screenshots/dice/" + dieColor);

            System.out.println("dir: " + dir + "  " + dir.isDirectory());
            String[] files = dir.list();
            System.out.println("files: " + files);

            for (String file : files) {
                int die = Integer.parseInt(file.substring(4, 5));
                System.out.println("file " + file + "  die " + die);
                String completePath = dir.getPath() + "/" + file;
                System.out.println("completePath: " + completePath);
                Mat img = Imgcodecs.imread(completePath);
                Mat gray = new Mat();

                Imgproc.cvtColor(img, gray, Imgproc.COLOR_BGR2GRAY);
                Imgproc.GaussianBlur(gray, gray, new Size(7, 7), 1.5, 1.5);
                {
                    MatOfInt channels = new MatOfInt(0);
                    Mat mask = new Mat();
                    Mat hist = new Mat();
                    MatOfInt histSize = new MatOfInt(256);
                    MatOfFloat ranges = new MatOfFloat(0, 256);
                    Imgproc.calcHist(Arrays.asList(gray), channels, mask, hist, histSize, ranges);
                    // System.out.println("hist.dump(): \n" + hist.dump());
                    assert (hist.cols() == 1);
                    assert (hist.rows() == 256);

                    StringBuilder sb = new StringBuilder();

                    double sum = 0;
                    double sumThresh = 0;

                    for (int i = 0; i < 256; ++i) {
                        String nr = String.valueOf(i);
                        for (int j = 0; j < 3 - nr.length(); ++j) {
                            sb.append(' ');
                        }
                        sb.append(nr).append(": ");
                        double val = hist.get(i, 0)[0];
                        sum += val;
                        if ((whiteDie && i <= threshold) || (!whiteDie && i > threshold)) {
                            sumThresh += val;
                        }
                        sb.append(val);
                        sb.append('\n');
                    }
                    double calcDie = (whiteDie ? sumThresh / pixPerDieWhite : sumThresh / pixPerDieBlack);
                    // System.out.println("Hist details:\n" + sb);
                    System.out.println("\n\ndieColor: " + dieColor);
                    System.out.println("die: " + die);
                    System.out.println("Calculated die: " + calcDie);
                    if (Math.round(calcDie) != die) {
                        System.err.println("\n\n***** FEHLER! calcDie " + calcDie + "   die " + die);
                    }
                    System.out.println("sum: " + sum);
                    System.out.println("sumThresh: " + sumThresh);
                    System.out.println("sumThresh / die: " + sumThresh / die);
                    System.out.println("num pixels in img: " + gray.rows() * gray.cols() + "\n\n");

                    if (whiteDie) {
                        whiteQuotSum += sumThresh / die;
                        ++whiteQuotNum;
                    } else {
                        blackQuotSum += sumThresh / die;
                        ++blackQuotNum;
                    }
                }

                {
                    // Zeichne threshold-bild mit allen pixeln <= thresh

                    Mat thresholdImg = new Mat();
                    Imgproc.threshold(gray, thresholdImg, threshold, 255, Imgproc.THRESH_BINARY);
                    Tools.showImg(file + " - threshold " + threshold,
                            MatToBufferedImage.matToBufferedImage(thresholdImg),
                            false);

                    MatOfInt channels = new MatOfInt(0);
                    Mat mask = new Mat();
                    Mat hist = new Mat();
                    MatOfInt histSize = new MatOfInt(256);
                    MatOfFloat ranges = new MatOfFloat(0, 256);
                    Imgproc.calcHist(Arrays.asList(thresholdImg), channels, mask, hist, histSize, ranges);

                    // System.out.println("Hist of thresholdImg:\n" + hist.dump());
                    double sum = whiteDie ? hist.get(0, 0)[0] : hist.get(255, 0)[0];
                    double dieFromThresholdImg = sum / (whiteDie ? pixPerDieWhite : pixPerDieBlack);
                    int dieFromThresholdImg1 = (int) Math.round(dieFromThresholdImg);
                    System.out.println("die " + die + "  dieFromThresholdImg " + dieFromThresholdImg);
                    if (dieFromThresholdImg1 != die) {
                        System.err.println(
                                "\n\n**** FEHLER dieFromThresholdImg1 " + dieFromThresholdImg1 + "   die " + die);
                        throw new IllegalStateException();
                    }
                }

                Mat circles = new Mat();
                int method = Imgproc.HOUGH_GRADIENT_ALT;
                // double dp = 1.20;
                // double minDist = 9;
                // double param1 = 100; // last: 900
                // double param2 = 11; // 30
                // int minRadius = 2;
                // int maxRadius = 5;

                double dp = 1.2;
                double minDist = 100;
                double param1 = 1; // last: 900
                double param2 = 0.1; // 30
                int minRadius = 3;
                int maxRadius = 5;

                // Recht gut:
                // double dp = 1.0;
                // double minDist = 44;
                // double param1 = 150;
                // double param2 = 10; // 30
                // int minRadius = 2;
                // int maxRadius = 4;

                // double dp = 1.0;
                // double minDist = 44;
                // double param1 = 100;
                // double param2 = 20; // 30
                // int minRadius = 2;
                // int maxRadius = 4;
                Imgproc.HoughCircles(gray, circles, method, dp, minDist, param1, param2, minRadius, maxRadius);
                int cols = circles.cols();
                System.out.println("cols " + cols);

                Mat result = new Mat();
                gray.copyTo(result);

                int numCircles = circles.cols();

                Point p = new Point();
                float[] data = new float[3];
                int blue = 0;
                int green = 0;
                int red = 255;
                Scalar color = new Scalar(blue, green, red);
                // System.out.println("elemSize: " + circles.elemSize());
                // System.out.println("elemSize1: " + circles.elemSize1());
                for (int i = 0; i < numCircles; ++i) {
                    int getRes = circles.get(0, i, data);
                    System.out.println("getRes " + getRes);
                    p.x = Math.round(data[0]);
                    p.y = Math.round(data[1]);
                    int radius = Math.round(data[2]);
                    System.out.println("radius " + radius);
                    Imgproc.circle(result, p, radius, color);
                }

                var resultImg = MatToBufferedImage.matToBufferedImage(result);

                Tools.showImg(file + (die == numCircles ? " - KORREKT" : " - FALSCH"), resultImg, false);

                ImgAndMousePosFrame f = new ImgAndMousePosFrame("gray - " + file);
                f.setImg(MatToBufferedImage.matToBufferedImage(gray));
                f.setMat(gray);
                f.setVisible(true);

            }
        }

        System.out.println("White: " + whiteQuotSum / whiteQuotNum);
        System.out.println("Black: " + blackQuotSum / blackQuotNum);

        // String[] files = {
        // "die-2",
        // "die-4",
        // "die-5",
        // "die-6",
        // };
        // int[] dice = {
        // 2,
        // 4,
        // 5,
        // 6
        // };

        // for (int j = 0; j < files.length; ++j) {
        // String file = files[j];
        // int die = dice[j];
        // Mat img = Imgcodecs.imread("screenshots/" + file + ".bmp");
        // Mat gray = new Mat();
        // Imgproc.cvtColor(img, gray, Imgproc.COLOR_BGR2GRAY);
        // Mat circles = new Mat();
        // int method = Imgproc.HOUGH_GRADIENT;
        // double dp = 1.20;
        // double minDist = 9;
        // double param1 = 100; // last: 900
        // double param2 = 11; // 30
        // int minRadius = 2;
        // int maxRadius = 5;

        // // Recht gut:
        // // double dp = 1.0;
        // // double minDist = 44;
        // // double param1 = 150;
        // // double param2 = 10; // 30
        // // int minRadius = 2;
        // // int maxRadius = 4;

        // // double dp = 1.0;
        // // double minDist = 44;
        // // double param1 = 100;
        // // double param2 = 20; // 30
        // // int minRadius = 2;
        // // int maxRadius = 4;
        // Imgproc.HoughCircles(gray, circles, method, dp, minDist, param1, param2,
        // minRadius, maxRadius);
        // int cols = circles.cols();
        // System.out.println("cols " + cols);

        // Mat result = new Mat();
        // img.copyTo(result);

        // int numCircles = circles.cols();

        // Point p = new Point();
        // float[] data = new float[3];
        // int blue = 0;
        // int green = 0;
        // int red = 255;
        // Scalar color = new Scalar(blue, green, red);
        // // System.out.println("elemSize: " + circles.elemSize());
        // // System.out.println("elemSize1: " + circles.elemSize1());
        // for (int i = 0; i < numCircles; ++i) {
        // int getRes = circles.get(0, i, data);
        // System.out.println("getRes " + getRes);
        // p.x = Math.round(data[0]);
        // p.y = Math.round(data[1]);
        // int radius = Math.round(data[2]);
        // System.out.println("radius " + radius);
        // Imgproc.circle(result, p, radius, color);
        // }

        // var resultImg = MatToBufferedImage.matToBufferedImage(result);

        // Tools.showImg(file + (die == numCircles ? " - KORREKT" : " - FALSCH"),
        // resultImg, false);
        // }

    }

    private static void testTemplateSearcher() throws Exception {
        initOpenCV();

        int wsum = 0, hmax = 0;
        var devices = GraphicsEnvironment.getLocalGraphicsEnvironment().getScreenDevices();
        for (var device : devices) {
            var dm = device.getDisplayMode();
            wsum += dm.getWidth();
            hmax = Math.max(hmax, dm.getHeight());
        }
        var screenRect = new Rectangle(0, 0, wsum, hmax);
        TemplateSearchers ts = new TemplateSearchers(screenRect);
        // TemplateSearcher s = new TemplateSearcher("cubeEmpty",
        // TemplateSearcher.defaultMinLimit, ".png");
        // {
        // // TemplateSearcher s = ts.resignFromOpp2();
        // var s = ts.playerBoxWhiteReady();
        // BufferedImage shot = MyRobot.shot(new Rectangle(0, 0, 1600, 800));
        // var res = s.run(shot, true);
        // // java.awt.Point pos = s.search(shot, true);
        // // System.out.println("pos: " + pos);
        // // Falscher maxVal bei nicht-ready: 0.7201589345932007
        // // Richtiger maxVal: 0.9008707404136658
        // System.out.println("playerBoxWhiteReady");
        // System.out.println("maxVal: " + res.maxVal);
        // System.out.println("minVal: " + res.minVal); // korrekt 8: 337060
        // System.out.println("maxLoc: " + res.maxLoc.x + "," + res.maxLoc.y);
        // System.out.println("minLoc: " + res.minLoc.x + "," + res.minLoc.y);
        // }
        // {
        // // TemplateSearcher s = ts.resignFromOpp2();
        // var s = ts.playerBoxWhite();
        // BufferedImage shot = MyRobot.shot(new Rectangle(0, 0, 1600, 800));
        // var res = s.run(shot, true);
        // // java.awt.Point pos = s.search(shot, true);
        // // System.out.println("pos: " + pos);
        // // Falscher maxVal bei ready: 0.63746178150177
        // // Richtiger maxVal: 1.0
        // System.out.println("playerBoxWhite");
        // System.out.println("maxVal: " + res.maxVal);
        // System.out.println("minVal: " + res.minVal); // korrekt 8: 337060
        // System.out.println("maxLoc: " + res.maxLoc.x + "," + res.maxLoc.y);
        // System.out.println("minLoc: " + res.minLoc.x + "," + res.minLoc.y);
        // }
        // {
        // // TemplateSearcher s = ts.resignFromOpp2();
        // var s = ts.playerBoxBlackReady();
        // BufferedImage shot = MyRobot.shot(new Rectangle(0, 0, 1600, 800));
        // var res = s.run(shot, true);
        // // java.awt.Point pos = s.search(shot, true);
        // // System.out.println("pos: " + pos);
        // // falscher maxVal bei nicht ready: 0.7056231498718262
        // // richtiger maxVal: 0.8509844541549683
        // System.out.println("playerBoxBlackReady");
        // System.out.println("maxVal: " + res.maxVal);
        // System.out.println("minVal: " + res.minVal); // korrekt 8: 337060
        // System.out.println("maxLoc: " + res.maxLoc.x + "," + res.maxLoc.y);
        // System.out.println("minLoc: " + res.minLoc.x + "," + res.minLoc.y);
        // }
        // {
        // // TemplateSearcher s = ts.resignFromOpp2();
        // var s = ts.playerBoxBlack();
        // BufferedImage shot = MyRobot.shot(new Rectangle(0, 0, 1600, 800));
        // var res = s.run(shot, true);
        // // java.awt.Point pos = s.search(shot, true);
        // // System.out.println("pos: " + pos);
        // // falscher maxVal bei ready: 0.9595199227333069
        // // richtiger maxVal: 1.0
        // System.out.println("playerBoxBlack");
        // System.out.println("maxVal: " + res.maxVal);
        // System.out.println("minVal: " + res.minVal); // korrekt 8: 337060
        // System.out.println("maxLoc: " + res.maxLoc.x + "," + res.maxLoc.y);
        // System.out.println("minLoc: " + res.minLoc.x + "," + res.minLoc.y);
        // }

        // {
        // // TemplateSearcher s = ts.resignFromOpp2();
        // var s = ts.cube(1);
        // BufferedImage shot = MyRobot.shot(new Rectangle(0, 0, 1600, 800));
        // var res = s.run(shot, true);
        // // java.awt.Point pos = s.search(shot, true);
        // // System.out.println("pos: " + pos);
        // // Falscher maxVal bei nicht-ready: 0.7201589345932007
        // // Richtiger maxVal: 0.9008707404136658
        // System.out.println("cube 1");
        // System.out.println("maxVal: " + res.maxVal);
        // System.out.println("minVal: " + res.minVal); // korrekt 8: 337060
        // System.out.println("maxLoc: " + res.maxLoc.x + "," + res.maxLoc.y);
        // System.out.println("minLoc: " + res.minLoc.x + "," + res.minLoc.y);
        // }

        var s = ts.chequerWhiteStar;
        Thread.sleep(100); // Zeit geben um lastPos zu laden
        var pos = s.run(MyRobot.shot(new Rectangle(0, 0, 1600, 1000)), true);
        System.out.println("pos " + pos);
        s.joinWorkers();
    }

    private static void testFastChequerSearch() throws Exception {
        initOpenCV();

        var x = new TestFastChequerSearch();
        x.setVisible(true);
    }

    private static void testBearInAggregation() throws Exception {
        MutableIntArray move = testMove(25, 24, 25, 24, 24, 23, 24, 23);
        WorkerState s = new WorkerState();
        s.match.getPlayer(0).setInitialField();
        var otherField = s.match.getPlayer(0).field;
        var result = new MutableIntArray(8);
        var tmp = s.tmp;
        Move.aggregate(1, 1, move, otherField, result, tmp);
        // expected: 25/24 25/23 24/23
        if (result.length() != 6 ||
                result.at(0) != 25 ||
                result.at(1) != 24 ||
                result.at(2) != 25 ||
                result.at(3) != 23 ||
                result.at(4) != 24 ||
                result.at(5) != 23) {

            var sb = new StringBuilder();
            sb.append("Calculated move: ");
            result.append(sb);
            sb.append("\nExpected move: 25 24 25 23 24 23");
            System.out.println(sb);
        } else {
            System.out.println("testBearinAggregation[1] successful.");
        }

        move = testMove(25, 24, 24, 23, 23, 22, 22, 21);
        Move.aggregate(1, 1, move, otherField, result, tmp);
        if (!testMove(25, 21).equals(result)) {
            var sb = new StringBuilder();
            sb.append("Calculated move: ");
            result.append(sb);
            sb.append("\nExpected move: 25 21");
            System.out.println(sb);
        } else {
            System.out.println("testBearinAggregation[2] successful.");
        }
    }

    private static void testMoveSplit() throws Exception {

    }

    private static void testMoveSplit(int die1, int die2, MutableIntArray input, MutableIntArray expectedOut,
            Field otherField, MutableIntArray out, MutableIntArray otherChequers) {
        otherField.clear();
        {
            int n = otherChequers.length();
            for (int i = 0; i < n; ++i) {
                int field = otherChequers.at(i);
                otherField.set(field, otherField.getChequers(field) + 1);
            }
        }

        Move.split(die1, die2, input, otherField, out);
    }

    private static void testJavaEfficiency() throws Exception {
        int n = 100000000;
        int loops = 100;
        var rnd = ThreadLocalRandom.current();
        MutableIntArray src = new MutableIntArray(n);
        MutableIntArray dst = new MutableIntArray(n);
        System.out.println("Build src ...");
        for (int i = 0; i < n; ++i) {
            src.add(rnd.nextInt());
        }

        var start1 = System.currentTimeMillis();
        for (int i = 0; i < loops; ++i) {
            dst.clear();
            dst.addRange(src, 0, n);
        }
        var end1 = System.currentTimeMillis();

        var start2 = System.currentTimeMillis();
        for (int i = 0; i < loops; ++i) {
            dst.clear();
            dst.addRange2(src, 0, n);
        }
        var end2 = System.currentTimeMillis();

        System.out.println("Duration 1: " + (end1 - start1));
        System.out.println("Duration 2: " + (end2 - start2));

    }

    private static BufferedImage toBGR(BufferedImage src) {
        BufferedImage dst = new BufferedImage(src.getWidth(), src.getHeight(), BufferedImage.TYPE_3BYTE_BGR);
        Graphics2D g = dst.createGraphics();
        g.setComposite(AlphaComposite.Src); // kopiert RGB, ignoriert Alpha
        g.drawImage(src, 0, 0, null);
        g.dispose();
        return dst;
    }

    private static void testChequersFromImg() throws Exception {
        initOpenCV();

        var img = toBGR(Tools.loadImg("screenshots/BUG_2025-10-14_18-10-17.png"));

        var forCalWhite = Tools.loadImg("screenshots/forCalWhite.png");
        CalibrationForSpin cal = new CalibrationForSpin(forCalWhite);
        TemplateSearchers ts = new TemplateSearchers(new Rectangle(1600, 1000));

        ts.chequerWhite.run(img, true);

        // fuer schwarz threshold wohl 0.8

        // FastChequerSearch chequers = new FastChequerSearch(cal, ts);
        // chequers.init(img);

    }

    public static void main(String[] args) throws Exception {
        // assert(false);
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
        // testOpenCv();
        // testSearchChequerCircles();
        // testSearchBearoffRects();
        // testDieCircles();
        // testTemplateSearcher();
        // testFastChequerSearch();
        // testBearInAggregation();
        // testMoveSplit();
        // testJavaEfficiency();
        // testChequersFromImg();
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