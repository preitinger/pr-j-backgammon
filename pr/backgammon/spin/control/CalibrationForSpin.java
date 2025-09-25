package pr.backgammon.spin.control;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Paint;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Toolkit;
import java.awt.image.BufferedImage;
import java.awt.image.Raster;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Comparator;

import pr.backgammon.spin.model.Player;
import pr.control.MyRobot;
import pr.control.Tools;

/*
 * Alternative Kalibrierung fuer Spin:
 * 
 * Alle Steine suchen wie in searchExactBlackChequerOnScreen() bzw.
 * searchExactWhiteChequerOnScreen.
 * Dann kann man eine gültige Startposition für Backgammon voraussetzen.
 * Daraus müsste man eindeutig die Position des Bretts, die eigene Farbe und die
 * Spielrichtung ableiten können.
 * Horizontale Abstände zwischen Steinen auf benachbarten Feldern können
 * variieren zwischen 45 und 50!
 * Vertikale Abstände zwischen Steinen auf einem Feld in der gleichen Ebene
 * ebenfalls.
 * Die Breite der Bar kann variieren zwischen 50 und 60.
 * Steine auf der Bar werden ungefähr vertikal zentriert einzeln ungestapelt
 * abgelegt.
 * Wir nehmen an, dass nie mehr Steine gleichzeitig auf der Bar sein werden, als
 * dort Platz
 * ist ohne dass Steine gestapelt werden müssen.
 * Ich habe es selbst bei exzessivsten Trainingsspielen mit dem Spin-Bot nicht
 * hinbekommen mehr als 5 Steine auf die Bar zu bekommen.
 * 
 * Zunächst müsste man alle Steinpositionen je Farbe sammeln.
 * 
 * Die Spielfeldecken können bestimmt werden durch die Wahl der jeweils
 * kleinsten x- und y-Koordinaten unter allen Steinen aller Farben für links
 * oben
 * bzw. der jeweils größten x- und y-Koordinaten unter allen Steinen aller
 * Farben für rechts unten.
 * 
 * Ein
 * 
 * Zackenspitzen in oberer Feldhaelfte: 624 672 721 769 816 865 967 1015 1065
 * 1113 1162 1210
 * 
 */
public class CalibrationForSpin {

    static void ln(String line) {
        System.out.println(line);
    }

    static enum ChequerColor {
        BLACK, WHITE
    }

    public static class Chequer {
        final ChequerColor color;
        final Point pos;

        Chequer(ChequerColor color1, Point pos1) {
            color = color1;
            pos = pos1;
        }

        boolean ownColor(boolean ownWhite) {
            return (color == ChequerColor.WHITE) == ownWhite;
        }

        void dump(String label) {
            if (label != null) {
                System.out.print(label + ":  ");
            }
            System.out.println("color " + color + "   pos " + pos);
        }
    }

    public final BufferedImage[] blackChequer, whiteChequer;
    // coordinates of the extreme chequer centers in the corners.
    public final int left, top, right, bottom;
    /**
     * true iff own player has the white chequers
     */
    public final boolean ownWhite;
    /*
     * true bedeutet man spielt von links oben ueber rechts oben, rechts unten nach
     * links unten
     * false bedeutet man spielt von rechts oben ueber links oben, links unten nach
     * rechts unten
     */
    public final boolean ownClockwise;
    public final int dxLeft, dxRight, dxBar;
    public final int dy;

    final Comparator<Chequer> sortX = new Comparator<Chequer>() {
        @Override
        public int compare(Chequer a, Chequer b) {
            if (a.pos.x < b.pos.x)
                return -1;
            if (a.pos.x > b.pos.x)
                return 1;
            return a.pos.y - b.pos.y;
        }
    };

    final static Comparator<Chequer> sortY = new Comparator<Chequer>() {
        @Override
        public int compare(Chequer a, Chequer b) {
            if (a.pos.y < b.pos.y)
                return -1;
            if (a.pos.y > b.pos.y)
                return 1;
            return a.pos.x - b.pos.x;
        }
    };

