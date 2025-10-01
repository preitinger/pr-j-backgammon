package pr.backgammon.jokers.control;

import pr.backgammon.model.Match;
import pr.model.MutableArray;
import pr.model.MutableIntArray;

public class DoublesShotOverBrokenPrime extends BaseJoker {

    @Override
    public String getName() {
        return "DoublesShotOverBrokenPrime";
    }

    @Override
    public String getDesc() {
        return """
An evaluable position is when there is a shot with a double through a broken prime.
There must be no direct shot, or any indirect shot or any shot with another double.
The broken prime must include at least 5 points, no matter how many before and after the gap.
The positive case is the roll that lets the chequer be hit, otherwise negative.
The source position on fields 19 through 25.
        """;
    }

    @Override
    public double getProb() {
        return 1.0 / 18;
    }

    @Override
    public CheckResult check(Match m, MutableArray<MutableIntArray> tmp) {
        if (m.active == -1) {
            return CheckResult.NONE;
        }
        if (m.roll.isEmpty()) {
            return CheckResult.NONE;
        }

        tmp.clear();
        var shots = tmp.add();
        shots.clear();
        findDirectShots(m, shots);
        if (shots.length() > 0) {
            return CheckResult.NONE;
        }

        findIndirectShots(m, shots);
        if (shots.length() > 0) {
            return CheckResult.NONE;
        }

        assert(shots.length() == 0);
        findShotsWithADouble(m, shots);
        int n = shots.length();
        
        if (n < 4) {
            return CheckResult.NONE;
        }

        int die = shots.at(0);
        int from = shots.at(2);
        int to = shots.at(3);

        for (int i = 4; i + 3 < n; i += 4) {
            int otherDie = shots.at(i);
            if (otherDie != die) {
                return CheckResult.NONE;
            }
        }

        if (!brokenPrimeBetween(m, from, to, 5)) {
            return CheckResult.NONE;
        }

        return m.roll.die1 == die && m.roll.die2 == die ? CheckResult.POSITIVE : CheckResult.NEGATIVE;
    }
    
}
