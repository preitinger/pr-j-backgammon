package pr.control;

import java.awt.Rectangle;

public class ScreenSearcher {
    public ScreenSearcher(Searcher searcher, Rectangle wholeScreen) {
        this.searcher = searcher;
        this.wholeScreen = wholeScreen;
    }

    public boolean runAndClick(int delayMs) throws InterruptedException {
        if (delayMs > 0) {
            Thread.sleep(delayMs);
        }
        return searcher.runAndClick(MyRobot.shot(wholeScreen).getRaster(), 0, 0);
    }

    public final Searcher searcher;
    private final Rectangle wholeScreen;
}
