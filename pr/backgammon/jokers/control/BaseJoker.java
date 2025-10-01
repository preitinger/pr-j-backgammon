package pr.backgammon.jokers.control;

import pr.backgammon.jokers.model.Joker;
import pr.backgammon.model.Match;
import pr.model.MutableArray;
import pr.model.MutableIntArray;

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
    public CheckResult evaluate(Match m, Joker out, MutableArray<MutableIntArray> tmp) {
        var res = check(m, tmp);
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

    public abstract CheckResult check(Match m, MutableArray<MutableIntArray> tmp);

    /**
     * detects if the non-active player has a prime between shotSrc and shotDest.
     * The positions shotSrc and shotDest are from the perspective of the active
     * player
     * who wants to cross the opponent's prime between th
     */
    protected final boolean brokenPrimeBetween(Match m, int shotSrc, int shotDest, int minNumPoints) {
        if (shotSrc <= shotDest) {
            throw new IllegalArgumentException();
        }

        var fieldOpp = m.getPlayer(1 - m.active).field;

        int points = 0;

        for (int pos = shotSrc - 1; pos > shotDest; --pos) {
            if (fieldOpp.getChequers(25 - pos) >= 2) {
                ++points;
            }
        }

        return (points >= minNumPoints);
    }

    /**
     * Find direct shots for the active player and insert their src and dest
     * positions as pairs to out.
     */
    protected final void findDirectShots(Match m, MutableIntArray out) {
        Utils.findDirectShots(m, out);
    }

    /**
     * Find indirect shots for the active player and add them as tripples (from,
     * mid, to)
     * to out.
     */
    protected final void findIndirectShots(Match m, MutableIntArray out) {
        Utils.findIndirectShots(m, out);
    }

    /**
     * Find shots with a double roll for the active player and add them as
     * quadruples
     * (die, numFromBar, from, to) to out.
     */
    protected final void findShotsWithADouble(Match m, MutableIntArray out) {
        Utils.findShotsWithADouble(m, out);
    }
}
