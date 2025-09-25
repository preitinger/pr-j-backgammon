package pr.backgammon.gnubg.model;

import pr.backgammon.model.Match;

public class GSetDice extends GnuCmd {
    private int die1, die2;

    public GSetDice(int die1, int die2) {
        if (die1 < 1 || die1 > 6 || die2 < 1 || die2 > 6) throw new IllegalArgumentException("Illegal dice " + die1 + " " + die2);
        this.die1 = die1;
        this.die2 = die2;
    }

    // @Override
    public void runOn(Match m) {
        m.roll.die1 = die1;
        m.roll.die2 = die2;
    }

    @Override
    protected void safeAppend(StringBuilder sb) {
        sb.append("set dice ").append(die1).append(' ').append(die2);
    }
    
}
