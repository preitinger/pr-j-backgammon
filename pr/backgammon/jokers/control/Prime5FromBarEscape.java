package pr.backgammon.jokers.control;

import pr.backgammon.model.Field;
import pr.backgammon.model.Match;
import pr.model.MutableArray;
import pr.model.MutableIntArray;

public class Prime5FromBarEscape extends BaseJoker {

    @Override
    public String getName() {
        return "Prime5FromBarEscape";
    }

    @Override
    public String getDesc() {
        return """
                An evaluable position is when the following two conditions hold.
                The active player has 1 chequer on the bar.
                The opponent has a prime of exact length 5 starting on a field between i=2 and i=7 until i + 4.
                Then, the positive case is when the player rolls (i-1) and 6 to be able to enter the home and jump over the prime.
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
        Field active = m.getPlayer(m.active).field;
        if (active.getChequers(25) != 1) {
            return CheckResult.NONE;
        }
        Field opp = m.getPlayer(1 - m.active).field;

        for (int i = 2; i <= 7; ++i) {
            boolean prime = true;

            for (int j = i; j < i + 5; ++j) {
                if (opp.getChequers(j) < 2) {
                    prime = false;
                    break;
                }
            }

            if (!prime || opp.getChequers(i + 5) >= 2) {
                continue;
            }

            return (m.roll.die1 == 6 && m.roll.die2 == i - 1) || (m.roll.die2 == 6 && m.roll.die1 == i - 1)
                    ? CheckResult.POSITIVE
                    : CheckResult.NEGATIVE;
        }

        return CheckResult.NONE;
    }

    public static void main(String[] args) {
        Prime5FromBarEscape var = new Prime5FromBarEscape();
        System.out.println("Description:\n" + var.getDesc());
    }

}
