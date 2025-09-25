package pr.model;

public class HistogramEntry {
    public int color;
    public int num;

    public HistogramEntry(int color, int num) {
        this.color = color;
        this.num = num;
    }

    public static void ln(String line) {
        System.out.println(line);
    }
}
