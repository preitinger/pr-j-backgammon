package pr.control;

import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.awt.image.Raster;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Map;
import java.util.TreeMap;

import pr.model.HistogramEntry;

public class Histogram {

    public static final Comparator<HistogramEntry> COMPARATOR_NUM = new Comparator<HistogramEntry>() {
        @Override
        public int compare(HistogramEntry arg0, HistogramEntry arg1) {
            return arg1.num - arg0.num;
        }
    };
    public static final Comparator<HistogramEntry> COMPARATOR_COLOR = new Comparator<HistogramEntry>() {
        @Override
        public int compare(HistogramEntry arg0, HistogramEntry arg1) {
            return arg1.color - arg0.color;
        }
    };

    public static void dump(Iterable<pr.model.HistogramEntry> i) {
        for (pr.model.HistogramEntry e : i) {
            HistogramEntry.ln("color: " + e.color + "   num: " + e.num);
        }
    }

    public static ArrayList<HistogramEntry> createHistogramm(BufferedImage img, Rectangle r) {
        TreeMap<Integer, Integer> m = new TreeMap<Integer, Integer>();
        int bottom = r.y + r.height;
        int right = r.x + r.width;
        Raster raster = img.getRaster();
        int[] pixel = null;
    
        for (int y = r.y; y < bottom; ++y) {
            for (int x = r.x; x < right; ++x) {
                pixel = raster.getPixel(x, y, pixel);
                Integer old = m.get(pixel[0]);
                m.put(pixel[0], old == null ? 1 : old + 1);
            }
        }
    
        ArrayList<HistogramEntry> l = new ArrayList<>(256);
    
        for (Map.Entry<Integer, Integer> x : m.entrySet()) {
            l.add(new HistogramEntry(x.getKey(), x.getValue()));
        }
    
        l.sort(COMPARATOR_COLOR);
        return l;
    }

}