    // PipCounts countPips(ArrayList<Chequer> allChequers) {
    // PipCounts p = new PipCounts();
    // p.own = p.other = 0;

    // for (int i = 1; i <= 24; ++i) {
    // Player player = playerOnField(allChequers, i);
    // switch (player) {
    // case OWN:
    // p.own += numChequersOnField(allChequers, i) * i;
    // break;
    // case OTHER:
    // p.other += numChequersOnField(allChequers, i) * (25 - i);
    // break;
    // default:
    // break;
    // }
    // }

    // ArrayList<Chequer> chequersOnBar = filterBar(allChequers);
    // for (Chequer c : chequersOnBar) {
    // if (c.ownColor(ownWhite)) {
    // p.own += 25;
    // } else {
    // p.other += 25;
    // }
    // }

    // return p;
    // }

    void dumpUniqueX(ArrayList<Chequer> l) {
        Point last = null;
        for (Chequer c : l) {
            Point p = c.pos;
            if (last == null || p.x != last.x) {
                System.out.println(p.x);
                last = p;
            }
        }
    }

    void dumpUniqueY(ArrayList<Chequer> l) {
        Point last = null;
        for (Chequer c : l) {
            Point p = c.pos;
            if (last == null || p.y != last.y) {
                System.out.println(p.y);
                last = p;
            }
        }
    }

    int midY() {
        return (top + bottom) >> 1;
    }

    boolean inTopHalf(int centerY) {
        return centerY < midY();
    }

    boolean inBottomHalf(int centerY) {
        return centerY > midY();
    }

    ArrayList<Chequer> filterColumn(
            ArrayList<Chequer> allChequers,
            int centerX,
            boolean topHalf) {

        ArrayList<Chequer> l = new ArrayList<>();
        // System.out.println("centerX " + centerX + " topHalf " + topHalf);

        for (Chequer c : allChequers) {
            Point p = c.pos;
            // c.dump("Regarded chequer");
            // boolean inTop = inTopHalf(p.y);
            // boolean inBottom = inBottomHalf(p.y);
            // System.out.println("inTop " + inTop + " inBottom " + inBottom);
            if (p.x > centerX - 10 && p.x < centerX + 10
                    && ((topHalf && inTopHalf(p.y)) || (!topHalf && inBottomHalf(p.y)))) {
                l.add(c);
            }
        }
        return l;
    }

    ArrayList<Chequer> filterBar(
            ArrayList<Chequer> allChequers) {

        ArrayList<Chequer> l = new ArrayList<>();
        int centerX = centerXOfBar();
        // System.out.println("centerX " + centerX + " topHalf " + topHalf);

        for (Chequer c : allChequers) {
            Point p = c.pos;
            // c.dump("Regarded chequer");
            // boolean inTop = inTopHalf(p.y);
            // boolean inBottom = inBottomHalf(p.y);
            // System.out.println("inTop " + inTop + " inBottom " + inBottom);
            if (p.x > centerX - 10 && p.x < centerX + 10) {
                l.add(c);
            }
        }
        return l;

    }

    ArrayList<Chequer> visibleChequersOnField(ArrayList<Chequer> allChequers, int field) {
        int centerX = centerXOfField(field);
        boolean topHalf = field >= 13;
        return filterColumn(allChequers, centerX, topHalf);
    }

    static boolean yEqual(int yIs, int yShould) {
        return yIs > yShould - 5 && yIs < yShould + 5;
    }

    public int numChequersOnField(ArrayList<Chequer> allChequers, int field) {
        if (field < 1 || field > 24)
            throw new IllegalArgumentException("field " + field);
        ArrayList<Chequer> visible = visibleChequersOnField(allChequers, field);
        // System.out.println("numChequersOnField for " + field);
        return numChequersOnField(visible, field >= 13);
    }

