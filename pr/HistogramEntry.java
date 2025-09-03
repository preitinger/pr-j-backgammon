package pr;

import java.util.Comparator;

public class HistogramEntry {
    public int color;
    public int num;

    public HistogramEntry(int color, int num) {
        this.color = color;
        this.num = num;
    }

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

    public static void dump(Iterable<HistogramEntry> i) {
        for (HistogramEntry e : i) {
            ln("color: " + e.color + "   num: " + e.num);
        }
    }

    static void ln(String line) {
        System.out.println(line);
    }
}
