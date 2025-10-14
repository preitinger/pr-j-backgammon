package pr.cv;

import java.awt.BorderLayout;
import java.awt.GridLayout;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.border.EmptyBorder;

public class CircleParamInput extends JPanel {
    public final JButton ok = new JButton("OK");
    public final ParamGrid paramGrid = new ParamGrid();

    public CircleParamInput() {
        setLayout(new BorderLayout());
        JPanel northP = new JPanel();
        northP.setLayout(new BorderLayout(10, 10));
        northP.add(paramGrid, BorderLayout.NORTH);
        northP.setBorder(new EmptyBorder(10, 10, 10, 10));

        northP.add(ok, BorderLayout.CENTER);
        add(northP, BorderLayout.NORTH);
    }

    private JTextField param(String name) {
        JTextField tf = new JTextField("");
        add(new JLabel(name));
        add(tf);
        return tf;
    }
}

class ParamGrid extends JPanel {
    public final JTextField dp, minDist, param1, param2, minRadius, maxRadius;
    public ParamGrid() {
        setLayout(new GridLayout(0, 2, 10, 10));
        dp = param("dp");
        minDist = param("minDist");
        param1 = param("param1");
        param2 = param("param2");
        minRadius = param("minRadius");
        maxRadius = param("maxRadius");
    }

    private JTextField param(String name) {
        JTextField tf = new JTextField("");
        add(new JLabel(name));
        add(tf);
        return tf;
    }

}