    int numChequersOnField(ArrayList<Chequer> visibleChequers, boolean topHalf) {
        if (visibleChequers.isEmpty())
            return 0;
        ArrayList<Chequer> sorted = new ArrayList<Chequer>(visibleChequers);
        sorted.sort(sortY);
        int diffY = topHalf ? dy : -dy;
        // dump("diffY", diffY);
        int layers = countLayers(sorted, diffY);
        // dump("layers", layers);
        int chequersInLowerLayers = 0;
        for (int i = 0; i < layers - 1; ++i) {
            chequersInLowerLayers += 5 - i;
        }
        // dump("chequersInLowerLayers", chequersInLowerLayers);

        int chequersInTopLayer = countVisibleChequersInLayer(sorted, diffY);
        // dump("chequersInTopLayer", chequersInTopLayer);
        return chequersInLowerLayers + chequersInTopLayer;
    }

    public Player playerOnField(ArrayList<Chequer> allChequers, int field) {
        ArrayList<Chequer> f = visibleChequersOnField(allChequers, field);
        if (f.isEmpty())
            return Player.NONE;
        return (f.get(0).color == ChequerColor.WHITE) == ownWhite ? Player.OWN : Player.OTHER;
    }

    void dumpPlayersOnAllFields() {
        ArrayList<Chequer> allChequers = searchAllVisibleChequers();

        for (int i = 1; i <= 24; ++i) {
            dump("player on " + i, playerOnField(allChequers, i));
        }
    }

    /**
     * @param diffY -dy falls aufwaerts, sonst +dy
     */
    int countLayers(ArrayList<Chequer> l, int diffY) {
        // 5 + 4 + 3 + 2 + 1
        int n = l.size();
        if (n < 1)
            throw new IllegalArgumentException("l.size() " + n);

        int firstInTopLayer = diffY < 0 ? n - 1 : 0;
        int isY = l.get(firstInTopLayer).pos.y;
        int yFirst = diffY < 0 ? bottom : top;

        for (int layer = 0; layer < 5; ++layer) {
            int shallY = yFirst + (diffY * layer) / 2;
            if (yEqual(isY, shallY)) {
                return layer + 1;
            }
        }

        throw new IllegalStateException("Counting of layers failed");
    }

    /**
     * falls aufwaerts, diffY = -dy, sonst +dy
     */
    int countVisibleChequersInLayer(ArrayList<Chequer> l, int diffY) {
        int n = l.size();
        if (n <= 1)
            return n;

        int idx = diffY < 0 ? n - 1 : 0;
        int dIdx = diffY < 0 ? -1 : 1;
        int endIdx = diffY < 0 ? -1 : n;
        int y = l.get(idx).pos.y;

        int res = 1;

        for (int i = idx + dIdx; i != endIdx; i += dIdx) {
            // dump("i", i);
            int nextY = l.get(i).pos.y;
            if (yEqual(nextY, y + diffY)) {
                ++res;
                y = nextY;
            } else {
                break;
            }
        }

        return res;
    }

    void dumpVisibleChequersOnAllFields(ArrayList<Chequer> allChequers) {
        System.out.println("visible chequers on all fields:");
        for (int field = 24; field >= 1; --field) {
            ArrayList<Chequer> l = visibleChequersOnField(allChequers, field);
            dump(Integer.toString(field), l);
        }
    }

    void dump(String label, boolean v) {
        if (label != null)
            System.out.print(label + ": ");
        System.out.println(v);
    }

    void dump(String label, int v) {
        if (label != null)
            System.out.print(label + ": ");
        System.out.println(v);
    }

    void dump(String label, Player p) {
        System.out.print(label + ": ");
        switch (p) {
            case NONE:
                System.out.println("NONE");
                break;
            case OWN:
                System.out.println("OWN");
                break;
            case OTHER:
                System.out.println("OTHER");
                break;
        }
    }

