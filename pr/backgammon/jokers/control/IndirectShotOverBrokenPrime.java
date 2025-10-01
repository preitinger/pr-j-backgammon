package pr.backgammon.jokers.control;

import pr.backgammon.model.Match;
import pr.model.MutableArray;
import pr.model.MutableIntArray;

public class IndirectShotOverBrokenPrime extends BaseJoker {

    @Override
    public String getName() {
        return "IndirectShotOverBrokenPrime";
    }

    @Override
    public String getDesc() {
        return """
An evaluable position is when there is an indirect shot through a broken prime with a non-doubles roll.
There must be no direct shot, or any other indirect shot or any shot with a double.
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

        findShotsWithADouble(m, shots);
        if (shots.length() > 0) {
            return CheckResult.NONE;
        }

        assert(shots.length() == 0);
        findIndirectShots(m, shots);
        int n = shots.length();
        
        if (n != 3) {
            return CheckResult.NONE;
        }

        int from = shots.at(0);
        int mid = shots.at(1);
        int to = shots.at(2);

        if (!brokenPrimeBetween(m, from, to, 5)) {
            return CheckResult.NONE;
        }

        int die1 = m.roll.die1;
        int die2 = m.roll.die2;

        if (die1 == from - mid) {
            return die2 == mid - to ? CheckResult.POSITIVE : CheckResult.NEGATIVE;
        } else {
            return die2 == from - mid && die1 == mid - to ? CheckResult.POSITIVE : CheckResult.NEGATIVE;
        }
    }

}
