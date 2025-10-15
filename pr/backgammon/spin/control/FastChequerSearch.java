package pr.backgammon.spin.control;

import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.util.ArrayList;

import pr.backgammon.model.Field;
import pr.backgammon.model.LocAndVal;
import pr.backgammon.spin.control.CalibrationForSpin.ChequerColor;
import pr.backgammon.spin.model.Player;
import pr.backgammon.spin.templatesearchers.Chequer;

/**
 * more efficient than the functions in CalibrationForSpin because here, a
 * smaller screenshot is analyzed
 * instead of a full screen shot as in CalibrationForSpin.
 */
public class FastChequerSearch {
    private final CalibrationForSpin cal;
    private final TemplateSearchers ts;
    private final Rectangle screenshotRect;
    private final int left, right, top, bottom;
    private ArrayList<LocAndVal> allChequers = new ArrayList<>();
    private int bearoffWhite, bearoffBlack;
    private final int[] tmpPixel = new int[3];

    private final float[] tmpFloats = new float[40 * 3];

    private static final int MAX_CHEQUER_DIST = 13;

    private final Field fieldOwn = new Field();
    private final Field fieldOpp = new Field();

    public FastChequerSearch(CalibrationForSpin cal, TemplateSearchers ts) {
        this.cal = cal;
        this.ts = ts;

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

        // System.out.println("FastChequerSearch: left " + left + " top " + top + "
        // right " + right + " bottom " + bottom);
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

    public void init(BufferedImage board) {
        // 2 Bildschirme: boardScreenshotRect:
        // java.awt.Rectangle[x=378,y=268,width=1088,height=574]
        // 1 Bildschirm : boardScreenshotRect:
        // java.awt.Rectangle[x=401,y=295,width=1088,height=574]
        // try {
        fieldOwn.clear();
        fieldOpp.clear();
        searchBearoff(board);
        searchAllVisibleChequers(board);
        // System.out.println("allChequers.size " + allChequers.size());

        // int xleft = 525 - 401;
        // int xright = 1262 - 401;
        // int yTop = 314 - 295;
        // int yBottom = 641 - 295;
        // Rectangle clip = new Rectangle();
        // Point pos = new Point();
        // int numWhite = 0;
        // int numBlack = 0;

        // // white bearoff chequers
        // clip.x = cal.ownWhite ? xright : xleft;
        // clip.y = cal.ownWhite ? yBottom : yTop;
        // clip.width = 1312 - 1262;
        // clip.height = 846 - 641;
        // var bearoffChequerWhite =
        // Tools.loadImg("pr/res/chequerBearoffWhite.png").getRaster();
        // // System.out.println("clip.y + clip.height=" + (clip.y + clip.height));
        // // System.out.println("bearoffChequerWhite.getHeight()=" +
        // bearoffChequerWhite.getHeight());
        // // System.out.println("board.getHeight()=" + board.getHeight());
        // // System.out.println("clip: " + clip);
        // // System.out.println("board.getWidth()=" + board.getWidth());

        // do {
        // if (Tools.searchImage(board, clip, bearoffChequerWhite, null, 2, pos) ==
        // null) {
        // break;
        // }
        // ++numWhite;
        // int dh = pos.y + bearoffChequerWhite.getHeight() - clip.y;
        // if (dh >= clip.height) {
        // throw new IllegalStateException("dh=" + dh + " clip.height=" + clip.height);
        // }
        // clip.y += dh;
        // clip.height -= dh;
        // } while (true);

        // // black bearoff chequers
        // clip.y = cal.ownWhite ? yTop : yBottom;
        // clip.height = 846 - 641;
        // // Rest unveraendert

        // var bearoffChequerBlack =
        // Tools.loadImg("pr/res/chequerBearoffBlack.png").getRaster();

        // do {
        // if (Tools.searchImage(board, clip, bearoffChequerBlack, null, 2, pos) ==
        // null) {
        // break;
        // }
        // ++numBlack;
        // int dh = pos.y + bearoffChequerWhite.getHeight() - clip.y;
        // if (dh >= clip.height) {
        // throw new IllegalStateException("dh=" + dh + " clip.height=" + clip.height);
        // }
        // clip.y += dh;
        // clip.height -= dh;
        // } while (true);

        // bearoffWhite = numWhite;
        // bearoffBlack = numBlack;

        // 1262, 314 fuer bearoffChequers bei ownWhite
        // 525, 314 fuer bearoffChequers bei !ownWhite

        // jeweils y bis 846

        // } catch (IOException ex) {
        // ex.printStackTrace();
        // throw new RuntimeException(ex);
        // }
    }

    private void searchBearoff(BufferedImage board) {
        /*
         * Bei !ownClockwise:
         * x: 900
         * Bei ownClockwise:
         * x: 155
         * 
         * y:
         * 
         * Bei ownWhite:
         * 
         * 1. schwarzer Bearoff: 222
         * 
         * 1. weißer Bearoff: 555
         * 3. weißer Bearoff: 528
         * 5. weißer Bearoff: 505
         * 14. weißer Bearoff: 394
         * 
         * Bei !ownWhite:
         * 
         * y: Weiß wie Schwarz von ownWhite und umgekehrt
         * 
         * Also vertikaler Abstand: (394 - 222) / 13 = 13.23
         */

        // FastChequerSearch: left 220 top 47 right 814 bottom 534

        final int x = cal.ownClockwise ? 155 : 900;
        final int yTop = 220;
        final int yBottom = 552;

        final int yWhite = cal.ownWhite ? yBottom : yTop;
        final int yBlack = cal.ownWhite ? yTop : yBottom;

        // 1: 220 und 9: 121
        final double dy = ((double) 220 - 121) / 8;
        // System.out.println("dy " + dy);

        this.bearoffWhite = countBearoff(board, x, yWhite, dy, true);
        this.bearoffBlack = countBearoff(board, x, yBlack, dy, false);
        this.fieldOwn.set(0, cal.ownWhite ? this.bearoffWhite : this.bearoffBlack);
        this.fieldOpp.set(0, cal.ownWhite ? this.bearoffBlack : this.bearoffWhite);
        // BufferedImage board2 = new BufferedImage(board.getWidth(), board.getHeight(),
        // board.getType());
        // board.copyData(board2.getRaster());
        // try {
        // Graphics2D g = board2.createGraphics();
        // g.setColor(Color.RED);
        // g.drawLine(x, yWhite, x, yBlack);
        // for (int i = 0; i < 15; ++i) {
        // int y2 = (int) Math.round(yWhite - i * dy);
        // g.drawLine(x - 5, y2, x + 5, y2);
        // }
        // g.dispose();
        // ImgAndMousePosFrame f = new ImgAndMousePosFrame();
        // f.setImg(board2);
        // f.setVisible(true);
        // Tools.showImg("copied", board2, false);
        // } catch (Exception ex) {
        // ex.printStackTrace();
        // }

        // try {
        // board = board.getSubimage(right + 88, top - cal.dy, 1, bottom + cal.dy - (top
        // - cal.dy));
        // SearchBearoffRects s = new SearchBearoffRects();
        // Mat img = BufferedImageToMat.toBgrMat(board);
        // Imgproc.GaussianBlur(img, img, new Size(9, 9), 0);
        // Mat img8bit = new Mat();
        // Imgproc.cvtColor(img, img8bit, Imgproc.COLOR_BGR2GRAY);
        // // Tools.showImg("CV_8S", MatToBufferedImage.matToBufferedImage(img8bit),
        // true);
        // s.run(img8bit);
        // } catch (Exception ex) {
        // ex.printStackTrace();
        // }

    }

    private int countBearoff(BufferedImage board, int x, int y, double dy, boolean white) {
        double y1 = y;
        int n = 0;
        var raster = board.getRaster();
        final int thresholdWhite = 150 * 3;
        final int thresholdBlack = 50 * 3;
        System.out.println("width " + board.getWidth() + " height " + board.getHeight());
        System.out.println("img type " + board.getType());
        while (n < 15) {
            y = (int) Math.round(y1);
            System.out.println("x " + x + " y " + y);
            assert (x < board.getWidth());
            assert (y < board.getHeight());
            raster.getPixel(x, y, tmpPixel);
            int sum = tmpPixel[0] + tmpPixel[1] + tmpPixel[2];
            // System.out.println("n " + n + " sum " + sum + " thresholdWhite " +
            // thresholdWhite + " thresholdBlack "
            // + thresholdBlack);

            if (white) {
                if (sum <= thresholdWhite) {
                    break;
                }
            } else {
                if (sum >= thresholdBlack) {
                    break;
                }
            }

            ++n;
            y1 -= dy;
        }

        return n;
    }

    private void searchAllVisibleChequers(BufferedImage board) {
        // ArrayList<Point> blackPositions = searchAllChequersOfColor(board,
        // cal.blackChequer, null);
        // ArrayList<Point> whitePositions = searchAllChequersOfColor(board,
        // cal.whiteChequer, null);
        // ArrayList<Chequer> blackChequers = new ArrayList<>();
        // ArrayList<Chequer> whiteChequers = new ArrayList<>();
        // for (Point p : blackPositions) {
        // blackChequers.add(new Chequer(ChequerColor.BLACK, p));
        // }
        // for (Point p : whitePositions) {
        // whiteChequers.add(new Chequer(ChequerColor.WHITE, p));
        // }
        // ArrayList<Chequer> allChequers = new ArrayList<Chequer>(blackChequers);
        // allChequers.addAll(whiteChequers);

        // System.out.println("allChequers.size() " + allChequers.size());
        // for (Chequer c : allChequers) {
        // System.out.println("chequer " + c.color + " " + c.pos);
        // }

        // SearchChequerCircles s = new SearchChequerCircles();
        // int n = s.run(board, tmpFloats);
        // ArrayList<Chequer> allChequers = this.allChequers;
        // allChequers.clear();

        // var raster = board.getRaster();
        // float[] pixel = new float[3];

        // for (int i = 0; i < n; ++i) {
        // int x = Math.round(tmpFloats[i * 3]);
        // int y = Math.round(tmpFloats[i * 3 + 1]);

        // pixel = raster.getPixel(x + 12, y, pixel); // nicht Mitte, sondern um 12 nach
        // rechts versetzt wegen des
        // // optionalen weissen bzw. schwarzen sterns in der mitte des
        // // selektierten chequers
        // boolean white = (pixel[0] + pixel[1] + pixel[2]) / 3 > 128;
        // allChequers.add(new Chequer(white ? ChequerColor.WHITE : ChequerColor.BLACK,
        // new Point(x, y)));
        // }

        allChequers.clear();

        LocAndVal[] bestWhite = ts.chequerWhite.findBestNonOverlappingCenters(board, 64);
        LocAndVal[] bestBlack = ts.chequerBlack.findBestNonOverlappingCenters(board, 64);

        {
            LocAndVal[] bestWhiteStar = ts.chequerWhiteStar.findBestNonOverlappingCenters(board, 6);

            int valid = 0;
            while (valid < bestWhiteStar.length && bestWhiteStar[valid].val > Chequer.LIMIT) {
                ++valid;
            }
            if (valid > 0) {
                var newBestWhite = new LocAndVal[valid + bestWhite.length];

                for (int i = 0; i < valid; ++i) {
                    newBestWhite[i] = bestWhiteStar[i];
                }
                for (int i = 0; i < bestWhite.length; ++i) {
                    newBestWhite[i + valid] = bestWhite[i];
                }
                bestWhite = newBestWhite;

                // System.out.println("new best white");
                // for (int i = 0; i < bestWhite.length; ++i) {
                // System.out.println("[" + i + "] " + bestWhite[i]);
                // }
            } else {
                // System.out.println("white star not found");
            }

            // var whiteStarPos = ts.search(ts.chequerWhiteStar, board);
            // if (whiteStarPos != null) {
            // // System.out.println("white star found");
            // var newBestWhite = new LocAndVal[bestWhite.length + 1];
            // newBestWhite[0] = new LocAndVal(whiteStarPos.y, whiteStarPos.x, 1);
            // for (int i = 0; i < bestWhite.length; ++i) {
            // newBestWhite[i + 1] = bestWhite[i];
            // }
            // bestWhite = newBestWhite;

            // // System.out.println("new best white");
            // // for (int i = 0; i < bestWhite.length; ++i) {
            // // System.out.println("[" + i + "] " + bestWhite[i]);
            // // }
            // } else {
            // // System.out.println("white star not found");
            // }
        }

        {
            LocAndVal[] bestBlackStar = ts.chequerBlackStar.findBestNonOverlappingCenters(board, 6);

            int valid = 0;
            while (valid < bestBlackStar.length && bestBlackStar[valid].val > Chequer.LIMIT) {
                ++valid;
            }
            if (valid > 0) {
                var newBestBlack = new LocAndVal[valid + bestBlack.length];

                for (int i = 0; i < valid; ++i) {
                    newBestBlack[i] = bestBlackStar[i];
                }
                for (int i = 0; i < bestBlack.length; ++i) {
                    newBestBlack[i + valid] = bestBlack[i];
                }
                bestBlack = newBestBlack;

                // System.out.println("new best black");
                // for (int i = 0; i < bestBlack.length; ++i) {
                // System.out.println("[" + i + "] " + bestBlack[i]);
                // }
            } else {
                // System.out.println("black star not found");
            }

            // var blackStarPos = ts.search(ts.chequerBlackStar, board);
            // if (blackStarPos != null) {
            // // System.out.println("black star found");
            // var newBestBlack = new LocAndVal[bestBlack.length + 1];
            // newBestBlack[0] = new LocAndVal(blackStarPos.y, blackStarPos.x, 1);
            // for (int i = 0; i < bestBlack.length; ++i) {
            // newBestBlack[i + 1] = bestBlack[i];
            // }
            // bestBlack = newBestBlack;

            // // System.out.println("new best black");
            // // for (int i = 0; i < bestBlack.length; ++i) {
            // // System.out.println("[" + i + "] " + bestBlack[i]);
            // // }
            // } else {
            // // System.out.println("black star not found");
            // }
        }

        // {
        // var blackStarPos = ts.search(ts.chequerBlackStar, board);
        // if (blackStarPos != null) {
        // // System.out.println("black star found");
        // var newBestBlack = new LocAndVal[bestBlack.length + 1];
        // newBestBlack[0] = new LocAndVal(blackStarPos.y, blackStarPos.x, 1);
        // for (int i = 0; i < bestBlack.length; ++i) {
        // newBestBlack[i + 1] = bestBlack[i];
        // }
        // bestBlack = newBestBlack;
        // } else {
        // // System.out.println("black star not found");
        // }
        // }

        {
            int num = 0;
            int max = Field.NUM_ALL_CHEQUERS - bearoffWhite;

            for (var wc : bestWhite) {
                if (num < max) {
                    ++num;
                    wc.custom = ChequerColor.WHITE;
                }
            }

            num = 0;
            max = Field.NUM_ALL_CHEQUERS - bearoffBlack;
            for (var bc : bestBlack) {
                if (num < max) {
                    ++num;
                    bc.custom = ChequerColor.BLACK;
                }
            }
        }
        // Now, additionally filter under precondition that searchBearoff() has already
        // been executed.
        //

        {
            var bestColor = bestWhite;
            String colorName = "white";
            int bearoff = bearoffWhite;

            scanChequers(bestColor, colorName, bearoff);
        }

        {
            var bestColor = bestBlack;
            String colorName = "black";
            int bearoff = bearoffBlack;

            scanChequers(bestColor, colorName, bearoff);
        }

        // {
        // System.out.println("Work on black chequers");
        // int next = 0;
        // int todo = Field.NUM_ALL_CHEQUERS - bearoffBlack;
        // allChequers.clear();
        // for (int i = 0; i < bestBlack.length; ++i) {
        // allChequers.add(bestBlack[i]);
        // }

        // while (todo > 0 && next < bestBlack.length) {
        // System.out.println("todo " + todo + " next " + next + " len " +
        // bestBlack.length);
        // var best = bestBlack[next++];
        // boolean isOnBar = onBar(best);

        // if (isOnBar) {
        // if (isOwnColor(best)) {
        // fieldOwn.set(25, fieldOwn.getChequers(25) + 1);
        // System.out.println("Own black on bar");
        // } else {
        // fieldOpp.set(25, fieldOpp.getChequers(25) + 1);
        // System.out.println("Opp black on bar");
        // }
        // --todo;
        // continue;
        // }

        // int field = fieldOf(best);
        // limitChequers(allChequers, next + todo);
        // int n = numChequersOnField(field);
        // if (isOwnColor(best)) {
        // if (fieldOwn.getChequers(field) > 0) {
        // System.out.println("Ignore black chequer on " + field + " for own color");
        // // ignore because this chequer has already been counted
        // continue;
        // }
        // if (fieldOpp.getChequers(25 - field) > 0) {
        // throw new IllegalStateException("Found invalid own chequer before all valid
        // were found!");
        // }
        // fieldOwn.set(field, n);
        // System.out.println("Own " + n + " black on " + field);
        // } else {
        // if (fieldOpp.getChequers(25 - field) > 0) {
        // System.out.println("Ignore black chequer on " + field + " for opp color");
        // // ignore because this chequer has already been counted
        // continue;
        // }
        // if (fieldOwn.getChequers(field) > 0) {
        // throw new IllegalStateException("Found invalid opp chequer before all valid
        // were found!");
        // }
        // fieldOpp.set(25 - field, n);
        // System.out.println("Opp " + n + " black on " + field);
        // }
        // todo -= n;

        // if (todo < 0) {
        // throw new IllegalStateException("Found too many valid black chequers! (todo="
        // + todo + ")");
        // }
        // }

        // if (todo > 0) {
        // throw new IllegalStateException("Found too few valid black chequers! (todo="
        // + todo + ")");
        // }
        // }
    }

    private void scanChequers(LocAndVal[] bestColor, String colorName, int bearoff) {
        // System.out.println("Work on " + colorName + " chequers");
        int next = 0;
        int todo = Field.NUM_ALL_CHEQUERS - bearoff;
        int toIgnore = 0;
        allChequers.clear();
        double threshold = 0.8;
        for (int i = 0; i < bestColor.length; ++i) {
            if (bestColor[i].val > threshold) {
                allChequers.add(bestColor[i]);
            }
        }

        while (todo > 0 && next < bestColor.length) {
            // System.out.println("todo " + todo + " next " + next + " len " +
            // bestColor.length);
            var best = bestColor[next++];
            boolean isOnBar = onBar(best);

            if (isOnBar) {
                if (isOwnColor(best)) {
                    fieldOwn.set(25, fieldOwn.getChequers(25) + 1);
                    // System.out.println("Own " + colorName + " on bar");
                } else {
                    fieldOpp.set(25, fieldOpp.getChequers(25) + 1);
                    // System.out.println("Opp " + colorName + " on bar");
                }
                --todo;
                continue;
            }

            int field;
            try {
                field = fieldOf(best);
            } catch (Exception ex) {
                System.out.println(
                        "Folgende Exception trat bei der Suche nach Chequern auf, muss aber nicht unbedingt ein Fehler sein, da manchmal der Deko-Chequer ganz rechts gefunden wird...");
                ex.printStackTrace();
                continue;
            }
            limitChequers(allChequers, next + todo + toIgnore);
            int n = numChequersOnField(field, best.val * 0.8);

            {
                int x = toIgnoreForCount(n);
                toIgnore += x;
                // System.out.println("toIgnore incremented by " + x + " to new value " +
                // toIgnore);
            }
            if (isOwnColor(best)) {
                if (fieldOwn.getChequers(field) > 0) {
                    // ignore because this chequer has already been counted
                    --toIgnore;
                    if (toIgnore < 0) {
                        throw new IllegalStateException("toIgnore became negative");
                    }
                    // System.out.println("Ignore " + colorName + " chequer on " + field + " for own
                    // color");
                    continue;
                }
                if (fieldOpp.getChequers(25 - field) > 0) {
                    throw new IllegalStateException("Found invalid own chequer before all valid were found!");
                }
                fieldOwn.set(field, n);
                // System.out.println("Own " + n + " " + colorName + " on " + field);
            } else {
                if (fieldOpp.getChequers(25 - field) > 0) {
                    // ignore because this chequer has already been counted
                    // System.out.println("Ignore " + colorName + " chequer on " + field + " for opp
                    // color");
                    continue;
                }
                if (fieldOwn.getChequers(field) > 0) {
                    throw new IllegalStateException("Found invalid opp chequer before all valid were found!");
                }
                fieldOpp.set(25 - field, n);
                // System.out.println("Opp " + n + " " + colorName + " on " + field);
            }
            todo -= n;

            if (todo < 0) {
                throw new IllegalStateException("Found too many valid " + colorName + " chequers! (todo=" + todo + ")");
            }
        }

        // System.out.println("toIgnore nach scanChequers for " + colorName + ": " +
        // toIgnore);

        if (todo > 0) {
            throw new IllegalStateException("Found too few valid " + colorName + " chequers! (todo=" + todo + ")");
        }
    }

    private void limitChequers(ArrayList<LocAndVal> chequers, int n) {
        while (chequers.size() > n) {
            chequers.removeLast();
        }
    }

    private int toIgnoreForCount(int count) {
        int nextLayerSize = 5;
        int rest = count;
        int layers = 0;

        while (rest > nextLayerSize) {
            rest -= nextLayerSize;
            --nextLayerSize;
            ++layers;
        }

        // System.out.println("toIgnoreForCount - count " + count + " rest " + rest + "
        // layers " + layers + " nextLayerSize " + nextLayerSize);

        return rest + (layers > 0 ? nextLayerSize + 1 - (rest + 1) : rest) - 1;
    }

    // public ArrayList<Point> searchAllChequersOfColor(Raster screenRa,
    // BufferedImage[] imgVariants,
    // ArrayList<Point> res) {
    // if (res == null)
    // res = new ArrayList<>();

    // for (var img : imgVariants) {
    // Raster imgRa = img.getData();
    // int iw = img.getWidth();
    // int ih = img.getHeight();
    // int iCenterX = (iw - 1) / 2;
    // int iCenterY = (ih - 1) / 2;
    // int radius = Math.min(iCenterX, iCenterY);
    // int radiusSq = radius * radius;
    // int sw = screenRa.getWidth();
    // int sh = screenRa.getHeight();
    // int[] sPixel = null, iPixel = null;

    // for (int centerY = radius; centerY < sh - radius; ++centerY) {
    // for (int centerX = radius; centerX < sw - radius; ++centerX) {
    // // System.out.println("center (" + centerX + "," + centerY + ")");
    // boolean different = false;
    // for (int dy = -radius; dy <= radius && !different; ++dy) {
    // int maxDx = (int) Math.round(Math.sqrt(radiusSq - dy * dy));

    // for (int dx = -maxDx; dx <= maxDx && !different; ++dx) {
    // sPixel = screenRa.getPixel(centerX + dx, centerY + dy, sPixel);
    // iPixel = imgRa.getPixel(iCenterX + dx, iCenterY + dy, iPixel);

    // // TODO zu enger vergleich evtl. grund, dass 'star chequers' auf der bar
    // nicht
    // // erkannt werden?
    // boolean change = true;
    // if (change) {
    // int diff = 2;
    // if (Math.abs(sPixel[0] - iPixel[0]) > diff || Math.abs(sPixel[1] - iPixel[1])
    // > diff
    // || Math.abs(sPixel[2] - iPixel[2]) > diff) {
    // different = true;
    // }

    // } else {
    // if (!(sPixel[0] == iPixel[0] && sPixel[1] == iPixel[1] && sPixel[2] ==
    // iPixel[2])) {
    // different = true;
    // }

    // }

    // }
    // }

    // if (!different) {
    // res.add(new Point(centerX, centerY));
    // }
    // }
    // }
    // }
    // return res;
    // }

    public Player playerOnField(int field) {
        if (allChequers == null) {
            throw new IllegalStateException("init() not yet called");
        }
        return playerOnField(this.allChequers, field);
    }

    public Player playerOnField(ArrayList<LocAndVal> allChequers, int field) {
        ArrayList<LocAndVal> f = visibleChequersOnField(allChequers, field);
        if (f.isEmpty())
            return Player.NONE;
        return (f.get(0).custom == ChequerColor.WHITE) == cal.ownWhite ? Player.OWN : Player.OTHER;
    }

    public ArrayList<LocAndVal> visibleChequersOnField(ArrayList<LocAndVal> allChequers, int field) {
        int centerX = centerXOfField(field);
        boolean topHalf = field >= 13;
        System.out.println("visibleChequersOnField " + field);
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

    /**
     * throws IllegalArgumentException if the row and col values of x do not match
     * to a field on the board.
     */
    private int fieldOf(LocAndVal x) throws IllegalArgumentException {
        if (x.col <= left - MAX_CHEQUER_DIST) {
            throw new IllegalArgumentException("col too far left");
        }
        if (x.col >= right + MAX_CHEQUER_DIST) {
            throw new IllegalArgumentException("col too far right");
        }
        if (x.row <= this.top - MAX_CHEQUER_DIST) {
            throw new IllegalArgumentException("row above top");
        }
        if (x.row >= this.bottom + MAX_CHEQUER_DIST) {
            throw new IllegalArgumentException("row below bottom");
        }

        boolean inLeftHalf = x.col < left + 5 * cal.dxLeft + MAX_CHEQUER_DIST;
        boolean inRightHalf = x.col > right - 5 * cal.dxRight - MAX_CHEQUER_DIST;
        int indexHor;

        if (inLeftHalf) {
            int fromLeft = (x.col - (left - (cal.dxLeft >> 1))) / cal.dxLeft;
            int idealX = left + fromLeft * cal.dxLeft;

            if (x.col <= idealX - MAX_CHEQUER_DIST) {
                throw new IllegalStateException("col too far left on pip with horizontal index from left " + fromLeft);
            }

            if (x.col >= idealX + MAX_CHEQUER_DIST) {
                throw new IllegalStateException("col too far right on pip with horizontal index from left" + fromLeft);
            }
            indexHor = fromLeft;

        } else if (inRightHalf) {
            int fromRight = (right + (cal.dxRight >> 1) - x.col) / cal.dxRight;
            int idealX = right - fromRight * cal.dxRight;

            if (x.col <= idealX - MAX_CHEQUER_DIST) {
                throw new IllegalStateException(
                        "col too far left on pip with horizontal index from right " + fromRight);
            }

            if (x.col >= idealX + MAX_CHEQUER_DIST) {
                throw new IllegalStateException(
                        "col too far right on pip with horizontal index from right " + fromRight);
            }

            indexHor = 11 - fromRight;
        } else {
            throw new IllegalStateException("Chequer probably on the bar");
        }

        if (this.inTopHalf(x.row)) {
            if (cal.ownClockwise) {
                return 24 - indexHor;
            } else {
                return 13 + indexHor;
            }
        } else if (this.inBottomHalf(x.row)) {
            if (cal.ownClockwise) {
                return 1 + indexHor;
            } else {
                return 12 - indexHor;
            }
        } else {
            throw new IllegalArgumentException("LocAndVal not in top or bottom half");
        }
    }

    public ArrayList<LocAndVal> filterColumn(
            ArrayList<LocAndVal> allChequers,
            int centerX,
            boolean topHalf) {

        ArrayList<LocAndVal> l = new ArrayList<>();
        // System.out.println("centerX " + centerX + " topHalf " + topHalf);

        for (var c : allChequers) {
            // c.dump("Regarded chequer");
            // boolean inTop = inTopHalf(p.y);
            // boolean inBottom = inBottomHalf(p.y);
            // System.out.println("inTop " + inTop + " inBottom " + inBottom);
            if (c.col > centerX - MAX_CHEQUER_DIST && c.col < centerX + MAX_CHEQUER_DIST
                    && ((topHalf && inTopHalf(c.row)) || (!topHalf && inBottomHalf(c.row)))) {
                l.add(c);
            } else {
                System.out.println("Ignore chequer " + c + "  centerX " + centerX + " topHalf " + topHalf);
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

    // ArrayList<LocAndVal> filterBar(
    // ArrayList<LocAndVal> allChequers) {

    // ArrayList<LocAndVal> l = new ArrayList<>();
    // int centerX = centerXOfBar();
    // // System.out.println("centerX " + centerX + " topHalf " + topHalf);

    // for (LocAndVal loc : allChequers) {
    // // Point p = c.pos;
    // // c.dump("Regarded chequer");
    // // boolean inTop = inTopHalf(p.y);
    // // boolean inBottom = inBottomHalf(p.y);
    // // System.out.println("inTop " + inTop + " inBottom " + inBottom);
    // if (loc.col > centerX - 10 && loc.col < centerX + 10) {
    // l.add(loc);
    // }
    // }
    // return l;

    // }

    private boolean onBar(LocAndVal c) {
        if (c.row <= top - MAX_CHEQUER_DIST) {
            throw new IllegalArgumentException("Chequer above top");
        }
        if (c.row >= bottom + MAX_CHEQUER_DIST) {
            throw new IllegalStateException("Chequer below bottom");
        }
        int centerX = centerXOfBar();
        return c.col > centerX - MAX_CHEQUER_DIST && c.col < centerX + MAX_CHEQUER_DIST;
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

    public int numChequersOnField(int field, double minVal) {
        if (allChequers == null) {
            throw new IllegalStateException("init() not yet called");
        }

        return numChequersOnField(this.allChequers, field, minVal);
    }

    public int numChequersOnField(ArrayList<LocAndVal> allChequers, int field, double minVal) {
        if (field < 1 || field > 24)
            throw new IllegalArgumentException("field " + field);
        ArrayList<LocAndVal> visible = visibleChequersOnField(allChequers, field);
        // if (field == 13) {
        // System.out.println("numChequersOnField for " + field);
        // System.out.println("visible.size " + visible.size());

        // }
        return numChequersOnField(visible, field >= 13, minVal);
    }

    int numChequersOnField(ArrayList<LocAndVal> visibleChequers, boolean topHalf, double minVal) {
        if (visibleChequers.isEmpty())
            return 0;

        int n = visibleChequers.size();
        ArrayList<LocAndVal> sorted = new ArrayList<LocAndVal>(n);
        for (int i = 0; i < n; ++i) {
            var next = visibleChequers.get(i);
            if (next.val >= minVal) {
                sorted.add(next);
            } else {
                System.out.println("leave out loc and val: " + next);
            }
        }
        // ArrayList<LocAndVal> sorted = new ArrayList<LocAndVal>(visibleChequers);
        sorted.sort(LocAndVal.sortRow);
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
    int countLayers(ArrayList<LocAndVal> l, int diffY) {
        System.out.println("countLayers: l is");
        for (var c : l) {
            System.out.println("c: " + c + " - " + c.custom);
        }
        // 5 + 4 + 3 + 2 + 1
        int n = l.size();
        System.out.println("n " + n);
        if (n < 1)
            throw new IllegalArgumentException("l.size() " + n);

        int firstInTopLayer = diffY < 0 ? n - 1 : 0;
        var c = l.get(firstInTopLayer);
        System.out.println("val of first in top layer: " + c.val);
        int isY = c.row;
        int yFirst = diffY < 0 ? bottom : top;
        System.out.println("firstInTopLayer " + firstInTopLayer);
        System.out.println("isY " + isY);
        System.out.println("yFirst " + yFirst);

        for (int layer = 0; layer < 5; ++layer) {
            int shallY = yFirst + (diffY * layer) / 2;
            System.out.println("layer " + layer + " shallY " + shallY);
            if (yEqual(isY, shallY)) {
                return layer + 1;
            }
        }

        throw new IllegalStateException("Counting of layers failed");
    }

    /**
     * falls aufwaerts, diffY = -dy, sonst +dy
     */
    int countVisibleChequersInLayer(ArrayList<LocAndVal> l, int diffY) {
        int n = l.size();
        if (n <= 1)
            return n;

        int idx = diffY < 0 ? n - 1 : 0;
        int dIdx = diffY < 0 ? -1 : 1;
        int endIdx = diffY < 0 ? -1 : n;
        int y = l.get(idx).row;

        int res = 1;

        for (int i = idx + dIdx; i != endIdx; i += dIdx) {
            // dump("i", i);
            var c = l.get(i);
            int nextY = c.row;
            if (yEqual(nextY, y + diffY)) {
                ++res;
                // System.out.println("counted in layer with val " + c.val);
                y = nextY;
            } else {
                break;
            }
        }

        return res;
    }

    static boolean yEqual(int yIs, int yShould) {
        return yIs > yShould - MAX_CHEQUER_DIST && yIs < yShould + MAX_CHEQUER_DIST;
    }

    private boolean isOwnColor(LocAndVal lav) {
        return lav.custom == ownColor();
    }

    private ChequerColor ownColor() {
        return cal.ownWhite ? ChequerColor.WHITE : ChequerColor.BLACK;
    }

    // public int[] chequersOnBars(int[] out) {
    // if (out == null || out.length < 2) {
    // out = new int[2];
    // }
    // ArrayList<LocAndVal> chequersOnBar = filterBar(allChequers);
    // out[0] = out[1] = 0;
    // for (LocAndVal c : chequersOnBar) {
    // if (isOwnColor(c)) {
    // ++out[0];
    // } else {
    // ++out[1];
    // }
    // }

    // return out;
    // }

    public int chequersOffWhite() {
        return bearoffWhite;
    }

    public int chequersOffBlack() {
        return bearoffBlack;
    }

    public void getFields(Field outOwn, Field outOpp, Runnable onError) {
        outOwn.set(this.fieldOwn);
        outOpp.set(this.fieldOpp);

        // outOwn.clear();
        // outOpp.clear();
        // int offOwn = 15, offOpp = 15;
        // int[] barNums = chequersOnBars(null);
        // outOwn.set(25, barNums[0]);
        // offOwn -= barNums[0];
        // System.out.println("barNums[0] " + barNums[0] + " barNums[1] " + barNums[1]);
        // outOpp.set(25, barNums[1]);
        // offOpp -= barNums[1];

        // for (int i = 24; i >= 1; --i) {
        // Player p = playerOnField(i);
        // if (p != Player.NONE) {
        // int chequers = numChequersOnField(i);
        // if (p == Player.OWN) {
        // outOwn.set(i, chequers);
        // System.out.println(chequers + " own on field " + i);
        // offOwn -= chequers;
        // } else {
        // outOpp.set(25 - i, chequers);
        // System.out.println(chequers + " opp on field " + i);
        // offOpp -= chequers;
        // }
        // }
        // }

        // if (offOwn < 0)
        // throw new IllegalStateException("offOwn=" + offOwn);
        // if (offOpp < 0)
        // throw new IllegalStateException("offOpp=" + offOpp);

        // int measuredOffOwn = (cal.ownWhite ? bearoffWhite : bearoffBlack);
        // int measuredOffOpp = (cal.ownWhite ? bearoffBlack : bearoffWhite);

        // if (offOwn != measuredOffOwn || offOpp != measuredOffOpp) {
        // StringBuilder sb = new StringBuilder();
        // System.out.println(sb.append("Unexpected state:
        // offOwn=").append(offOwn).append(", offOpp=").append(offOpp)
        // .append(", bearoffWhite=").append(bearoffWhite).append(",
        // bearoffBlack").append(bearoffBlack));

        // if (onError != null) {
        // onError.run();
        // }

        // }
        // outOwn.set(0, measuredOffOwn);
        // outOpp.set(0, measuredOffOpp);

    }

    public void getFields(Field outOwn, Field outOpp) {
        getFields(outOwn, outOpp, null);
    }
}
