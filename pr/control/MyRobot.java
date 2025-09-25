package pr.control;

import java.awt.Rectangle;
import java.awt.event.InputEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.TreeSet;
import java.util.concurrent.ThreadLocalRandom;

public class MyRobot {
    // private static final ExecutorService executor =
    // Executors.newSingleThreadExecutor();
    private static final java.awt.Robot r = createRobot();

    private static java.awt.Robot createRobot() {
        try {

            return new java.awt.Robot();
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }

    public static void move(int x, int y) {
        synchronized (r) {
            r.mouseMove(x, y);
        }
    }

    public static void press() {
        synchronized (r) {
            r.mousePress(InputEvent.BUTTON1_DOWN_MASK);
        }
    }

    public static void release() {
        synchronized (r) {
            r.mouseRelease(InputEvent.BUTTON1_DOWN_MASK);
        }
    }

    public static void click(Rectangle randomRect) throws InterruptedException {
        click(randomRect.x, randomRect.y, randomRect.width, randomRect.height);
    }

    public static void click(int x, int y, int w, int h) throws InterruptedException {
        w = Math.max(1, w);
        h = Math.max(1, h);
        int pos = ThreadLocalRandom.current().nextInt(w * h);
        int x1 = x + pos % w;
        int y1 = y + pos / w;
        move(x1, y1);
        Thread.sleep(100);
        press();
        Thread.sleep(100);
        release();
    }

    public static void keyPress(int code) {
        synchronized (r) {
            r.keyPress(code);
        }
    }

    public static void keyRelease(int code) {
        synchronized (r) {
            r.keyRelease(code);
        }
    }

    private static TreeSet<Integer> nonFakeShots = createNonFakeShots();
    private static int nextShot = 0;

    private static TreeSet<Integer> createNonFakeShots() {
        TreeSet<Integer> res = new TreeSet<>();
        res.add(0);
        return res;
    }

    public static BufferedImage shot(Rectangle rect) {
        // if (!nonFakeShots.contains(nextShot++)) {
        //     return fakeShot(rect);
        // }

        synchronized (r) {
            return r.createScreenCapture(rect);
        }
    }

    // fake implementation for testing
    public static BufferedImage fakeShot(Rectangle rect) {
        try {
            var img = Tools.loadImg(new File("screenshots/bug_2025-09-17.png"));
            System.out.println("img.width " + img.getWidth() + "   img.height " + img.getHeight());
            System.out.println("rect " + rect);
            return img.getSubimage(rect.x, rect.y, rect.width, rect.height);

            // synchronized (r) {
            // return r.createScreenCapture(rect);
            // }
        } catch (Exception ex) {
            ex.printStackTrace();
            return null;
        }

    }

    // bringt nichts - einfacher einfach synchronisierte statische Funktionen
    // anzubieten und ggf.
    // notwendige verzoegerungen individuell mit Thread.sleep() zu implementieren:
    // public class MoveTask {
    // private int x, y;
    // private Runnable runnable = new Runnable() {
    // @Override
    // public final void run() {
    // r.mouseMove(x, y);
    // }
    // };

    // public MoveTask() {
    // x = 0;
    // y = 0;
    // }

    // public void run(int x, int y) throws InterruptedException, ExecutionException
    // {
    // this.x = x;
    // this.y = y;
    // executor.submit(runnable).get();
    // }

    // }
}
