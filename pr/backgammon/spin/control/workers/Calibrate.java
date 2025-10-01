package pr.backgammon.spin.control.workers;

import java.io.IOException;

import pr.backgammon.spin.control.CalibrationForSpin;
import pr.backgammon.spin.control.ScreenSearchers;
import pr.control.MyWorker;

public abstract class Calibrate extends MyWorker<CalibrateRes, Void> {
    private final ScreenSearchers s;

    public Calibrate(ScreenSearchers s) {
        this.s = s;
    }

    public static CalibrateRes runIt(ScreenSearchers s) throws InterruptedException {
        CalibrateRes res = new CalibrateRes();

        if (!s.spiele.runAndClick(500)) {
            res.error = "Button Spiele nicht gefunden!";
            return res;
        }
        if (!s.neuesSpiel.runAndClick(500)) {
            res.error = "Button Neues Spiel nicht gefunden!";
            return res;
        }
        if (!s.unsichtbaresSpiel.runAndClick(500)) {
            res.error = "Radiobutton Unsichtbares Spiel nicht gefunden!";
            return res;
        }
        if (!s.spielAnlegen.runAndClick(300)) {
            res.error = "Button Spiel anlegen nicht gefunden!";
            return res;
        }
        if (!s.enterWhite.runAndClick(1000)) {
            res.error = "Button enter white nicht gefunden!";
            return res;
        }
        Thread.sleep(500);
        try {
            res.calWhite = new CalibrationForSpin();
        } catch (IOException ex) {
            ex.printStackTrace();
            res.error = "Fehler bei Kalibrierung für Weiß: " + ex.getMessage();
            return res;
        }
        if (!s.bVerlassen.runAndClick(0)) {
            res.error = "Button Verlassen nicht gefunden!";
            return res;
        }
        if (!s.enterBlack.runAndClick(500)) {
            res.error = "Button enter black nicht gefunden!";
            return res;
        }
        Thread.sleep(500);
        try {
            res.calBlack = new CalibrationForSpin();
        } catch (IOException ex) {
            ex.printStackTrace();
            res.error = "Fehler bei Kalibrierung für Schwarz: " + ex.getMessage();
            return res;
        }
        
        if (!s.bSchliessen.runAndClick(0)) {
            res.error = "Button Schließen nicht gefunden!";
            return res;
        }

        return res;
    }

    @Override
    public CalibrateRes doIt() throws InterruptedException {
        return runIt(s);

        // CalibrateRes res = new CalibrateRes();

        // Thread.sleep(1000);

        // // Spiele
        // MyRobot.click(375, 175, 10, 10);
        // Thread.sleep(1000);
        // // Neues Spiel
        // MyRobot.click(304, 380, 80, 2);
        // Thread.sleep(1000);
        // // Unsichtbares Match
        // MyRobot.click(305, 604, 20, 2);
        // Thread.sleep(300);
        // // Spiel anlegen
        // MyRobot.click(255, 644, 322 - 255, 651 - 644);
        // Thread.sleep(1000);
        // // Eintritt Weiß
        // MyRobot.click(860, 550, 20, 20);
        // Thread.sleep(500);
        // try {
        // res.calWhite = new CalibrationForSpin();
        // } catch (IOException ex) {
        // ex.printStackTrace();
        // }
        // // Verlassen
        // MyRobot.click(1347, 608, 1410 - 1347, 614 - 608);
        // Thread.sleep(600);
        // // Eintritt Schwarz
        // MyRobot.click(907, 553, 925 - 907, 572 - 553);
        // Thread.sleep(500);
        // try {
        // res.calBlack = new CalibrationForSpin();
        // } catch (IOException ex) {
        // ex.printStackTrace();
        // }
        // MyRobot.click(1375, 232, 1435 - 1375, 239 - 232);
        // return res;
    }
}
