package pr.backgammon;

public class Player {
    private String name;
    private final Field field;
    private int score;
    private Roll roll = null;
    /**
     * resign value that the other player has offered to this player
     */
    private int resign = 0;

    public Player(String name) {
        this.name = name;
        field = new Field();
        score = 0;
    }

    public String getName() {
        return name;
    }

    public int getScore() {
        return score;
    }

    public void resetField() {
        field.reset();
    }

    public void resetScore() {
        score = 0;
    }

    public void resetRoll() {
        roll = null;
    }

    public void resetResign() {
        resign = 0;
    }

    public void setRoll(Roll roll) {
        this.roll = roll;
    }

    public Roll getRoll() {
        return roll;
    }

    public void hit(int to) {
        field.hit(to);
    }

    public void moveChecker(int from, int to) {
        field.moveChecker(from, to);
    }

    public boolean isLastCheckerOn(int from) {
        return field.isLastCheckerOn(from);
    }

    public void checkFree(int to) {
        field.checkFree(to);
    }

    public Field copyField() {
        return new Field(field);
    }

    public boolean canMove(/* readonly */Player other, int die) {
        return other.canMoveOther(field, die);
    }

    private boolean canMoveOther(/* readonly */Field fieldOfOtherMovingPlayer, int die) {
        return Field.canMoveAnyCheckerWithDie(fieldOfOtherMovingPlayer, this.field, die);
    }

    public void offerResign(int val) {
        assert (val >= 1 && val <= 3);
        if (resign > 0) {
            // already resign offered
            throw new IllegalStateException("Already resign offered " + resign);
        }
        resign = val;
    }

    public int getOfferedResign() {
        return resign;
    }

    public int getCheckers(int field) {
        return this.field.getCheckers(field);
    }

    public void testBearoff() {
        field.testBearoff();
    }

    public void win(int points) {
        score += points;
    }

    public boolean bearoffComplete() {
        return field.getCheckers(0) == 15;
    }

    public boolean hasCheckerBefore18() {
        for (int i = 19; i <= 25; ++i) {
            if (field.getCheckers(i) > 0) {
                return true;
            }
        }

        return false;
    }

    public void debugField(int[] newField) {
        field.debugField(newField);
    }
}
