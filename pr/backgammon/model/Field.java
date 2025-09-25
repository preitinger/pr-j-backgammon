package pr.backgammon.model;

import java.util.Arrays;

import pr.model.Mutable;
import pr.model.MutableIntArray;

public class Field implements Mutable<Field> {
    private static final long serialVersionUID = 1L;

    public static final int NUM_ALL_CHEQUERS = 15; // spaeter evtl. variabel fuer andere Varianten
    /**
     * 25: bar
     * 24-1: regular fields
     * 0: off
     */
    private int[] field = new int[26];

    public Field() {
        setInitial();
    }

    // Use Field.set() instead:
    @Deprecated
    public Field(Field field2) {
        this.field = field2.field.clone();
    }

    public void setInitial() {
        Arrays.fill(field, 0);
        field[25] = 0;
        field[24] = 2;
        field[13] = 5;
        field[8] = 3;
        field[6] = 5;
        
        int sum = 0;
        for (int i = 0; i < field.length; ++i) {
            sum += field[i];
        }

        assert(sum == NUM_ALL_CHEQUERS);
    }

    public void clear() {
        Arrays.fill(field, 0);
    }

    public void set(int pos, int val) {
        field[pos] = val;
    }

    /**
     * deep copy from other to this
     */
    public void set(/* readonly */Field other) {
        System.arraycopy(other.field, 0, this.field, 0, field.length);
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

    public void eventuallyHit(int field) {
        if (field < 0 || field > 25) {
            throw new IllegalArgumentException();
        }

        if (field == 25) {
            // Auf der Bar kein Werfen moeglich
            return;
        }

        if (this.field[field] > 1 || this.field[field] < 0) {
            throw new IllegalStateException();
        }

        if (this.field[field] == 1) {
            this.field[field] = 0;
            ++this.field[25];
        }
    }

    public int pipCount() {
        int sum = 0;

        for (int i = 1; i < field.length; ++i) {
            sum += i * field[i];
        }

        return sum;
    }

    public void moveChequer(int from, int to) {
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

    public boolean isLastChequerOn(int from) {
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

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof Field)) {
            return false;
        }
        Field other = (Field) o;
        assert(this.field != null && other.field != null && this.field.length == other.field.length);
        for (int i = 0; i < field.length; ++i) {
            if (field[i] != other.field[i]) {
                return false;
            }
        }

        return true;
    }

    // /**
    // * The value of pm.hit is eventually adapted as necessary in the return value.
    // * Then a new object is returned.
    // * If pm.hit is already correct and the part move can be done, pm is returned
    // * without creating a new object.
    // * If the part move can neither be done hitting nor non-hitting, null is
    // * returned.
    // */
    // public static PartMove canPartMove(/* readonly */PartMove pm, /* readonly
    // */Field active,
    // /* readonly */Field other, int die) {

    // if (pm.from < 25 && active.field[25] > 0)
    // return null;
    // if (active.field[pm.from] < 1)
    // return null;
    // if (pm.to > 0 && other.field[25 - pm.to] > 1)
    // return null;
    // if (pm.to > 0 && pm.from - die != pm.to)
    // return null;
    // if (pm.from - die > pm.to)
    // return null;
    // if (pm.to == 0 && !active.allInLastQuarter(pm.from))
    // return null;

    // if (pm.to == 0 && die > pm.from - pm.to) {
    // // Test, if waste is allowed
    // if (!active.isLastChequerOn(pm.from))
    // return null;
    // }

    // boolean hit;
    // if (pm.to == 0) {
    // hit = false;
    // } else {
    // assert (other.field[25 - pm.to] <= 1);
    // hit = other.field[25 - pm.to] == 1;
    // }

    // if (hit != pm.hit) {
    // return new PartMove(pm.from, pm.to, hit);
    // } else {
    // return pm;
    // }
    // }

    public int getChequers(int field) {
        return this.field[field];
    }

