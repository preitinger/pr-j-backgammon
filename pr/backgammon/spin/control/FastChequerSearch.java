package pr.backgammon.spin.control;

import java.awt.Point;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.awt.image.Raster;
import java.io.IOException;
import java.util.ArrayList;

import pr.backgammon.model.Field;
import pr.backgammon.spin.control.CalibrationForSpin.Chequer;
import pr.backgammon.spin.control.CalibrationForSpin.ChequerColor;
import pr.backgammon.spin.model.Player;
import pr.control.Tools;

/**
 * more efficient than the functions in CalibrationForSpin because here, a
 * smaller screenshot is analyzed
 * instead of a full screen shot as in CalibrationForSpin.
 */
public class FastChequerSearch {
    private final CalibrationForSpin cal;
    private final Rectangle screenshotRect;
    private final int left, right, top, bottom;
    private ArrayList<Chequer> allChequers = null;
    private int bearoffWhite, bearoffBlack;

    public FastChequerSearch(CalibrationForSpin cal) {
        this.cal = cal;

        screenshotRect = new Rectangle();
        int gapx = 9 * cal.dxLeft / 2; // TODO war / 4
        int xForLeaveButton = 60;
        screenshotRect.x = cal.left - gapx;
        screenshotRect.y = cal.top - cal.dy;
        screenshotRect.width = cal.right - cal.left + gapx + gapx + xForLeaveButton;
        screenshotRect.height = cal.bottom - cal.top + cal.dy * 2 +
                (905 - 870); // extra space at bottom for status line

        int translatex = -screenshotRect.x;
        int translatey = -screenshotRect.y;
        left = cal.left + translatex;
        right = cal.right + translatex;
        top = cal.top + translatey;
        bottom = cal.bottom + translatey;
    }

    public Rectangle boardScreenshotRect(Rectangle screenshotRect) {
        if (screenshotRect == null) {
            screenshotRect = new Rectangle();
        }
        screenshotRect.x = this.screenshotRect.x;
        screenshotRect.y = this.screenshotRect.y;
        screenshotRect.width = this.screenshotRect.width;
        screenshotRect.height = this.screenshotRect.height;
        return screenshotRect;
    }

    public void init(Raster board) {
        // 2 Bildschirme: boardScreenshotRect:
        // java.awt.Rectangle[x=378,y=268,width=1088,height=574]
        // 1 Bildschirm : boardScreenshotRect:
        // java.awt.Rectangle[x=401,y=295,width=1088,height=574]
        try {
            allChequers = searchAllVisibleChequers(board);

            int xleft = 525 - 401;
            int xright = 1262 - 401;
            int yTop = 314 - 295;
            int yBottom = 641 - 295;
            Rectangle clip = new Rectangle();
            Point pos = new Point();
            int numWhite = 0;
            int numBlack = 0;

            // white bearoff chequers
            clip.x = cal.ownWhite ? xright : xleft;
            clip.y = cal.ownWhite ? yBottom : yTop;
            clip.width = 1312 - 1262;
            clip.height = 846 - 641;
            var bearoffChequerWhite = Tools.loadImg("pr/res/chequerBearoffWhite.png").getRaster();
            // System.out.println("clip.y + clip.height=" + (clip.y + clip.height));
            // System.out.println("bearoffChequerWhite.getHeight()=" + bearoffChequerWhite.getHeight());
            // System.out.println("board.getHeight()=" + board.getHeight());
            // System.out.println("clip: " + clip);
            // System.out.println("board.getWidth()=" + board.getWidth());

            do {
                if (Tools.searchImage(board, clip, bearoffChequerWhite, null, 2, pos) == null) {
                    break;
                }
                ++numWhite;
                int dh = pos.y + bearoffChequerWhite.getHeight() - clip.y;
                if (dh >= clip.height) {
                    throw new IllegalStateException("dh=" + dh + "   clip.height=" + clip.height);
                }
                clip.y += dh;
                clip.height -= dh;
            } while (true);

            // black bearoff chequers
            clip.y = cal.ownWhite ? yTop : yBottom;
            clip.height = 846 - 641;
            // Rest unveraendert

            var bearoffChequerBlack = Tools.loadImg("pr/res/chequerBearoffBlack.png").getRaster();

            do {
                if (Tools.searchImage(board, clip, bearoffChequerBlack, null, 2, pos) == null) {
                    break;
                }
                ++numBlack;
                int dh = pos.y + bearoffChequerWhite.getHeight() - clip.y;
                if (dh >= clip.height) {
                    throw new IllegalStateException("dh=" + dh + "   clip.height=" + clip.height);
                }
                clip.y += dh;
                clip.height -= dh;
            } while (true);

            bearoffWhite = numWhite;
            bearoffBlack = numBlack;

            // 1262, 314 fuer bearoffChequers bei ownWhite
            // 525, 314 fuer bearoffChequers bei !ownWhite

            // jeweils y bis 846

        } catch (IOException ex) {
            ex.printStackTrace();
            throw new RuntimeException(ex);
        }
    }

    private ArrayList<Chequer> searchAllVisibleChequers(Raster board) {
        ArrayList<Point> blackPositions = searchAllChequersOfColor(board, cal.blackChequer, null);
        ArrayList<Point> whitePositions = searchAllChequersOfColor(board, cal.whiteChequer, null);
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

        // System.out.println("allChequers.size() " + allChequers.size());
        // for (Chequer c : allChequers) {
        // System.out.println("chequer " + c.color + " " + c.pos);
        // }
        return allChequers;

    }

