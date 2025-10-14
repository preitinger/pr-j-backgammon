package pr.backgammon.spin.templatesearchers;

import java.io.IOException;

import org.opencv.core.Mat;

public class ChequerWhite extends Chequer {
    private final Mat result = new Mat();

    public ChequerWhite() throws IOException {
        super("chequerWhite");
    }
}
