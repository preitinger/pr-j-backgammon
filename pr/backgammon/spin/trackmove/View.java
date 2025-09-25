package pr.backgammon.spin.trackmove;

import java.awt.BorderLayout;
import java.awt.event.ActionListener;
import java.util.List;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSplitPane;

import pr.backgammon.view.MatchView;
import pr.view.IGrid;

public class View extends JFrame {
    private List<Data> data = null;
    private final JLabel label;
    private final ImageIcon shot = new ImageIcon();
    private final JLabel shotLabel;
    private final MatchView matchView = new MatchView(null, true, false);
    private final JButton left, right;
    private int num = 0;
    private int idx = 0;

    public View(ActionListener onCalibrate, ActionListener onStart, ActionListener onStop) {
        super("Track One Move");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        JPanel shotP = new JPanel();
        shotP.setLayout(new BorderLayout());
        shotP.add(label = new JLabel(), BorderLayout.NORTH);
        shotP.add(shotLabel = new JLabel(shot), BorderLayout.CENTER);
        JSplitPane sp = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, true, shotP, matchView);
        sp.setResizeWeight(0.5);
        JPanel buttons = new JPanel();
        buttons.add(button("Calibrate", onCalibrate));
        buttons.add(button("Start", onStart));
        buttons.add(button("Stop", onStop));
        buttons.add(left = button("<", e -> {
            if (idx > 0) {
                --idx;
            }
            updatePane();
            setButtonsEnabled();
        }));
        buttons.add(right = button(">", e -> {
            if (idx + 1 < num) {
                ++idx;
            }
            updatePane();
            setButtonsEnabled();
        }));

        var grid = IGrid.create();

        grid.rect(0, 0, 1, 1);
        grid.fill().both();
        grid.weight().x(1);
        grid.weight().y(1);
        grid.add(sp);

        grid.rect(0, 1, 1, 1);
        grid.weight().x(0);
        grid.weight().y(0);
        grid.add(buttons);

        setLayout(new BorderLayout());
        add(grid.asComponent(), BorderLayout.CENTER);

        updatePane();
        setButtonsEnabled();
    }

    public void setData(List<Data> data) {
        this.data = data;
        num = data.size();
        // if (idx >= num) {
        idx = num - 1;
        // }

        if (idx < 0) {
            idx = 0;
        }

        updatePane();
        setButtonsEnabled();
        
        shotLabel.revalidate();
        shotLabel.repaint();
    }

    private void updatePane() {
        if (data == null || num == 0) {
            label.setText("No data");
            matchView.setMatch(null, null);
        } else {
            var d = data.get(idx);
            shot.setImage(d.shot);
            label.setText("Shot " + (idx + 1) + " / " + num);
            matchView.setMatch(d.match, null);
        }
    }

    private JButton button(String label, ActionListener l) {
        JButton b = new JButton(label);
        b.addActionListener(l);
        return b;
    }

    private void setButtonsEnabled() {
        left.setEnabled(idx > 0);
        right.setEnabled(idx + 1 < num);
    }

}
