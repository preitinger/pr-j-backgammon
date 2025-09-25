package pr.backgammon.spin;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GraphicsEnvironment;
import java.awt.GridLayout;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.image.BufferedImage;
import java.awt.image.Raster;
import java.io.IOException;
import java.util.ArrayList;

import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.SwingWorker;
import javax.swing.Timer;

import pr.backgammon.control.AllMoves;
import pr.backgammon.model.FindTaskArray;
import pr.backgammon.model.Match;
import pr.backgammon.model.OngoingMove;
import pr.backgammon.model.Roll;
import pr.backgammon.spin.control.BoardSearchers;
import pr.backgammon.spin.control.BoardSearchers.ResignResponse;
import pr.backgammon.spin.control.CalibrationForSpin;
import pr.backgammon.spin.control.CalibrationForSpin.Chequer;
import pr.backgammon.spin.control.FastChequerSearch;
import pr.backgammon.spin.control.Rolls;
import pr.backgammon.spin.control.SpinRolls;
import pr.backgammon.spin.control.workers.ChatTextViaClipboard;
import pr.backgammon.spin.control.workers.ReadPlayerNames;
import pr.backgammon.spin.view.AllMovesPanel;
import pr.backgammon.spin.view.ScreenshotDialog;
import pr.backgammon.spin.view.SpinTrackFrame;
import pr.backgammon.view.MatchView;
import pr.control.MyRobot;
import pr.control.Searcher;
import pr.model.MutableArray;
import pr.model.MutableIntArray;

/**
 * Abgeloest von MatchControl; aber wird noch wegen der praktischen Tools wie Positionsanzeige, Screenshots, etc. verwendet.
 */
public class SpinTracking implements SpinTrackFrame.Listener {
    abstract class State {
        final String type;

        State(String type) {
            this.type = type;
        }

        String getType() {
            return type;
        }

        void calibrate() {
            System.out.println("No calibration in state " + type);
        }

        void startTracking() {
            System.out.println("Tracking cannot be started in state " + type);
        }

        void trackNow() {
            System.out.println("trackNow() not allowed in state " + type);
        }

        public void testScan() {
            System.out.println("testScan() not allowed in state " + type);
        }

        public void abortMatch() {
            System.out.println("abortMatch() not allowed in state " + type);
        }

        public void cancel() {
            System.out.println("cancel() not allowed in state " + type);
        }

        void screenshot() {
            System.out.println("screenshot() not allowed in state " + type);
        }

    }

    class Idle extends State {

        Idle() {
            super("idle");
        }

        @Override
        void calibrate() {
            try {
                cal = new CalibrationForSpin();
                if (cal.ownWhite) {
                    calWhite = cal;
                } else {
                    calBlack = cal;
                }
                fastChequerSearch = new FastChequerSearch(cal);
                rolls = new Rolls(cal); // TODO ggf. ersetzen
                fastChequerSearch.boardScreenshotRect(tmpRect);
                searchers = new BoardSearchers(cal, tmpRect);
                spinRolls = new SpinRolls(cal, tmpRect);
                String s = "Kalibriert für " + (calWhite != null && calBlack == null ? "Weiß"
                        : calWhite == null && calBlack != null ? "Schwarz" : "Weiß und Schwarz");
                System.out.println(s);
                frame.setCalibration(s);

            } catch (Exception e) {
                e.printStackTrace();
                JOptionPane.showMessageDialog(frame, "Kein Brett erkannt!");
            }
        }

        @Override
        void startTracking() {
            if (cal == null) {
                frame.addNotification("Erst kalibrieren!");
            } else {
                frame.setTrackVisible(false);
                frame.setTrackInitVisible(true);
                state = TRACKING_INIT;
            }
        }

