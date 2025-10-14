package pr.control;

import java.awt.image.BufferedImage;
import java.io.IOException;

import org.opencv.core.Core;
import org.opencv.core.Core.MinMaxLocResult;
import org.opencv.core.Mat;
import org.opencv.core.Point;
import org.opencv.core.Rect;
import org.opencv.core.Scalar;
import org.opencv.imgproc.Imgproc;

import pr.cv.BufferedImageToMat;
import pr.cv.MatToBufferedImage;

public class TemplateSearcherOld {
    public static final double DEFAULT_MIN_LIMIT = 2000000;

    private final Mat template = new Mat();
    private final Mat templateGray = new Mat();
    private final Mat shot = new Mat();
    private final Mat shotGray = new Mat();
    private final Mat result = new Mat();

    private String name;
    private Point lastPos = null;

    public TemplateSearcherOld(String name, double minLimit, String extension) throws IOException {
        this.name = name;
        BufferedImage img = Tools.loadImg("pr/res/" + name + normExtension(extension));

        toMat(img, template);
        gray(template, templateGray);
        smooth(templateGray);
        // new LoadLastPosWorker().execute();
    }

    private static String normExtension(String extension) {
        if (!extension.startsWith(".")) {
            extension = "." + extension;
        }

        return extension;
    }

    public TemplateSearcherOld(String file, double minLimit) throws IOException {
        this(file, minLimit, ".bmp");
    }

    public TemplateSearcherOld(String file) throws IOException {
        this(file, DEFAULT_MIN_LIMIT, ".bmp");
    }

    public java.awt.Point search(BufferedImage shotImg, boolean verbose) {
        throw new RuntimeException("nyi");
        // var res = run(shotImg, verbose);

        // if (res.minVal < this.minLimit) {
        // return new Point((int) Math.round(res.minLoc.x), (int)
        // Math.round(res.minLoc.y));
        // }

        // return null;

    }

    public java.awt.Point search(BufferedImage shotImg) {
        return search(shotImg, false);
    }

    public MinMaxLocResult run(BufferedImage shotImg, boolean showResult) {

        toMat(shotImg, shot);
        gray(shot, shotGray);
        smooth(shotGray);
        // var mode = Imgproc.TM_CCOEFF_NORMED;
        var method = Imgproc.TM_SQDIFF_NORMED; // min: 0.003680944675579667

        Imgproc.matchTemplate(shotGray, templateGray, result, method);
        var minMaxRes = Core.minMaxLoc(result);

        // for (int y = 0; y < result.rows(); ++y) {
        // for (int x = 0; x < result.cols(); ++x) {
        // double[] val = result.get(y, x);
        // System.out.println("(" + x + "," + y + "): " + (val == null || val.length ==
        // 0 ? "leer" : val[0]));
        // }
        // }

        if (showResult) {
            Mat show = new Mat();
            shot.copyTo(show);

            Rect r = new Rect(minMaxRes.maxLoc, template.size());
            Scalar color = new Scalar(0, 0, 255);
            Imgproc.rectangle(show, r, color);
            r.x = (int) Math.round(minMaxRes.minLoc.x);
            r.y = (int) Math.round(minMaxRes.minLoc.y);
            color.set(new double[] { 0, 255, 0 });
            Imgproc.rectangle(show, r, color);
            showImg(show);
            showImg(templateGray);
            try {
                Tools.showImg("shotGray", MatToBufferedImage.matToBufferedImage(shotGray), false);
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }

        return minMaxRes;
    }

    private void toMat(BufferedImage img, Mat mat) {
        BufferedImageToMat.toMat(img, mat);
    }

    private void gray(Mat src, Mat dst) {
        src.copyTo(dst);
        // Imgproc.cvtColor(src, dst, Imgproc.COLOR_BGR2GRAY);
    }

    private void smooth(Mat inline) {
        // Imgproc.GaussianBlur(inline, inline, new Size(3, 3), 0, 0,
        // Core.BORDER_REPLICATE);
    }

    private void showImg(Mat img) {
        try {
            Tools.showImg("TemplateSearcher", MatToBufferedImage.matToBufferedImage(img), false);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public int templateWidth() {
        return templateGray.cols();
    }

    public int templateHeight() {
        return templateGray.rows();
    }

    private void storeLastPos() {
        // if (lastPos != null) {
        //     new StoreLastPosWorker(name, lastPos).execute();
        // }
    }

    // class LoadLastPosWorker extends MyWorker<Void, Void> {

    //     @Override
    //     public void resultOnEventDispatchThread(Void result) {
    //     }

    //     @Override
    //     public Void doIt() throws Exception {
    //         DataInputStream in = new DataInputStream(
    //                 new BufferedInputStream(new FileInputStream("boardPos/" + name + ".pos")));
    //         try {
    //             if (lastPos == null) {
    //                 lastPos = new Point();
    //             }
    //             LastPosIO.read(in, lastPos);
    //         } finally {
    //             in.close();
    //         }
    //         return null;
    //     }

    // }
}

// class MatchVariant {
//     int method;
//     boolean useMin;
//     double limit;
// }

// class StoreLastPosWorker extends MyWorker<Void, Void> {
//     private final String name;
//     private final Point lastPos;

//     public StoreLastPosWorker(String name, Point lastPos) {
//         this.name = name;
//         this.lastPos = lastPos;
//     }

//     @Override
//     public void resultOnEventDispatchThread(Void result) {

//     }

//     @Override
//     public Void doIt() throws Exception {
//         File dir = new File("boardPos");
//         if (!dir.isDirectory()) {
//             if (!dir.mkdir()) {
//                 throw new RuntimeException("Could not create directory '" + dir.getAbsolutePath() + "'");
//             }
//         }

//         DataOutputStream o = new DataOutputStream(
//                 new BufferedOutputStream(new FileOutputStream(new File(dir, name + ".pos"))));
//         try {
//             LastPosIO.write(o, lastPos);
//         } finally {
//             o.close();
//         }

//         return null;
//     }

// }

// class LastPosIO {
//     public static void write(DataOutput o, Point lastPos) throws IOException {
//         o.writeDouble(lastPos.x);
//         o.writeDouble(lastPos.y);
//     }

//     public static void read(DataInput i, Point lastPos) throws IOException {
//         lastPos.x = i.readDouble();
//         lastPos.y = i.readDouble();
//     }
// }