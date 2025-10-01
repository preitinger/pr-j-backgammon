package pr.backgammon.jokers.control;

import pr.backgammon.model.Match;
import pr.model.MutableArray;
import pr.model.MutableIntArray;

public class BarHomeOnly1Blot extends BaseJoker {

    @Override
    public String getName() {
        return "BarHomeOnly1Blot";
    }

    @Override
    public String getDesc() {
        return """
An evaluable position is when the opponent has closed the home of the active player except for one pip
where is a blot of the opponent.
The positive case is then when the active player rolls the die necessary to enter and hit, otherwise negative.
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
        int numPoints = 0, numFree = 0;
        int blotField = 0;

        for (int i = 1; i <= 6; ++i) {
            int n = opp.getChequers(i);
            if (n >= 2) {
                ++numPoints;
            } else if (n == 0) {
                ++numFree;
            } else {
                blotField = i;
            }
        }

        if (!(numPoints == 5 && numFree == 0)) {
            return CheckResult.NONE;
        }

        assert(blotField >= 1 && blotField <= 6);
        return m.roll.die1 == blotField || m.roll.die2 == blotField ? CheckResult.POSITIVE : CheckResult.NEGATIVE;
    }
    
}
