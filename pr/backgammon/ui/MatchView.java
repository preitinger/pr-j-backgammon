package pr.backgammon.ui;

import java.awt.*;
import java.awt.font.LineMetrics;

import javax.swing.JPanel;

import pr.backgammon.Match;
import pr.backgammon.Roll;

public class MatchView extends JPanel {
    private final Match match;
    private final Polygon polygon = new Polygon();
    private final boolean clockwise;
    private double dieAngle1, dieAngle2;

    public MatchView(Match match, boolean clockwise) {
        this.match = match;
        this.clockwise = clockwise;
        this.dieAngle1 = -Math.PI / 8 + Math.random() * Math.PI / 4;
        this.dieAngle2 = -Math.PI / 8 + Math.random() * Math.PI / 4;
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g;
        int w = getWidth();
        int h = getHeight();
        final int gapw = 10;
        final int gaph = 10;
        // Width is separated into 12 columns, bar, off, and cube
        int unit = (w - gapw - gapw) / 18 / 2;
        int unitH = (h - gaph - gaph) / 25;
        if (unitH < unit)
            unit = unitH;
        int unit2 = unit + unit;
        int left = (w - unit * 36) / 2;
        int top = (h - 26 * unit) / 2;
        int bottom = top + 26 * unit;
        int cubeX = clockwise ? left + (36 - 3) * unit : left + unit;
        int cubeW = unit2;
        int cubeH = unit2;
        int cubeOwnY = bottom - unit - cubeH;
        int cubeOppY = top + unit;
        int cubeInitY = top + (26 * unit - cubeH) / 2;
        int cubeOfferedOppX = left + 9 * unit;
        int cubeOfferedOwnX = left + 23 * unit;
        int own = match.getIndexOfOwn();
        int cubeOwner = match.getCubeOwner();
        System.out.println("cubeOwner " + cubeOwner);
        paintBoard(g2, left, top, unit);
        paintBorder(g2, left, top, unit);

        Color[] checkerColors = {
                Color.WHITE,
                Color.BLACK,
        };

        paintCheckersOnPips(g2, left, top, unit, own, checkerColors);
        paintCheckersOnBar(g2, left, top, unit, own, checkerColors);

        paintCube(g2,
                match.isCubeOffered() ? (cubeOwner == own ? cubeOfferedOwnX : cubeOfferedOppX) : cubeX,
                match.isCubeOffered() ? cubeInitY
                        : (cubeOwner == -1 ? cubeInitY : cubeOwner == own ? cubeOwnY : cubeOppY),
                cubeW,
                cubeH,
                match.getCubeVal(), cubeOwner == -1 ? (clockwise ? Math.PI / 2 : -Math.PI / 2) : cubeOwner == own ? 0 : Math.PI,
                Color.WHITE,
                Color.RED);

        int offeredResign = match.getOfferedResign();
        if (offeredResign != 0) {
            paintCube(g2,
                    offeredResign > 0 ? cubeOfferedOppX : cubeOfferedOwnX,
                    cubeInitY,
                    cubeW,
                    cubeH,
                    Math.abs(offeredResign),
                    offeredResign > 0 ? 0 : Math.PI,
                    Color.WHITE,
                    Color.GRAY);
        }

        paintScore(g2, left, top, unit, own);
        paintRoll(g2, left, top, unit, own);
    }

    private void paintRoll(Graphics2D g, int boardx, int boardy, int unit, int own) {
        int active = match.getActivePlayer();
        if (active != -1) {
            Roll roll = match.getRoll();
            if (roll != null) {
                boolean initial = match.isInitialRoll();
                int x1, x2, fg1, fg2;
                if (initial) {
                    x1 = boardx + unit * 9;
                    fg1 = 0;
                    x2 = boardx + unit * 27;
                    fg2 = 1;
                } else if(active != own) {
                    x1 = boardx + unit * 7;
                    fg1 = 0;
                    x2 = boardx + unit * 11;
                    fg2 = 0;
                } else {
                    x1 = boardx + unit * 25;
                    fg1 = 1;
                    x2 = boardx + unit * 29;
                    fg2 = 1;
                }
                int y = boardy + 13 * unit;
                Color[] colors = { Color.WHITE, Color.BLACK };
                paintCube(g, x1 - unit, y - unit, unit * 2, unit * 2, roll.die1(), dieAngle1, colors[fg1], colors[1 - fg1]);
                paintCube(g, x2 - unit, y - unit, unit * 2, unit * 2, roll.die2(), dieAngle2, colors[fg2], colors[1 - fg2]);
            }
        }
    }

