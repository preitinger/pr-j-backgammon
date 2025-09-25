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

import pr.control.Tools;

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

    public SpinRolls(CalibrationForSpin cal, Rectangle boardScreenshotRect) {
        this.cal = cal;
        this.diceGeometrics = new DiceGeometrics(cal);
        subFromScreen = new Rectangle();
        diceGeometrics.screenshot(subFromScreen);
        subFromBoard = new Rectangle(
            subFromScreen.x - boardScreenshotRect.x,
            subFromScreen.y - boardScreenshotRect.y,
            subFromScreen.width,
            subFromScreen.height
        );
    }

    public void detectFromBoardShot(Raster shot) {
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

    private void detectDice(Raster raster, int startx, int starty) {
        Rectangle rectl = new Rectangle();
        Rectangle rectr = new Rectangle();
        int ownBodyMin = cal.ownWhite ? DIE_WHITE_BODY_MIN : DIE_BLACK_BODY_MIN;
        int ownBodyMax = cal.ownWhite ? DIE_WHITE_BODY_MAX : DIE_BLACK_BODY_MAX;
        int oppBodyMin = !cal.ownWhite ? DIE_WHITE_BODY_MIN : DIE_BLACK_BODY_MIN;
        int oppBodyMax = !cal.ownWhite ? DIE_WHITE_BODY_MAX : DIE_BLACK_BODY_MAX;

        int ownEyeMin = cal.ownWhite ? DIE_WHITE_EYE_MIN : DIE_BLACK_EYE_MIN;
        int ownEyeMax = cal.ownWhite ? DIE_WHITE_EYE_MAX : DIE_BLACK_EYE_MAX;
        int oppEyeMin = !cal.ownWhite ? DIE_WHITE_EYE_MIN : DIE_BLACK_EYE_MIN;
        int oppEyeMax = !cal.ownWhite ? DIE_WHITE_EYE_MAX : DIE_BLACK_EYE_MAX;

        diceLeft[0] = diceLeft[1] = diceRight[0] = diceRight[1] = 0;
        
        {
            // try initial roll

            diceGeometrics.initialRoll(rectl, rectr);
            rectl.x += startx;
            rectl.y += starty;
            rectr.x += startx;
            rectr.y += starty;
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
            rectl.x += startx;
            rectl.y += starty;
            rectr.x += startx;
            rectr.y += starty;
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
            rectl.x += startx;
            rectl.y += starty;
            rectr.x += startx;
            rectr.y += starty;
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
        BufferedImage diceImgFromBoard = boardShot.getSubimage(this.subFromBoard.x, subFromBoard.y, subFromBoard.width, subFromBoard.height);

        Raster raster = screen.getRaster();
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

        detectDice(diceImgFromBoard.getRaster(), 0, 0);
        System.out.println("diceImgFromBoard");
        dump();

        detectDice(diceImg.getRaster(), 0, 0);
        System.out.println("diceImg");
        dump();


        System.out.println("now, try to detect from whole screen raster...");
        detectDice(raster, subFromScreen.x, subFromScreen.y);
        dump();

        System.out.println("now, try to detect from board raster...");
        detectDice(boardShot.getRaster(), subFromBoard.x, subFromBoard.y);
        Arrays.fill(this.diceLeft, 0);
        Arrays.fill(this.diceRight, 0);
        dump();

        System.out.println("now directly detectFromBoard");
        detectFromBoardShot(boardShot.getRaster());
        dump();
    }

    public void dump() {
        System.out.println("isInitialRollDice  " + isInitialDice());
        System.out.println("isOppDice  " + isOppDice());
        System.out.println("isOwnDice  " + isOwnDice());
        System.out.println("die1 " + die1() + "   die2 " + die2());

    }
}
