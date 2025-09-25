package pr.backgammon.spin.control.workers;

import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.event.KeyEvent;
import java.io.IOException;

import pr.control.MyRobot;
import pr.control.MyWorker;

public abstract class ChatTextViaClipboard extends MyWorker<String, Void> {
    // private final Rectangle range;
    private final int delayMs;

    public ChatTextViaClipboard(int delayMs) {
        // this.range = range;
        this.delayMs = delayMs;
    }

    public static String runIt(int delayMs) throws InterruptedException, UnsupportedFlavorException, IOException {
        Thread.sleep(delayMs);

        // move(range.x, range.y);
        // sleep();
        // press();
        // sleep();
        // release();
        // sleep();
        // press();
        // sleep();
        // move(range.x + range.width, range.y + range.height);
        // Thread.sleep(1000); // laenger warten, damit zeit um runter zu scrollen
        // release();
        
        {
            // 1390, 1005
            MyRobot.move(1390, 1005);
            sleep();
            MyRobot.press();
            sleep();
            MyRobot.release();
            sleep();
            MyRobot.press();
            sleep();
            MyRobot.move(1306, 997);
            Thread.sleep(1000);
            MyRobot.release();

        }

        sleep();
        MyRobot.keyPress(KeyEvent.VK_CONTROL);
        sleep();
        MyRobot.keyPress(KeyEvent.VK_C);
        sleep();
        MyRobot.keyRelease(KeyEvent.VK_C);
        sleep();
        MyRobot.keyRelease(KeyEvent.VK_CONTROL);
        sleep();
        MyRobot.move(0, 0);
        sleep();
        Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
        if (clipboard.isDataFlavorAvailable(DataFlavor.stringFlavor)) {
            return (String) clipboard.getData(DataFlavor.stringFlavor);
        }
        return null;

    }

    public String doIt() throws InterruptedException, UnsupportedFlavorException, IOException {
        return runIt(this.delayMs);
    }

    // private void press() {
    //     r.mousePress(InputEvent.BUTTON1_DOWN_MASK);
    // }

    // private void release() {
    //     r.mouseRelease(InputEvent.BUTTON1_DOWN_MASK);
    // }

    // private void move(int x, int y) {
    //     r.mouseMove(x, y);
    // }

    private static void sleep() throws InterruptedException {
        Thread.sleep(100);
        // Thread.sleep(2000);
    }
}