    private void paintScore(Graphics2D g, int boardx, int boardy, int unit, int own) {
        int scoreOwn = match.getScore(own);
        int scoreOpp = match.getScore(1 - own);
        int centery = boardy + unit * 13;
        int d1 = unit * 4;
        int d2 = d1 + unit;
        g.setColor(Color.BLACK);
        int x = boardx + (clockwise ? 34 : 2) * unit;
        paintCenteredText(g, String.valueOf(scoreOwn), x, centery + d1);
        paintCenteredText(g, match.getPlayerName(own), x, centery + d2);

        paintCenteredText(g, match.getPlayerName(1 - own), x, centery - d2);
        paintCenteredText(g, String.valueOf(scoreOpp), x, centery - d1);
    }

    private void paintCheckersOnBar(Graphics2D g, int boardx, int boardy, int unit, int own, Color[] checkerColors) {
        int barx = boardx + 17 * unit;
        int barOwn = match.getCheckers(25);
        int barOpp = -match.getCheckers(0);

        if (barOpp > 0) {
            Color bg = checkerColors[1];
            Color fg = checkerColors[0];
            paintCheckerRow(g, barx, boardy + 16 * unit, true, unit, fg, bg, barOpp, 3);
        }

        if (barOwn > 0) {
            Color bg = checkerColors[0];
            Color fg = checkerColors[1];
            paintCheckerRow(g, barx, boardy + 10 * unit, false, unit, fg, bg, barOwn, 3);
        }
    }

    private void paintCheckersOnPips(Graphics2D g, int x, int y, int unit, int own, Color[] colors) {
        int unit2 = unit + unit;

        int top = y + unit;
        int bottom = y + unit * 25;

        int field, n;
        Color fg, bg;

        for (int i = 0; i < 6; ++i) {

            field = clockwise ? 24 - i : 13 + i;
            n = match.getCheckers(field);
            bg = n > 0 ? colors[0] : colors[1];
            fg = n > 0 ? colors[1] : colors[0];
            if (n < 0)
                n = -n;
            if (n != 0)
                paintCheckersOnPip(g, x + unit2 * (2 + i), top, true, unit, fg, bg, n);
            // System.out.println("field " + field + " n " + n);

            field = clockwise ? 1 + i : 12 - i;
            n = match.getCheckers(field);
            bg = n > 0 ? colors[0] : colors[1];
            fg = n > 0 ? colors[1] : colors[0];
            if (n < 0)
                n = -n;
            if (n != 0)
                paintCheckersOnPip(g, x + unit2 * (2 + i), bottom, false, unit, fg, bg, n);
            // System.out.println("field " + field + " n " + n);

            field = clockwise ? 18 - i : 19 + i;
            n = match.getCheckers(field);
            bg = n > 0 ? colors[0] : colors[1];
            fg = n > 0 ? colors[1] : colors[0];
            if (n < 0)
                n = -n;
            if (n != 0)
                paintCheckersOnPip(g, x + (10 + i) * unit2, top, true, unit, fg, bg, n);
            // System.out.println("field " + field + " n " + n);

            field = clockwise ? 7 + i : 6 - i;
            n = match.getCheckers(field);
            bg = n > 0 ? colors[0] : colors[1];
            fg = n > 0 ? colors[1] : colors[0];
            if (n < 0)
                n = -n;
            if (n != 0)
                paintCheckersOnPip(g, x + unit2 * (10 + i), bottom, false, unit, fg, bg, n);
            // System.out.println("field " + field + " n " + n);
        }
    }

    private void paintChecker(Graphics2D g, int x, int y, int unit, Color color) {
        g.setPaint(color);
        g.fillOval(x, y, unit * 2, unit * 2);
    }

