package pr.view;

import java.awt.Dimension;
import java.awt.MouseInfo;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;

import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.Timer;

public class MousePosView extends JLabel implements ActionListener {
    private final Timer timer;

    public MousePosView() {
        timer = new Timer(200, this);
        setPreferredSize(new Dimension(200, 100));
    }

    public MousePosView(JComponent watched) {
        timer = null;
        watched.addMouseMotionListener(new MouseMotionAdapter() {
            StringBuilder sb = new StringBuilder();

            @Override
            public void mouseMoved(MouseEvent e) {
                // System.out.println("mouseMoved " + e.getX() + " " + e.getY());
                sb.setLength(0);
                sb.append('(');
                sb.append(e.getX());
                sb.append(',');
                sb.append(e.getY());
                sb.append(')');
                setText(sb.toString());
            }
        });
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        var pointerInfo = MouseInfo.getPointerInfo();
        var location = pointerInfo.getLocation();
        setText("(" + location.x + "," + location.y + ")");
    }

    public void start() {
        if (timer != null) {
            timer.start();
        }
    }

    public void stop() {
        if (timer != null) {
            timer.stop();
        }
    }
}
