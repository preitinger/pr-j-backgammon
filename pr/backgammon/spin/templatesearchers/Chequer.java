package pr.backgammon.spin.templatesearchers;

import java.awt.Point;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.Arrays;
import java.util.Comparator;
import java.util.PriorityQueue;

import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.Rect;
import org.opencv.imgproc.Imgproc;

import pr.backgammon.model.LocAndVal;
import pr.control.TemplateSearcher;
import pr.control.Tools;
import pr.cv.MatToBufferedImage;
import pr.view.ImgAndMousePosFrame;

public class Chequer extends TemplateSearcher {
    private final Mat result = new Mat();

    public Chequer(String name) throws IOException {
        super(name, ".png");
    }

    @Override
    protected Point run(Mat shot, Point lastPos, Point out, boolean trace) {

        // if (lastPos != null && lastPos.y + template.height() <= shot.height()
        // && lastPos.x + template.width() <= shot.width()) {
        // // First, try the exact last position.
        // Mat sub = shot.submat(lastPos.y, lastPos.y + template.height(), lastPos.x,
        // lastPos.x + template.width());
        // Point quickResult = run1(sub, out);
        // if (quickResult != null) {
        // quickResult.translate(lastPos.x, lastPos.y);
        // return quickResult;
        // }
        // }

        // Otherwise, search in complete shot.
        return run1(shot, out, trace);
    }

    public LocAndVal[] findBestNonOverlappingCenters(BufferedImage img, int k) {
        toMat(img, shot);
        int method = Imgproc.TM_CCOEFF_NORMED;
        Imgproc.matchTemplate(shot, template, result, method);
        var best = find_k_best_non_overlapping(64, result);
        int dx = template.width() / 2;
        int dy = template.height() / 2;

        for (int i = 0; i < best.length; ++i) {
            best[i].row += dy;
            best[i].col += dx;
        }
        return best;
    }

    public static final double LIMIT = 0.93;

    private Point run1(Mat shot, Point out, boolean trace) {
        // scan nach schwarzem stern bei sichtbarem schwarzen stern: 1.0
        // scan nach schwarzem stern bei sichtbarem weißen stern:
        // scan nach weißem stern bei sichtbarem weißen stern: 0.95 oder 0.99 oder 1
        // scan nach weißem stern bei sichtbarem schwarzen stern:
        int method = Imgproc.TM_CCOEFF_NORMED;
        Imgproc.matchTemplate(shot, template, result, method);
        var minMaxRes = Core.minMaxLoc(result);
        if (trace) {
            dumpMax(minMaxRes);
            System.out.println("after dumpMax");
            var best = find_k_best_non_overlapping(64, result);
            System.out.println("best.length" + best.length);
            showMatches(shot, best);
            dumpBest(shot, result);
            // showRect(shot, minMaxRes.maxLoc, RED);

        }

        if (minMaxRes.maxVal <= LIMIT) {
            return null;
        }
        if (out == null) {
            out = new Point();
        }
        maxToPoint(minMaxRes, out);
        int dx = template.width() / 2;
        int dy = template.height() / 2;
        out.x += dx;
        out.y += dy;
        return out;
    }

    class PixelComparer implements Comparator<Integer> {
        int w, h;
        Mat m;
        float[] valA = { 0 };
        float[] valB = { 0 };

        void init(Mat m) {
            this.m = m;
            w = m.width();
            h = m.height();
        }

        @Override
        public int compare(Integer a, Integer b) {
            int rowa = a / w;
            int cola = a % w;
            int rowb = b / w;
            int colb = b % w;
            m.get(rowa, cola, valA);
            m.get(rowb, colb, valB);
            return valA[0] < valB[0] ? -1 : valA[0] > valB[0] ? 1 : 0;
        }
    }

    PixelComparer pixelComparer = new PixelComparer();

    // MutableIntArray.sort mit pixelComparer dauert viel zu lang. Daher
    // alternativer Versuch:

