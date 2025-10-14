package pr.cv;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;

import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.imgproc.Imgproc;

public class BufferedImageToMat {

    // Wandelt ein beliebiges BufferedImage in ein BGR-Mat (CV_8UC3) um
    public static Mat toBgrMat(BufferedImage bi) {
        if (bi == null)
            throw new IllegalArgumentException("BufferedImage ist null");

        // Auf gängige Typen normalisieren, damit wir ein Byte-Array bekommen
        BufferedImage work = normalizeToSupportedType(bi);

        byte[] pixels = ((DataBufferByte) work.getRaster().getDataBuffer()).getData();
        int width = work.getWidth();
        int height = work.getHeight();

        switch (work.getType()) {
            case BufferedImage.TYPE_3BYTE_BGR: {
                Mat bgr = new Mat(height, width, CvType.CV_8UC3);
                bgr.put(0, 0, pixels); // direkte Kopie, Reihenfolge passt (B,G,R)
                return bgr;
            }
            case BufferedImage.TYPE_4BYTE_ABGR: {
                // Zuerst als BGRA in Mat, dann nach BGR konvertieren
                Mat bgra = new Mat(height, width, CvType.CV_8UC4);
                bgra.put(0, 0, pixels);
                Mat bgr = new Mat();
                Imgproc.cvtColor(bgra, bgr, Imgproc.COLOR_BGRA2BGR);
                bgra.release();
                return bgr;
            }
            case BufferedImage.TYPE_BYTE_GRAY: {
                // Gray -> BGR (3 Kanäle)
                Mat gray = new Mat(height, width, CvType.CV_8UC1);
                gray.put(0, 0, pixels);
                Mat bgr = new Mat();
                Imgproc.cvtColor(gray, bgr, Imgproc.COLOR_GRAY2BGR);
                gray.release();
                return bgr;
            }
            default:
                // Sollte durch normalizeToSupportedType nicht passieren
                throw new IllegalArgumentException("Nicht unterstützter Bildtyp: " + work.getType());
        }
    }

    public static Mat toMat(BufferedImage bi, Mat reusable) {
        // Sicherstellen: TYPE_3BYTE_BGR ist am einfachsten
        if (bi.getType() != BufferedImage.TYPE_3BYTE_BGR) {
            BufferedImage tmp = new BufferedImage(bi.getWidth(), bi.getHeight(), BufferedImage.TYPE_3BYTE_BGR);
            tmp.getGraphics().drawImage(bi, 0, 0, null);
            bi = tmp;
        }
        byte[] data = ((DataBufferByte) bi.getRaster().getDataBuffer()).getData();
        if (reusable == null) {
            reusable = new Mat(bi.getHeight(), bi.getWidth(), CvType.CV_8UC3);
        } else if (reusable.empty() ||
                reusable.rows() != bi.getHeight() || reusable.cols() != bi.getWidth()
                || reusable.type() != CvType.CV_8UC3) {
            reusable.create(bi.getHeight(), bi.getWidth(), CvType.CV_8UC3);
        }
        reusable.put(0, 0, data);
        // reusable.data().put(data);
        return reusable;
    }

    private static BufferedImage normalizeToSupportedType(BufferedImage bi) {
        int t = bi.getType();
        if (t == BufferedImage.TYPE_3BYTE_BGR ||
                t == BufferedImage.TYPE_4BYTE_ABGR ||
                t == BufferedImage.TYPE_BYTE_GRAY) {
            return bi;
        }
        // In 3BYTE_BGR rendern (vermeidet komplizierte Konvertierungen)
        BufferedImage out = new BufferedImage(bi.getWidth(), bi.getHeight(), BufferedImage.TYPE_3BYTE_BGR);
        Graphics2D g = out.createGraphics();
        g.drawImage(bi, 0, 0, null);
        g.dispose();
        return out;
    }
}
