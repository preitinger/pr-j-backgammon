package pr.backgammon.spin.control;

import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.imgproc.Imgproc;

import pr.control.Tools;
import pr.cv.MatToBufferedImage;

public class SearchBearoffRects {

    public SearchBearoffRects() {
    }

    public void run(Mat img8bit) throws Exception {
        Mat edges = new Mat();
        // {
        //     double threshold2 = 125;
        //     double threshold1 = threshold2 / 2 * 1;
        //     Imgproc.Canny(img8bit, new Mat(), edges, threshold1, threshold2);
        // }
        {
            Imgproc.Scharr(img8bit, edges, CvType.CV_16S, 0, 1);
        }
        Tools.showImg("edges", MatToBufferedImage.matToBufferedImage(edges), false);

        float dy = 12.23F;

        System.out.println("edges.rows " + edges.rows());
        System.out.println("edges.cols " + edges.cols());

        int oy = 65;
        int ox = 47;
        
        for (int i = 0; i < 15; ++i) {
            int y = Math.round(613 - dy * i);
            System.out.println("#" + i + ": " + y);
            var pixel = edges.get(y - oy, 96 - ox);
            System.out.println("pixel.length" + pixel.length);
            for (int j = 0; j < pixel.length; ++j) {
                System.out.println("pixel " + j + "    " + pixel[j]);
            }
        }

        // Ursprung: 47, 65
        // 96, 613 - #1
        // 96, 600 - #2
        // 96, 589   #3
        // 96, 454   #14
    }
}
