package pr.backgammon.spin.templatesearchers;

import java.awt.Point;
import java.io.IOException;

import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.imgproc.Imgproc;

import pr.control.TemplateSearcher;

public class PlayerBoxBlackReady extends TemplateSearcher {
    private final Mat result = new Mat();

    public PlayerBoxBlackReady() throws IOException {
        super("playerBoxBlackReadyComplete", ".bmp");
    }

    @Override
    protected Point run(Mat shot, Point lastPos, Point out, boolean trace) {

        // Hier eine solche Optimierung schlecht weil sonst white und black verwechselt
        // werden kann und das limit aber gerade noch unterschritten wird.
        // einfacher hier ohne diese optimierung:
        // if (lastPos != null && lastPos.y + template.height() <= shot.height() &&
        // lastPos.x + template.width() <= shot.width()) {
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

    private Point run1(Mat shot, Point out, boolean trace) {
        final double limit = 0.18;

        Imgproc.matchTemplate(shot, template, result, Imgproc.TM_SQDIFF_NORMED);
        var minMaxRes = Core.minMaxLoc(result);

        if (trace) {
            dumpMin(minMaxRes);
            showRect(shot, minMaxRes.minLoc, RED);
        }

        if (minMaxRes.minVal >= limit) {
            return null;
        }
        if (out == null) {
            out = new Point();
        }
        minToPoint(minMaxRes, out);
        return out;

    }

}
