package pr.backgammon;

import java.util.Arrays;

public class Field {
    /**
     * 25: bar
     * 24-1: regular fields
     * 0: off
     */
    private int[] field = new int[26];

    public Field() {
        reset();
    }

    public Field(Field field2) {
        this.field = field2.field.clone();
    }

    public void reset() {
        Arrays.fill(field, 0);
        field[25] = 0;
        field[24] = 2;
        field[13] = 5;
        field[8] = 3;
        field[6] = 5;
    }

    public void testBearoff() {
        Arrays.fill(field, 0);
        field[6] = 2;
        field[0] = 13;
    }

    public void hit(int field) {
        if (field < 0 || field > 25) {
            throw new IllegalArgumentException();
        }

        if (this.field[field] != 1) {
            throw new IllegalStateException();
        }

        this.field[field] = 0;
        ++this.field[25];
    }

    public void moveChecker(int from, int to) {
        if (from > 25 || from < 1) {
            throw new IllegalArgumentException();
        }
        if (to > 24 || to < 0) {
            throw new IllegalArgumentException();
        }
        if (field[from] < 1) {
            throw new IllegalStateException();
        }
        --field[from];
        ++field[to];
    }

    public boolean isLastCheckerOn(int from) {
        if (field[from] <= 0) {
            return false;
        }

        for (int i = from + 1; i <= 25; ++i) {
            if (field[i] > 0) {
                return false;
            }
        }

        return true;
    }

    public void checkFree(int to) {
        if (to < 0 || to > 25) {
            throw new IllegalArgumentException();
        }
        if (field[to] > 0) {
            throw new IllegalStateException();
        }
    }

    public static boolean canMoveAnyCheckerWithDie(/* readonly */Field active, /* readonly */ Field other, int die) {
        if (active.field[25] > 0) {
            // must move from bar
            return other.field[die] <= 1;
        } else {
            for (int from = 24; from >= 1; --from) {
                int to = from - die;
                if (to < 0)
                    to = 0;
                PartMove pm = new PartMove(from, to, false);
                if (canPartMove(pm, active, other, die) != null) {
                    return true;
                }
            }

            return false;
        }
    }

    /**
     * The value of pm.hit is eventually adapted as necessary in the return value.
     * Then a new object is returned.
     * If pm.hit is already correct and the part move can be done, pm is returned
     * without creating a new object.
     * If the part move can neither be done hitting nor non-hitting, null is
     * returned.
     */
    public static PartMove canPartMove(/* readonly */PartMove pm, /* readonly */Field active,
            /* readonly */Field other, int die) {

        if (pm.from < 25 && active.field[25] > 0)
            return null;
        if (active.field[pm.from] < 1)
            return null;
        if (pm.to > 0 && other.field[25 - pm.to] > 1)
            return null;
        if (pm.to > 0 && pm.from - die != pm.to)
            return null;
        if (pm.from - die > pm.to)
            return null;
        if (pm.to == 0 && !active.allInLastQuarter(pm.from))
            return null;

        if (pm.to == 0 && die > pm.from - pm.to) {
            // Test, if waste is allowed
            if (!active.isLastCheckerOn(pm.from))
                return null;
        }

        boolean hit;
        if (pm.to == 0) {
            hit = false;
        } else {
            assert (other.field[25 - pm.to] <= 1);
            hit = other.field[25 - pm.to] == 1;
        }

        if (hit != pm.hit) {
            return new PartMove(pm.from, pm.to, hit);
        } else {
            return pm;
        }
    }

    public int getCheckers(int field) {
        return this.field[field];
    }

    private boolean allInLastQuarter(int from) {
        for (int i = 7; i <= 25; ++i) {
            if (field[i] > 0)
                return false;
        }

        return true;
    }

    public void debugField(int[] newField) {
        this.field = newField;
    }
}
