package pr.backgammon.control;

import java.util.concurrent.ThreadLocalRandom;

import pr.backgammon.model.Roll;

public class RandomRoll {
    public static Roll create(Roll r) {
        if (r == null) {
            r = new Roll();
        }
        int x = ThreadLocalRandom.current().nextInt(36);
        r.die1 = x / 6 + 1;
        r.die2 = x % 6 + 1;
        return r;
    }
}
