package pr.backgammon.view;

import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Polygon;
import java.awt.RenderingHints;
import java.awt.font.LineMetrics;
import java.awt.image.BufferedImage;

import javax.swing.JPanel;

import pr.backgammon.model.Match;
import pr.backgammon.model.OngoingMove;
import pr.backgammon.model.Roll;
import pr.control.Tools;

/**
 * Pip-Nummerierung ist wie folgt. Weiß spielt immer von 1 nach 24. Schwarz
 * spielt immer von 24 nach 1.
 */
public class MatchView extends JPanel {
    private static final boolean NO_SCALE = false; // set to true for testing after drawing the board without scaling to
                                                  // the panel size.

    private final BufferedImage boardBackground;
    private Match match = null;
    private OngoingMove ongoingMove = null;
    private final Polygon polygon = new Polygon();
    private boolean ownWhite, clockwise;
    private double dieAngle1, dieAngle2;
    private MatchViewListener listener = null;
    private boolean painted = false;
    // private int boardx;
    // private int boardy;
    // private int unit;
    private int pipWidth;
    private int pipHeight;
    private int l1;
    private int r1;
    private int l2;
    private int r2;
    private int t;
    private int b;
    private static final Color BOARD_COLOR = new Color(42, 48, 61);
    private static final Color[] PIP_COLORS = {
            new Color(90, 84, 78),
            new Color(17, 16, 18)
    };

    private final Color[] chequerColors = new Color[2];
    private final Color TEXT_BG_COLOR = PIP_COLORS[0];

    private static BufferedImage loadBoardBackground() {
        try {
            return Tools.loadImg("pr/res/board/BoardBackground.png");
        } catch (Exception ex) {
            ex.printStackTrace();
            return null;
        }
    }

    public MatchView(Match match, boolean ownWhite, boolean clockwise) {
        this.boardBackground = loadBoardBackground();
        this.match = match;
        this.ownWhite = ownWhite;
        this.clockwise = clockwise;
        this.dieAngle1 = -Math.PI / 8 + Math.random() * Math.PI / 4;
        this.dieAngle2 = -Math.PI / 8 + Math.random() * Math.PI / 4;

        setFocusable(true);

        // addMouseListener(new MouseAdapter() {

        // @Override
        // public void mouseClicked(MouseEvent e) {
        // int x = e.getX();
        // int y = e.getY();
        // int field = containingField(x, y);
        // if (listener != null && field != -1) {
        // listener.pipClicked(clockwise ? field : 25 - field);
        // }
        // }
        // });
        // addMouseMotionListener(new MouseMotionAdapter() {
        // @Override
        // public void mouseMoved(MouseEvent e) {
        // if (listener == null)
        // return;
        // int x = e.getX();
        // int y = e.getY();
        // int field = containingField(x, y);
        // if (match.ongoingMove.hoveredField != -1 && field != hoveredField) {
        // listener.pipExited(clockwise ? hoveredField : 25 - hoveredField);
        // hoveredField = -1;
        // }
        // if (hoveredField == -1 && field != -1) {
        // hoveredField = field;
        // listener.pipEntered(clockwise ? hoveredField : 25 - hoveredField);
        // }
        // }
        // });
    }

    public void setListener(MatchViewListener l) {
        if (listener != null) {
            removeMouseListener(listener);
            removeMouseMotionListener(listener);
            removeKeyListener(listener);
        }

        listener = l;

        if (listener != null) {
            addMouseListener(listener);
            addMouseMotionListener(listener);
            addKeyListener(listener);
        }
    }