    void dump() {
        dump("left", left);
        dump("top", top);
        dump("right", right);
        dump("bottom", bottom);
        dump("ownWhite", ownWhite);
        dump("ownClockwise", ownClockwise);
        dump("dxLeft", dxLeft);
        dump("dxRight", dxRight);
        dump("dxBar", dxBar);
        dump("dy", dy);

        ln("new CalibrationForSpin(\n");
        ln(left + ",");
        ln(top + ",");
        ln(right + ",");
        ln(bottom + ",");
        ln(ownWhite + ",");
        ln(ownClockwise + ",");
        ln(dxLeft + ",");
        ln(dxRight + ",");
        ln(dxBar + ",");
        ln(dy + ")");

    }

    void dump(String label, ArrayList<Chequer> l) {
        System.out.println(label + ":");
        for (Chequer c : l) {
            c.dump(null);
        }
    }

    CalibrationForSpin(int left, int top, int right, int bottom,
            boolean ownWhite, boolean ownClockwise, int dxLeft, int dxRight,
            int dxBar,
            int dy) throws IOException {

        blackChequer = new BufferedImage[2];
        blackChequer[0] = Tools.loadImg(new File("./blackChequer.png"));
        // blackChequer[1] = loadImg(new File("./blackChequerStar.png"));
        blackChequer[1] = Tools.loadImg(new File("./spin-black-star-cut-circle2.png"));
        whiteChequer = new BufferedImage[2];
        whiteChequer[0] = Tools.loadImg(new File("./whiteChequer.png"));
        // whiteChequer[1] = loadImg(new File("./whiteChequerStar.png"));
        whiteChequer[1] = Tools.loadImg(new File("./spin-white-star-cut-circle2.png")); // TODO geaendert von
        // whiteChequerStar.png

        this.left = left;
        this.top = top;
        this.right = right;
        this.bottom = bottom;
        this.ownWhite = ownWhite;
        this.ownClockwise = ownClockwise;
        this.dxLeft = dxLeft;
        this.dxRight = dxRight;
        this.dxBar = dxBar;
        this.dy = dy;
    }