        @Override
        void screenshot() {
            javax.swing.Timer timer = new javax.swing.Timer(2000, new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {

                    BufferedImage shot;
                    if (fastChequerSearch != null && !frame.getWholeScreen()) {
                        shot = searchers.boardShot();
                    } else {
                        shot = MyRobot.shot(screenRect);
                    }
                    ScreenshotDialog dlg = new ScreenshotDialog(frame, shot);
                    dlg.pack();
                    dlg.setVisible(true);

                }
            });
            timer.setRepeats(false);
            timer.start();

        }
    }

    public final ArrayList<SwingWorker<?, ?>> workers = new ArrayList<>();

    class TrackingInit extends State {
        TrackingInit() {
            super("trackingInit");
        }

        @Override
        void trackNow() {

            int matchLen = Integer.parseInt(frame.getMatchLen());
            int scoreOpp = Integer.parseInt(frame.getScoreOpp());
            int scoreOwn = Integer.parseInt(frame.getScoreOwn());
            match = new Match(new String[] { frame.getNameOpp(), frame.getNameOwn() }, 1,
                    matchLen, scoreOpp, scoreOwn, false);
            match.cube.used = false; // TODO detect automatically if cube is used
            // matchWriter = new MatchWriter2(matchLen, frame.getNameOwn(),
            // frame.getNameOpp());

            frame.setTrackInitVisible(false);
            matchView.setMatch(match, new OngoingMove());
            matchView.setClockwise(cal.ownClockwise);
            matchView.setOwnWhite(cal.ownWhite);
            frame.setTrackMainVisible(true);

            // final StringBuilder sb = matchWriter.appendMatch(null, matchLen,
            // frame.getNameOwn(), frame.getNameOpp());
            // matchWriter.appendGame(sb, 1, scoreOpp, scoreOwn);

            // BufferedImage boardShot = searchers.boardShot();
            // Raster boardRaster = boardShot.getRaster();
            // FirstAnalysis firstAnalysis = new FirstAnalysis(searchers, boardRaster) {
            // @Override
            // protected void resultOnEventDispatchThread(FirstAnalysisRes firstAnalysis) {
            // System.out.println("first Analysis returned " + firstAnalysis.type);
            // switch (firstAnalysis.type) {
            // case OWN_MATCH: {
            // System.out.println("initial roll " + firstAnalysis.initialRoll.toString());
            // if (firstAnalysis.initialRoll != null) {
            // match.debug(firstAnalysis.chequersAsForMatchDebug, firstAnalysis.initialRoll,
            // null,
            // null, 1,
            // false);
            // matchWriter.appendRoll(sb, match.getActivePlayer() == 1,
            // firstAnalysis.initialRoll.die1(),
            // firstAnalysis.initialRoll.die2());
            // appendText(sb.toString());
            // sb.setLength(0);

            // WaitForOwnMove wfow = new WaitForOwnMove(searchers) {
            // @Override
            // protected void resultOnEventDispatchThread(WaitForOwnMoveRes result) {
            // switch (result.type) {
            // case OWN_MOVE:
            // StringBuilder sb = matchWriter.appendMove(null, true, result.move);
            // appendText(sb.toString());
            // throw new RuntimeException("nyi");
            // // break;
            // case DLG:
            // throw new RuntimeException("nyi");
            // }
            // }
            // };
            // wfow.execute();
            // } else {
            // throw new RuntimeException("nyi");
            // }
            // break;
            // }
            // default:
            // break;
            // }

            // appendText(sb.toString());

            // throw new RuntimeException("nyi");

            // }
            // };
            // firstAnalysis.execute();
            // workers.add(firstAnalysis);

            // var trackBoard = new TrackBoard(calWhite, calBlack) {
            //     @Override
            //     protected void process(List<TrackBoardMsg> chunks) {
            //         System.out.println("processing chunks");
            //         StringBuilder sb = new StringBuilder("Messages:\n");
            //         for (TrackBoardMsg chunk : chunks) {
            //             sb.append(chunk.msg).append('\n');
            //         }
            //         appendText(sb.toString());
            //     }
            // };
            // trackBoard.execute();
            // workers.add(trackBoard);

            state = TRACKING_MAIN;
        }

        @Override
        public void cancel() {
            for (var sw : workers) {
                sw.cancel(true);
            }
            workers.clear();
            frame.setTrackInitVisible(false);
            frame.setTrackVisible(true);
            state = IDLE;
        }
    }

    class TrackingMain extends State {
        TrackingMain() {
            super("trackingMain");
        }

        @Override
        public void testScan() {
            fastScanField();
            matchView.repaint();
            System.out.println("nach testScan");
            System.out.println(match.appendPosition(new StringBuilder("\n\nCurrent Position:\n\n")).toString());
            MutableArray<MutableIntArray> out = new MutableArray<MutableIntArray>(
                    15 * 15 * 15 * 15) {
                @Override
                protected MutableIntArray createInstance() {
                    return new MutableIntArray(8);
                }
            };
            FindTaskArray todo = new FindTaskArray(15 * 15 * 15 * 15);
            AllMoves.find(match, out, todo);
            JDialog dlg = new JDialog(frame);
            Roll roll = match.roll;
            if (roll != null) {
                dlg.add(new AllMovesPanel(match.getActivePlayer() == 1, roll.die1(), roll.die2(), out));

                dlg.pack();
                dlg.setVisible(true);
            }
            // Field.findAllMoves(match.getPl, null, 0, 0, null, null);
        }

        @Override
        public void abortMatch() {
            for (var worker : workers) {
                worker.cancel(true);
            }

            workers.clear();

            match = null;
            frame.setTrackVisible(true);
            frame.setTrackMainVisible(false);
            state = IDLE;
        }
    }

    private final State IDLE = new Idle(),
            TRACKING_INIT = new TrackingInit(),
            TRACKING_MAIN = new TrackingMain();

    private State state = IDLE;
    private CalibrationForSpin calWhite = null, calBlack = null, cal = null;
    private FastChequerSearch fastChequerSearch = null;
    private final SpinTrackFrame frame;
    private Match match = null;

    private final MatchView matchView;
    private final Rectangle screenRect = new Rectangle(), tmpRect = new Rectangle();
    private Rolls rolls = null;
    private SpinRolls spinRolls = null;
    private BoardSearchers searchers = null;

    public SpinTracking() {

        matchView = new MatchView(/* new Match(new String[] { "FAKE 1", "FAKE"}, 1, 5, false) */ null, true, false);
        frame = new SpinTrackFrame(this, matchView);
        screenRect.setLocation(0, 0);
        // We assume that all screens are horizontally aligned and sum up all widths
        int wsum = 0, hmax = 0;
        var devices = GraphicsEnvironment.getLocalGraphicsEnvironment().getScreenDevices();
        for (var device : devices) {
            var dm = device.getDisplayMode();
            wsum += dm.getWidth();
            hmax = Math.max(hmax, dm.getHeight());
        }
        screenRect.setSize(wsum, hmax);
    }

    private BufferedImage captureScreen() {
        return MyRobot.shot(screenRect);
    }

    private boolean swapCalibration() throws IOException {
        if (cal == calWhite) {
            if (calBlack != null) {
                setCalibration(calBlack);

                return true;
            } else {
                return false;
            }
        } else {
            if (calWhite != null) {
                setCalibration(calWhite);
                return true;
            } else {
                return false;
            }
        }
    }

    private void setCalibration(CalibrationForSpin newCal) throws IOException {
        this.cal = newCal;
        fastChequerSearch = new FastChequerSearch(cal);
        rolls = new Rolls(cal); // TODO ggf. ersetzen
        fastChequerSearch.boardScreenshotRect(tmpRect);
        searchers = new BoardSearchers(cal, tmpRect);
        spinRolls = new SpinRolls(cal, tmpRect);

    }

    private void fastScanField() {
        fastChequerSearch.boardScreenshotRect(tmpRect);
        BufferedImage board = MyRobot.shot(tmpRect);
        Raster boardRaster = board.getRaster();
        fastChequerSearch.init(boardRaster);
        int[] a = new int[26 * 2];
        for (int field = 24; field >= 1; --field) {
            pr.backgammon.spin.model.Player player = fastChequerSearch.playerOnField(field);
            a[field * 2] = field;
            a[field * 2 + 1] = fastChequerSearch.numChequersOnField(field)
                    * (player == pr.backgammon.spin.model.Player.OTHER ? -1 : 1);
        }
        a[25 * 2] = 25;
        int[] chequersOnBar = fastChequerSearch.chequersOnBars(null);
        a[25 * 2 + 1] = chequersOnBar[0]; // own chequers positive at 0
        a[0] = 0;
        a[0 + 1] = -chequersOnBar[1]; // opp chequers positive at 1

        // rolls noch nicht endgueltig vorerst zum testen beibehalten aus scanField():
        // rolls.detectDiceFromFullScreenshot(captureScreen()); // dies natuerlich
        // ineffizient, da hier neuer kompletter
        // // screenshot gemacht wird.
        spinRolls.detectFromBoardShot(boardRaster);
        spinRolls.dump();
        Roll initialRoll = null, oppRoll = null, ownRoll = null;
        if (spinRolls.isInitialDice()) {
            initialRoll = new Roll(spinRolls.die1(), spinRolls.die2());
        } else if (spinRolls.isOppDice()) {
            oppRoll = new Roll(spinRolls.die1(), spinRolls.die2());
        } else if (spinRolls.isOwnDice()) {
            ownRoll = new Roll(spinRolls.die1(), spinRolls.die2());
        }
        this.match.debug(a, initialRoll, oppRoll, ownRoll, 1, false); // TODO cube noch nicht erkannt - auch hier nur
                                                                      // ein test

    }

    private void scanField() {
        BufferedImage screen = captureScreen();
        ArrayList<Chequer> visibleChequers = cal.searchAllVisibleChequers(screen);
        int[] a = new int[26 * 2];
        for (int field = 24; field >= 1; --field) {
            pr.backgammon.spin.model.Player player = cal.playerOnField(visibleChequers, field);
            a[field * 2] = field;
            a[field * 2 + 1] = cal.numChequersOnField(visibleChequers, field)
                    * (player == pr.backgammon.spin.model.Player.OTHER ? -1 : 1);
        }
        rolls.detectDiceFromFullScreenshot(screen);
        Roll initialRoll = null, oppRoll = null, ownRoll = null;
        if (rolls.isInitialDice()) {
            initialRoll = new Roll(rolls.getInitialDie1(), rolls.getInitialDie2());
        } else if (rolls.isOppDice()) {
            oppRoll = new Roll(rolls.getOppDie1(), rolls.getOppDie2());
        } else if (rolls.isOwnDice()) {
            ownRoll = new Roll(rolls.getOwnDie1(), rolls.getOwnDie2());
        }
        this.match.debug(a, initialRoll, oppRoll, ownRoll, 1, false);
    }

    public void run() {
        frame.setTrackInitVisible(false);
        frame.setTrackMainVisible(false);
        frame.setVisible(true);
    }

    public void calibrate() {
        state.calibrate();
    }

    @Override
    public void startTracking() {
        state.startTracking();
    }

    @Override
    public void trackNow() {
        state.trackNow();
    }

    @Override
    public void testScan() {
        state.testScan();

    }

    public void abortMatch() {
        state.abortMatch();
    }

    @Override
    public void cancel() {
        state.cancel();
    }

    @Override
    public void screenshot() {
        state.screenshot();
    }

    @Override
    public void debugAction() {
        // debugBereit();
        // debugDlgCorner();
        // debugResignAny();
        // debugSearchAll();
        // debugToggleContinuousSearch();
        // debugWaitForResignResponse();
        // debugFirstAnalysis();
        // debugChatTextViaClipboard();
        // debugMouseDlg();
        debugReadPlayerNames();
    }

    @SuppressWarnings("unused")
    private void debugBereit() {
        try {
            if (fastChequerSearch == null) {
                System.out.println("not yet calibrated!");
                return;
            }
            Rectangle boardRect = fastChequerSearch.boardScreenshotRect(null);
            BufferedImage boardImg = MyRobot.shot(boardRect);
            Point found = searchers.bereit.run(boardImg.getRaster());
            System.out.println("bereit found: " + found);

            if (found != null) {
                MyRobot.move(boardRect.x + found.x, boardRect.y + found.y);
            }

        } catch (Exception ex) {
            ex.printStackTrace();
        }

    }

    @SuppressWarnings("unused")
    private void debugDlgCorner() {
        try {
            if (fastChequerSearch == null) {
                System.out.println("not yet calibrated!");
                return;
            }
            Rectangle boardRect = fastChequerSearch.boardScreenshotRect(null);
            BufferedImage boardImg = MyRobot.shot(boardRect);
            Point found = searchers.dlgCorner.run(boardImg.getRaster());
            System.out.println("dlgCorner found: " + found);

            if (found != null) {
                MyRobot.move(boardRect.x + found.x, boardRect.y + found.y);
            }

        } catch (Exception ex) {
            ex.printStackTrace();
        }

    }

    @SuppressWarnings("unused")
    private void debugResignAny() {
        try {
            if (fastChequerSearch == null) {
                JOptionPane.showMessageDialog(frame, "Not calibrated!");
                System.out.println("not yet calibrated!");
                return;
            }
            Rectangle boardRect = fastChequerSearch.boardScreenshotRect(null);

            // Vor dem Screenshot dem Tester Zeit geben, um den "Einfach Button" gedrueckt
            // zu halten:

            Timer timer = new Timer(2000, new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    BufferedImage boardImg = MyRobot.shot(boardRect);
                    Raster boardRaster = boardImg.getRaster();
                    StringBuilder sb = new StringBuilder();
                    Point found;

                    found = searchers.dlgCorner.run(boardRaster);
                    sb.append("dlgCorner found: " + found);
                    sb.append('\n');

                    found = searchers.resignFromOwn1.run(boardRaster);
                    sb.append("resignFromOwn1 found: " + found);
                    sb.append('\n');

                    found = searchers.resignFromOwn2.run(boardRaster);
                    sb.append("resignFromOwn2 found: " + found);
                    sb.append('\n');

                    found = searchers.resignFromOwn3.run(boardRaster);
                    sb.append("resignFromOwn3 found: " + found);
                    sb.append('\n');

                    found = searchers.resignFromOpp1.run(boardRaster);
                    sb.append("resignFromOpp1 found: " + found);
                    sb.append('\n');

                    found = searchers.resignFromOpp2.run(boardRaster);
                    sb.append("resignFromOpp2 found: " + found);
                    sb.append('\n');

                    found = searchers.resignFromOpp3.run(boardRaster);
                    sb.append("resignFromOpp3 found: " + found);
                    sb.append('\n');

                    found = searchers.ja.run(boardRaster);
                    sb.append("ja found: " + found);
                    sb.append('\n');

                    found = searchers.nein.run(boardRaster);
                    sb.append("nein found: " + found);
                    sb.append('\n');

                    JOptionPane.showMessageDialog(frame, sb.toString());
                    // if (found != null) {
                    // r.mouseMove(boardRect.x + found.x, boardRect.y + found.y);
                    // }

                }
            });
            timer.setRepeats(false);
            timer.start();

        } catch (Exception ex) {
            ex.printStackTrace();
        }

    }

    @SuppressWarnings("unused")
    private void debugSearchAll() {
        try {
            if (fastChequerSearch == null) {
                JOptionPane.showMessageDialog(frame, "Not calibrated!");
                System.out.println("not yet calibrated!");
                return;
            }
            Rectangle boardRect = fastChequerSearch.boardScreenshotRect(null);

            // Vor dem Screenshot dem Tester Zeit geben, um den "Einfach Button" gedrueckt
            // zu halten:

            Timer timer = new Timer(2000, new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    BufferedImage boardImg = MyRobot.shot(boardRect);
                    Raster boardRaster = boardImg.getRaster();
                    StringBuilder sb = new StringBuilder();
                    Point found;

                    Searcher[] allSearchers = {
                            searchers.annehmen,
                            searchers.aufgeben,
                            searchers.bereit,
                            searchers.dlgCorner,
                            searchers.ja,
                            searchers.nein,
                            searchers.resignFromOpp1,
                            searchers.resignFromOpp2,
                            searchers.resignFromOpp3,
                            searchers.resignFromOwn1,
                            searchers.resignFromOwn2,
                            searchers.resignFromOwn3,
                            searchers.verdoppeln,

                            searchers.verdoppelnOpp,
                            searchers.cube2,
                            searchers.cube4,
                            searchers.cube8,
                            searchers.cube16,
                            searchers.cube32,
                    };
                    String[] allNames = {
                            "annehmen",
                            "aufgeben",
                            "bereit",
                            "dlgCorner",
                            "ja",
                            "nein",
                            "resignFromOpp1",
                            "resignFromOpp2",
                            "resignFromOpp3",
                            "resignFromOwn1",
                            "resignFromOwn2",
                            "resignFromOwn3",
                            "verdoppeln",
                            "verdoppelnOpp",
                            "cube2",
                            "cube4",
                            "cube8",
                            "cube16",
                            "cube32",
                    };

                    for (int i = 0; i < allSearchers.length; ++i) {
                        Searcher s = allSearchers[i];
                        String name = allNames[i];
                        found = s.run(boardRaster);
                        if (found != null)
                            sb.append(name).append(" ").append(found.x).append(',').append(found.y).append('\n');
                    }

                    JOptionPane.showMessageDialog(frame, sb.toString());
                    // if (found != null) {
                    // r.mouseMove(boardRect.x + found.x, boardRect.y + found.y);
                    // }

                }
            });
            timer.setRepeats(false);
            timer.start();

        } catch (Exception ex) {
            ex.printStackTrace();
        }

    }

    private Timer continuousSearchTimer = null;
    private ContinuousSearchDlg continuousSearchDlg = null;

    // private BufferedWriter fileWriter;
    // private MatchWriter2 matchWriter;

    @SuppressWarnings("unused")
    private void debugToggleContinuousSearch() {
        try {
            if (continuousSearchTimer == null) {
                // lazy
                continuousSearchTimer = new Timer(10, new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        onContinuousSearch();

                    }
                });
            }
            if (continuousSearchDlg == null) {
                // lazy
                continuousSearchDlg = new ContinuousSearchDlg(frame, searchers,
                        fastChequerSearch.boardScreenshotRect(null));
            }
            if (continuousSearchTimer.isRunning()) {
                continuousSearchTimer.stop();
                continuousSearchDlg.setVisible(false);
                System.out.println("continuous invisible");
            } else {
                continuousSearchDlg.setVisible(true);
                continuousSearchTimer.start();
                System.out.println("continuous visible");
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    void onContinuousSearch() {
        continuousSearchDlg.update();
    }

    @SuppressWarnings("unused")
    private void debugWaitForResignResponse() {
        new Thread() {
            String msg = null;

            @Override
            public void run() {
                try {
                    ResignResponse response = searchers.waitForResignResponse();
                    switch (response) {
                        case YES:
                            msg = "Ja";
                            break;
                        case NO:
                            msg = "Nein";
                            break;
                        case TOO_FAST:
                            msg = "Zu schnell!";
                            break;
                    }
                } catch (InterruptedException ex) {
                    msg = "InterruptedException";
                }
                SwingUtilities.invokeLater(new Runnable() {
                    @Override
                    public void run() {
                        JOptionPane.showMessageDialog(frame, msg, "Resign Result", JOptionPane.INFORMATION_MESSAGE);
                    }
                });
            }
        }.start();
    }

    // @SuppressWarnings("unused")
    // private void debugFirstAnalysis() {

    // try {
    // String msg = null;
    // Raster board = searchers.boardShot().getRaster();
    // FirstAnalysisRes response = searchers.firstAnalysis(board);
    // if (response.chequersAsForMatchDebug != null && match != null) {
    // System.out.println("before match.debug");
    // match.debug(response.chequersAsForMatchDebug, response.initialRoll,
    // response.oppRoll, response.ownRoll,
    // response.cubeVal, false); // TODO cubeOffered noch nicht erkannt
    // matchView.repaint();
    // }
    // boolean rightCal = response.calWhite == this.cal.ownWhite;
    // msg = response.type.toString() + " "
    // + (rightCal ? "richtige Kalibrierung" : "falsche Kalibrierung");
    // JOptionPane.showMessageDialog(frame, msg, "First Analysis Result",
    // JOptionPane.INFORMATION_MESSAGE);

    // if (!rightCal) {
    // msg = swapCalibration() ? "Kalibrierung vertauscht." : "Kalibrierung konnte
    // nicht vertauscht werden!";
    // JOptionPane.showMessageDialog(frame, msg);
    // }
    // } catch (Exception ex) {
    // ex.printStackTrace();
    // }
    // }

    private void debugChatTextViaClipboard() {
        if (cal == null) {
            JOptionPane.showMessageDialog(frame, "Nicht kalibriert!");
            return;
        }
        // Rectangle sel = new Rectangle(cal.left - cal.dxLeft,
        // cal.bottom + 3 * cal.dy, cal.right + cal.dxRight - (cal.left - cal.dxLeft),
        // cal.dy * 3);
        // if (true) return;
        new ChatTextViaClipboard(1000) {
            @Override
            public void resultOnEventDispatchThread(String result) {
                JOptionPane.showMessageDialog(frame, "from chat window: '" + result + "'");
            }
            // protected void done() {
            // JOptionPane.showMessageDialog(frame, "from chat window: '" + get() + "'");
            // }
        }.execute();
    }

    private void debugMouseDlg() {
        final JDialog dlg = new JDialog(frame);
        final JLabel pos = new JLabel();
        dlg.add(pos);

        dlg.addComponentListener(new ComponentAdapter() {
            Point location = new Point();
            Dimension size = new Dimension();

            @Override
            public void componentMoved(ComponentEvent e) {
                location = dlg.getLocationOnScreen();
                updatePos();
            }

            @Override
            public void componentResized(ComponentEvent e) {
                dlg.getSize(size);
                updatePos();
            }

            private void updatePos() {
                pos.setText("(" + location.x + "," + location.y + ")  " + size.width + "x" + size.height);
            }
        });
        dlg.setSize(50, 50);
        dlg.setVisible(true);
    }

    private void debugReadPlayerNames() {
        if (cal == null) {
            JOptionPane.showMessageDialog(frame, "Nicht kalibriert!");
            return;
        }
        String[] playerNames = { null, null };
        Raster board = searchers.boardShot().getRaster();
        (new ReadPlayerNames(searchers, board, playerNames) {
            @Override
            public void resultOnEventDispatchThread(Void result) {
                JOptionPane.showMessageDialog(frame,
                        "Look at System.out - name 0 '" + playerNames[0] + "'  name 1 '" + playerNames[1] + "'");
            }
        }).execute();
    }

    private void appendText(String s) {
        frame.appendText(s);
    }

}