    @Override
    protected void paintComponent(Graphics g1) {
        Graphics2D g = (Graphics2D) g1;

        if (boardBackground == null) {
            paintComponentOld(g);
            return;
        }

        g.setRenderingHint(
                RenderingHints.KEY_INTERPOLATION,
                RenderingHints.VALUE_INTERPOLATION_BILINEAR

        );

        int w = getWidth();
        int h = getHeight();
        int bgw = boardBackground.getWidth();
        int bgh = boardBackground.getHeight();
        int background_offx = 77;
        int background_offy = 18;
        int barWidth = 32;
        pipWidth = roundInt(262 - background_offx) / 6;
        pipHeight = 178 - background_offy;
        int vertPipDist = 242 - 178;
        int borderLeftWidth = 16; // TODO measure
        int borderRightWidth = 16; // TODO measure

        double scale = calcBgScale(w, h);
        bgw = scaleInt(bgw, scale);
        bgh = scaleInt(bgh, scale);
        background_offx = scaleInt(background_offx, scale);
        background_offy = scaleInt(background_offy, scale);
        barWidth = scaleInt(barWidth, scale);
        pipWidth = scaleInt(pipWidth, scale);
        pipHeight = scaleInt(pipHeight, scale);
        vertPipDist = scaleInt(vertPipDist, scale);
        borderLeftWidth = scaleInt(borderLeftWidth, scale);
        borderRightWidth = scaleInt(borderRightWidth, scale);

        int left = (w - bgw) >> 1;
        int top = (h - bgh) >> 1;
        if (NO_SCALE) {
            left = top = 0;
        }
        int right = w - left;
        int bottom = h - top;
        l1 = left + background_offx;
        r1 = l1 + pipWidth * 6;
        t = top + background_offy;
        l2 = r1 + barWidth;
        r2 = l2 + pipWidth * 6;
        t = top + background_offy;
        b = t + pipHeight + vertPipDist + pipHeight;

        drawBoardBackground(g, scale, left, top);

        if (match == null) {
            g.setColor(Color.BLACK);
            g.setFont(g.getFont().deriveFont(Font.BOLD, 25));
            this.paintCenteredText(g, "Aktuell kein Match", w / 2, h / 2);
            painted = false;
            return;
        }

        drawBoard(g, pipWidth, barWidth, pipHeight, vertPipDist);
        int own = match.getIndexOfOwn();

        Color ownColor = ownWhite ? Color.WHITE : Color.BLACK;
        Color oppColor = ownWhite ? Color.BLACK : Color.WHITE;

        chequerColors[0] = ownColor;
        chequerColors[1] = oppColor;

        drawChequersOnPips(g, own, chequerColors);
        drawChequersOnBar(g, chequerColors);
        drawChequersOff(g, chequerColors, borderLeftWidth, borderRightWidth);

        int cubeOfferedOppX = (l1 + r1) >> 1;
        int cubeOfferedOwnX = (l2 + r2) >> 1;
        int cubeOwner = match.getCubeOwner();
        int cubeW = pipWidth;
        int cubeH = pipWidth;
        int cubeOwnY = b - cubeH;
        int cubeOppY = t;
        int cubeX = clockwise ? r2 + borderRightWidth : l1 - borderLeftWidth - cubeW;

        int cubeInitY = (t + b - cubeH) >> 1;

        if (match.cube.used) {

            String cubeString = match.isCrawfordRound() ? "CR" : String.valueOf(match.getCubeVal());
            paintCube(g,
                    match.isCubeOffered() ? (cubeOwner == own ? cubeOfferedOwnX : cubeOfferedOppX) : cubeX,
                    match.isCubeOffered() ? cubeInitY
                            : (cubeOwner == -1 ? cubeInitY : cubeOwner == own ? cubeOwnY : cubeOppY),
                    cubeW,
                    cubeH,
                    cubeString,
                    cubeOwner == -1 ? (clockwise ? Math.PI / 2 : -Math.PI / 2) : cubeOwner == own ? 0 : Math.PI,
                    Color.WHITE,
                    Color.RED);

        }
        int offeredResign = match.getOfferedResign();
        if (offeredResign != 0) {
            paintCube(g,
                    offeredResign > 0 ? cubeOfferedOppX : cubeOfferedOwnX,
                    cubeInitY,
                    cubeW,
                    cubeH,
                    Math.abs(offeredResign),
                    offeredResign > 0 ? 0 : Math.PI,
                    Color.WHITE,
                    Color.GRAY);
        }
        drawScore(g, own, borderLeftWidth, borderRightWidth);
        drawRoll(g, own);
        drawPipCounts(g, own);

        // final int unit2 = pipWidth;
        // final int unit12 = unit2 * 6;
        // final int unit11 = unit12 - unit;
        // int l1 = boardx + unit2 * 2;
        // int r1 = l1 + unit12;
        // int l2 = boardx + unit2 * 10;
        // int r2 = l2 + unit12;
        // int t = boardy + unit;
        // int b = boardy + unit * 25;

        {
            // paint pips

        }

        // if (boardBackground != null) {
        // double scaleX = (double) (unit * 36) / boardBackground.getWidth();
        // double scaleY = (double) (unit * 26) / boardBackground.getHeight();
        // var oldTransform = g.getTransform();
        // g.setRenderingHint(RenderingHints.KEY_INTERPOLATION,
        // RenderingHints.VALUE_INTERPOLATION_BILINEAR);
        // g.translate(boardx, boardy);
        // g.scale(scaleX, scaleY);
        // g.drawImage(boardBackground, 0, 0, null);
        // g.setTransform(oldTransform);
        // System.out.println("background drawn");
        // System.out.println("unit " + unit + " boardx " + boardx + " boardy " + boardy
        // + " scaleX " + scaleX + " scaleY " + scaleY);
        // System.out.println("boardx - gapw " + (boardx - gapw) + " boardy - gaph " +
        // (boardy - gaph));
        // // g.drawImage(boardBackground, BufferedImageOp., w, h);
        // // return;
        // }

        painted = true;

    }

