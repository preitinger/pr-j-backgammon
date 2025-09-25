package pr.control;

import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.MouseInfo;
import java.awt.Point;
import java.awt.PointerInfo;
import java.awt.Rectangle;
import java.awt.Robot;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.awt.image.BufferedImage;
import java.awt.image.Raster;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

import javax.imageio.ImageIO;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.Timer;

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

    public static final BufferedImage loadImg(String resourcePath) throws IOException {
        return ImageIO.read(readResourceFile(resourcePath));
    }

    /**
     * @param resourcePath - for example "pr/res/image.png"
     */
    public static final InputStream readResourceFile(String resourcePath) throws IOException {
        return Tools.class.getClassLoader().getResourceAsStream(resourcePath);
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
                if (pixel[0] >= minColor && pixel[0] <= maxColor)
                    ++n;
            }
        }

        return n;
    }

    public static void appendSpaces(StringBuilder sb, int n) {
        for (int i = 0; i < n; ++i) {
            sb.append(' ');
        }
    }

    public static interface ScreenshotRectFromMouseCallback {
        void countdown(int count);

        void done(BufferedImage screenshot);
    }

    private static class ScreenshotRectFromMouseTimerListener implements ActionListener {
        final Robot r;
        // private final int count0;
        private final int count1;

        Timer timer;
        int step = 0;
        int count = 3;
        int x, y, w, h;
        private final ScreenshotRectFromMouseCallback cb;

        ScreenshotRectFromMouseTimerListener(Robot r, ScreenshotRectFromMouseCallback cb, int count0, int count1) {
            this.r = r;
            this.cb = cb;
            // this.count0 = count0;
            this.count1 = count1;
            this.count = count0;
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            switch (step) {
                case 0: {
                    if (--count > 0) {
                        cb.countdown(count);
                    } else {
                        PointerInfo pi = MouseInfo.getPointerInfo();
                        Point mousePos = pi.getLocation();
                        x = mousePos.x;
                        y = mousePos.y;
                        step = 1;
                        count = count1;
                        cb.countdown(count);
                    }
                    break;
                }
                case 1: {
                    if (--count > 0) {
                        cb.countdown(count);
                    } else {
                        PointerInfo pi = MouseInfo.getPointerInfo();
                        Point mousePos = pi.getLocation();
                        w = mousePos.x - x;
                        h = mousePos.y - y;

                        cb.done(r.createScreenCapture(new Rectangle(x, y, w, h)));
                        timer.stop();
                    }
                    break;
                }
                case 2: {

                }

            }
        }
    }

    public static void screenshotRectFromMouse(Robot r, ScreenshotRectFromMouseCallback cb) {
        int count0 = 3, count1 = 3;
        ScreenshotRectFromMouseTimerListener listener = new ScreenshotRectFromMouseTimerListener(r, cb, count0, count1);
        final Timer t = new Timer(1000, listener);
        listener.timer = t;
        cb.countdown(count0);
        t.start();
    }

    /**
     * If `result` is null a new Point object is created, otherwise `result` is used
     * as return value.
     * Then, searches the image `sub` in `container` line by line (left to right,
     * then top to bottom).
     * If it is found, the location relative to the origin of the whole raster
     * {@code container} is returned.
     */
    public static Point searchImage(Raster container, Rectangle clipContainer, Raster sub, Rectangle clipSub,
            int maxPixelDiff, Point result) {
        // System.out.println("clipContainer: " + clipContainer);
        // System.out.println("clipSub: " + clipSub);

        // Folgendes muss jetzt nicht mehr erfuellt sein, weil es korrigiert wird in sw, sh, cw, ch.
        // if (clipContainer != null
        //         && (clipContainer.x < 0 || clipContainer.x + clipContainer.width > container.getWidth())) {
        //     throw new IllegalArgumentException(
        //             "clipContainer=" + clipContainer + ", container.getWidth()=" + container.getWidth());
        // }
        // if (clipContainer != null
        //         && (clipContainer.y < 0 || clipContainer.y + clipContainer.height > container.getHeight())) {
        //     throw new IllegalArgumentException(
        //             "clipContainer=" + clipContainer + ", container.getHeight()=" + container.getHeight());
        // }

        int sw = clipSub == null ? sub.getWidth() : Math.min(clipSub.width, sub.getWidth() - clipSub.x);
        int sh = clipSub == null ? sub.getHeight() : Math.min(clipSub.height, sub.getHeight() - clipSub.y);
        int cw = clipContainer == null ? container.getWidth() : Math.min(clipContainer.width, container.getWidth() - clipContainer.x);
        int ch = clipContainer == null ? container.getHeight() : Math.min(clipContainer.height, container.getHeight() - clipContainer.y);
        if (sw <= 0 || sh <= 0 || cw <= 0 || ch <= 0)
            return null;
        if (sw > cw || sh > ch)
            return null;

        int cminY = clipContainer == null ? 0 : clipContainer.y;
        int cminX = clipContainer == null ? 0 : clipContainer.x;
        int cmaxY = cminY + ch - sh;
        int cmaxX = cminX + cw - sw;
        int sminY = clipSub == null ? 0 : clipSub.y;
        int sminX = clipSub == null ? 0 : clipSub.x;
        int smaxX = sminX + sw - 1;
        int smaxY = sminY + sh - 1;

        // dump("cminY", cminY);
        // dump("cminX", cminX);
        // dump("cmaxY", cmaxY);
        // dump("cmaxX", cmaxX);
        // dump("sminY", sminY);
        // dump("sminX", sminX);
        // dump("smaxY", smaxY);
        // dump("smaxX", smaxX);

        if (result == null) {
            result = new Point();
        }

        int[] cp = null, sp = null;

        // maximal moeglicher wert fuer cx: cmaxX + (smaxX - sminX) == cmaxX + sw - 1 ==
        // cminX + cw - sw + sw - 1 == cminX + cw - 1

        for (int y = cminY; y <= cmaxY; ++y) {
            for (int x = cminX; x <= cmaxX; ++x) {
                boolean notYetDifferent = true;
                for (int sy = sminY, cy = y; sy <= smaxY && notYetDifferent; ++sy, ++cy) {
                    for (int sx = sminX, cx = x; sx <= smaxX && notYetDifferent; ++sx, ++cx) {
                        try {
                            cp = container.getPixel(cx, cy, cp);
                            sp = sub.getPixel(sx, sy, sp);
                        } catch (Exception ex) {
                            dump("cminY", cminY);
                            dump("cminX", cminX);
                            dump("cmaxY", cmaxY);
                            dump("cmaxX", cmaxX);
                            dump("sminY", sminY);
                            dump("sminX", sminX);
                            dump("smaxY", smaxY);
                            dump("smaxX", smaxX);
                            dump("x", x);
                            dump("y", y);
                            dump("sx", sx);
                            dump("sy", sy);
                            dump("cx", cx);
                            dump("cy", cy);
                            throw new RuntimeException(ex);
                        }

                        if (!pixelsSimilar(cp, sp, maxPixelDiff)) {
                            notYetDifferent = false;
                        }
                    }
                }

                if (notYetDifferent) {
                    // found
                    result.x = x;
                    result.y = y;
                    return result;
                }
            }
        }

        // not found
        return null;
    }

    private static void dump(String string, int x) {
        System.out.println(string + ": " + x);
    }

    public static boolean pixelsSimilar(int[] a, int[] b, int diff) {
        return (Math.abs(a[0] - b[0]) <= diff &&
                Math.abs(a[1] - b[1]) <= diff &&
                Math.abs(a[2] - b[2]) <= diff);
    }

    public static String dateTimeString() {
        return DateTimeFormatter.ofPattern("yyyy-MM-dd_HH-mm-ss").format(ZonedDateTime.now());
    }
}