    public CalibrationForSpin(BufferedImage screen) throws IOException {
        if (screen == null) {
            screen = createScreenCapture();
        }

        blackChequer = new BufferedImage[2];
        // blackChequer[0] = Tools.loadImg(new File("res/blackChequer.png"));
        blackChequer[0] = Tools.loadImg("pr/res/blackChequer.png");
        blackChequer[1] = Tools.loadImg("pr/res/spin-black-star-cut-circle2.png");
        whiteChequer = new BufferedImage[2];
        whiteChequer[0] = Tools.loadImg("pr/res/whiteChequer.png");
        // whiteChequer[1] = loadImg(new File("./whiteChequerStar.png"));
        whiteChequer[1] = Tools.loadImg("pr/res/spin-white-star-cut-circle2.png"); // TODO geaendert von
        // whiteChequerStar.png
        ArrayList<Point> blackPositions = searchAllChequersOfColor(screen, blackChequer, null);
        ArrayList<Point> whitePositions = searchAllChequersOfColor(screen, whiteChequer, null);
        ArrayList<Chequer> blackChequers = new ArrayList<>();
        ArrayList<Chequer> whiteChequers = new ArrayList<>();
        for (Point p : blackPositions) {
            blackChequers.add(new Chequer(ChequerColor.BLACK, p));
        }
        for (Point p : whitePositions) {
            whiteChequers.add(new Chequer(ChequerColor.WHITE, p));
        }
        ArrayList<Chequer> allChequers = new ArrayList<Chequer>(blackChequers);
        allChequers.addAll(whiteChequers);
        allChequers.sort(sortX);
        ArrayList<Point> sortedCenterX = new ArrayList<>();
        for (Chequer c : allChequers) {
            sortedCenterX.add(c.pos);
        }
        System.out.println("All occurring x coordinates");
        dumpUniqueX(allChequers);
        int minX = allChequers.get(0).pos.x;
        int maxX = allChequers.get(allChequers.size() - 1).pos.x;
        allChequers.sort(sortY);
        System.out.println("All occurring y coordinates");
        dumpUniqueY(allChequers);
        int minY = allChequers.get(0).pos.y;
        int maxY = allChequers.get(allChequers.size() - 1).pos.y;
        System.out.println("minX " + minX);
        System.out.println("maxX " + maxX);
        System.out.println("minY " + minY);
        System.out.println("maxY " + maxY);
        left = minX;
        top = minY;
        right = maxX;
        bottom = maxY;

        // Herausfinden, ob die eigenen 2 Steine in der oberen Haelfte links oder rechts
        // sind, und welche Farbe sie haben. Das Feld mit den eigenen 2 Steinen wird als
        // Feld 24 definiert,
        // das mit den gegnerischen 2 Steinen als Feld 1
        ArrayList<Chequer> topLeft = filterColumn(allChequers, left,
                true);
        // dump("chequers in top left field", topLeft);
        if (topLeft.size() == 2) {
            this.ownClockwise = true;
            if (topLeft.get(0).color == ChequerColor.BLACK) {
                if (topLeft.get(1).color != ChequerColor.BLACK)
                    throw new IllegalStateException("different chequer colors in one field at start position?");
                this.ownWhite = false;
            } else {
                assert (topLeft.get(0).color == ChequerColor.WHITE && topLeft.get(2).color == ChequerColor.WHITE);
                this.ownWhite = true;
            }
        } else {
            assert (topLeft.size() == 5);
            this.ownClockwise = false;
            if (topLeft.get(0).color == ChequerColor.BLACK) {
                if (topLeft.get(1).color != ChequerColor.BLACK)
                    throw new IllegalStateException("different chequer colors in one field at start position?");
                this.ownWhite = false;
            } else {
                assert (topLeft.get(0).color == ChequerColor.WHITE && topLeft.get(2).color == ChequerColor.WHITE);
                this.ownWhite = true;
            }
        }

        {
            ArrayList<Integer> filteredCenterX = new ArrayList<>();
            Point last = null;
            int n = 0;
            int sum = 0;
            for (Point p : sortedCenterX) {
                if (last == null) {
                    sum = p.x;
                    n = 1;
                    last = p;
                } else {
                    if (p.x > last.x + 10) {
                        filteredCenterX.add((sum + (n >> 1)) / n);
                        sum = p.x;
                        n = 1;
                        last = p;
                    } else {
                        sum += p.x;
                        ++n;
                    }
                }
            }
            if (n > 0) {
                filteredCenterX.add((sum + (n >> 1)) / n);
            }

            int x0 = filteredCenterX.get(0);
            int x1 = filteredCenterX.get(1);
            int x2 = filteredCenterX.get(2);
            int x3 = filteredCenterX.get(3);
            dump("x0", x0);
            dump("x1", x1);
            dump("x2", x2);
            dump("x3", x3);

            if (ownClockwise) {
                dxLeft = (x1 - x0 + 3) / 5;
                dxRight = (x3 - x2 + 2) / 4;
                dxBar = x2 - (3 * dxRight + dxLeft) / 2 - x1;
            } else {
                dxLeft = (filteredCenterX.get(1) - filteredCenterX.get(0) + 2) / 4;
                dxRight = (filteredCenterX.get(3) - filteredCenterX.get(2) + 3) / 5;
                dxBar = x2 - (3 * dxLeft + dxRight) / 2 - x1;
            }
        }

        {
            ArrayList<Chequer> chequersOn19 = visibleChequersOnField(allChequers, 19);
            if (chequersOn19.size() != 5)
                throw new IllegalStateException(chequersOn19.size() + " chequers on 19, not 5");
            chequersOn19.sort(sortY);
            dy = (chequersOn19.get(4).pos.y - chequersOn19.get(0).pos.y + 2) / 4;
        }

        System.out.println("\nComplete Calibration:");
        dump();
        System.out.println();

    }

