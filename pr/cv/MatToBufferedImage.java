package pr.cv;


import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;

import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;

public class MatToBufferedImage {

    public static BufferedImage matToBufferedImage(Mat mat) {
        if (mat == null || mat.empty()) {
            throw new IllegalArgumentException("Mat ist null oder leer");
        }
        // int type = mat.type();
        int channels = mat.channels();
        int width = mat.cols();
        int height = mat.rows();

        // Nur 8-Bit-Mats direkt unterstützen
        if (mat.depth() != CvType.CV_8U) {
            // Optional: Konvertiere in 8U (hier einfache Skalierung ohne Normierung)
            Mat tmp = new Mat();
            mat.convertTo(tmp, CvType.CV_8U);
            mat = tmp;
            channels = mat.channels();
        }

        BufferedImage img;
        byte[] data = new byte[width * height * channels];
        mat.get(0, 0, data);

        switch (channels) {
            case 1: // GRAY (CV_8UC1)
                img = new BufferedImage(width, height, BufferedImage.TYPE_BYTE_GRAY);
                img.getRaster().setDataElements(0, 0, width, height, data);
                return img;

            case 3: // BGR (CV_8UC3) -> TYPE_3BYTE_BGR passt direkt
                img = new BufferedImage(width, height, BufferedImage.TYPE_3BYTE_BGR);
                byte[] target3 = ((DataBufferByte) img.getRaster().getDataBuffer()).getData();
                System.arraycopy(data, 0, target3, 0, data.length);
                return img;

            case 4: // BGRA (CV_8UC4) -> TYPE_4BYTE_ABGR (Kanäle tauschen)
                img = new BufferedImage(width, height, BufferedImage.TYPE_4BYTE_ABGR);
                byte[] target4 = ((DataBufferByte) img.getRaster().getDataBuffer()).getData();
                // Konvertiere BGRA -> ABGR pro Pixel
                for (int i = 0; i < width * height; i++) {
                    int si = i * 4;
                    int di = i * 4;
                    byte b = data[si];
                    byte g = data[si + 1];
                    byte r = data[si + 2];
                    byte a = data[si + 3];
                    target4[di]     = a; // A
                    target4[di + 1] = b; // B
                    target4[di + 2] = g; // G
                    target4[di + 3] = r; // R
                }
                return img;

            default:
                throw new IllegalArgumentException("Nicht unterstützte Kanalanzahl: " + channels);
        }
    }

    // Beispielnutzung
    public static void main(String[] args) {
        System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
        // Mat mat = ... dein Mat (BGR/GRAY/BGRA)
        // BufferedImage img = matToBufferedImage(mat);
    }
}
