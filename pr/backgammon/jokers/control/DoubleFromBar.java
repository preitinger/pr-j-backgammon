package pr.backgammon.jokers.control;

import pr.backgammon.model.Field;
import pr.backgammon.model.Match;
import pr.model.MutableArray;
import pr.model.MutableIntArray;

public class DoubleFromBar extends BaseJoker {
    private final int die;

    public DoubleFromBar(int die) {
        this.die = die;
    }

    @Override
    public String getName() {
        return ("FromBar" + die) + die;
    }

    @Override
    public String getDesc() {
        return """
An evaluable position for a certain home field is when a chequer is on the bar and the field `25 - die` can be entered from there.
The positive case is when the roll is doubles of `die`.
The negative case is otherwise.
                """;
    }

    @Override
    public double getProb() {
        return 1.0 / 36;
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
        if (active.getChequers(25) < 1) {
            return CheckResult.NONE;
        }
        Field opp = m.getPlayer(1 - m.active).field;

        if (opp.getChequers(die) > 1) {
            return CheckResult.NONE;
        }
        return m.roll.die1 == die && m.roll.die2 == die ? CheckResult.POSITIVE : CheckResult.NEGATIVE;
    }
    
}