    private void drawPipCounts(Graphics2D g, int own) {
        int xleft = l1 - pipWidth;
        int xright = r2 + pipWidth;
        int top = t;
        int bottom = b;
        int ymiddle = (top + bottom) >> 1;

        int pipsOwn = match.getPlayer(match.own).field.pipCount();
        int pipsOpp = match.getPlayer(1 - match.own).field.pipCount();
        int diff = pipsOwn - pipsOpp;

        g.setColor(Color.WHITE);
        int px = clockwise ? xleft : xright;

        paintCenteredText(g, String.valueOf(diff), px, ymiddle);
        paintCenteredText(g, String.valueOf(pipsOwn), px, ymiddle + pipWidth * 3 / 2);
        paintCenteredText(g, String.valueOf(pipsOpp), px, ymiddle - pipWidth * 3 / 2);
    }

    private void drawRoll(Graphics2D g, int own) {
        int active = match.getActivePlayer();
        if (active != -1) {
            Roll roll = match.roll;
            if (!roll.isEmpty()) {
                boolean initial = match.isInitialRoll();
                int x1, x2, fg1, fg2;
                if (initial) {
                    x1 = (l1 + r1 - pipWidth) >> 1;
                    fg1 = ownWhite ? 0 : 1;
                    x2 = (l2 + r2 - pipWidth) >> 1;
                    fg2 = ownWhite ? 1 : 0;
                } else if (active != own) {
                    x1 = ((l1 + r1 - pipWidth) >> 1) - pipWidth;
                    fg1 = ownWhite ? 0 : 1;
                    x2 = x1 + pipWidth + pipWidth;
                    fg2 = ownWhite ? 0 : 1;
                } else {
                    x1 = ((l2 + r2 - pipWidth) >> 1) - pipWidth;
                    fg1 = ownWhite ? 1 : 0;
                    x2 = x1 + pipWidth + pipWidth;
                    fg2 = ownWhite ? 1 : 0;
                }
                int y = (t + b - pipWidth) >> 1;
                Color[] colors = { Color.WHITE, Color.BLACK };
                paintCube(g, x1, y, pipWidth, pipWidth, roll.die1(), dieAngle1, colors[fg1],
                        colors[1 - fg1]);
                paintCube(g, x2, y, pipWidth, pipWidth, roll.die2(), dieAngle2, colors[fg2],
                        colors[1 - fg2]);
            }
        }
    }

    private void drawScore(Graphics2D g, int own, int borderLeftWidth, int borderRightWidth) {
        int scoreOwn = match.getScore(own);
        int scoreOpp = match.getScore(1 - own);
        int centery = (t + b) >> 1;
        int d1 = pipWidth * 2;
        int d2 = d1 + pipWidth;
        g.setColor(Color.WHITE);
        // int x = boardx + (clockwise ? 34 : 2) * unit;
        int x = clockwise ? r2 + borderRightWidth + pipWidth : l1 - borderLeftWidth - pipWidth;
        paintCenteredText(g, String.valueOf(scoreOwn), x, centery + d1);
        paintCenteredText(g, match.getPlayerName(own), x, centery + d2);

        paintCenteredText(g, match.getPlayerName(1 - own), x, centery - d2);
        paintCenteredText(g, String.valueOf(scoreOpp), x, centery - d1);
    }

    private void drawChequersOff(Graphics2D g, Color[] chequerColors, int borderLeftWidth, int borderRightWidth) {
        int offOwn = match.getPlayer(match.own).field.getChequers(0);
        int offOpp = match.getPlayer(1 - match.own).field.getChequers(0);

        int unit2 = pipWidth;

        int top = t;
        int bottom = b;

        int xright = r2 + borderRightWidth;
        int xleft = l1 - borderLeftWidth - pipWidth;
        int h = pipWidth / 4;

        // own chequers off

        for (int i = 0; i < offOwn; ++i) {
            paintChequerOff(g, clockwise ? xleft : xright, bottom - h * (i + 1), unit2, h, chequerColors[1],
                    chequerColors[0]);
        }

        // opp chequers off

        for (int i = 0; i < offOpp; ++i) {
            paintChequerOff(g, clockwise ? xleft : xright, top + i * h, unit2, h, chequerColors[0], chequerColors[1]);
        }
    }

    private void drawChequersOnBar(Graphics2D g, Color[] chequerColors) {
        int barx = r1;
        int barOwn = match.getChequers(25);
        int barOpp = -match.getChequers(0);

        if (barOpp > 0) {
            Color bg = chequerColors[1];
            Color fg = chequerColors[0];
            paintChequerRow(g, barx, b - 5 * pipWidth, true, pipWidth >> 1, fg, bg, barOpp, 3);
        }

        if (barOwn > 0) {
            Color bg = chequerColors[0];
            Color fg = chequerColors[1];
            paintChequerRow(g, barx, t + 5 * pipWidth, false, pipWidth >> 1, fg, bg, barOwn, 3);
        }
    }

