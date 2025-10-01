package pr.backgammon.jokers.control;

import pr.backgammon.model.Match;
import pr.model.MutableArray;
import pr.model.MutableIntArray;

public class BarHomeOnly1 extends BaseJoker {

    @Override
    public String getName() {
        return "BarHomeOnly1";
    }

    @Override
    public String getDesc() {
        return """
An evaluable position is when the opponent has closed the home of the active player except for one pip which is
free (without a blot of the opponent) or contains chequers of the active player.
The positive case is then when the active player rolls the die necessary to enter, otherwise negative.
                                                """;
    }

    @Override
    public double getProb() {
        return 11.0 / 36;
    }

    @Override
    public CheckResult check(Match m, MutableArray<MutableIntArray> tmp) {
        if (m.active == -1 || m.roll.isEmpty()) {
            return CheckResult.NONE;
        }

        var opp = m.getPlayer(1 - m.active).field;
        int numPoints = 0, numBlots = 0;
        int freeField = 0;

        for (int i = 1; i <= 6; ++i) {
            int n = opp.getChequers(i);
            if (n >= 2) {
                ++numPoints;
            } else if (n == 1) {
                ++numBlots;
            } else {
                freeField = i;
            }
        }

        if (!(numPoints == 5 && numBlots == 0)) {
            return CheckResult.NONE;
        }

        assert(freeField >= 1 && freeField <= 6);
        return m.roll.die1 == freeField || m.roll.die2 == freeField ? CheckResult.POSITIVE : CheckResult.NEGATIVE;
    }

}
