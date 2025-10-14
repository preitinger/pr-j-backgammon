package pr;

import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;

import javax.swing.JPanel;

public class ImgPanel extends JPanel {
    private BufferedImage img = null;
    private final Dimension prefSize = new Dimension(0, 0);

    public ImgPanel(BufferedImage img) {
        this.img = img;
    }

    @Override
    protected void paintComponent(Graphics g1) {
        Graphics2D g = (Graphics2D) g1;
        // g.drawRenderedImage(img, null);

        if (img != null) {
            g.drawImage(img, 0, 0, img.getWidth(), img.getHeight(), null);
        }
    }

    public void setImg(BufferedImage img) {
        this.img = img;
        prefSize.width = img.getWidth();
        prefSize.height = img.getHeight();
        setPreferredSize(prefSize);
        repaint();
    }

    public BufferedImage getImg() {
        return img;
    }
}
