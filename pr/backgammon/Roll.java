package pr.backgammon;

public class Roll {
    private final int die1, die2;
    
    public Roll(int die1, int die2) {
        this.die1 = die1;
        this.die2 = die2;
    }

    public int die1() {
        return die1;
    }

    public int die2() {
        return die2;
    }
}
