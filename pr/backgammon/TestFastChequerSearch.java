package pr.backgammon;

import java.awt.BorderLayout;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.Timer;

import pr.backgammon.model.Match;
import pr.backgammon.spin.control.CalibrationForSpin;
import pr.backgammon.spin.control.FastChequerSearch;
import pr.backgammon.spin.control.TemplateSearchers;
import pr.backgammon.view.MatchView;
import pr.control.MyRobot;
import pr.control.MyWorker;
import pr.view.IGrid;

public class TestFastChequerSearch extends JFrame {
    CalibrationForSpin cal = null;
    JButton calibrate = new JButton("Calibrate"), start = new JButton("start"), stop = new JButton("stop"),
            check = new JButton("check");
    JLabel status = new JLabel();
    JPanel center = new JPanel();
    JPanel south = new JPanel();
    TemplateSearchers ts;
    FastChequerSearch cs;
    Rectangle boardScreenshotRect;
    final MatchView matchView = new MatchView(null, true, false);
    final Match match = new Match();
    final Timer timer = new Timer(500, (e) -> {
    });
    private final StringBuilder sb = new StringBuilder();
    private ActionListener timerListener = new ActionListener() {
        @Override
        public void actionPerformed(ActionEvent arg0) {
            final BufferedImage shot = MyRobot.shot(boardScreenshotRect);
            // Runnable onError = () -> {
            // timer.stop();
            // ts.chequerWhite.run(shot);
            // ts.chequerBlack.run(shot);
            // // var circleWatcher = new CircleWatcher();
            // // circleWatcher.pack();
            // // circleWatcher.setVisible(true);
            // // // circleWatcher.start();
            // // circleWatcher.setShot(shot);
            // // System.out.println("CircleWatcher shown");
            // // start.setVisible(true);

            // };
            try {
                cs.init(shot);
                cs.getFields(match.getPlayer(match.own).field, match.getPlayer(1 - match.own).field);
                matchView.setMatch(match, null);
                // sb.setLength(0);
                // sb.append("Own field:\n");
                // ownField.append(sb);
                // sb.append("Opp field:\n");
                // oppField.append(sb);
                // System.out.println(sb);
            } catch (Exception ex) {
                ex.printStackTrace();
                timer.stop();
                ts.chequerWhite.run(shot, true);
                ts.chequerWhiteStar.run(shot, true);
                ts.chequerBlack.run(shot, true);
                ts.chequerBlackStar.run(shot, true);
            }
        }
    };

    public TestFastChequerSearch() {
        match.own = 1;
        match.matchLen = 3;

        setLayout(new BorderLayout(20, 20));
        center.setLayout(new BorderLayout());

        IGrid grid = IGrid.create();
        center.add(matchView);
        south.add(status);
        south.add(calibrate);
        south.add(start);
        south.add(stop);
        south.add(check);
        // add(center, BorderLayout.CENTER);
        grid.rect(0, 0, 1, 1);
        grid.fill().both();
        grid.weight().x(1);
        grid.weight().y(1);
        // center.setBorder(BorderFactory.createBevelBorder(BevelBorder.LOWERED, Color.RED, Color.BLACK));
        grid.add(center);

        grid.rect(0, 1, 1, 1);
        grid.fill().none();
        grid.weight().x(0);
        grid.weight().y(0);
        grid.add(south);
        // add(south, BorderLayout.SOUTH);
        // grid.asComponent().setBorder(BorderFactory.createBevelBorder(BevelBorder.LOWERED, Color.RED, Color.BLACK));
        add(grid.asComponent(), BorderLayout.CENTER);
        timer.addActionListener(timerListener);
        start.addActionListener((e) -> {
            startTimer();
        });
        stop.addActionListener((e) -> {
            stopTimer();
        });
        check.addActionListener((e) -> {
            final BufferedImage shot = MyRobot.shot(boardScreenshotRect);
            ts.chequerWhite.run(shot, true);
            ts.chequerWhiteStar.run(shot, true);
            ts.chequerBlack.run(shot, true);
            ts.chequerBlackStar.run(shot, true);

        });

        setSize(1400, 1000);

        calibrate.addActionListener((e) -> {
            status.setText("Calibrating");
            new MyWorker<CalibrationForSpin, Void>() {

                @Override
                public void resultOnEventDispatchThread(CalibrationForSpin result) {
                    try {
                        if (result != null) {
                            cal = result;
                            status.setText("Calibrated for " + (cal.ownWhite ? "white" : "black") + ".");
                            ts = new TemplateSearchers(new Rectangle(0, 0, 1600, 1000));

                            cs = new FastChequerSearch(cal, ts);
                            System.out.println("cs created");
                            // startTimer();
                        } else {
                            status.setText("Calibration failed.");
                            calibrate.setVisible(true);
                        }
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                }

                @Override
                public CalibrationForSpin doIt() throws Exception {
                    try {
                        return new CalibrationForSpin();
                    } catch (Exception ex) {
                        ex.printStackTrace();
                        return null;
                    }
                }

            }.execute();
            calibrate.setVisible(false);
        });

        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
    }

    private void startTimer() {
        boardScreenshotRect = cs.boardScreenshotRect(boardScreenshotRect);
        timer.start();
        stop.setVisible(true);
    }

    private void stopTimer() {
        timer.stop();
        start.setVisible(true);
    }
}
