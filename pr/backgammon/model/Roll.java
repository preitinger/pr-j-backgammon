package pr.backgammon.model;

import pr.model.Mutable;

public class Roll implements Mutable<Roll> {
    private static final long serialVersionUID = 1L;

    public int die1, die2;

    public Roll() {
        this.die1 = 0;
        this.die2 = 0;
    }

    public Roll(int die1, int die2) {
        this.die1 = die1;
        this.die2 = die2;
    }

    public void set(Roll other) {
        this.die1 = other.die1;
        this.die2 = other.die2;
    }

    public int die1() {
        return die1;
    }

    public int die2() {
        return die2;
    }

    public boolean isEmpty() {
        return die1 == 0 || die2 == 0;
    }

    public StringBuilder append(StringBuilder sb) {
        if (sb == null)
            sb = new StringBuilder();
        if (die1 != 0) {
            sb.append(die1).append(die2);
        }
        return sb;
    }

    public void setEmpty() {
        die1 = die2 = 0;
    }
}
