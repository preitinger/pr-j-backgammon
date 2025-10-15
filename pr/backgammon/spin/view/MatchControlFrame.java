package pr.backgammon.spin.view;

import java.awt.Dimension;
import java.awt.GraphicsEnvironment;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTextArea;

import pr.view.IGrid;

public class MatchControlFrame extends JFrame {
    private final MatchControlFrameListener listener;
    private final JTextArea textArea, commands;
    private final JCheckBox autorollCb, alternativeControlCb;
    private boolean forwardAutoroll = true;

    public MatchControlFrame(JComponent matchView, JComponent matchMenu, JComponent jokersViewOwn,
            JComponent jokersViewOpp, MatchControlFrameListener listener) {
        
        super("Spin-Matchkontrolle");
        this.listener = listener;
        JScrollPane commentsSp = new JScrollPane(textArea = new JTextArea(""));
        textArea.setWrapStyleWord(true);
        textArea.setLineWrap(true);
        commentsSp.setPreferredSize(new Dimension(200, 200));
        JScrollPane commandsSp = new JScrollPane(commands = new JTextArea(""));
        commands.setEditable(false);
        commands.setWrapStyleWord(true);
        commands.setLineWrap(true);
        commandsSp.setPreferredSize(new Dimension(100, 100));
        autorollCb = new JCheckBox("Autowurf");
        autorollCb.setEnabled(false);
        autorollCb.addItemListener(new ItemListener() {
            @Override
            public void itemStateChanged(ItemEvent e) {
                if (forwardAutoroll) {
                    listener.onAutowurfChanged(e.getStateChange() == ItemEvent.SELECTED);
                }
            }
        });
        alternativeControlCb = new JCheckBox("Altern. Steuerung");
        alternativeControlCb.addItemListener(new ItemListener() {
            @Override
            public void itemStateChanged(ItemEvent e) {
                if (forwardAutoroll) {
                    listener.onAlternativeControlChanged(e.getStateChange() == ItemEvent.SELECTED);
                }
            }
        });
        JSplitPane rightLeftSplit = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, true, commentsSp, commandsSp);

        IGrid jgrid = IGrid.create();
        jgrid.rect(0, 0, 1, 1);
        jgrid.fill().both();
        jgrid.weight().y(0.1);
        jgrid.insets().left(5);
        jgrid.add(new JLabel("Eigene Joker"));

        jgrid.y(1);
        jgrid.weight().x(1);
        jgrid.weight().y(1);
        jgrid.add(jokersViewOwn);

        jgrid.y(2);
        jgrid.weight().y(0.1);
        jgrid.add(new JLabel("Joker des Gegners"));

        jgrid.y(3);
        jgrid.weight().y(1);
        jgrid.add(jokersViewOpp);

        JSplitPane rightSplit = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, true, rightLeftSplit, jgrid.asComponent());
        rightSplit.setResizeWeight(0.7);
        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, true, matchView, rightSplit);
        splitPane.setResizeWeight(0.9);

        // neues Layout mit Grid

        var grid = IGrid.create();

        // grid.rect(0, 0, 2, 1);
        // grid.weight().x(10);
        // grid.weight().y(3);
        // grid.fill().both();
        // grid.add(matchView);

        // grid.rect(2, 0, 1, 1);
        // grid.weight().x(1);
        // grid.weight().y(1);
        // grid.add(commentsSp);

        // grid.rect(3, 0, 1, 1);
        // grid.weight().x(0);
        // grid.weight().y(0);
        // grid.add(commandsSp);

        grid.rect(0, 0, 4, 1);
        grid.weight().x(1);
        grid.weight().y(1);
        grid.fill().both();
        grid.add(splitPane);

        grid.rect(0, 1, 1, 1);
        grid.weight().x(0);
        grid.weight().y(0);
        grid.fill().none();
        grid.insets().left(15);
        grid.anchor().west();
        grid.add(autorollCb);

        grid.rect(0, 2, 1, 1);
        grid.anchor().west();
        grid.add(alternativeControlCb);

        grid.rect(1, 1, 3, 2);
        grid.weight().x(1);
        grid.insets().top(15);
        grid.insets().left(15);
        grid.insets().right(15);
        grid.insets().bottom(15);
        grid.fill().both();
        grid.add(matchMenu);

        setContentPane(grid.asComponent());

        // // altes Layout mit BorderLayout:

        // JPanel southP = new JPanel(new BorderLayout(10, 10));
        // JPanel eastP = new JPanel(new BorderLayout());

        // setLayout(new BorderLayout(10, 10));
        // add(matchView, BorderLayout.CENTER);
        // southP.add(autorollCb, BorderLayout.WEST);
        // southP.add(matchMenu, BorderLayout.CENTER);
        // add(southP, BorderLayout.SOUTH);

        // eastP.add(sp, BorderLayout.CENTER);
        // add(eastP, BorderLayout.EAST);

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
        setLocation(right - 1300, 0);
        setSize(800, 600);

        setBounds(right - 1112, 27, 1112, 812);

        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                listener.onClose();
            }
        });
    }

    public void appendText(String s) {
        textArea.append(s);
    }

    public void appendCmd(String s) {
        commands.append(s);
    }

    public boolean getAutoroll() {
        return autorollCb.isSelected();
    }

    public void setAutoroll(boolean selected) {
        forwardAutoroll = false;
        autorollCb.setSelected(selected);
        System.out.println("after autorollCb.setSelected");
        forwardAutoroll = true;
    }

    public void setAutorollEnabled(boolean enabled) {
        autorollCb.setEnabled(enabled);
    }
}
