package pr.backgammon.spin.control;

import java.awt.Rectangle;
import java.awt.Toolkit;
import java.awt.image.BufferedImage;
import java.awt.image.Raster;
import java.io.File;
import java.util.ArrayList;

import pr.control.MyRobot;
import pr.control.Tools;

/**
 * Auch zu ersetzen durch ein `FastRolls` analog zu `FastChequerSearch`.
 * Aber ggf. ganz anderes Interface notwendig fuer `SpinTracking`.
 * Dies ist kopiert von `spin.de` in `disney-plus`.
 */
public class Rolls {
    public Rolls(CalibrationForSpin cal) {
        this.cal = cal;
        this.screenRect = new Rectangle(Toolkit.getDefaultToolkit().getScreenSize());
        this.diceGeometrics = new DiceGeometrics(cal);
        this.diceRect = new Rectangle();
        this.diceGeometrics.screenshot(diceRect);
    }

    // private final ArrayList<BufferedImage> fakeScreens = loadFakeScreens();
    private final ArrayList<BufferedImage> fakeScreens = new ArrayList<>();

    private static ArrayList<BufferedImage> loadFakeScreens() {
        ArrayList<BufferedImage> res = new ArrayList<>();
        try {
            res.add(Tools.loadImg(new File("screen-last.png")));
            res.add(Tools.loadImg(new File("screen-current.png")));
            return res;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    // private static BufferedImage loadFakeScreen() {
    // static BufferedImage[] fakeScreens = new BufferedImage[2];
    // fake
    // try {
    // return Tools.loadImg(new File("black-own-16.png"));
    // } catch (Exception e) {
    // throw new RuntimeException(e);
    // }
    // }

    // private final BufferedImage FAKE_SCREEN = null; // loadFakeScreen();
    // private final BufferedImage FAKE_SCREEN = loadFakeScreen();

    public BufferedImage createScreenCapture() {
        // if (FAKE_SCREEN == null) {
        // return r.createScreenCapture(screenRect);
        if (fakeScreens.isEmpty()) {
            return MyRobot.shot(screenRect);
        } else {
            BufferedImage img = fakeScreens.get(0);

            if (fakeScreens.size() > 1) {
                fakeScreens.remove(0);
            }

            return img;
        }
    }

    public BufferedImage createDiceScreenshot() {
        return MyRobot.shot(diceRect);
    }

    public void waitForInitialRoll() throws InterruptedException {
        do {
            Thread.sleep(100);
            detectDice(createDiceScreenshot());
        } while (!isInitialDice());

        this.screenshot1 = createScreenCapture();
        detectDice(screenshot1.getSubimage(diceRect.x, diceRect.y, diceRect.width, diceRect.height));
        int[] oldLeft = diceLeft.clone();
        int[] oldRight = diceRight.clone();
        assert(oldLeft[0] == diceLeft[0]);
        assert(oldLeft[1] == diceLeft[1]);
        assert(oldRight[0] == diceRight[0]);
        assert(oldRight[1] == diceRight[1]);
        dumpDice();
    }

    public void waitForAnyRoll() throws InterruptedException {
        do {
            Thread.sleep(100);
            detectDice(createDiceScreenshot());
        } while (!(isInitialDice() || isOppDice() || isOwnDice()));

        this.screenshot1 = createScreenCapture();
        int[] oldLeft = diceLeft.clone();
        int[] oldRight = diceRight.clone();
        detectDice(screenshot1.getSubimage(diceRect.x, diceRect.y, diceRect.width, diceRect.height));
        assert(oldLeft[0] == diceLeft[0]);
        assert(oldLeft[1] == diceLeft[1]);
        assert(oldRight[0] == diceRight[0]);
        assert(oldRight[1] == diceRight[1]);
        dumpDice();
    }

    public void waitForNewRoll() throws InterruptedException {
        int[] oldLeft = diceLeft.clone();
        int[] oldRight = diceRight.clone();
        do {
            Thread.sleep(100);
            detectDice(createDiceScreenshot());
        } while (!((isInitialDice() || isOppDice() || isOwnDice()) && (oldLeft[0] != diceLeft[0]
                || oldLeft[1] != diceLeft[1] || oldRight[0] != diceRight[0] || oldRight[1] != diceRight[1])));

        this.screenshot1 = createScreenCapture();
        System.arraycopy(diceLeft, 0, oldLeft, 0, diceLeft.length);
        System.arraycopy(diceRight, 0, oldRight, 0, diceRight.length);
        detectDice(screenshot1.getSubimage(diceRect.x, diceRect.y, diceRect.width, diceRect.height));
        assert(oldLeft[0] == diceLeft[0]);
        assert(oldLeft[1] == diceLeft[1]);
        assert(oldRight[0] == diceRight[0]);
        assert(oldRight[1] == diceRight[1]);
        dumpDice();
    }

    private void dumpDice() {
        if (isInitialDice()) {
            ln("initial dice   " + getInitialDie1() + " " + getInitialDie2());
        } else if (isOppDice()) {
            ln("opp dice   " + getOppDie1() + " " + getOppDie2());
        } else if (isOwnDice()) {
            ln("own dice   " + getOwnDie1() + " " + getOwnDie2());
        }
    }

    private static int DIE_N_BLACK = 19;
    private static int DIE_N_WHITE = 20 * 7 / 6;

    private static int calcDieBlack(int pixels) {
        return (pixels + (DIE_N_BLACK >> 1)) / DIE_N_BLACK;
    }

    private static int calcDieWhite(int pixels) {
        return (pixels + (DIE_N_WHITE >> 1)) / DIE_N_WHITE;
    }

    private int calcOppDie(int pixels) {
        return cal.ownWhite ? calcDieBlack(pixels) : calcDieWhite(pixels);
    }

    private int calcOwnDie(int pixels) {
        return cal.ownWhite ? calcDieWhite(pixels) : calcDieBlack(pixels);
    }

    public void detectDiceFromFullScreenshot(BufferedImage fullScreenshot) {
        // screenshot1 auch setzen, damit anschliessender Aufruf getScreenshot() nicht zu unerwartetem Verhalten fuehrt
        detectDice((screenshot1 = fullScreenshot).getSubimage(diceRect.x, diceRect.y, diceRect.width, diceRect.height));
    }

    private void detectDice(BufferedImage diceScreenshot) {
        Rectangle rectl = new Rectangle();
        Rectangle rectr = new Rectangle();
        Raster raster = diceScreenshot.getRaster();
        int ownBodyMin = cal.ownWhite ? DIE_WHITE_BODY_MIN : DIE_BLACK_BODY_MIN;
        int ownBodyMax = cal.ownWhite ? DIE_WHITE_BODY_MAX : DIE_BLACK_BODY_MAX;
        int oppBodyMin = !cal.ownWhite ? DIE_WHITE_BODY_MIN : DIE_BLACK_BODY_MIN;
        int oppBodyMax = !cal.ownWhite ? DIE_WHITE_BODY_MAX : DIE_BLACK_BODY_MAX;

        int ownEyeMin = cal.ownWhite ? DIE_WHITE_EYE_MIN : DIE_BLACK_EYE_MIN;
        int ownEyeMax = cal.ownWhite ? DIE_WHITE_EYE_MAX : DIE_BLACK_EYE_MAX;
        int oppEyeMin = !cal.ownWhite ? DIE_WHITE_EYE_MIN : DIE_BLACK_EYE_MIN;
        int oppEyeMax = !cal.ownWhite ? DIE_WHITE_EYE_MAX : DIE_BLACK_EYE_MAX;

        {
            // try initial roll

            diceGeometrics.initialRoll(rectl, rectr);
            int body1 = Tools.countColorRange(raster, rectl, oppBodyMin, oppBodyMax);
            int die1 = Tools.countColorRange(raster, rectl, oppEyeMin, oppEyeMax);
            int body2 = Tools.countColorRange(raster, rectr, ownBodyMin, ownBodyMax);
            int die2 = Tools.countColorRange(raster, rectr, ownEyeMin, ownEyeMax);

            if (body1 > 500 && body2 > 500) {
                // ln("body1 " + body1 + " die1 " + die1 + " body2 " + body2 + " die2 "
                // + die2);
                diceLeft[0] = calcOppDie(die1);
                diceLeft[1] = 0;
                diceRight[0] = calcOwnDie(die2);
                diceRight[1] = 0;
                return;
            }
        }
        {
            // try opp roll

            diceGeometrics.oppRoll(rectl, rectr);
            int body1 = Tools.countColorRange(raster, rectl, oppBodyMin, oppBodyMax);
            int die1 = Tools.countColorRange(raster, rectl, oppEyeMin, oppEyeMax);
            int body2 = Tools.countColorRange(raster, rectr, oppBodyMin, oppBodyMax);
            int die2 = Tools.countColorRange(raster, rectr, oppEyeMin, oppEyeMax);

            if (body1 > 500 && body2 > 500) {
                // ln("body1 " + body1 + " die1 " + die1 + " body2 " + body2 + " die2 "
                // + die2);
                diceLeft[0] = calcOppDie(die1);
                diceLeft[1] = calcOppDie(die2);
                diceRight[0] = 0;
                diceRight[1] = 0;
                return;
            }
        }
        {
            // try own roll

            diceGeometrics.ownRoll(rectl, rectr);
            int body1 = Tools.countColorRange(raster, rectl, ownBodyMin, ownBodyMax);
            int die1 = Tools.countColorRange(raster, rectl, ownEyeMin, ownEyeMax);
            int body2 = Tools.countColorRange(raster, rectr, ownBodyMin, ownBodyMax);
            int die2 = Tools.countColorRange(raster, rectr, ownEyeMin, ownEyeMax);

            if (body1 > 500 && body2 > 500) {
                // ln("body1 " + body1 + " die1 " + die1 + " body2 " + body2 + " die2 "
                // + die2);
                diceLeft[0] = 0;
                diceLeft[1] = 0;
                diceRight[0] = calcOwnDie(die1);
                diceRight[1] = calcOwnDie(die2);
                return;
            }
        }

    }

    public boolean isInitialDice() {
        return diceLeft[0] > 0 && diceLeft[1] == 0 && diceRight[0] > 0 && diceRight[1] == 0;
    }

    public boolean isOwnDice() {
        return diceLeft[0] == 0 && diceLeft[1] == 0 && diceRight[0] > 0 && diceRight[1] > 0;
    }

    public boolean isOppDice() {
        return diceRight[0] == 0 && diceRight[1] == 0 && diceLeft[0] > 0 && diceLeft[1] > 0;
    }

    public int getInitialDie1() {
        return diceLeft[0];
    }

    public int getInitialDie2() {
        return diceRight[0];
    }

    public int getOppDie1() {
        return diceLeft[0];
    }

    public int getOppDie2() {
        return diceLeft[1];
    }

    public int getOwnDie1() {
        return diceRight[0];
    }

    public int getOwnDie2() {
        return diceRight[1];
    }

    public BufferedImage getScreenshot() {
        return screenshot1;
    }

    private final CalibrationForSpin cal;
    private final Rectangle screenRect, diceRect;
    private final int[] diceLeft = { 0, 0 };
    private final int[] diceRight = { 0, 0 };
    private final DiceGeometrics diceGeometrics;
    private BufferedImage screenshot1;

    private static final int DIE_BLACK_BODY_MIN = 0;
    private static final int DIE_BLACK_BODY_MAX = 30;
    private static final int DIE_BLACK_EYE_MIN = 220;
    private static final int DIE_BLACK_EYE_MAX = 255;

    private static final int DIE_WHITE_BODY_MIN = 220;
    private static final int DIE_WHITE_BODY_MAX = 255;
    private static final int DIE_WHITE_EYE_MIN = 0;
    private static final int DIE_WHITE_EYE_MAX = 70;

    static void ln(String line) {
        System.out.println(line);
    }
}
