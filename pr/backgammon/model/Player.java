package pr.backgammon.model;

import pr.model.Mutable;

public class Player implements Mutable<Player> {
    private static final long serialVersionUID = 1L;

    public String name;
    public final Field field;
    public int score;
    /**
     * resign value that the other player has offered to this player
     */
    public int resign = 0;

    public Player(String name, int score) {
        this.name = name;
        field = new Field();
        this.score = score;
    }

    /**
     * deep copy from other to this
     */
    public void set(/* readonly */Player other) {
        this.name = other.name;
        this.field.set(other.field);
        this.score = other.score;
        this.resign = other.resign;
    }

    public void reset(String name, int score) {
        this.name = name;
        this.field.setInitial();
        this.score = score;
        this.resign = 0;
    }

    public Field getField() {
        return field;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getScore() {
        return score;
    }

    public void resetField() {
        field.setInitial();
    }

    public void resetScore() {
        score = 0;
    }

    public void resetResign() {
        resign = 0;
    }

    public void hit(int to) {
        field.hit(to);
    }

    public void eventuallyHit(int to) {
        field.eventuallyHit(to);
    }

    public void moveChequer(int from, int to) {
        field.moveChequer(from, to);
    }

    public boolean isLastChequerOn(int from) {
        return field.isLastChequerOn(from);
    }

    public void checkFree(int to) {
        field.checkFree(to);
    }

    // use Field.set(other) instead
    @Deprecated
    public Field copyField() {
        return new Field(field);
    }

    // public boolean canMove(/* readonly */Player other, int die) {
    // return other.canMoveOther(field, die);
    // }

    // private boolean canMoveOther(/* readonly */Field fieldOfOtherMovingPlayer,
    // int die) {
    // return AllMoves.canMoveAnyChequerWithDie(fieldOfOtherMovingPlayer,
    // this.field, die);
    // }

    public void offerResign(int val) {
        assert (val >= 1 && val <= 3);
        if (resign > 0) {
            // already resign offered
            throw new IllegalStateException("Already resign offered " + resign);
        }
        resign = val;
    }

    /**
     * @return - the value that the other player would like to resign
     */
    public int getOfferedResign() {
        return resign;
    }

    public int getChequers(int field) {
        return this.field.getChequers(field);
    }

    public void setChequers(int field, int num) {
        if (num < 0) {
            throw new IllegalArgumentException("num must be >= 0");
        }
        this.field.set(field, num);
    }

    public void testBearoff() {
        field.testBearoff();
    }

    public void win(int points) {
        score += points;
    }

    public boolean bearoffComplete() {
        return field.getChequers(0) == Field.NUM_ALL_CHEQUERS;
    }

    public boolean hasChequerBefore18() {
        for (int i = 19; i <= 25; ++i) {
            if (field.getChequers(i) > 0) {
                return true;
            }
        }

        return false;
    }

    public void debug(int[] newField) {
        field.debugField(newField);
    }

    public StringBuilder appendPositionAndCube(StringBuilder sb, int cubeVal) {
        if (sb == null)
            sb = new StringBuilder();
        sb.append(getName()).append(":\n");

        field.appendPosition(sb);

        if (cubeVal > 1) {
            sb.append("  ").append(cubeVal);
        }

        return sb;
    }

    // public void findAllMoves(Player opp, int die1, int die2,
    // MutableArray<MutableIntArray> out,
    // FindTaskArray todo) {
    // Field.findAllMoves(field, opp.field, die1, die2, out, todo);
    // }
}
