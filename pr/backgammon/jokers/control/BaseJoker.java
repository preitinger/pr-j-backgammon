package pr.backgammon.jokers.control;

import pr.backgammon.jokers.model.Joker;
import pr.backgammon.model.Match;

public abstract class BaseJoker {

    public static enum CheckResult {
        POSITIVE,
        NEGATIVE,
        NONE,
    }

    public abstract String getName();

    public abstract String getDesc();

    public abstract double getProb();

    public final void init(Joker joker) {
        joker.name = getName();
        joker.desc = getDesc();
        joker.prob = getProb();
    }

    /**
     * Check, if {@code m} is is a joker situation. If so, increment out.pos, if the
     * roll is positive, or out.neg, if the roll is negative.
     */
    public CheckResult evaluate(Match m, Joker out) {
        var res = check(m);
        switch (res) {
            case POSITIVE:
                ++out.pos;
                break;
            case NEGATIVE:
                ++out.neg;
                break;
            default:
                // do nothing
        }

        return res;
    }

    public abstract CheckResult check(Match m);
}
