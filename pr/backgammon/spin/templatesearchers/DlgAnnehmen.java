package pr.backgammon.spin.templatesearchers;

import java.awt.Point;
import java.io.IOException;

import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.imgproc.Imgproc;

import pr.control.TemplateSearcher;

public class DlgAnnehmen extends TemplateSearcher {
    private final Mat result = new Mat();

    public DlgAnnehmen() throws IOException {
        super("annehmenUnpressed", ".png");
    }

    @Override
    protected Point run(Mat shot, Point lastPos, Point out, boolean trace) {

        if (lastPos != null && lastPos.y + template.height() <= shot.height()
                && lastPos.x + template.width() <= shot.width()) {
            // First, try the exact last position.
            Mat sub = shot.submat(lastPos.y, lastPos.y + template.height(), lastPos.x, lastPos.x + template.width());
            Point quickResult = run1(sub, out, trace);
            if (quickResult != null) {
                quickResult.translate(lastPos.x, lastPos.y);
                return quickResult;
            }
        }

        // Otherwise, search in complete shot.
        return run1(shot, out, trace);
    }

    private Point run1(Mat shot, Point out, boolean trace) {
        final double limit = 0.97;

        Imgproc.matchTemplate(shot, template, result, Imgproc.TM_CCOEFF_NORMED);
        var minMaxRes = Core.minMaxLoc(result);

        if (trace) {
            dumpMax(minMaxRes);
            showRect(shot, minMaxRes.maxLoc, RED);
        }

        if (minMaxRes.maxVal <= limit) {
            return null;
        }
        if (out == null) {
            out = new Point();
        }
        maxToPoint(minMaxRes, out);
        return out;

    }

}
