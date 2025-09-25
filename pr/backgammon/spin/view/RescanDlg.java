package pr.backgammon.spin.view;

import java.awt.BorderLayout;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JTextArea;

public class RescanDlg extends JDialog {
    public final JButton scan, cancel;

    public RescanDlg(JFrame parent) {
        super(parent, "Match neu erfassen");
        setLocationRelativeTo(parent);
        scan = new JButton("Jetzt hat eigener Spieler gewürfelt, aber noch nichts gezogen.");
        cancel = new JButton("Abbrechen");
        JPanel south = new JPanel();
        south.add(scan);
        south.add(cancel);
        setLayout(new BorderLayout(15, 15));
        add(south, BorderLayout.SOUTH);
        JTextArea ta = new JTextArea("Es muss wieder mit dem Spin-Browser synchronisiert werden. Dazu bitte manuell weiter spielen um folgende Situation herzustellen:");
        ta.setLineWrap(true);
        ta.setWrapStyleWord(true);
        add(ta, BorderLayout.CENTER);
        pack();
    }
}
