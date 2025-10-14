package pr.backgammon.spin.templatesearchers;

import java.io.IOException;

import org.opencv.core.Mat;

public class ChequerBlack extends Chequer {
    private final Mat result = new Mat();

    public ChequerBlack() throws IOException {
        super("chequerBlack");
    }
}