    private int scaleInt(int x, double scale) {
        return roundInt(x * scale);
    }

    private int roundInt(double x) {
        return (int) Math.round(x);
    }

    private void drawBoardBackground(Graphics2D g, double scale, int left, int top) {
        var oldTransform = g.getTransform();
        try {
            g.translate(left, top);
            g.scale(scale, scale);
            g.drawImage(boardBackground, 0, 0, null);
        } finally {
            g.setTransform(oldTransform);
        }
    }

    private double calcBgScale(int w, int h) {
        if (NO_SCALE) {
            return 1;
        }
        double scale;
        double scaleX = (double) w / boardBackground.getWidth();
        double scaleY = (double) h / boardBackground.getHeight();
        scale = Math.min(scaleX, scaleY);
        // scale = 1; // TODO just to measure
        return scale;
    }

    private void drawBoard(Graphics2D g, int pipWidth, int barWidth, int pipHeight, int vertPipDist) {

        // bar not to be drawn because part of the background image
        // g.setPaint(Color.ORANGE.darker());
        // g.fillRect(x + (4 + 12) * unit, top, unit2 * 2, unit * 24);
        // g.setColor(Color.BLACK);
        // g.drawRect(x + (4 + 12) * unit, top, unit2 * 2, unit * 24);

        g.setColor(BOARD_COLOR);
        g.fillRect(l1, t, r1 - l1, b - t);
        g.fillRect(l2, t, r2 - l2 + 2, b - t);

        for (int i = 0; i < 6; ++i) {
            int colorIdxDown = i & 1;
            int colorIdxUp = 1 - colorIdxDown;
            Color colorDown = PIP_COLORS[colorIdxDown];
            Color colorUp = PIP_COLORS[colorIdxUp];
            int field = clockwise ? 24 - i : 12 - i;
            drawPip(g, l1 + i * pipWidth, t, true, pipWidth, pipHeight, colorDown, ongoingMove.highlightedPip == field);
            // paintPip(g, x + unit2 * (2 + i), top, true, unit, colorDown,
            // ongoingMove != null && ongoingMove.highlightedPip == field);
            field = clockwise ? 1 + i : 13 + i;
            drawPip(g, l1 + i * pipWidth, b, false, pipWidth, pipHeight, colorUp,
                    ongoingMove != null && ongoingMove.highlightedPip == field);
            // paintPip(g, x + unit2 * (2 + i), bottom, false, unit, colorUp,
            // ongoingMove != null && ongoingMove.highlightedPip == field);
            field = clockwise ? 18 - i : 6 - i;
            drawPip(g, l2 + i * pipWidth, t, true, pipWidth, pipHeight, colorDown,
                    ongoingMove != null && ongoingMove.highlightedPip == field);
            // paintPip(g, x + unit2 * (10 + i), top, true, unit, colorDown,
            // ongoingMove != null && ongoingMove.highlightedPip == field);
            field = clockwise ? 7 + i : 19 + i;
            drawPip(g, l2 + i * pipWidth, b, false, pipWidth, pipHeight, colorUp,
                    ongoingMove != null && ongoingMove.highlightedPip == field);
            // paintPip(g, x + unit2 * (10 + i), bottom, false, unit, colorUp,
            // ongoingMove != null && ongoingMove.highlightedPip == field);
        }
    }

    private void drawPip(Graphics2D g, int x, int y, boolean down, int pipWidth, int pipHeight, Color color,
            boolean highlighted) {
        Polygon p = polygon;
        g.setPaint(color);
        p.reset();
        p.addPoint(x, y);
        p.addPoint(x + (pipWidth >> 1), y + pipHeight * (down ? 1 : -1));
        p.addPoint(x + pipWidth, y);
        g.fillPolygon(p);

        if (highlighted) {
            g.setColor(Color.RED);
            g.drawPolygon(p);
        }
        // g.setColor(highlighted ? Color.RED : Color.BLACK);
        // g.drawPolygon(p);

    }