    public boolean allInLastQuarter(int from) {
        for (int i = 7; i <= 25; ++i) {
            if (field[i] > 0)
                return false;
        }

        return true;
    }

    public void debugField(int[] newField) {
        this.field = newField;
    }

    public StringBuilder appendPosition(StringBuilder sb) {
        if (sb == null)
            sb = new StringBuilder();

        for (int i = 25; i >= 0; --i) {
            if (field[i] > 0) {
                sb.append("  ");
                pad(sb, fieldName(i), 3).append(": ").append(field[i]).append('\n');
            }
        }

        return sb;
    }

    private String fieldName(int field) {
        switch (field) {
            case 25:
                return "bar";
            case 0:
                return "off";
            default:
                return String.valueOf(field);
        }
    }

    private StringBuilder pad(StringBuilder sb, String s, int preferredLen) {
        for (int i = 0; i < preferredLen - s.length(); ++i) {
            sb.append(' ');
        }
        return sb.append(s);
    }

    /**
     * without full checks, just modify quickly.
     * But hitting is detected and eventually executed correctly.
     * Illegal hitting (2 chequers of other on to) produces an
     * IllegalStateException.
     */
    public static void runPartMove(Field active, Field other, int from, int to) {
        if (to > 0 && other.field[25 - to] > 1) {
            throw new IllegalStateException();
        }
        if (to > 0 && other.field[25 - to] == 1) {
            other.field[25 - to] = 0;
            ++other.field[25];
        }

        --active.field[from];
        ++active.field[to];
    }

    public StringBuilder append(StringBuilder sb) {
        if (sb == null) {
            sb = new StringBuilder();
        }

        for (int i = 25; i >= 0; --i) {
            if (field[i] > 0) {
                sb.append(i).append(": ").append(field[i]).append('\n');
            }
        }

        return sb;
    }

    public boolean isInitial() {
        for (int i = 25; i >= 0; --i) {
            int expected = 0;
            switch (i) {
                case 24:
                    expected = 2;
                    break;
                case 13:
                    expected = 5;
                    break;
                case 8:
                    expected = 3;
                    break;
                case 6:
                    expected = 5;
                    break;
                default:
                    expected = 0;
                    break;
            }
            if (field[i] != expected) {
                return false;
            }
        }

        return true;
    }

    public static MovePrio getMovePrio(MutableIntArray move, int smallDie) {
        int n = move.length() / 2;
        if (n > 1) {
            return MovePrio.MAX;
        }
        if (n == 0) {
            return MovePrio.EMPTY;
        }
        int from = move.at(0);
        int to = move.at(1);
        if (to - from > smallDie) {
            return MovePrio.BIG;
        }
        // MutablePartMove pm = at(0);
        // if (pm.to - pm.from > smallDie) {
        // return MovePrio.BIG;
        // }
        return MovePrio.SMALL;

    }

    /**
     * erstellt ein Array wie es direkt an den Konstruktor von
     * pr.backgammon.gnubg.GSetBoardSimple übergeben werden kann.
     * Siehe Kommantar zu GSetBoardSimple(int[] board).
     */
    public static int[] getBoardSimple(Field active, Field other, int[] board) {

        if (board == null || board.length != 26) {
            board = new int[26];
        }

        board[0] = active.getChequers(25);
        board[25] = other.getChequers(25);

        for (int i = 1; i <= 24; ++i) {
            int chequersActive = active.getChequers(i);
            int chequersOther = other.getChequers(25 - i);
            if (chequersActive > 0) {
                if (chequersOther > 0) {
                    throw new IllegalStateException("Field " + i + " contains chequers from both players?!");
                }
                board[i] = chequersActive;
            } else {
                board[i] = -chequersOther;
            }
        }

        return board;
    }

    public boolean numChequersAsExpected() {
        int n = 0;
        for (int i = 0; i < field.length; ++i) {
            n += field[i];
        }
        return n == NUM_ALL_CHEQUERS;
    }
}
