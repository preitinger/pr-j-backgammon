package pr.backgammon.spin.control;

import java.awt.GridLayout;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.awt.image.Raster;
import java.util.Arrays;

import javax.swing.ImageIcon;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;

import org.opencv.core.Mat;
import org.opencv.core.MatOfFloat;
import org.opencv.core.MatOfInt;
import org.opencv.core.Size;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;

import pr.control.Tools;
import pr.cv.BufferedImageToMat;

/**
 * detect rolls in Match on spin.de
 */
public class SpinRolls {
    private final CalibrationForSpin cal;
    private final int[] diceLeft = { 0, 0 };
    private final int[] diceRight = { 0, 0 };
    private final DiceGeometrics diceGeometrics;
    /**
     * loest Rolls.diceRect ab.
     */
    private final Rectangle subFromScreen;
    private final Rectangle subFromBoard;

    public void storeImg(BufferedImage board, int initialOppOwn, boolean left, boolean white, int die) {
        Rectangle rectl = new Rectangle();
        Rectangle rectr = new Rectangle();

        switch (initialOppOwn) {
            case 0:
                diceGeometrics.initialRoll(rectl, rectr);
                break;
            case 1:
                diceGeometrics.oppRoll(rectl, rectr);
                break;
            case 2:
                diceGeometrics.ownRoll(rectl, rectr);
                break;
            default:
                throw new IllegalArgumentException();
        }
        int startx = subFromBoard.x;
        int starty = subFromBoard.y;

        rectl.x += startx;
        rectl.y += starty;
        rectr.x += startx;
        rectr.y += starty;

        Rectangle rect = left ? rectl : rectr;
        BufferedImage dieImg = board.getSubimage(rect.x, rect.y, rect.width, rect.height);
        imgForCircles = BufferedImageToMat.toMat(dieImg, imgForCircles);
        try {
            Imgcodecs.imwrite(
                    "screenshots/dice/" + (white ? "white" : "black") + "/die-" + die + "_" + System.currentTimeMillis()
                            + ".bmp",
                    imgForCircles);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public SpinRolls(CalibrationForSpin cal, Rectangle boardScreenshotRect) {
        this.cal = cal;
        this.diceGeometrics = new DiceGeometrics(cal);
        subFromScreen = new Rectangle();
        diceGeometrics.screenshot(subFromScreen);
        subFromBoard = new Rectangle(
                subFromScreen.x - boardScreenshotRect.x,
                subFromScreen.y - boardScreenshotRect.y,
                subFromScreen.width,
                subFromScreen.height);
    }

    public void detectFromBoardShot(BufferedImage shot) {
        // System.out.println("subFromBoard " + subFromBoard);
        detectDice(shot, subFromBoard.x, subFromBoard.y);
    }

    private static final int DIE_BLACK_BODY_MIN = 0;
    private static final int DIE_BLACK_BODY_MAX = 30;
    private static final int DIE_BLACK_EYE_MIN = 220;
    private static final int DIE_BLACK_EYE_MAX = 255;

    private static final int DIE_WHITE_BODY_MIN = 220;
    private static final int DIE_WHITE_BODY_MAX = 255;
    private static final int DIE_WHITE_EYE_MIN = 0;
    private static final int DIE_WHITE_EYE_MAX = 70;

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

    private void detectDice(BufferedImage shot, int startx, int starty) {
        Raster raster = shot.getRaster();
        // System.out.println("startx " + startx + "   starty " + starty);
        Rectangle rectl = new Rectangle();
        Rectangle rectr = new Rectangle();
        int ownBodyMin = cal.ownWhite ? DIE_WHITE_BODY_MIN : DIE_BLACK_BODY_MIN;
        int ownBodyMax = cal.ownWhite ? DIE_WHITE_BODY_MAX : DIE_BLACK_BODY_MAX;
        int oppBodyMin = !cal.ownWhite ? DIE_WHITE_BODY_MIN : DIE_BLACK_BODY_MIN;
        int oppBodyMax = !cal.ownWhite ? DIE_WHITE_BODY_MAX : DIE_BLACK_BODY_MAX;

        // int ownEyeMin = cal.ownWhite ? DIE_WHITE_EYE_MIN : DIE_BLACK_EYE_MIN;
        // int ownEyeMax = cal.ownWhite ? DIE_WHITE_EYE_MAX : DIE_BLACK_EYE_MAX;
        // int oppEyeMin = !cal.ownWhite ? DIE_WHITE_EYE_MIN : DIE_BLACK_EYE_MIN;
        // int oppEyeMax = !cal.ownWhite ? DIE_WHITE_EYE_MAX : DIE_BLACK_EYE_MAX;

        diceLeft[0] = diceLeft[1] = diceRight[0] = diceRight[1] = 0;

        {
            // try initial roll

            diceGeometrics.initialRoll(rectl, rectr);
            rectl.x += startx;
            rectl.y += starty;
            rectr.x += startx;
            rectr.y += starty;
            int body1 = Tools.countColorRange(raster, rectl, oppBodyMin, oppBodyMax);
            // int die1 = Tools.countColorRange(raster, rectl, oppEyeMin, oppEyeMax);
            int body2 = Tools.countColorRange(raster, rectr, ownBodyMin, ownBodyMax);
            // int die2 = Tools.countColorRange(raster, rectr, ownEyeMin, ownEyeMax);

            if (body1 > 500 && body2 > 500) {
                // ln("body1 " + body1 + " die1 " + die1 + " body2 " + body2 + " die2 "
                // + die2);
                // int circles1 = searchCircles(shot.getSubimage(rectl.x, rectl.y, rectl.width,
                // rectl.height), "testDie1");
                // int circles2 = searchCircles(shot.getSubimage(rectr.x, rectr.y, rectr.width,
                // rectr.height), "testDie5");
                boolean whiteL = !cal.ownWhite, whiteR = cal.ownWhite;
                int circlesL = dieFromThreshold(shot.getSubimage(rectl.x, rectl.y, rectl.width, rectl.height), whiteL);
                int circlesR = dieFromThreshold(shot.getSubimage(rectr.x, rectr.y, rectr.width, rectr.height), whiteR);
                // System.out.println("circlesL " + circlesL + "  circlesR " + circlesR);
                // diceLeft[0] = calcOppDie(die1);
                diceLeft[0] = circlesL;
                diceLeft[1] = 0;
                // diceRight[0] = calcOwnDie(die2);
                diceRight[0] = circlesR;
                diceRight[1] = 0;
                return;
            }
        }
        {
            // try opp roll

            diceGeometrics.oppRoll(rectl, rectr);
            rectl.x += startx;
            rectl.y += starty;
            rectr.x += startx;
            rectr.y += starty;
            int body1 = Tools.countColorRange(raster, rectl, oppBodyMin, oppBodyMax);
            // int die1 = Tools.countColorRange(raster, rectl, oppEyeMin, oppEyeMax);
            int body2 = Tools.countColorRange(raster, rectr, oppBodyMin, oppBodyMax);
            // int die2 = Tools.countColorRange(raster, rectr, oppEyeMin, oppEyeMax);

            if (body1 > 500 && body2 > 500) {
                // ln("body1 " + body1 + " die1 " + die1 + " body2 " + body2 + " die2 "
                // + die2);
                // int circles1 = searchCircles(shot.getSubimage(rectl.x, rectl.y, rectl.width,
                // rectl.height), "testDie1");
                // int circles2 = searchCircles(shot.getSubimage(rectr.x, rectr.y, rectr.width,
                // rectr.height), "testDie5");
                // System.out.println("circles1 " + circles1 + " circles2 " + circles2);
                // diceLeft[0] = calcOppDie(die1);
                // diceLeft[1] = calcOppDie(die2);
                boolean whiteL = !cal.ownWhite, whiteR = !cal.ownWhite;
                int circlesL = dieFromThreshold(shot.getSubimage(rectl.x, rectl.y, rectl.width, rectl.height), whiteL);
                int circlesR = dieFromThreshold(shot.getSubimage(rectr.x, rectr.y, rectr.width, rectr.height), whiteR);
                // System.out.println("circlesL " + circlesL + "  circlesR " + circlesR);
                diceLeft[0] = circlesL;
                diceLeft[1] = circlesR;
                diceRight[0] = 0;
                diceRight[1] = 0;
                return;
            }
        }
        {
            // try own roll

            diceGeometrics.ownRoll(rectl, rectr);
            rectl.x += startx;
            rectl.y += starty;
            rectr.x += startx;
            rectr.y += starty;
            // System.out.println("rectl " + rectl);
            // System.out.println("rectr " + rectr);
            int body1 = Tools.countColorRange(raster, rectl, ownBodyMin, ownBodyMax);
            // int die1 = Tools.countColorRange(raster, rectl, ownEyeMin, ownEyeMax);
            int body2 = Tools.countColorRange(raster, rectr, ownBodyMin, ownBodyMax);
            // int die2 = Tools.countColorRange(raster, rectr, ownEyeMin, ownEyeMax);

            if (body1 > 500 && body2 > 500) {
                // ln("body1 " + body1 + " die1 " + die1 + " body2 " + body2 + " die2 "
                // + die2);
                diceLeft[0] = 0;
                diceLeft[1] = 0;
                // int circles1 = searchCircles(shot.getSubimage(rectl.x, rectl.y, rectl.width,
                // rectl.height), "testDie1");
                // int circles2 = searchCircles(shot.getSubimage(rectr.x, rectr.y, rectr.width,
                // rectr.height), "testDie5");
                // System.out.println("circles1 " + circles1 + " circles2 " + circles2);
                boolean whiteL = cal.ownWhite, whiteR = cal.ownWhite;
                int circlesL = dieFromThreshold(shot.getSubimage(rectl.x, rectl.y, rectl.width, rectl.height), whiteL);
                int circlesR = dieFromThreshold(shot.getSubimage(rectr.x, rectr.y, rectr.width, rectr.height), whiteR);
                // System.out.println("circlesL " + circlesL + "  circlesR " + circlesR);
                diceRight[0] = circlesL;
                diceRight[1] = circlesR;
                // diceRight[0] = calcOwnDie(die1);
                // diceRight[1] = calcOwnDie(die2);
                return;
            }
        }

    }

    private Mat imgForCircles = new Mat();
    private Mat imgGrayForCircles = new Mat();
    private Mat circles = new Mat();

    private int dieFromThreshold(BufferedImage img, boolean whiteDie) {
        // final double pixPerDieWhite = 14.236111111111109;
        // final double pixPerDieBlack = 3.989102564102564;
        final double pixPerDieWhite = 14.273148148148145;
        final double pixPerDieBlack = 3.9434210526315794; // 3.989102564102564; //4.03333333333333;
        final int threshold = whiteDie ? 100 : 185;

        imgForCircles = BufferedImageToMat.toMat(img, imgForCircles);
        Imgproc.cvtColor(imgForCircles, imgGrayForCircles, Imgproc.COLOR_BGR2GRAY);
        Mat gray = imgGrayForCircles;
        Imgproc.GaussianBlur(gray, gray, new Size(7, 7), 1.5, 1.5);
        Mat thresholdImg = new Mat();
        Imgproc.threshold(gray, thresholdImg, threshold, 255, Imgproc.THRESH_BINARY);

        // try {
        //     Tools.showImg("threshold " + threshold, MatToBufferedImage.matToBufferedImage(thresholdImg),
        //             false);
        // } catch (Exception ex) {
        //     ex.printStackTrace();
        // }

        MatOfInt channels = new MatOfInt(0);
        Mat mask = new Mat();
        Mat hist = new Mat();
        MatOfInt histSize = new MatOfInt(256);
        MatOfFloat ranges = new MatOfFloat(0, 256);
        Imgproc.calcHist(Arrays.asList(thresholdImg), channels, mask, hist, histSize, ranges);

        // System.out.println("Hist of thresholdImg:\n" + hist.dump());
        double sum = whiteDie ? hist.get(0, 0)[0] : hist.get(255, 0)[0];
        double dieFromThresholdImg = sum / (whiteDie ? pixPerDieWhite : pixPerDieBlack);
        int dieFromThresholdImg1 = (int) Math.round(dieFromThresholdImg);

        return dieFromThresholdImg1;
    }

    private int searchCircles(BufferedImage dieImg, String dbgFile) {
        // Mat img = BufferedImageToMat.toBgrMat(dieImg);
        imgForCircles = BufferedImageToMat.toMat(dieImg, imgForCircles);
        Imgproc.cvtColor(imgForCircles, imgGrayForCircles, Imgproc.COLOR_BGR2GRAY);
        // try {
        // Imgcodecs.imwrite("screenshots/" + dbgFile + ".bmp", gray);
        // // System.exit(1);
        // // Tools.showImg("gray", MatToBufferedImage.matToBufferedImage(gray), false);
        // } catch (Exception ex) {
        // ex.printStackTrace();
        // }
        int method = Imgproc.HOUGH_GRADIENT;

        double dp = 1.20;
        double minDist = 9;
        double param1 = 100; // last: 900
        double param2 = 11; // 30
        int minRadius = 2;
        int maxRadius = 5;

        // double dp = 1.00;
        // double minDist = 5;
        // double param1 = 900; // 10 zu wenig
        // double param2 = 8; // 30
        // int minRadius = 2;
        // int maxRadius = 5;

        // double dp = 1.2;
        // double minDist = 5;
        // double param1 = 60; // 60 // 70
        // double param2 = 13; // 30
        // int minRadius = 2;
        // int maxRadius = 5;
        Imgproc.HoughCircles(imgGrayForCircles, circles, method, dp, minDist, param1, param2, minRadius, maxRadius);
        int cols = circles.cols();
        // System.out.println("cols " + cols);

        return cols;
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

    public int die1() {
        return isInitialDice() ? getInitialDie1() : isOwnDice() ? getOwnDie1() : isOppDice() ? getOppDie1() : 0;
    }

    public int die2() {
        return isInitialDice() ? getInitialDie2() : isOwnDice() ? getOwnDie2() : isOppDice() ? getOppDie2() : 0;
    }

    private int getInitialDie1() {
        return diceLeft[0];
    }

    private int getInitialDie2() {
        return diceRight[0];
    }

    private int getOppDie1() {
        return diceLeft[0];
    }

    private int getOppDie2() {
        return diceLeft[1];
    }

    private int getOwnDie1() {
        return diceRight[0];
    }

    private int getOwnDie2() {
        return diceRight[1];
    }

    public void debug(BufferedImage screen, Rolls rolls, FastChequerSearch fastChequerSearch) throws Exception {

        Rectangle boardScreenshotRect = fastChequerSearch.boardScreenshotRect(null);
        System.out.println("subFromBoard " + subFromBoard);
        System.out.println("subFromScreen " + subFromScreen);
        System.out.println("boardScreenshotRect " + boardScreenshotRect);

        BoardSearchers searchers = new BoardSearchers(cal, boardScreenshotRect);
        BufferedImage boardShot = searchers.boardShot();
        BufferedImage diceImgFromBoard = boardShot.getSubimage(this.subFromBoard.x, subFromBoard.y, subFromBoard.width,
                subFromBoard.height);

        // Raster raster = screen.getRaster();
        BufferedImage screenshotFromRolls = rolls.createDiceScreenshot();
        Rectangle diceRect = this.subFromScreen;
        BufferedImage diceImg = screen.getSubimage(diceRect.x, diceRect.y, diceRect.width, diceRect.height);
        // JPanel
        // detectDice(raster, DIE_BLACK_BODY_MIN, DIE_BLACK_BODY_MAX);

        JPanel grid = new JPanel(new GridLayout(2, 3, 10, 10));
        grid.add(new JLabel("Rolls"));
        grid.add(new JLabel("subFromScreen"));
        grid.add(new JLabel("subFromBoard"));
        grid.add(new JLabel(new ImageIcon(screenshotFromRolls)));
        grid.add(new JLabel(new ImageIcon(diceImg)));
        grid.add(new JLabel(new ImageIcon(diceImgFromBoard)));
        JDialog dlg = new JDialog();
        dlg.add(grid);
        dlg.pack();
        dlg.setVisible(true);

        detectDice(diceImgFromBoard, 0, 0);
        System.out.println("diceImgFromBoard");
        dump();

        detectDice(diceImg, 0, 0);
        System.out.println("diceImg");
        dump();

        System.out.println("now, try to detect from whole screen raster...");
        detectDice(screen, subFromScreen.x, subFromScreen.y);
        dump();

        System.out.println("now, try to detect from board raster...");
        detectDice(boardShot, subFromBoard.x, subFromBoard.y);
        Arrays.fill(this.diceLeft, 0);
        Arrays.fill(this.diceRight, 0);
        dump();

        System.out.println("now directly detectFromBoard");
        detectFromBoardShot(boardShot);
        dump();
    }

    public void dump() {
        System.out.println("isInitialRollDice  " + isInitialDice());
        System.out.println("isOppDice  " + isOppDice());
        System.out.println("isOwnDice  " + isOwnDice());
        System.out.println("die1 " + die1() + "   die2 " + die2());

    }
}