    private void drawChequersOnPips(Graphics2D g, int own, Color[] colors) {
        // int unit2 = unit + unit;

        int top = t;
        int bottom = b;

        int field, n;
        Color fg, bg;

        for (int i = 0; i < 6; ++i) {

            field = clockwise ? 24 - i : 13 + i;
            n = match.getChequers(field);
            bg = n > 0 ? colors[0] : colors[1];
            fg = n > 0 ? colors[1] : colors[0];
            if (n < 0)
                n = -n;
            if (n != 0)
                drawChequersOnPip(g, l1 + pipWidth * i, t, true, fg, bg, n);
            // System.out.println("field " + field + " n " + n);

            field = clockwise ? 1 + i : 12 - i;
            n = match.getChequers(field);
            bg = n > 0 ? colors[0] : colors[1];
            fg = n > 0 ? colors[1] : colors[0];
            if (n < 0)
                n = -n;
            if (n != 0)
                drawChequersOnPip(g, l1 + pipWidth * i, bottom, false, fg, bg, n);
            // System.out.println("field " + field + " n " + n);

            field = clockwise ? 18 - i : 19 + i;
            n = match.getChequers(field);
            bg = n > 0 ? colors[0] : colors[1];
            fg = n > 0 ? colors[1] : colors[0];
            if (n < 0)
                n = -n;
            if (n != 0)
                drawChequersOnPip(g, l2 + pipWidth * i, top, true, fg, bg, n);
            // System.out.println("field " + field + " n " + n);

            field = clockwise ? 7 + i : 6 - i;
            n = match.getChequers(field);
            bg = n > 0 ? colors[0] : colors[1];
            fg = n > 0 ? colors[1] : colors[0];
            if (n < 0)
                n = -n;
            if (n != 0)
                drawChequersOnPip(g, l2 + pipWidth * i, bottom, false, fg, bg, n);
            // System.out.println("field " + field + " n " + n);
        }
    }

    private void drawChequersOnPip(Graphics2D g, int x, int y, boolean down, Color fg, Color bg, int n) {
        int unit = pipWidth / 2;
        paintChequerRow(g, x, y, down, unit, fg, bg, n, 5);
    }

    ///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    protected void paintComponentOld(Graphics g) {
        // System.out.println("paintComponent");

        Graphics2D g2 = (Graphics2D) g;
        int w = getWidth();
        int h = getHeight();
        g.setColor(getBackground());
        g.fillRect(0, 0, w, h);
        // g.setColor(Color.YELLOW);
        // g.drawRect(0, 0, w - 1, h - 1);

        if (match == null) {
            g.setColor(Color.BLACK);
            g.setFont(g.getFont().deriveFont(Font.BOLD, 25));
            this.paintCenteredText(g2, "Aktuell kein Match", w / 2, h / 2);
            painted = false;
            return;
        }

        final int gapw = 10;
        final int gaph = 10;
        int unit = (w - gapw - gapw) / 18 / 2;
        int unitH = (h - gaph - gaph) / 26;
        if (unitH < unit)
            unit = unitH;
        int unit2 = unit + unit;
        int boardx = (w - unit * 36) / 2;
        int boardy = (h - 26 * unit) / 2;
        int bottom = boardy + 26 * unit;
        int cubeX = clockwise ? boardx + (36 - 3) * unit : boardx + unit;
        int cubeW = unit2;
        int cubeH = unit2;
        int cubeOwnY = bottom - unit - cubeH;
        int cubeOppY = boardy + unit;
        int cubeInitY = boardy + (26 * unit - cubeH) / 2;
        int cubeOfferedOppX = boardx + 14 * unit;
        int cubeOfferedOwnX = boardx + 18 * unit;
        int own = match.getIndexOfOwn();
        int cubeOwner = match.getCubeOwner();
        pipWidth = unit2;
        pipHeight = unit * 11;
        l1 = boardx + unit2 + unit2;
        r1 = l1 + pipWidth * 6;
        l2 = boardx + unit2 * 10;
        r2 = l2 + pipWidth * 6;
        t = boardy + unit;
        b = t + 24 * unit;

        // System.out.println("cubeOwner " + cubeOwner);
        paintBoard(g2, boardx, boardy, unit);
        paintBorder(g2, boardx, boardy, unit);

        Color ownColor = ownWhite ? Color.WHITE : Color.BLACK;
        Color oppColor = ownWhite ? Color.BLACK : Color.WHITE;

        chequerColors[0] = ownColor;
        chequerColors[1] = oppColor;

        paintChequersOnPips(g2, boardx, boardy, unit, own, chequerColors);
        paintChequersOnBar(g2, boardx, boardy, unit, chequerColors);
        paintChequersOff(g2, boardx, boardy, unit, own, chequerColors);

        if (match.cube.used) {
            String cubeString = match.isCrawfordRound() ? "CR" : String.valueOf(match.getCubeVal());
            paintCube(g2,
                    match.isCubeOffered() ? (cubeOwner == own ? cubeOfferedOwnX : cubeOfferedOppX) : cubeX,
                    match.isCubeOffered() ? cubeInitY
                            : (cubeOwner == -1 ? cubeInitY : cubeOwner == own ? cubeOwnY : cubeOppY),
                    cubeW,
                    cubeH,
                    cubeString,
                    cubeOwner == -1 ? (clockwise ? Math.PI / 2 : -Math.PI / 2) : cubeOwner == own ? 0 : Math.PI,
                    Color.WHITE,
                    Color.RED);

        }

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

