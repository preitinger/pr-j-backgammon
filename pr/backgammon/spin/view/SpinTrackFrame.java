package pr.backgammon.spin.view;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GraphicsEnvironment;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.MouseInfo;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;

import javax.swing.AbstractAction;
import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;

import pr.view.Menu;
import pr.view.MenuListener;

public class SpinTrackFrame extends JFrame {
    public static interface Listener {
        void calibrate();

        void startTracking();

        void trackNow();

        void testScan();

        void cancel();

        void screenshot();

        void debugAction();

        void abortMatch();
    }

    private final Listener listener;

    private final JLabel calibration;
    private final JLabel location;
    private final Point tmpPoint = new Point();
    private final Dimension tmpDimension = new Dimension();

    private final JButton track, trackNow, cancel, screenshot, debug;
    private final JPanel trackContainer, trackInitContainer, trackMainContainer;
    private final JComponent trackInit, trackMain;
    private final JTextField matchLen, nameOpp, nameOwn, scoreOpp, scoreOwn;
    private final JTextArea matchText;
    private final JCheckBox wholeScreen;

    public SpinTrackFrame(Listener tracking, JComponent matchView) {
        super("spin.de - tracker");
        this.listener = tracking;
        GridBagLayout gb = new GridBagLayout();
        JPanel northP = new JPanel(gb);
        northP.setBorder(LineBorder.createBlackLineBorder());
        GridBagConstraints gc = new GridBagConstraints();
        gc.gridx = 0;
        location = new JLabel();
        location.setBorder(new EmptyBorder(0, 0, 10, 0));
        gb.setConstraints(location, gc);
        northP.add(location);
        calibration = new JLabel("Noch nicht kalibriert.");
        calibration.setBorder(new EmptyBorder(0, 0, 10, 0));
        gb.setConstraints(calibration, gc);
        northP.add(calibration);
        JButton calibrate = new JButton(new AbstractAction("Kalibrieren") {
            @Override
            public void actionPerformed(ActionEvent e) {
                tracking.calibrate();
            }
        });
        gc.insets.bottom = 10;
        gb.setConstraints(calibrate, gc);
        northP.add(calibrate);

        {
            JPanel ssp = new JPanel();
            ssp.add(wholeScreen = new JCheckBox("Ganzer Bildschirm"));
            screenshot = new JButton(new AbstractAction("Screenshot") {
                @Override
                public void actionPerformed(ActionEvent e) {
                    listener.screenshot();
                }
            });
            ssp.add(screenshot);
            gb.setConstraints(ssp, gc);
            northP.add(ssp);
        }

        JPanel bp = new JPanel(new BorderLayout(10, 10));
        bp.add(northP, BorderLayout.NORTH);

        matchView.setPreferredSize(new Dimension(440, 400));
        bp.add(matchView, BorderLayout.CENTER);

        gb = new GridBagLayout();
        JPanel southP = new JPanel(gb);

        {
            Menu menu = new Menu(true);

            menu.setListener(new MenuListener() {
                public void onSelect(String event) {
                    // TODO Auto-generated method stub

                }

            });
            gc.fill = GridBagConstraints.NONE;
            gb.setConstraints(menu, gc);
            southP.add(menu);
        }

        JScrollPane sp = new JScrollPane(matchText = new JTextArea());
        matchText.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 12));
        sp.setPreferredSize(new Dimension(150, 150));
        gc.fill = GridBagConstraints.BOTH;
        gc.weightx = gc.weighty = 1;
        gb.setConstraints(sp, gc);
        southP.add(sp);

        gc.insets.bottom = 0;

        track = new JButton(new AbstractAction("Match tracken") {
            @Override
            public void actionPerformed(ActionEvent e) {
                listener.startTracking();

            }
        });
        trackContainer = container(track);
        gc.fill = GridBagConstraints.NONE;
        gb.setConstraints(trackContainer, gc);
        southP.add(trackContainer);

        trackInit = Box.createVerticalBox();
        {
            JPanel grid = new JPanel(new GridLayout(0, 2, 15, 10));
            grid.add(new JLabel("Matchlänge"));
            grid.add(matchLen = new JTextField("5"));
            grid.add(new JLabel("Gegner"));
            grid.add(nameOpp = new JTextField("Gegner"));
            grid.add(new JLabel("Du"));
            grid.add(nameOwn = new JTextField("Ich"));
            grid.add(new JLabel("Punkte Gegner"));
            grid.add(scoreOpp = new JTextField("0"));
            grid.add(new JLabel("Punkte Du"));
            grid.add(scoreOwn = new JTextField("0"));
            trackInit.add(grid);

            JPanel buttons = new JPanel();
            buttons.add(trackNow = new JButton(new AbstractAction("Jetzt tracken") {
                @Override
                public void actionPerformed(ActionEvent e) {
                    listener.trackNow();
                }
            }));
            buttons.add(cancel = new JButton(new AbstractAction("Abbrechen") {
                @Override
                public void actionPerformed(ActionEvent e) {
                    listener.cancel();
                }
            }));
            trackInit.add(buttons);
        }
        trackInitContainer = container(trackInit);
        // track
        gb.setConstraints(trackInitContainer, gc);
        southP.add(trackInitContainer);

        trackMain = new JPanel();
        {
            trackMain.add(new JButton(new AbstractAction("Testscan") {
                @Override
                public void actionPerformed(ActionEvent e) {
                    listener.testScan();
                }
            }));
            trackMain.add(new JButton(new AbstractAction("Match abbrechen") {
                @Override
                public void actionPerformed(ActionEvent e) {
                    listener.abortMatch();
                }
            }));

        }
        trackMainContainer = container(trackMain);
        gc.fill = GridBagConstraints.HORIZONTAL;
        gb.setConstraints(trackMainContainer, gc);
        gc.fill = GridBagConstraints.HORIZONTAL;
        southP.add(trackMainContainer);

        debug = new JButton(new AbstractAction("Debug action") {
            @Override
            public void actionPerformed(ActionEvent e) {
                listener.debugAction();
            }
        });
        gc.fill = GridBagConstraints.NONE;
        gc.insets.top = 10;
        gb.setConstraints(debug, gc);
        southP.add(debug);
        bp.add(southP, BorderLayout.SOUTH);

        JPanel contentPane = bp;
        contentPane.setBorder(new EmptyBorder(10, 10, 10, 10));
        setContentPane(contentPane);

        this.addComponentListener(new ComponentAdapter() {
            @Override
            public void componentMoved(ComponentEvent e) {
                updateLocation();
            }

            @Override
            public void componentResized(ComponentEvent e) {
                updateLocation();
                revalidate();
                doLayout();
            }
        });

        addWindowListener(new WindowListener() {

            @Override
            public void windowActivated(WindowEvent e) {
            }

            @Override
            public void windowClosed(WindowEvent e) {
            }

            @Override
            public void windowClosing(WindowEvent e) {
            }

            @Override
            public void windowDeactivated(WindowEvent e) {
            }

            @Override
            public void windowDeiconified(WindowEvent e) {
            }

            @Override
            public void windowIconified(WindowEvent e) {
            }

            @Override
            public void windowOpened(WindowEvent e) {
                updateLocation();
            }

        });

        final int w = 446;
        final int h = 888;
        setSize(w, h);
        int right;
        {
            var ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
            var screens = ge.getScreenDevices();
            right = 0;
            if (screens.length > 0) {
                right += screens[0].getDisplayMode().getWidth();
            }
            if (screens.length > 1) {
                right += screens[1].getDisplayMode().getWidth();
            }
        }
        setLocation(right - w, 0);

        new javax.swing.Timer(1000, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                updateLocation();
            }
        }).start();
    }

    private static final FlowLayout containerLayout = new FlowLayout(FlowLayout.CENTER, 0, 0);

    private JPanel container(JComponent child) {
        JPanel p = new JPanel(containerLayout);
        p.add(child);
        return p;
    }

    public void setCalibration(String text) {
        calibration.setText(text);
    }

    private final StringBuilder tmpsb = new StringBuilder();

    private void updateLocation() {
        getLocation(tmpPoint);
        getSize(tmpDimension);
        Point mouse = MouseInfo.getPointerInfo().getLocation();
        var ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
        var screens = ge.getScreenDevices();
        tmpsb.setLength(0);
        tmpsb.append('(').append(tmpPoint.x).append(',').append(tmpPoint.y).append(")  ").append(tmpDimension.width)
                .append('x').append(tmpDimension.height).append("   Mause (").append(mouse.x).append(',')
                .append(mouse.y).append(")   Screens");
        for (var screen : screens) {
            var d = screen.getDisplayMode();
            tmpsb.append(' ').append(d.getWidth()).append('x').append(d.getHeight());
        }

        location.setText(tmpsb.toString());

    }

    public void addNotification(String string) {
        JOptionPane.showMessageDialog(this, string);
    }

    public void setTrackVisible(boolean visible) {
        // track.setVisible(visible);

        logicVisible(trackContainer, track, visible);
    }

    public void setTrackInitVisible(boolean b) {
        logicVisible(trackInitContainer, trackInit, b);
    }

    private void logicVisible(JPanel container, JComponent child, boolean visible) {
        if (visible && container.getComponentCount() == 0)
            container.add(child);
        if (!visible && container.getComponentCount() > 0)
            container.remove(child);
        child.revalidate();

    }

    public void setTrackMainVisible(boolean b) {
        logicVisible(trackMainContainer, trackMain, b);
    }

    public String getMatchLen() {
        return matchLen.getText();
    }

    public String getNameOpp() {
        return nameOpp.getText();
    }

    public String getNameOwn() {
        return nameOwn.getText();
    }

    public String getScoreOpp() {
        return scoreOpp.getText();
    }

    public String getScoreOwn() {
        return scoreOwn.getText();
    }

    public void appendText(String s) {
        matchText.append(s);
    }

    public boolean getWholeScreen() {
        return wholeScreen.isSelected();
    }
}
