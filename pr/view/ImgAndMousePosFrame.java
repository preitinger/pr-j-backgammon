package pr.view;

import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.image.BufferedImage;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

import org.opencv.core.Mat;

import pr.ImgPanel;

public class ImgAndMousePosFrame extends JFrame {
    private final JLabel mousePos;
    private final JLabel pixel;
    private final ImgPanel imgPanel;
    private final StringBuilder sb = new StringBuilder();
    private Mat mat = null;

    public ImgAndMousePosFrame(String title) {
        super(title);
        JPanel right = new JPanel();
        right.setLayout(new GridLayout(0, 1));
        right.add(mousePos = new JLabel());
        right.add(pixel = new JLabel());
        setLayout(new BorderLayout());
        add(right, BorderLayout.EAST);
        add(new JScrollPane(imgPanel = new ImgPanel(null)), BorderLayout.CENTER);
        setSize(1200, 750);

        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                stopMouseTracking();
            }
        });
        imgPanel.addMouseMotionListener(new MouseMotionAdapter() {
            @Override
            public void mouseMoved(MouseEvent e) {
                // System.out.println("mouseMoved " + e.getX() + " " + e.getY());
                sb.setLength(0);
                sb.append('(');
                sb.append(e.getX());
                sb.append(',');
                sb.append(e.getY());
                sb.append(')');
                mousePos.setText(sb.toString());
                BufferedImage img = imgPanel.getImg();

                sb.setLength(0);

                try {
                    if (mat != null) {
                        double[] pixel = mat.get(e.getY(), e.getX());
                        if (pixel != null && pixel.length == 1) {
                            sb.append("Mat " + pixel[0]);
                        }
                    }
                    if (img != null) {
                        int rgb = img.getRGB(e.getX(), e.getY());
                        sb.append(" Px (");
                        int red = (rgb >> 16) & 255;
                        int green = (rgb >> 8) & 255;
                        int blue = rgb & 255;
                        sb.append(red).append(',').append(green).append(',').append(blue).append(')');
                    }
                } catch (ArrayIndexOutOfBoundsException ex) {
                    // pixel einfach leer
                }
                pixel.setText(sb.toString());
            }
        });
    }

    public void setImg(BufferedImage img) {
        imgPanel.setImg(img);
    }

    public void setMat(Mat m) {
        this.mat = m;
    }

    public void startMouseTracking() {
        // mousePos.start();
    }

    public void stopMouseTracking() {
        // mousePos.stop();
    }
}