    private void paintBorder(Graphics2D g, int x, int y, int unit) {
        g.setColor(Color.BLACK);
        g.drawRect(x, y, unit * 36, unit * 26);
    }

    private void paintBoard(Graphics2D g, int x, int y, int unit) {
        int unit2 = unit + unit;

        int top = y + unit;
        int bottom = y + unit * 25;

        g.setPaint(Color.ORANGE.darker());
        g.fillRect(x + (4 + 12) * unit, top, unit2 * 2, unit * 24);
        g.setColor(Color.BLACK);
        g.drawRect(x + (4 + 12) * unit, top, unit2 * 2, unit * 24);

        Color[] colors = {
                Color.GREEN.darker(),
                Color.GRAY
        };

        for (int i = 0; i < 6; ++i) {
            int colorIdxDown = i & 1;
            int colorIdxUp = 1 - colorIdxDown;
            Color colorDown = colors[colorIdxDown];
            Color colorUp = colors[colorIdxUp];
            paintPip(g, x + unit2 * (2 + i), top, true, unit, colorDown);
            paintPip(g, x + unit2 * (2 + i), bottom, false, unit, colorUp);
            paintPip(g, x + (10 + i) * unit2, top, true, unit, colorDown);
            paintPip(g, x + unit2 * (10 + i), bottom, false, unit, colorUp);
        }
    }

    private void paintCheckerRow(Graphics2D g, int x, int y, boolean down, int unit, Color fg, Color bg, int n,
            int maxPaint) {
        g.setPaint(bg);
        int npaint = Math.min(maxPaint, n);
        int unit2 = unit + unit;

        if (!down) {
            y -= unit2;
        }

        for (int i = 0; i < npaint; ++i) {
            paintChecker(g, x, y + (i * unit2) * (down ? 1 : -1), unit, bg);
        }

        if (n > npaint) {
            g.setColor(fg);
            paintCenteredText(g, String.valueOf(n), x + unit, y + unit + (npaint - 1) * unit2 * (down ? 1 : -1));
        }

    }

    private void paintCheckersOnPip(Graphics2D g, int x, int y, boolean down, int unit, Color fg, Color bg, int n) {
        paintCheckerRow(g, x, y, down, unit, fg, bg, n, 5);
    }

    // private void paintCheckersOnBar(Graphics2D g, int x, int y, boolean down, int
    // unit, Color fg, Color bg, int n) {
    // paintCheckerRow(g, x, y, down, unit, fg, bg, n, 3);
    // }

    private void paintCenteredText(Graphics2D g, String s, int centerX, int centerY) {
        FontMetrics fm = g.getFontMetrics();
        LineMetrics lm = fm.getLineMetrics(s, g);
        g.drawString(s, centerX - fm.stringWidth(s) / 2, centerY - lm.getHeight() / 2 + lm.getAscent());
    }

    private void paintPip(Graphics2D g, int x, int y, boolean down, int unit, Color color) {
        Polygon p = polygon;
        g.setPaint(color);
        p.reset();
        p.addPoint(x, y);
        p.addPoint(x + unit, y + (11 * unit) * (down ? 1 : -1));
        p.addPoint(x + 2 * unit, y);
        g.fillPolygon(p);
    }

    private void paintCube(Graphics2D g, int x, int y, int w, int h, int val, double rotation, Color fg, Color bg) {
        g.translate(x + w / 2, y + h / 2);
        g.rotate(rotation);
        g.setPaint(bg);
        g.fillRoundRect(-w / 2, -h / 2, w, h, 10, 10);
        // g.setPaint(Color.WHITE);
        // g.fillRect(x + 12, y + 12, w - 24, h - 24);
        String s = String.valueOf(val);
        Font oldFont = g.getFont();
        g.setFont(oldFont.deriveFont(Font.BOLD));
        FontMetrics fm = g.getFontMetrics();
        LineMetrics lm = fm.getLineMetrics(s, g);
        g.setColor(fg);
        g.drawString(s, (-fm.stringWidth(s)) / 2, (-lm.getHeight()) / 2 + lm.getAscent());
        g.rotate(-rotation);
        g.translate(-(x + w / 2), -(y + h / 2));
        g.setFont(oldFont);
    }
}
