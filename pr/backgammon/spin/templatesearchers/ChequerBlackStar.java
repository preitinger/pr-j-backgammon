package pr.backgammon.spin.templatesearchers;

import java.io.IOException;

import org.opencv.core.Mat;

public class ChequerBlackStar extends Chequer {
    private final Mat result = new Mat();

    public ChequerBlackStar() throws IOException {
        super("chequerBlackStar");
    }
}