    private static BufferedImage createScreenCapture() {
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        int sw = screenSize.width;
        int sh = screenSize.height;
        return MyRobot.shot(new Rectangle(0, 0, sw, sh));

    }

    public CalibrationForSpin() throws IOException {
        this(createScreenCapture());
    }

    static CalibrationForSpin storedForWhite() throws IOException {
        return new CalibrationForSpin(
                621,
                341,
                1209,
                823,
                true,
                false,
                49,
                49,
                52,
                46);
    }

    static CalibrationForSpin storedForBlack() throws IOException {
        return new CalibrationForSpin(
                621,
                341,
                1209,
                823,
                false,
                true,
                48,
                49,
                56,
                46);

    }

    public class FieldAnalyzer {
        final ArrayList<Chequer> allChequers;
        public Player player = Player.NONE;
        public int num = 0;

        public FieldAnalyzer(ArrayList<Chequer> allChequers) {
            this.allChequers = allChequers;
        }

        public void run(int field) {
            player = playerOnField(allChequers, field);
            num = CalibrationForSpin.this.numChequersOnField(allChequers, field);
        }
    }

    public FieldAnalyzer createFieldAnalyzer(ArrayList<Chequer> allChequers) {
        return new FieldAnalyzer(allChequers);
    }

    public BarAnalyzer createBarAnalyzer(ArrayList<Chequer> allChequers) {
        return new BarAnalyzer(allChequers);
    }

    public class BarAnalyzer {
        final ArrayList<Chequer> allChequers;
        public int own = 0, other = 0;

        public BarAnalyzer(ArrayList<Chequer> allChequers) {
            this.allChequers = allChequers;
        }

        public void run() {
            var chequersOnBar = filterBar(allChequers);

            for (var chequer : chequersOnBar) {
                if (chequer.ownColor(ownWhite)) {
                    ++own;
                } else {
                    ++other;
                }

            }
        }

    }

    class ControlView {
        final BufferedImage img;

        ControlView(ArrayList<Chequer> allChequers) {
            int rl = left - 2 * dxLeft;
            int rt = top - 2 * dy;
            int rr = right + 2 * dxLeft;
            int rb = bottom + 2 * dy;
            img = MyRobot.shot(new Rectangle(rl, rt, rr - rl, rb - rt));
            Graphics2D g = img.createGraphics();
            g.translate(-rl, -rt);
            FieldAnalyzer a = new FieldAnalyzer(allChequers);
            for (int i = 1; i <= 24; ++i) {
                a.run(i);
                switch (a.player) {
                    case OWN:
                        // no break
                    case OTHER:
                        fieldAnnotation(g, i, a.player, a.num);
                        break;
                    default:
                        break;

                }
            }
            var barAnalyzer = new BarAnalyzer(allChequers);
            barAnalyzer.run();
            if (barAnalyzer.own > 0) {
                barAnnotation(g, Player.OWN, barAnalyzer.own);
            }
            if (barAnalyzer.other > 0) {
                barAnnotation(g, Player.OTHER, barAnalyzer.other);
            }
            g.dispose();
        }

        private void drawCentered(Graphics g, Rectangle r, String s) {
            // ln("drawCentered " + r + " " + s);
            var fm = g.getFontMetrics();
            var sw = fm.stringWidth(s);
            var ascent = fm.getAscent();
            var descent = fm.getDescent();
            g.drawString(s, r.x + (r.width - sw) / 2, r.y + (r.height + ascent + descent) / 2 - descent);
        }

        private final Rectangle tmpRect = new Rectangle();