    // public class LocAndVal implements Comparable<LocAndVal> {
    // public int row;
    // public int col;
    // public float val;

    // public LocAndVal() {
    // }

    // public LocAndVal(int row, int col, float val) {
    // this.row = row;
    // this.col = col;
    // this.val = val;
    // }

    // public String toString() {
    // return "[row=" + row + ", col=" + col + ", val=" + val + "]";
    // }

    // @Override
    // public int compareTo(LocAndVal other) {
    // return this.val < other.val ? -1 : this.val > other.val ? 1 : 0;
    // }

    // /**
    // * @param tmp - must have width = template.width() and height =
    // * template.height(). x and y will be set in the function
    // */
    // public boolean overlapsWith(LocAndVal other, int templateWidth, int
    // templateHeight) {
    // return ((col <= other.col && other.col < col + templateWidth)
    // || (other.col <= col && col < other.col + templateWidth)) &&
    // ((row <= other.row && other.row < row + templateHeight)
    // || (other.row <= row && row < other.row + templateHeight));
    // }
    // }

    private void dumpBest(Mat shot, Mat result) {
        int h = result.height();
        int w = result.width();
        int n = w * h;
        System.out.println("n " + n);
        float[] tmp = new float[1];
        result.get(347, 572, tmp);
        System.out.println("(572, 347): " + tmp[0] + "   |   " + result.get(347, 572)[0]);

        // {
        // long start = System.currentTimeMillis();
        // MutableIntArray pixels = new MutableIntArray(n);
        // for (int i = 0; i < n; ++i) {
        // pixels.add(i);
        // }
        // pixelComparer.init(result);
        // pixels.sort(pixelComparer);

        // long end = System.currentTimeMillis();

        // System.out.println("Sort took " + (end - start) + "ms.");

        // System.out.println("Best pixels:");
        // for (int i = 0; i < Math.min(16, n); ++i) {
        // int j = pixels.at(i);
        // int row = j / w;
        // int col = j % w;
        // result.get(row, col, pixelComparer.valA);
        // System.out.println("(" + col + "," + row + "): " + pixelComparer.valA[0]);
        // }
        // }

        {
            // Alternative
            result.get(347, 572, tmp);
            System.out.println("(572, 347): " + tmp[0]);
            long start = System.currentTimeMillis();

            LocAndVal[] a = new LocAndVal[n];
            for (int i = 0; i < n; ++i) {
                a[i] = new LocAndVal();
                a[i].row = i / w;
                a[i].col = i % w;
                result.get(a[i].row, a[i].col, tmp);
                a[i].val = tmp[0];
            }

            System.out.println("a[572 + 347 * w]: " + a[572 + 347 * w]);

            Arrays.sort(a,
                    new Comparator<LocAndVal>() {

                        @Override
                        public int compare(LocAndVal a, LocAndVal b) {
                            return a.val < b.val ? -1 : a.val > b.val ? 1 : 0;
                        }
                    });

            long end = System.currentTimeMillis();

            System.out.println("Alternative Sort took " + (end - start) + "ms.");
            System.out.println("Best from alternative:");
            for (int i = n - 1; i >= n - Math.min(16, n); --i) {
                System.out.println("(" + a[i].col + "," + a[i].row + "): " + a[i].val);

            }

            Mat numImg = new Mat();
            shot.copyTo(numImg);

            Rect rec = new Rect();
            rec.width = template.width();
            rec.height = template.height();
            org.opencv.core.Point textPoint = new org.opencv.core.Point();
            int fontFace = Imgproc.FONT_HERSHEY_PLAIN;
            double fontScale = 1;

            LocAndVal[] bestNonOverlapping = new LocAndVal[16];
            int templateWidth = template.width();
            int templateHeight = template.height();
            int found = 0;
            for (int next = n - 1; found < 16 && next >= 0; --next) {
                var nextItem = a[next];
                boolean anyOverlapping = false;
                for (int j = 0; j < found; ++j) {
                    if (bestNonOverlapping[j].overlapsWith(a[next], templateWidth, templateHeight)) {
                        anyOverlapping = true;
                        break;
                    }
                }

                if (!anyOverlapping) {
                    bestNonOverlapping[found++] = nextItem;
                }
            }

            for (int i = 0; i < found; ++i) {
                var x = bestNonOverlapping[i];
                rec.x = x.col;
                rec.y = x.row;
                Imgproc.rectangle(numImg, rec, RED);
                textPoint.x = rec.x + 4;
                textPoint.y = rec.y + rec.height - 4;
                Imgproc.putText(numImg, String.valueOf(i), textPoint, fontFace, fontScale, RED);
            }

            try {
                Tools.showImg(name + " - 16 best matches", MatToBufferedImage.matToBufferedImage(numImg), false);
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
    }

    public void showMatches(Mat shot, LocAndVal[] best) {
        Mat numImg = new Mat();
        shot.copyTo(numImg);

        int found = best.length;
        Rect rec = new Rect();
        rec.width = template.width();
        rec.height = template.height();
        org.opencv.core.Point textPoint = new org.opencv.core.Point();
        int fontFace = Imgproc.FONT_HERSHEY_PLAIN;
        double fontScale = 1;

        for (int i = 0; i < found; ++i) {
            var x = best[i];
            rec.x = x.col;
            rec.y = x.row;
            Imgproc.rectangle(numImg, rec, RED);
            textPoint.x = rec.x + 4;
            textPoint.y = rec.y + rec.height - 4;
            Imgproc.putText(numImg, String.valueOf(i), textPoint, fontFace, fontScale, RED);
        }

        try {
            ImgAndMousePosFrame f = new ImgAndMousePosFrame(name + " - Matches");
            f.setImg(MatToBufferedImage.matToBufferedImage(numImg));
            f.setVisible(true);
            // Tools.showImg("Matches", MatToBufferedImage.matToBufferedImage(numImg),
            // false);
        } catch (Exception ex) {
            ex.printStackTrace();
        }

    }

    private LocAndVal[] find_k_best_non_overlapping(int k, Mat result) {
        PriorityQueue<LocAndVal> heap = new PriorityQueue<>();
        int rows = result.rows();
        int cols = result.cols();
        float[] tmp = { 0 };
        int templateWidth = template.width();
        int templateHeight = template.height();

        for (int row = 0; row < rows; ++row) {
            for (int col = 0; col < cols; ++col) {
                if (row == 306 && col == 1169) {
                    System.out.println("hier");
                }
                result.get(row, col, tmp);
                float val = tmp[0];

                if (heap.size() >= k) {
                    LocAndVal oldMin = heap.peek();
                    if (val <= oldMin.val)
                        continue;
                }

                boolean overlapping = false;
                LocAndVal newElem = new LocAndVal(row, col, val);

                // for (LocAndVal old : heap) {
                // if (old.overlapsWith(newElem, templateWidth, templateHeight)) {
                // overlapping = true;
                // break;
                // }
                // }
                if (!overlapping) {
                    if (heap.size() >= k) {
                        heap.poll();
                    }

                    heap.add(newElem);
                }
            }
        }

        LocAndVal[] best = new LocAndVal[heap.size()];
        for (int i = best.length - 1; i >= 0; --i) {
            best[i] = heap.poll();
        }

        int keep = 0;

        for (int i = 0; i < best.length; ++i) {
            boolean overlapping = false;
            for (int j = 0; j < i; ++j) {
                if (best[i].overlapsWith(best[j], templateWidth, templateHeight)) {
                    overlapping = true;
                    break;
                }
            }

            if (!overlapping) {
                best[keep++] = best[i];
            }
        }

        if (keep < best.length) {
            return Arrays.copyOfRange(best, 0, keep);
        } else {
            return best;
        }
    }

}
