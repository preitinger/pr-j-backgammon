package pr.control;

import java.awt.Point;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.awt.image.Raster;
import java.io.IOException;

/**
 * Enthaelt ein Teilbild.
 * Kann dieses Teilbild in immer wieder neuen Bildern suchen
 */
public class Searcher {
    private final Raster toSearch;
    private final Rectangle clipToSearch, clipToSearchIn;
    private final int maxPixelDiff;
    private Point result = new Point();

    public Searcher(Raster toSearch, Rectangle clipToSearch, Rectangle clipToSearchIn, int maxPixelDiff) {
        this.toSearch = toSearch;
        this.clipToSearch = clipToSearch;
        this.clipToSearchIn = clipToSearchIn;
        this.maxPixelDiff = maxPixelDiff;
    }

    public static Searcher create(String fileName, Rectangle clipToSearch, Rectangle clipToSearchIn, int maxPixelDiff) throws IOException {
        BufferedImage img = Tools.loadImg("pr/res/" + fileName + ".png");
        return new Searcher(img.getRaster(), clipToSearch, clipToSearchIn, maxPixelDiff);
    }

    public Point run(Raster toSearchIn) {
        return (result = Tools.searchImage(toSearchIn, clipToSearchIn, toSearch, clipToSearch, maxPixelDiff, result));
    }

    public Point run(Raster toSearchIn, Rectangle clipToSearchIn) {
        return (result = Tools.searchImage(toSearchIn, clipToSearchIn, toSearch, clipToSearch, maxPixelDiff, result));
    }

    public boolean runAndClick(Raster toSearchIn, int rasterX, int rasterY) throws InterruptedException {
        Point pos = run(toSearchIn);
        if (pos == null) return false;
        doClick(rasterX, rasterY, pos);
        return true;
    }

    public boolean runAndClick(Raster toSearchIn, Rectangle clipToSearchIn, int rasterX, int rasterY) throws InterruptedException {
        Point pos = run(toSearchIn, clipToSearchIn);
        if (pos == null) return false;
        doClick(rasterX, rasterY, pos);
        return true;
    }

    private void doClick(int rasterX, int rasterY, Point pos) throws InterruptedException {
        if (pos != null) {
            int x = rasterX + pos.x;
            int y = rasterY + pos.y;
            int w, h;
            if (clipToSearch == null) {
                w = toSearch.getWidth();
                h = toSearch.getHeight();
            } else {
                w = clipToSearch.width;
                h = clipToSearch.height;
            }
            MyRobot.click(x, y, w, h);
        }
    }
}