        private void annotation(Graphics2D g, Rectangle r, Player p, String s) {
            boolean whitePlayer = (ownWhite == (p == Player.OWN));
            Paint bg = whitePlayer ? Color.WHITE : Color.BLACK;
            Paint fg = whitePlayer ? Color.BLACK : Color.WHITE;
            // g.setStroke(fg);
            g.setPaint(bg);
            g.fillRect(tmpRect.x, tmpRect.y, tmpRect.width, tmpRect.height);
            g.setPaint(fg);
            drawCentered(g, tmpRect, s);

        }

        private void fieldAnnotation(Graphics2D g, int field, Player p, int num) {
            Rectangle r = tmpRect;
            boolean topHalf = field >= 13;
            boolean leftHalf = ownClockwise == (field >= 19 || (field >= 7 && field <= 12));
            int dx = leftHalf ? dxLeft : dxRight;
            r.x = centerXOfField(field) - dx / 2;
            r.width = dx;
            r.y = centerYOfField(field) + (topHalf ? -dy - dy / 2 - 5 : dy / 2 + 5);
            r.height = dy;
            annotation(g, r, p, Integer.toString(num));
        }

        private void barAnnotation(Graphics2D g, Player p, int num) {
            Rectangle r = tmpRect;
            boolean topHalf = p == Player.OWN;
            int dx = dxBar;
            r.x = centerXOfBar() - dx / 2;
            r.width = dx;
            r.y = (topHalf ? top - dy - dy / 2 - 5 : bottom + dy / 2 + 5);
            r.height = dy;
            annotation(g, r, p, Integer.toString(num));
        }
    }

    public void showControlView(ArrayList<Chequer> allChequers) throws InterruptedException {
        ControlView v = new ControlView(allChequers);
        Tools.showImg("Control View", v.img, true);
    }

    public ArrayList<Chequer> searchAllVisibleChequers(BufferedImage screen) {
        ArrayList<Point> blackPositions = searchAllChequersOfColor(screen, blackChequer, null);
        ArrayList<Point> whitePositions = searchAllChequersOfColor(screen, whiteChequer, null);
        ArrayList<Chequer> blackChequers = new ArrayList<>();
        ArrayList<Chequer> whiteChequers = new ArrayList<>();
        for (Point p : blackPositions) {
            blackChequers.add(new Chequer(ChequerColor.BLACK, p));
        }
        for (Point p : whitePositions) {
            whiteChequers.add(new Chequer(ChequerColor.WHITE, p));
        }
        ArrayList<Chequer> allChequers = new ArrayList<Chequer>(blackChequers);
        allChequers.addAll(whiteChequers);

        return allChequers;
    }

    public ArrayList<Chequer> searchAllVisibleChequers() {
        return searchAllVisibleChequers(createScreenCapture());
    }

    public ArrayList<Point> searchAllChequersOfColor(BufferedImage screen, BufferedImage[] imgVariants,
            ArrayList<Point> res) {
        if (res == null)
            res = new ArrayList<>();

        Raster screenRa = screen.getData();

        for (var img : imgVariants) {
            Raster imgRa = img.getData();
            int iw = img.getWidth();
            int ih = img.getHeight();
            int iCenterX = (iw - 1) / 2;
            int iCenterY = (ih - 1) / 2;
            int radius = Math.min(iCenterX, iCenterY);
            int radiusSq = radius * radius;
            int sw = screen.getWidth();
            int sh = screen.getHeight();
            int[] sPixel = null, iPixel = null;

            for (int centerY = radius; centerY < sh - radius; ++centerY) {
                for (int centerX = radius; centerX < sw - radius; ++centerX) {
                    // System.out.println("center (" + centerX + "," + centerY + ")");
                    boolean different = false;
                    for (int dy = -radius; dy <= radius && !different; ++dy) {
                        int maxDx = (int) Math.round(Math.sqrt(radiusSq - dy * dy));

                        for (int dx = -maxDx; dx <= maxDx && !different; ++dx) {
                            sPixel = screenRa.getPixel(centerX + dx, centerY + dy, sPixel);
                            iPixel = imgRa.getPixel(iCenterX + dx, iCenterY + dy, iPixel);

                            // TODO zu enger vergleich evtl. grund, dass 'star chequers' auf der bar nicht
                            // erkannt werden?
                            boolean change = true;
                            if (change) {
                                int diff = 2;
                                if (Math.abs(sPixel[0] - iPixel[0]) > diff || Math.abs(sPixel[1] - iPixel[1]) > diff
                                        || Math.abs(sPixel[2] - iPixel[2]) > diff) {
                                    different = true;
                                }

                            } else {
                                if (!(sPixel[0] == iPixel[0] && sPixel[1] == iPixel[1] && sPixel[2] == iPixel[2])) {
                                    different = true;
                                }

                            }

                        }
                    }

                    if (!different) {
                        res.add(new Point(centerX, centerY));
                    }
                }
            }
        }
        return res;
    }

