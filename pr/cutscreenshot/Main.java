package pr.cutscreenshot;

import java.awt.BorderLayout;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JList;
import javax.swing.JScrollPane;
import javax.swing.SwingUtilities;

import pr.backgammon.spin.view.ScreenshotDialog;
import pr.control.Tools;

public class Main extends JFrame {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            var main = new Main();
            main.setVisible(true);
        });

    }

    private BufferedImage shot = null;

    public Main() {
        super("Cut Screenshot");
        setDefaultCloseOperation(EXIT_ON_CLOSE);

        setLayout(new BorderLayout());
        File dir = new File("screenshots");
        String[] files = dir.list();
        System.out.println("files.length" + files.length);
        JList<String> list = new JList<String>(files);
        JDialog dlg = new JDialog(this);
        dlg.setLayout(new BorderLayout());
        dlg.add(new JScrollPane(list), BorderLayout.CENTER);
        list.addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                int sel = list.getSelectedIndex();
                if (sel != -1) {
                    try {
                        shot = Tools.loadImg(new File(dir, files[sel]));
                        dlg.setVisible(false);
                        dlg.dispose();
                        ScreenshotDialog screenshotDialog = new ScreenshotDialog(Main.this, shot);
                        screenshotDialog.pack();
                        screenshotDialog.setVisible(true);
                        // Main.this.add(new JLabel(new ImageIcon(shot)), BorderLayout.CENTER);
                        Main.this.validate();
                        Main.this.repaint();
                    } catch (IOException ex) {
                        ex.printStackTrace();
                    }
                }
            }
        });
        dlg.setSize(400, 400);
        dlg.setVisible(true);
        setSize(400, 1000);
    }
}
