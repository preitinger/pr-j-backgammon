package pr.backgammon.spin.view;

import java.awt.BorderLayout;
import java.awt.Dimension;

import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

import pr.model.MutableArray;
import pr.model.MutableIntArray;

public class AllMovesPanel extends JPanel {

    public AllMovesPanel(boolean own, int die1, int die2, MutableArray<MutableIntArray> out) {
        int n = out.length();
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < n; ++i) {
            out.at(i).append(sb);
            sb.append('\n');
        }
        JTextArea ta = new JTextArea(sb.toString());
        JScrollPane sp = new JScrollPane(ta);
        sp.setPreferredSize(new Dimension(100, 100));
        setLayout(new BorderLayout());
        add(sp);
    }

}
