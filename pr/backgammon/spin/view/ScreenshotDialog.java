package pr.backgammon.spin.view;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.Window;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileOutputStream;

import javax.imageio.ImageIO;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;

public class ScreenshotDialog extends JDialog {
    SavePanel savePanel = null;
    JPanel img;

    public ScreenshotDialog(Window owner, BufferedImage shot) {
        super(owner, "Screenshot", ModalityType.DOCUMENT_MODAL);
        Rectangle drag = new Rectangle(10, 10, 10, 10);
        setLayout(new BorderLayout(10, 10));

        img = new JPanel() {
            protected void paintComponent(java.awt.Graphics g1) {
                Graphics2D g = (Graphics2D) g1;
                g.drawRenderedImage(shot, null);
                g.setColor(Color.WHITE);
                g.setXORMode(Color.BLACK);
                g.drawRect(drag.x, drag.y, drag.width, drag.height);
            }
        };
        img.setPreferredSize(new Dimension(shot.getWidth(), shot.getHeight()));
        add(new JScrollPane(img), BorderLayout.CENTER);
        add(new SavePanel(new SavePanel.Cb() {
            @Override
            public void enter(String name) {
                // save screenshot
                try {
                    new File("screenshots").mkdir();
                    ImageIO.write(shot.getSubimage(drag.x, drag.y, drag.width, drag.height), "png",
                            new FileOutputStream("./screenshots/" + name + ".png"));

                    ScreenshotDialog.this.setVisible(false);
                    ScreenshotDialog.this.dispose();
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }

            @Override
            public void escape() {
                // removeSavePanel();
            }

            // private void removeSavePanel() {
            // Container parent = savePanel.getParent();
            // parent.remove(savePanel);
            // parent.add(img);
            // parent.revalidate();
            // parent.repaint();

            // }
        }), BorderLayout.SOUTH);

        img.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                System.out.println("mousePressed");
                drag.x = e.getX();
                drag.y = e.getY();
                drag.width = 0;
                drag.height = 0;
                img.repaint();
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                // Container parent = img.getParent();
                // parent.remove(img);
                // savePanel = new SavePanel(new SavePanel.Cb() {
                // @Override
                // public void enter(String name) {
                // // save screenshot
                // ScreenshotDialog.this.setVisible(false);
                // ScreenshotDialog.this.dispose();
                // }

                // @Override
                // public void escape() {
                // removeSavePanel();
                // }

                // private void removeSavePanel() {
                // Container parent = savePanel.getParent();
                // parent.remove(savePanel);
                // parent.add(img);
                // parent.revalidate();
                // parent.repaint();

                // }
                // });
                // parent.add(savePanel);
                // parent.repaint();
                // savePanel.updateFocus();
            }
        });
        img.addMouseMotionListener(new MouseMotionAdapter() {
            @Override
            public void mouseDragged(MouseEvent e) {
                System.out.println("mouseDragged");
                drag.width = e.getX() - drag.x;
                drag.height = e.getY() - drag.y;
                img.repaint();
            }
        });
    }
}

class SavePanel extends JPanel {
    interface Cb {
        void enter(String name);

        void escape();
    }

    final JTextField name;

    SavePanel(final Cb cb) {
        add(new JLabel("Dateiname (ohne Pfad Endung) - wird in ./screenshots abgelegt"));
        add(name = new JTextField(20));
        name.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                switch (e.getKeyCode()) {
                    case KeyEvent.VK_ENTER:
                        cb.enter(name.getText());
                        break;
                    case KeyEvent.VK_ESCAPE:
                        cb.escape();
                        break;
                }
            }
        });
    }

    void updateFocus() {
        name.requestFocusInWindow();
    }
}