    public ArrayList<Point> searchAllChequersOfColor(Raster screenRa, BufferedImage[] imgVariants,
            ArrayList<Point> res) {
        if (res == null)
            res = new ArrayList<>();

        for (var img : imgVariants) {
            Raster imgRa = img.getData();
            int iw = img.getWidth();
            int ih = img.getHeight();
            int iCenterX = (iw - 1) / 2;
            int iCenterY = (ih - 1) / 2;
            int radius = Math.min(iCenterX, iCenterY);
            int radiusSq = radius * radius;
            int sw = screenRa.getWidth();
            int sh = screenRa.getHeight();
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

    public Player playerOnField(int field) {
        if (allChequers == null) {
            throw new IllegalStateException("init() not yet called");
        }
        return playerOnField(this.allChequers, field);
    }

    public Player playerOnField(ArrayList<Chequer> allChequers, int field) {
        ArrayList<Chequer> f = visibleChequersOnField(allChequers, field);
        if (f.isEmpty())
            return Player.NONE;
        return (f.get(0).color == ChequerColor.WHITE) == cal.ownWhite ? Player.OWN : Player.OTHER;
    }

    public ArrayList<Chequer> visibleChequersOnField(ArrayList<Chequer> allChequers, int field) {
        int centerX = centerXOfField(field);
        boolean topHalf = field >= 13;
        return filterColumn(allChequers, centerX, topHalf);
    }

    public int centerXOfField(int field) {
        if (field < 1 || field > 24)
            throw new IllegalArgumentException("field " + field);

        if (cal.ownClockwise) {
            if (field >= 19)
                return left + cal.dxLeft * (24 - field);
            if (field >= 13)
                return right - cal.dxRight * (field - 13);
            if (field >= 7)
                return right - cal.dxRight * (12 - field);
            return left + cal.dxLeft * (field - 1);
        } else {
            if (field >= 19)
                return right - cal.dxRight * (24 - field);
            if (field >= 13)
                return left + cal.dxLeft * (field - 13);
            if (field >= 7)
                return left + cal.dxLeft * (12 - field);
            return right - cal.dxRight * (field - 1);
        }

    }

    public ArrayList<Chequer> filterColumn(
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

    int centerXOfBar() {
        if (cal.ownClockwise) {
            // 24 links
            return centerXOfField(19) + (cal.dxLeft + cal.dxBar) / 2;
        } else {
            // 24 rechts
            return centerXOfField(19) - (cal.dxLeft + cal.dxBar) / 2;
        }
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

    boolean inTopHalf(int centerY) {
        return centerY < midY();
    }

    boolean inBottomHalf(int centerY) {
        return centerY > midY();
    }

    int midY() {
        return (top + bottom) >> 1;
    }

    public int numChequersOnField(int field) {
        if (allChequers == null) {
            throw new IllegalStateException("init() not yet called");
        }

        return numChequersOnField(this.allChequers, field);
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
        sorted.sort(CalibrationForSpin.sortY);
        int diffY = topHalf ? cal.dy : -cal.dy;
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
            if (CalibrationForSpin.yEqual(isY, shallY)) {
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
            if (CalibrationForSpin.yEqual(nextY, y + diffY)) {
                ++res;
                y = nextY;
            } else {
                break;
            }
        }

        return res;
    }

    public int[] chequersOnBars(int[] out) {
        if (out == null || out.length < 2) {
            out = new int[2];
        }
        ArrayList<Chequer> chequersOnBar = filterBar(allChequers);
        out[0] = out[1] = 0;
        for (Chequer c : chequersOnBar) {
            if (c.ownColor(cal.ownWhite)) {
                ++out[0];
            } else {
                ++out[1];
            }
        }

        return out;
    }

    public int chequersOffWhite() {
        return bearoffWhite;
    }

    public int chequersOffBlack() {
        return bearoffBlack;
    }

    public void getFields(Field outOwn, Field outOpp) {
        outOwn.clear();
        outOpp.clear();
        int offOwn = 15, offOpp = 15;
        int[] barNums = chequersOnBars(null);
        outOwn.set(25, barNums[0]);
        offOwn -= barNums[0];
        outOpp.set(25, barNums[1]);
        offOpp -= barNums[1];

        for (int i = 24; i >= 1; --i) {
            Player p = playerOnField(i);
            if (p != Player.NONE) {
                int chequers = numChequersOnField(i);
                if (p == Player.OWN) {
                    outOwn.set(i, chequers);
                    offOwn -= chequers;
                } else {
                    outOpp.set(25 - i, chequers);
                    offOpp -= chequers;
                }
            }
        }

        if (offOwn < 0)
            throw new IllegalStateException("offOwn=" + offOwn);
        if (offOpp < 0)
            throw new IllegalStateException("offOpp=" + offOpp);

        int measuredOffOwn = (cal.ownWhite ? bearoffWhite : bearoffBlack);
        int measuredOffOpp = (cal.ownWhite ? bearoffBlack : bearoffWhite);

        if (offOwn != measuredOffOwn || offOpp != measuredOffOpp) {
            StringBuilder sb = new StringBuilder();
            System.out.println(sb.append("Unexpected state: offOwn=").append(offOwn).append(", offOpp=").append(offOpp)
                    .append(", bearoffWhite=").append(bearoffWhite).append(", bearoffBlack").append(bearoffBlack));

        }
        outOwn.set(0, measuredOffOwn);
        outOpp.set(0, measuredOffOpp);
    }
}
