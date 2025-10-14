package pr.control;

import java.awt.image.BufferedImage;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.DataInput;
import java.io.DataInputStream;
import java.io.DataOutput;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import org.opencv.core.Core.MinMaxLocResult;
import org.opencv.imgproc.Imgproc;
import org.opencv.core.Mat;
import org.opencv.core.Point;
import org.opencv.core.Rect;
import org.opencv.core.Scalar;

import pr.cv.BufferedImageToMat;
import pr.cv.MatToBufferedImage;
import pr.view.ImgAndMousePosFrame;

public abstract class TemplateSearcher {
    public final String name;
    protected final Mat template = new Mat();
    protected final Mat shot = new Mat();
    private MyWorker<?, ?> worker = null, pendingWorker = null;

    protected java.awt.Point lastPos = null;

    public TemplateSearcher(String name, String extension) throws IOException {
        this.name = name;
        toMat(Tools.loadImg("pr/res/" + name + normExtension(extension)), template);
        (worker = new LoadLastPosWorker()).execute();
    }

    public final java.awt.Point run(BufferedImage board, boolean trace) {
        return run(board, null, trace);
    }

    /**
     * @return - If the search was successful, the location of the object is
     *         returned. Otherwise, null is returned. If the search is successful
     *         and result is not null, the location is stored in the given param
     *         result and the same object is returned as result.
     */
    public final java.awt.Point run(BufferedImage board, java.awt.Point result, boolean trace) {
        toMat(board, shot);
        java.awt.Point pos = run(shot, lastPos, result, trace);
        if (pos != null) {
            if (lastPos == null) {
                lastPos = new java.awt.Point(pos);
            } else {
                lastPos.setLocation(pos);
            }
            storeLastPos();
        }
        return pos;
    }

    public final boolean click(BufferedImage shot, int shotX, int shotY, int delayMs) throws InterruptedException {
        java.awt.Point found = run(shot, false);
        if (found == null) {
            return false;
        }
        MyRobot.click(shotX, shotY, template.width(), template.height());
        return true;
    }

    public void joinWorkers() throws InterruptedException {
        if (worker != null) {
            worker.join();
            worker = null;

            if (pendingWorker != null) {
                pendingWorker.execute();
                pendingWorker.join();
                pendingWorker = null;
            }
        }
    }

    private void storeLastPos() {
        if (lastPos != null) {
            var newWorker = new StoreLastPosWorker(name, lastPos) {
                @Override
                public void resultOnEventDispatchThread(Void result) {
                    if (pendingWorker != null) {
                        worker = pendingWorker;
                        pendingWorker = null;
                        worker.execute();
                    }
                }
            };
            if (worker != null) {
                pendingWorker = newWorker;
            } else {
                (worker = newWorker).execute();
            }
        }
    }

    protected abstract java.awt.Point run(Mat board, java.awt.Point lastPos, java.awt.Point result, boolean trace);

    public static void toMat(BufferedImage img, Mat mat) {
        BufferedImageToMat.toMat(img, mat);
    }

    public static BufferedImage toImg(Mat mat) {
        return MatToBufferedImage.matToBufferedImage(mat);
    }

    private static String normExtension(String extension) {
        if (!extension.startsWith(".")) {
            extension = "." + extension;
        }

        return extension;
    }

    protected final void dumpMin(MinMaxLocResult x) {
        System.out.println("minLoc: " + x.minLoc);
        System.out.println("minVal: " + x.minVal);
    }

    protected final void dumpMax(MinMaxLocResult x) {
        // try {
        // throw new RuntimeException("stack trace");
        // } catch (Exception ex) {
        // ex.printStackTrace();
        // }
        System.out.println("maxLoc: " + x.maxLoc);
        System.out.println("maxVal: " + x.maxVal);
    }

    protected final void showRect(Mat shot, Point loc, Scalar color) {
        Mat withRect = new Mat();
        shot.copyTo(withRect);
        Imgproc.rectangle(withRect, new Rect(roundInt(loc.x), roundInt(loc.y), template.width(), template.height()),
                color);
        ImgAndMousePosFrame f = new ImgAndMousePosFrame("Found " + name);
        f.setImg(toImg(withRect));
        f.setVisible(true);
    }

    protected final int roundInt(double d) {
        return (int) Math.round(d);
    }

    protected final void locToPoint(Point loc, java.awt.Point out) {
        out.x = roundInt(loc.x);
        out.y = roundInt(loc.y);

    }

    protected final void minToPoint(MinMaxLocResult res, java.awt.Point out) {
        locToPoint(res.minLoc, out);
    }

    protected final void maxToPoint(MinMaxLocResult res, java.awt.Point out) {
        locToPoint(res.maxLoc, out);
    }

    protected static final Scalar RED = new Scalar(0, 0, 255); // BGR

    class LoadLastPosWorker extends MyWorker<Void, Void> {

        @Override
        public void resultOnEventDispatchThread(Void result) {
            if (pendingWorker != null) {
                worker = pendingWorker;
                pendingWorker = null;
                worker.execute();
            }
        }

        @Override
        public Void doIt() throws Exception {
            try {
                DataInputStream in = new DataInputStream(
                        new BufferedInputStream(new FileInputStream("boardPos/" + name + ".pos")));
                try {
                    java.awt.Point newPos = new java.awt.Point();
                    LastPosIO.read(in, newPos);
                    lastPos = newPos;
                } finally {
                    in.close();
                }
            } catch (FileNotFoundException ex) {
                // ignore
            }
            return null;
        }

    }

    public int width() {
        return template.width();
    }

    public int height() {
        return template.height();
    }
}

class MatchVariant {
    int method;
    boolean useMin;
    double limit;
}

abstract class StoreLastPosWorker extends MyWorker<Void, Void> {
    private final String name;
    private final java.awt.Point lastPos;

    public StoreLastPosWorker(String name, java.awt.Point lastPos) {
        this.name = name;
        this.lastPos = lastPos;
    }

    @Override
    public Void doIt() throws Exception {
        File dir = new File("boardPos");
        if (!dir.isDirectory()) {
            if (!dir.mkdir()) {
                throw new RuntimeException("Could not create directory '" + dir.getAbsolutePath() + "'");
            }
        }

        DataOutputStream o = new DataOutputStream(
                new BufferedOutputStream(new FileOutputStream(new File(dir, name + ".pos"))));
        try {
            LastPosIO.write(o, lastPos);
        } finally {
            o.close();
        }

        return null;
    }

}

class LastPosIO {
    public static void write(DataOutput o, java.awt.Point lastPos) throws IOException {
        o.writeInt(lastPos.x);
        o.writeInt(lastPos.y);
    }

    public static void read(DataInput i, java.awt.Point lastPos) throws IOException {
        lastPos.x = i.readInt();
        lastPos.y = i.readInt();
    }
}