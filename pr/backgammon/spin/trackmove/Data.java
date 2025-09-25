package pr.backgammon.spin.trackmove;

import java.awt.image.BufferedImage;

import pr.backgammon.model.Match;

public class Data {
    public BufferedImage shot;
    public final Match match = new Match();

    public Data() {
    }

    public Data(BufferedImage shot, Match match) {
        this.match.set(match);
    }
}
