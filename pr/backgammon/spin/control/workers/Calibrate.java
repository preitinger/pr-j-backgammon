package pr.backgammon.spin.control.workers;

import java.awt.Point;
import java.awt.image.BufferedImage;
import java.io.IOException;

import pr.backgammon.spin.control.CalibrationForSpin;
import pr.backgammon.spin.control.TemplateSearchers;
import pr.control.MyRobot;
import pr.control.MyWorker;
import pr.control.TemplateSearcher;

public abstract class Calibrate extends MyWorker<CalibrateRes, Void> {
    // private final ScreenSearchers s;
    private final TemplateSearchers ts;

    public Calibrate(TemplateSearchers ts) {
        // this.s = s;
        this.ts = ts;
    }

    // public static CalibrateRes runIt(TemplateSearchers ts) throws InterruptedException {
    //     CalibrateRes res = new CalibrateRes();

    //     ts.spiele.click(MyRobot.shot(ts.wholeScreen), ts.wholeScreen.x, ts.wholeScreen.y, 0);
    //     if (!s.spiele.runAndClick(500)) {
    //         res.error = "Button Spiele nicht gefunden!";
    //         return res;
    //     }
    //     if (!s.neuesSpiel.runAndClick(500)) {
    //         res.error = "Button Neues Spiel nicht gefunden!";
    //         return res;
    //     }
    //     if (!s.unsichtbaresSpiel.runAndClick(500)) {
    //         res.error = "Radiobutton Unsichtbares Spiel nicht gefunden!";
    //         return res;
    //     }
    //     if (!s.spielAnlegen.runAndClick(300)) {
    //         res.error = "Button Spiel anlegen nicht gefunden!";
    //         return res;
    //     }
    //     if (!s.enterWhite.runAndClick(1000)) {
    //         res.error = "Button enter white nicht gefunden!";
    //         return res;
    //     }
    //     Thread.sleep(500);
    //     try {
    //         res.calWhite = new CalibrationForSpin();
    //     } catch (IOException ex) {
    //         ex.printStackTrace();
    //         res.error = "Fehler bei Kalibrierung für Weiß: " + ex.getMessage();
    //         return res;
    //     }
    //     if (!s.bVerlassen.runAndClick(0)) {
    //         res.error = "Button Verlassen nicht gefunden!";
    //         return res;
    //     }
    //     if (!s.enterBlack.runAndClick(500)) {
    //         res.error = "Button enter black nicht gefunden!";
    //         return res;
    //     }
    //     Thread.sleep(500);
    //     try {
    //         res.calBlack = new CalibrationForSpin();
    //     } catch (IOException ex) {
    //         ex.printStackTrace();
    //         res.error = "Fehler bei Kalibrierung für Schwarz: " + ex.getMessage();
    //         return res;
    //     }

    //     if (!s.bSchliessen.runAndClick(0)) {
    //         res.error = "Button Schließen nicht gefunden!";
    //         return res;
    //     }

    //     return res;
    // }

    @Override
    public CalibrateRes doIt() throws InterruptedException {
        CalibrateRes res = new CalibrateRes();

        waitAndClick(ts.spiele);
        waitAndClick(ts.linkBackgammon);
        Thread.sleep(500);
        waitAndClick(ts.neuesSpiel);
        waitAndClick(ts.unsichtbaresSpiel);
        waitAndClick(ts.spielAnlegen);
        waitAndClick(ts.spielAnlegen);
        waitAndClick(ts.enterWhite);
        Thread.sleep(500);
        try {
            res.calWhite = new CalibrationForSpin();
        } catch (IOException ex) {
            res.error = "Fehler bei Kalibrierung für Weiß: " + ex.getMessage();
            return res;
        }
        waitAndClick(ts.bVerlassen);
        waitAndClick(ts.enterBlack);
        Thread.sleep(500);
        try {
            res.calBlack = new CalibrationForSpin();
        } catch (IOException ex) {
            ex.printStackTrace();
            res.error = "Fehler bei Kalibrierung für Schwarz: " + ex.getMessage();
            return res;
        }
        waitAndClick(ts.bSchliessen);
        return res;

    }

    private Point result = null;

    private void waitAndClick(TemplateSearcher s) throws InterruptedException {
        do {
            result = s.run(shot(), result, false);
            if (result != null) {
                break;
            }
            Thread.sleep(100);
            // Thread.sleep(2000);
        } while (true);
        MyRobot.click(result.x, result.y, s.width(), s.height());
    }

    private BufferedImage shot() {
        return MyRobot.shot(ts.wholeScreen);
    }
}