        paintScore(g2, boardx, boardy, unit, own);
        paintRoll(g2, boardx, boardy, unit, own);
        paintPipCounts(g2, boardx, boardy, unit, own);

        painted = true;
    }

    private void paintRoll(Graphics2D g, int boardx, int boardy, int unit, int own) {
        int active = match.getActivePlayer();
        if (active != -1) {
            Roll roll = match.roll;
            if (!roll.isEmpty()) {
                boolean initial = match.isInitialRoll();
                int x1, x2, fg1, fg2;
                if (initial) {
                    x1 = boardx + unit * 9;
                    fg1 = ownWhite ? 0 : 1;
                    x2 = boardx + unit * 27;
                    fg2 = ownWhite ? 1 : 0;
                } else if (active != own) {
                    x1 = boardx + unit * 7;
                    fg1 = ownWhite ? 0 : 1;
                    x2 = boardx + unit * 11;
                    fg2 = ownWhite ? 0 : 1;
                } else {
                    x1 = boardx + unit * 25;
                    fg1 = ownWhite ? 1 : 0;
                    x2 = boardx + unit * 29;
                    fg2 = ownWhite ? 1 : 0;
                }
                int y = boardy + 13 * unit;
                Color[] colors = { Color.WHITE, Color.BLACK };
                paintCube(g, x1 - unit, y - unit, unit * 2, unit * 2, roll.die1(), dieAngle1, colors[fg1],
                        colors[1 - fg1]);
                paintCube(g, x2 - unit, y - unit, unit * 2, unit * 2, roll.die2(), dieAngle2, colors[fg2],
                        colors[1 - fg2]);
            }
        }
    }

    private void paintScore(Graphics2D g, int boardx, int boardy, int unit, int own) {
        int scoreOwn = match.getScore(own);
        int scoreOpp = match.getScore(1 - own);
        int centery = boardy + unit * 13;
        int d1 = unit * 4;
        int d2 = d1 + unit + unit;
        g.setColor(Color.BLACK);
        int x = boardx + (clockwise ? 34 : 2) * unit;
        paintCenteredText(g, String.valueOf(scoreOwn), x, centery + d1);
        paintCenteredText(g, match.getPlayerName(own), x, centery + d2);

        paintCenteredText(g, match.getPlayerName(1 - own), x, centery - d2);
        paintCenteredText(g, String.valueOf(scoreOpp), x, centery - d1);
    }

    private void paintChequersOnBar(Graphics2D g, int boardx, int boardy, int unit, Color[] chequerColors) {
        int barx = boardx + 17 * unit;
        int barOwn = match.getChequers(25);
        int barOpp = -match.getChequers(0);

        if (barOpp > 0) {
            Color bg = chequerColors[1];
            Color fg = chequerColors[0];
            paintChequerRow(g, barx, boardy + 16 * unit, true, unit, fg, bg, barOpp, 3);
        }

        if (barOwn > 0) {
            Color bg = chequerColors[0];
            Color fg = chequerColors[1];
            paintChequerRow(g, barx, boardy + 10 * unit, false, unit, fg, bg, barOwn, 3);
        }
    }

    private void paintChequersOff(Graphics2D g, int boardx, int boardy, int unit, int own, Color[] chequerColors) {
        int offOwn = match.getPlayer(match.own).field.getChequers(0);
        int offOpp = match.getPlayer(1 - match.own).field.getChequers(0);

        int unit2 = unit + unit;

        int top = boardy + unit;
        int bottom = boardy + unit * 25;

        int xright = boardx + 16 * unit2 + unit;
        int xleft = boardx + unit;
        int h = unit / 2;

        // own chequers off

        for (int i = 0; i < offOwn; ++i) {
            paintChequerOff(g, clockwise ? xleft : xright, bottom - h * (i + 1), unit2, h, chequerColors[1],
                    chequerColors[0]);
        }

        // opp chequers off

        for (int i = 0; i < offOpp; ++i) {
            paintChequerOff(g, clockwise ? xleft : xright, top + i * h, unit2, h, chequerColors[0], chequerColors[1]);
        }
    }

    private void paintChequerOff(Graphics2D g, int cx, int cy, int w, int h, Color fg, Color bg) {
        g.setPaint(bg);
        g.fillRect(cx, cy, w, h);
        g.setColor(fg);
        g.drawRect(cx, cy, w, h);
    }

    private void paintPipCounts(Graphics2D g, int boardx, int boardy, int unit, int own) {
        int unit2 = unit + unit;
        int xleft = boardx + unit2;
        int xright = boardx + 17 * unit2;
        int top = boardy + unit;
        int bottom = boardy + unit * 25;
        int ymiddle = (top + bottom) >> 1;

        int pipsOwn = match.getPlayer(match.own).field.pipCount();
        int pipsOpp = match.getPlayer(1 - match.own).field.pipCount();
        int diff = pipsOwn - pipsOpp;

        g.setColor(Color.BLACK);
        int px = clockwise ? xleft : xright;

        paintCenteredText(g, String.valueOf(diff), px, ymiddle);
        paintCenteredText(g, String.valueOf(pipsOwn), px, ymiddle + unit * 3);
        paintCenteredText(g, String.valueOf(pipsOpp), px, ymiddle - unit * 3);
    }

    private void paintChequersOnPips(Graphics2D g, int x, int y, int unit, int own, Color[] colors) {
        int unit2 = unit + unit;

        int top = y + unit;
        int bottom = y + unit * 25;

        int field, n;
        Color fg, bg;

        for (int i = 0; i < 6; ++i) {

            field = clockwise ? 24 - i : 13 + i;
            n = match.getChequers(field);
            bg = n > 0 ? colors[0] : colors[1];
            fg = n > 0 ? colors[1] : colors[0];
            if (n < 0)
                n = -n;
            if (n != 0)
                paintChequersOnPip(g, x + unit2 * (2 + i), top, true, unit, fg, bg, n);
            // System.out.println("field " + field + " n " + n);

            field = clockwise ? 1 + i : 12 - i;
            n = match.getChequers(field);
            bg = n > 0 ? colors[0] : colors[1];
            fg = n > 0 ? colors[1] : colors[0];
            if (n < 0)
                n = -n;
            if (n != 0)
                paintChequersOnPip(g, x + unit2 * (2 + i), bottom, false, unit, fg, bg, n);
            // System.out.println("field " + field + " n " + n);

            field = clockwise ? 18 - i : 19 + i;
            n = match.getChequers(field);
            bg = n > 0 ? colors[0] : colors[1];
            fg = n > 0 ? colors[1] : colors[0];
            if (n < 0)
                n = -n;
            if (n != 0)
                paintChequersOnPip(g, x + (10 + i) * unit2, top, true, unit, fg, bg, n);
            // System.out.println("field " + field + " n " + n);

            field = clockwise ? 7 + i : 6 - i;
            n = match.getChequers(field);
            bg = n > 0 ? colors[0] : colors[1];
            fg = n > 0 ? colors[1] : colors[0];
            if (n < 0)
                n = -n;
            if (n != 0)
                paintChequersOnPip(g, x + unit2 * (10 + i), bottom, false, unit, fg, bg, n);
            // System.out.println("field " + field + " n " + n);
        }
    }

    private void paintChequer(Graphics2D g, int x, int y, int unit, Color color) {
        g.setPaint(color);
        g.fillOval(x, y, unit * 2, unit * 2);
        // g.setColor(Color.BLACK);
        g.setColor(PIP_COLORS[0]);
        g.drawOval(x, y, unit * 2, unit * 2);
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
            int field = clockwise ? 24 - i : 12 - i;
            paintPip(g, x + unit2 * (2 + i), top, true, unit, colorDown,
                    ongoingMove != null && ongoingMove.highlightedPip == field);
            field = clockwise ? 1 + i : 13 + i;
            paintPip(g, x + unit2 * (2 + i), bottom, false, unit, colorUp,
                    ongoingMove != null && ongoingMove.highlightedPip == field);
            field = clockwise ? 18 - i : 6 - i;
            paintPip(g, x + unit2 * (10 + i), top, true, unit, colorDown,
                    ongoingMove != null && ongoingMove.highlightedPip == field);
            field = clockwise ? 7 + i : 19 + i;
            paintPip(g, x + unit2 * (10 + i), bottom, false, unit, colorUp,
                    ongoingMove != null && ongoingMove.highlightedPip == field);
        }
    }

    /**
     * Pip-Nummerierung ist wie folgt. Weiß spielt immer von 1 nach 24. Schwarz
     * spielt immer von 24 nach 1.
     */
    public int containingField(int x, int y) {

        if (!painted) {
            // System.out.println("not painted!");
            return -1;
        }

        if (x >= l1 && x < r1) {
            // left board side
            int i = (x - l1) / pipWidth;

            if (y >= t && y < t + pipHeight) {
                // top left quarter
                return clockwise ? (ownWhite ? 1 + i : 24 - i) : (ownWhite ? 12 - i : 13 + i);
            }

            if (y <= b && y > b - pipHeight) {
                // bottom left quarter
                return clockwise ? (ownWhite ? 24 - i : 1 + i) : (ownWhite ? 13 + i : 12 - i);
            }
        } else if (x >= l2 && x < r2) {
            int i = (x - l2) / pipWidth;

            if (y >= t && y < t + pipHeight) {
                // top right quarter
                return clockwise ? (ownWhite ? 7 + i : 18 - i) : (ownWhite ? 6 - i : 19 + i);
            }

            if (y <= b && y > b - pipHeight) {
                // bottom right quarter
                return clockwise ? (ownWhite ? 18 - i : 7 + i) : (ownWhite ? 19 + i : 6 - i);
            }
        }

        return -1;
    }

    private void paintChequerRow(Graphics2D g, int x, int y, boolean down, int unit, Color fg, Color bg, int n,
            int maxPaint) {
        g.setPaint(bg);
        int npaint = Math.min(maxPaint, n);
        int unit2 = unit + unit;

        if (!down) {
            y -= unit2;
        }

        for (int i = 0; i < npaint; ++i) {
            paintChequer(g, x, y + (i * unit2) * (down ? 1 : -1), unit, bg);
        }

        if (n > npaint) {
            g.setColor(Color.white);
            paintCenteredText(g, String.valueOf(n), x + unit, y + unit + (npaint - 1) * unit2 * (down ? 1 : -1));
        }

    }

    private void paintChequersOnPip(Graphics2D g, int x, int y, boolean down, int unit, Color fg, Color bg, int n) {
        paintChequerRow(g, x, y, down, unit, fg, bg, n, 5);
    }

    // private void paintChequersOnBar(Graphics2D g, int x, int y, boolean down, int
    // unit, Color fg, Color bg, int n) {
    // paintChequerRow(g, x, y, down, unit, fg, bg, n, 3);
    // }

    private void paintCenteredText(Graphics2D g, String s, int centerX, int centerY) {
        FontMetrics fm = g.getFontMetrics();
        if (s == null) {
            System.out.println("s==null in paintCenteredText");
            return;
        }
        LineMetrics lm = fm.getLineMetrics(s, g);
        Color oldColor = g.getColor();
        g.setColor(TEXT_BG_COLOR);
        int sw = fm.stringWidth(s);
        float sh = lm.getHeight();
        g.fillRect(centerX - sw / 2 - 3, roundInt(centerY - sh / 2) - 3, sw + 6, roundInt(sh) + 6);
        g.setColor(oldColor);
        g.drawString(s, centerX - sw / 2, centerY - sh / 2 + lm.getAscent());
    }

    private void paintPip(Graphics2D g, int x, int y, boolean down, int unit, Color color, boolean highlighted) {
        Polygon p = polygon;
        g.setPaint(color);
        p.reset();
        p.addPoint(x, y);
        p.addPoint(x + unit, y + (11 * unit) * (down ? 1 : -1));
        p.addPoint(x + 2 * unit, y);
        g.fillPolygon(p);

        g.setColor(highlighted ? Color.RED : Color.BLACK);
        g.drawPolygon(p);
    }

    private void paintCube(Graphics2D g, int x, int y, int w, int h, int val, double rotation, Color fg, Color bg) {
        paintCube(g, x, y, w, h, String.valueOf(val), rotation, fg, bg);
    }

    private void paintCube(Graphics2D g, int x, int y, int w, int h, String s, double rotation, Color fg, Color bg) {
        g.translate(x + w / 2, y + h / 2);
        g.rotate(rotation);
        g.setPaint(bg);
        g.fillRoundRect(-w / 2, -h / 2, w, h, 10, 10);
        // g.setPaint(Color.WHITE);
        // g.fillRect(x + 12, y + 12, w - 24, h - 24);
        Font oldFont = g.getFont();
        g.setFont(oldFont.deriveFont(Font.BOLD, 16));
        FontMetrics fm = g.getFontMetrics();
        LineMetrics lm = fm.getLineMetrics(s, g);
        g.setColor(fg);
        g.drawRoundRect(-w / 2, -h / 2, w, h, 10, 10);
        g.drawString(s, (-fm.stringWidth(s)) / 2, (-lm.getHeight()) / 2 + lm.getAscent());
        g.rotate(-rotation);
        g.translate(-(x + w / 2), -(y + h / 2));
        g.setFont(oldFont);
    }

    public void setMatch(Match match, OngoingMove ongoingMove) {
        this.match = match;
        this.ongoingMove = ongoingMove;
        invalidate();
        repaint();
    }

    public void setClockwise(boolean clockwise) {
        this.clockwise = clockwise;
        invalidate();
    }

    public void setOwnWhite(boolean ownWhite) {
        this.ownWhite = ownWhite;
    }

    public boolean getOwnWhite() {
        return ownWhite;
    }

    public boolean getClockwise() {
        return clockwise;
    }
}
