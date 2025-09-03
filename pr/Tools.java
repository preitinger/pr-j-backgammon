package pr;

import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.awt.image.Raster;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Map;
import java.util.TreeMap;

import javax.imageio.ImageIO;
import javax.swing.JFrame;
import javax.swing.JPanel;

public class Tools {

    public static final BufferedImage grayImg(BufferedImage img) {
        int w = img.getWidth();
        int h = img.getHeight();
        Raster ra = img.getRaster();
        BufferedImage resImg = new BufferedImage(w, h, BufferedImage.TYPE_BYTE_GRAY);
        // WritableRaster ra2 = ra.createCompatibleWritableRaster();
        int radius = 7;
        int[] pixel = new int[3];
        int n = radius + radius + 1;
        n *= n;
        int[] grayPixel = new int[1];
        for (int i = radius; i < h - radius; ++i) {
            for (int j = radius; j < w - radius; ++j) {
                ra.getPixel(j, i, pixel);
                int gray = (pixel[0] + pixel[1] + pixel[2]) / 3;
                // res1[0] = res1[1] = res1[2] = gray;
                grayPixel[0] = gray;
                resImg.getRaster().setPixel(j, i, grayPixel);
                // ra2.setPixel(j, i, res1);
            }
        }

        // return new BufferedImage(img.getColorModel(), ra2,
        // img.isAlphaPremultiplied(), null);
        return resImg;
    }

    public static final BufferedImage loadImg(File f) throws IOException {
        return ImageIO.read(f);
    }

    public static final void showImg(String label, BufferedImage img, boolean wait) throws InterruptedException {
        final boolean[] closed = { false };
        JFrame f = new JFrame(label);
        f.setContentPane(new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g;
                g2.drawImage(img, null, null);
            }
        });
        f.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        f.setPreferredSize(new Dimension(1000, 800));
        f.pack();
        f.setVisible(true);
        if (wait) {
            f.addWindowListener(new WindowListener() {

                @Override
                public void windowActivated(WindowEvent e) {
                }

                @Override
                public void windowClosed(WindowEvent e) {
                    synchronized (closed) {
                        closed[0] = true;
                        closed.notifyAll();
                    }
                }

                @Override
                public void windowClosing(WindowEvent e) {
                }

                @Override
                public void windowDeactivated(WindowEvent e) {
                }

                @Override
                public void windowDeiconified(WindowEvent e) {
                }

                @Override
                public void windowIconified(WindowEvent e) {
                }

                @Override
                public void windowOpened(WindowEvent e) {
                }

            });
        }

        if (wait) {
            synchronized (closed) {
                while (!closed[0]) {
                    closed.wait();
                }
            }
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

        l.sort(HistogramEntry.COMPARATOR_COLOR);
        return l;
    }
 
    public static int countColorRange(Raster raster, Rectangle r, int minColor, int maxColor) {
        int n = 0;

        int left = r.x;
        int right = left + r.width;
        int top = r.y;
        int bottom = top + r.height;
        int[] pixel = null;

        for (int y = top; y < bottom; ++y) {
            for (int x = left; x < right; ++x) {
                pixel = raster.getPixel(x, y, pixel);
                if (pixel[0] >= minColor && pixel[0] <= maxColor) ++n;
            }
        }

        return n;
    }

    public static void appendSpaces(StringBuilder sb, int n) {
        for (int i = 0; i < n; ++i) {
            sb.append(' ');
        }
    }
   
}
