package pr.cv;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.image.BufferedImage;

import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.SwingUtilities;
import javax.swing.Timer;

import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.Point;
import org.opencv.core.Scalar;
import org.opencv.imgproc.Imgproc;

import pr.ImgPanel;
import pr.control.MyRobot;

public class CircleWatcher extends JFrame implements ActionListener {

    double dp = 1.1;
    double minDist = 40;
    double param1 = 99; // 100 klein?
    double param2 = 32.7; // 33
    int minRadius = 14;
    int maxRadius = 14;

    private static final int WIDTH = 1600, HEIGHT = 950;
    private Timer timer = null;
    private final ImgPanel imgPanel;

    public CircleWatcher() {
        super("Circle Watcher");
        setLayout(new BorderLayout());
        imgPanel = new ImgPanel(null);
        imgPanel.setPreferredSize(new Dimension(WIDTH, HEIGHT));
        add(new JScrollPane(imgPanel), BorderLayout.CENTER);
        CircleParamInput params = new CircleParamInput();
        add(params, BorderLayout.EAST);

        params.paramGrid.dp.setText(String.valueOf(dp));
        params.paramGrid.minDist.setText(String.valueOf(minDist));
        params.paramGrid.param1.setText(String.valueOf(param1));
        params.paramGrid.param2.setText(String.valueOf(param2));
        params.paramGrid.minRadius.setText(String.valueOf(minRadius));
        params.paramGrid.maxRadius.setText(String.valueOf(maxRadius));

        params.ok.addActionListener((e) -> {
            dp = Double.parseDouble(params.paramGrid.dp.getText());
            minDist = Double.parseDouble(params.paramGrid.minDist.getText());
            param1 = Double.parseDouble(params.paramGrid.param1.getText());
            param2 = Double.parseDouble(params.paramGrid.param2.getText());
            minRadius = Integer.parseInt(params.paramGrid.minRadius.getText());
            maxRadius = Integer.parseInt(params.paramGrid.maxRadius.getText());
            if (lastShot != null) {
                setShot(lastShot);
            }
        });

        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                if (timer != null) {
                    timer.stop();
                }
                setVisible(false);
                dispose();
            }
        });
    }

    public void start() {
        timer = new Timer(500, this);
        timer.start();
    }

    private BufferedImage lastShot = null;

    public void setShot(BufferedImage shot) {
        lastShot = shot;
        Mat img = BufferedImageToMat.toBgrMat(shot);

        Mat gray = new Mat();
        Imgproc.cvtColor(img, gray, Imgproc.COLOR_BGR2GRAY);

        // 2) Rauschen reduzieren (Median- oder Gaussian-Blur)
        // {
        // double sigmaX = 1.2;
        // double sigmaY = 1.2;
        // int borderType = Core.BORDER_CONSTANT;
        // Imgproc.GaussianBlur(gray, gray, new Size(3, 3), sigmaX, sigmaY, borderType);
        // // Tools.showImg("GaussianBlur", MatToBufferedImage.matToBufferedImage(gray),
        // false);
        // }

        Mat circles = new Mat();
        int method = Imgproc.HOUGH_GRADIENT;
        // double dp = 1.0;
        // double minDist = 44;
        // double param1 = 100;
        // double param2 = 10; // 30
        // int minRadius = 11;
        // int maxRadius = 13;

        // Recht gut:
        // double dp = 1.0;
        // double minDist = 44;
        // double param1 = 150;
        // double param2 = 10; // 30
        // int minRadius = 11;
        // int maxRadius = 13;

        Imgproc.HoughCircles(gray, circles, method, dp, minDist, param1, param2, minRadius, maxRadius);
        System.out.println("Gefundene Kreise: " + circles.cols());
        System.out.println("Gefundene Kreise: " + circles);
        System.out.println("rows: " + circles.rows());
        Mat result = new Mat();
        img.copyTo(result);

        int numCircles = circles.cols();
        Point p = new Point();
        float[] data = new float[3];
        int blue = 0;
        int green = 0;
        int red = 255;
        Scalar color = new Scalar(blue, green, red);
        // System.out.println("elemSize: " + circles.elemSize());
        // System.out.println("elemSize1: " + circles.elemSize1());
        for (int i = 0; i < numCircles; ++i) {
            /* int getRes = */ circles.get(0, i, data);
            p.x = Math.round(data[0]);
            p.y = Math.round(data[1]);
            int radius = Math.round(data[2]);
            System.out.println("Kreis (" + p.x + "," + p.y + ";" + radius + ")");
            Imgproc.circle(result, p, radius, color);
        }

        var resultImg = MatToBufferedImage.matToBufferedImage(result);
        imgPanel.setImg(resultImg);

    }

    public static void main(String[] args) {

        try {
            // lädt libopencv_java412.so über java.library.path/LD_LIBRARY_PATH
            System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
        } catch (UnsatisfiedLinkError e) {
            // Fallback: absoluter Pfad, wenn du keinen Pfad setzen willst
            System.load("/home/peter/opencv_4.12/opencv/build/lib/libopencv_java4130.so");
        }

        SwingUtilities.invokeLater(() -> {
            CircleWatcher w = new CircleWatcher();
            w.pack();
            w.setVisible(true);
            w.start();
        });

    }

    @Override
    public void actionPerformed(ActionEvent e) {
        setShot(MyRobot.shot(new Rectangle(0, 0, WIDTH, HEIGHT)));
    }
}
