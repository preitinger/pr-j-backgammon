package pr.backgammon.spin.templatesearchers;

import java.io.IOException;

import org.opencv.core.Mat;

public class ChequerWhiteStar extends Chequer {
    private final Mat result = new Mat();

    public ChequerWhiteStar() throws IOException {
        super("chequerWhiteStar");
    }
}
