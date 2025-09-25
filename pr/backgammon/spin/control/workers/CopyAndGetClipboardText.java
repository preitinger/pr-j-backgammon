package pr.backgammon.spin.control.workers;

import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.DataFlavor;
import java.awt.event.KeyEvent;

import pr.control.MyRobot;
import pr.control.MyWorker;

public abstract class CopyAndGetClipboardText extends MyWorker<String, Void> {
    public static String runIt() throws InterruptedException {
        MyRobot.keyPress(KeyEvent.VK_CONTROL);
        MyRobot.keyPress(KeyEvent.VK_C);
        sleep();
        MyRobot.keyRelease(KeyEvent.VK_C);
        MyRobot.keyRelease(KeyEvent.VK_CONTROL);
        Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
        if (clipboard.isDataFlavorAvailable(DataFlavor.stringFlavor)) {
            try {
                return (String) clipboard.getData(DataFlavor.stringFlavor);
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
        return null;

    }

    @Override
    public String doIt() throws Exception {
        return runIt();
    }

    private static void sleep() throws InterruptedException {
        Thread.sleep(100);
    }
}