    public ArrayList<Point> searchAllChequersOfColor(BufferedImage[] imgVariants, ArrayList<Point> res) {
        return searchAllChequersOfColor(createScreenCapture(), imgVariants, res);
    }

    public int centerXOfField(int field) {
        if (field < 1 || field > 24)
            throw new IllegalArgumentException("field " + field);

        if (ownClockwise) {
            if (field >= 19)
                return left + dxLeft * (24 - field);
            if (field >= 13)
                return right - dxRight * (field - 13);
            if (field >= 7)
                return right - dxRight * (12 - field);
            return left + dxLeft * (field - 1);
        } else {
            if (field >= 19)
                return right - dxRight * (24 - field);
            if (field >= 13)
                return left + dxLeft * (field - 13);
            if (field >= 7)
                return left + dxLeft * (12 - field);
            return right - dxRight * (field - 1);
        }

    }

    public int centerXOfBar() {
        if (ownClockwise) {
            // 24 links
            return centerXOfField(19) + (dxLeft + dxBar) / 2;
        } else {
            // 24 rechts
            return centerXOfField(19) - (dxLeft + dxBar) / 2;
        }
    }

    public int centerXOfOff(Player p) {
        int lastField;

        switch (p) {
            case NONE:
                throw new IllegalArgumentException("NONE not allowed for centerXOfOff()");
            case OWN:
                lastField = ownWhite ? 24 : 1;
                break;
            case OTHER:
                lastField = ownWhite ? 1 : 24;
                break;
            default:
                throw new IllegalStateException();

        }
        switch (p) {
            case NONE:
                throw new IllegalArgumentException("NONE not allowed for centerXOfOff()");
            case OWN:
                // no break
            case OTHER:
                if (ownClockwise) {
                    return centerXOfField(lastField) - dxLeft * 3 / 2;
                } else {
                    return centerXOfField(lastField) + dxRight * 3 / 2;
                }
            default:
                throw new IllegalStateException();
        }
    }

    /**
     * 24 immer oben, 1 immer unten. Eigener Spieler immer von 24 nach 1.
     */
    public int centerYOfOff(Player p) {
        switch (p) {
            case NONE:
                throw new IllegalArgumentException("NONE not allowed for centerYOfOff()");
            case OWN:
                return centerYOfField(1);
            case OTHER:
                return centerYOfField(24);
            default:
                throw new IllegalStateException();
        }

    }

    /**
     * 24 ist immer oben, 1 ist immer unten, egal welche Farben
     */
    public int centerYOfField(int field) {
        if (field < 1 || field > 24)
            throw new IllegalArgumentException("field " + field);

        return field >= 13 ? top : bottom;
    }

    void dumpCenterXOfFields() {
        System.out.println("centerX of fields:");
        for (int field = 24; field >= 1; --field) {
            dump(Integer.toString(field), centerXOfField(field));
        }
    }

    void dumpCenterYOfFields() {
        System.out.println("centerY of fields:");
        for (int field = 24; field >= 1; --field) {
            dump(Integer.toString(field), centerYOfField(field));
        }
    }
}
