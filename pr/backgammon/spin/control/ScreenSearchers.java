package pr.backgammon.spin.control;

import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.io.IOException;

import pr.control.MyRobot;
import pr.control.ScreenSearcher;
import pr.control.Searcher;

public class ScreenSearchers {
    public ScreenSearchers(Rectangle wholeScreen) throws IOException {
        this.wholeScreen = wholeScreen;
        // spiele = standardSearcher("spiele", wholeScreen);
        // neuesSpiel = standardSearcher("neuesSpiel", wholeScreen);
        // unsichtbaresSpiel = standardSearcher("unsichtbaresSpiel", wholeScreen);
        // spielAnlegen = standardSearcher("spielAnlegen", wholeScreen);

        // enterWhite = standardSearcher("enterWhite", wholeScreen);
        // enterBlack = standardSearcher("enterBlack", wholeScreen);

        // bVerlassen = standardSearcher("bVerlassen", wholeScreen);
        // bSchliessen = standardSearcher("bSchliessen", wholeScreen);

    }

    private static ScreenSearcher standardSearcher(String fileName, Rectangle wholeScreen) throws IOException {
        return new ScreenSearcher(Searcher.create(fileName, null, null, 2), wholeScreen);
    }

    public BufferedImage screenshot() {
        return MyRobot.shot(wholeScreen);
    }

    // public final ScreenSearcher spiele, neuesSpiel, unsichtbaresSpiel, spielAnlegen,
    // enterWhite, enterBlack,
    // bVerlassen, bSchliessen;

    private final Rectangle wholeScreen;

}