class ContinuousSearchDlg extends JDialog {
        final Searcher[] allSearchers;
    final String[] allNames;
    final JTextField[] results;
    final Rectangle boardRect;
    final JTextArea log;

    ContinuousSearchDlg(JFrame owner, BoardSearchers searchers, Rectangle boardRect) {
        super(owner, "Continuous Search", ModalityType.DOCUMENT_MODAL);
        this.allSearchers = new Searcher[] {
                // searchers.annehmen, searchers.aufgeben, searchers.bereit,

                searchers.dlgCorner,
                searchers.ja,
                searchers.nein,

                // searchers.resignFromOpp1, searchers.resignFromOpp2,
                // searchers.resignFromOpp3, searchers.resignFromOwn1, searchers.resignFromOwn2,
                // searchers.resignFromOwn3,
                // searchers.verdoppeln,

                // searchers.verdoppelnOpp, searchers.cube2, searchers.cube4, searchers.cube8,
                // searchers.cube16,
                // searchers.cube32,

        };
        this.allNames = new String[] {
                // "annehmen",
                // "aufgeben",
                // "bereit",

                "dlgCorner",
                "ja",
                "nein",

                // "resignFromOpp1",
                // "resignFromOpp2",
                // "resignFromOpp3",
                // "resignFromOwn1",
                // "resignFromOwn2",
                // "resignFromOwn3",
                // "verdoppeln",
                // "verdoppelnOpp",
                // "cube2",
                // "cube4",
                // "cube8",
                // "cube16",
                // "cube32",
        };

        this.boardRect = boardRect;

        setLayout(new BorderLayout());
        JPanel grid = new JPanel(new GridLayout(0, 2));

        results = new JTextField[allSearchers.length];

        for (int i = 0; i < allSearchers.length; ++i) {
            grid.add(new JLabel(allNames[i]));
            grid.add(results[i] = new JTextField(20));
        }

        add(grid, BorderLayout.CENTER);

        log = new JTextArea();
        JScrollPane sp = new JScrollPane(log);
        sp.setPreferredSize(new Dimension(300, 300));
        add(sp, BorderLayout.SOUTH);
        pack();
    }

    void update() {
        System.out.println("update");
        BufferedImage boardImg = MyRobot.shot(boardRect);
        Raster boardRaster = boardImg.getRaster();
        StringBuilder sb = new StringBuilder();

        for (int i = 0; i < allSearchers.length; ++i) {
            Point found = allSearchers[i].run(boardRaster);
            String newText = found == null ? "" : found.x + "," + found.y;
            if (!newText.equals(results[i].getText())) {
                sb.append(allNames[i]).append(": ").append(newText).append('\n');
            }
            results[i].setText(newText);
        }

        if (sb.length() > 0) {
            log.append(sb.append('\n').toString());
        }
    }
}