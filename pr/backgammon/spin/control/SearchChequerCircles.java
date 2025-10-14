package pr.backgammon.spin.control;

import java.awt.image.BufferedImage;

import org.opencv.core.Mat;
import org.opencv.imgproc.Imgproc;

import pr.cv.BufferedImageToMat;

public class SearchChequerCircles {
    /**
     * out.length must be multiple of three.
     * 
     * Tripples containing (x, y, radius) will be written to out.
     * The number of written tripples is returned.
     */
    public int run(BufferedImage img1, float[] out) {
        Mat img = BufferedImageToMat.toBgrMat(img1);

        Mat gray = new Mat();
        Imgproc.cvtColor(img, gray, Imgproc.COLOR_BGR2GRAY);

        // 2) Rauschen reduzieren (Median- oder Gaussian-Blur)
        // {
        //     double sigmaX = 1.2;
        //     double sigmaY = 1.2;
        //     int borderType = Core.BORDER_CONSTANT;
        //     Imgproc.GaussianBlur(gray, gray, new Size(9, 9), sigmaX, sigmaY, borderType);
        //     // Tools.showImg("GaussianBlur", MatToBufferedImage.matToBufferedImage(gray), false);
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

        // double dp = 1.0;
        // double minDist = 44;
        // double param1 = 100;
        // double param2 = 20; // 30
        // int minRadius = 11;
        // int maxRadius = 13;

        double dp = 1.1;
        double minDist = 40;
        double param1 = 99; // 100 klein?
        double param2 = 32.7; // 33
        int minRadius = 14;
        int maxRadius = 14;
        // double dp = 1.1;
        // double minDist = 10;
        // double param1 = 150; // 100 klein?
        // double param2 = 33; // 30
        // int minRadius = 15;
        // int maxRadius = 15;

        Imgproc.HoughCircles(gray, circles, method, dp, minDist, param1, param2, minRadius, maxRadius);
        int cols = circles.cols();
        // System.out.println("cols " + cols);

        int numBytes = cols == 0 ? 0 : circles.get(0, 0, out);
        int n = numBytes / (int) circles.elemSize();

        if (n != circles.cols()) {
            throw new IllegalStateException("result did not fit into out - n=" + n);
        }

        return n;
    }
}
