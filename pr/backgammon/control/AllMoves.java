package pr.backgammon.control;

import pr.backgammon.PartMoveResult;
import pr.backgammon.model.Field;
import pr.backgammon.model.FindTask;
import pr.backgammon.model.FindTaskArray;
import pr.backgammon.model.Match;
import pr.backgammon.model.MovePrio;
import pr.model.MutableArray;
import pr.model.MutableIntArray;

public class AllMoves {
    // public static void find(Match match) {
    //     find(match, match.allMoves, match.findTaskArray);
    // }

    public static void find(Match match, MutableArray<MutableIntArray> out, FindTaskArray todo) {
        out.clear();
        todo.clear();
        if (match.active >= 0 && !match.roll.isEmpty()) {
            int other = 1 - match.active;
            Field factive = match.getPlayer(match.active).getField();
            Field fother = match.getPlayer(other).getField();
            findAllMoves(factive, fother, match.roll.die1, match.roll.die2, out, todo);
        } else {
            System.out.println("\n*** ALLMOVES.FIND() SKIPPED BECAUSE NOBODY ACTIVE OR ROLL IS EMPTY!\n");
        }
    }

    /**
     * writes found valid moves to {@code out[offset]}, ...,
     * {@code out[offset + (n - 1)]} and returns {@code n}.
     */
    public static void findAllMoves(/* readonly */Field active, /* readonly */Field other, int die1, int die2,
            MutableArray<MutableIntArray> out,
            FindTaskArray todo) {

        long start = System.currentTimeMillis();

        // System.out.println("findAllMoves:\nactive:\n" + active.append(null).toString() + "\nother:\n"
                // + other.append(null).toString());
        out.clear();
        todo.clear();
        // MutableIntArray tmp = new MutableIntArray(15);
        FindTaskArray currentTask = new FindTaskArray(1);
        MutableIntArray tmpPartMoves = new MutableIntArray(15 * 2);

        int big, small;
        if (die1 < die2) {
            big = die2;
            small = die1;
        } else {
            big = die1;
            small = die2;
        }

        {
            FindTask task = todo.add();
            task.availableDice.clear();
            task.availableDice.add(big);
            task.availableDice.add(small);
            if (big == small) {
                task.availableDice.add(big);
                task.availableDice.add(big);
            }
            task.active.set(active);
            task.other.set(other);
            task.move.clear();
        }

        // 0 is empty move, 1 is move only small die, 2 is move only big die, 3 is move
        // all dice
        MovePrio foundMovePrio = MovePrio.EMPTY;

        while (todo.length() > 0) {
            currentTask.clear();
            todo.move(0, currentTask);
            // {
            //     StringBuilder sb = new StringBuilder();
            //     sb.append("Run task:\n");
            //     currentTask.at(0).append(sb);
            //     System.out.println(sb.toString());
            // }
            if (currentTask.length() != 1)
                throw new IllegalStateException();
            int ndice = currentTask.at(0).availableDice.length();
            if (ndice == 0) {
                throw new IllegalStateException("added entry to todo with 0 dice?!");
            }
            boolean anyFound = false;
            boolean lastDie = ndice == 1;
            for (int i = 0; i < ndice; ++i) {
                int die = currentTask.at(0).availableDice.at(i);
                if (i > 0 && die == currentTask.at(0).availableDice.at(i - 1)) {
                    continue; // shortage for double roll
                }
                findAllPartMovesWithDie(currentTask.at(0).active, currentTask.at(0).other, die, tmpPartMoves);
                // allChequersMovableWithDie(active, other, die, tmp);
                int ntmp = tmpPartMoves.length();
                if (ntmp > 0) {
                    anyFound = true;
                }
                for (int j = ntmp - 2; j >= 0; j -= 2) {

                    if (lastDie) {
                        foundMovePrio = MovePrio.MAX;

                        var addedMove = out.add();
                        addedMove.set(currentTask.at(0).move);
                        addedMove.add(tmpPartMoves.at(j));
                        addedMove.add(tmpPartMoves.at(j + 1));
                    } else {
                        // add a task to todo, trying the remaining dice or die

                        FindTask addingTask = todo.add();
                        addingTask.set(currentTask.at(0));
                        Field.runPartMove(addingTask.active, addingTask.other, tmpPartMoves.at(j),
                                tmpPartMoves.at(j + 1));
                        // remove last die from addingTask.availableDice
                        boolean found = false;
                        for (int k = addingTask.availableDice.length() - 1; k >= 0; --k) {
                            if (addingTask.availableDice.at(k) == die) {
                                addingTask.availableDice.swapOut(k);
                                found = true;
                                break;
                            }
                        }
                        if (!found) {
                            throw new IllegalStateException("Bug in any copy?!");
                        }
                        // tmpPartMoves.move(j, addingTask.move);
                        addingTask.move.add(tmpPartMoves.at(j));
                        addingTask.move.add(tmpPartMoves.at(j + 1));
                    }
                }
            }
            if (!anyFound) {
                // move is complete
                int n = currentTask.at(0).move.length();
                if (n == 2) { // 2 bedeutet 1 zug mit 2 zahlen
                    // wenn der große noch übrige ist, wurde der kleine wuerfel verwendet ;-)
                    MovePrio newPrio = Field.getMovePrio(currentTask.at(0).move, small);
                    if (newPrio.ordinal() >= foundMovePrio.ordinal()) {
                        foundMovePrio = newPrio;
                        out.add().set(currentTask.at(0).move);
                    }
                } else if (n == 0) {
                    if (foundMovePrio == MovePrio.EMPTY) {
                        out.add().set(currentTask.at(0).move);
                    }
                } else {
                    if (small != big) {
                        throw new IllegalStateException("Pasch fuer diesen Fall erwartet!");
                    }
                    foundMovePrio = MovePrio.MAX;
                    out.add().set(currentTask.at(0).move);
                }
                continue;
            }
        }

        // Now, remove all moves that have lower prio than the maximal found prio.
        for (int i = out.length() - 1; i >= 0; --i) {
            int minOrdinal = foundMovePrio.ordinal();
            if (Field.getMovePrio(out.at(i), small).ordinal() < minOrdinal) {
                out.swapOut(i);
            }
            // if (MutableMove.getPrio(out.at(i), small).ordinal() < minOrdinal) {
            // out.swapOut(i);
            // }
        }

        long end = System.currentTimeMillis();
        System.out.println("AllMoves.findAllMoves() took " + (end - start) + "ms.");
    }

    /**
     * @param out - contains pairs of int which each represent a possible part move
     *            where the first number represents from and the second number
     *            represents to.
     */
    public static void findAllPartMovesWithDie(/* readonly */Field active, /* readonly */Field other, int die,
            MutableIntArray out) {

        out.clear();

        if (active.getChequers(25) > 0) {
            // must move from bar

            PartMoveResult opt = canPartMove(25, 25 - die, active, other, die);
            switch (opt) {
                case NO:
                    break;

                case HIT:
                case NO_HIT: {
                    // MutablePartMove pm = out.add();
                    // pm.from = 25;
                    // pm.to = 25 - die;
                    // pm.hit = opt == PartMoveResult.HIT;
                    out.add(25);
                    out.add(25 - die);
                    break;
                }
            }
        } else {
            for (int from = 24; from >= 1; --from) {
                int to = from - die;
                if (to < 0)
                    to = 0;

                PartMoveResult opt = canPartMove(from, to, active, other, die);

                if (opt != PartMoveResult.NO) {
                    out.add(from);
                    out.add(to);
                    // MutablePartMove pm = out.add();
                    // pm.from = from;
                    // pm.to = to;
                    // pm.hit = opt == PartMoveResult.HIT;
                }
            }
        }
    }

    public static boolean isValidStart(/* readonly */MutableArray<MutableIntArray> allValidMoves, /* readonly */ MutableIntArray start) {
        // Search element in allValidMoves that starts with [...ongoingMove, from]
        int n = allValidMoves.length();
        int startLen = start.length();

        for (int i = 0; i < n; ++i) {
            MutableIntArray validMove = allValidMoves.at(i);
            if (validMove.length() < startLen) {
                continue;
            }
            boolean different = false;
            for (int j = 0; j < startLen; ++j) {
                if (validMove.at(j) != start.at(j)) {
                    different = true;
                    break;
                }
            }
            if (!different) {
                return true;
            }
        }

        return false;
    }

    public static boolean isValid(MutableArray<MutableIntArray> allMoves, MutableIntArray move) {
        int n = allMoves.length();

        for (int i = 0; i < n; ++i) {
            MutableIntArray validMove = allMoves.at(i);
            if (validMove.equals(move)) {
                return true;
            }
        }

        return false;
    }

    public static PartMoveResult canPartMove(int from, int to, /* readonly */Field active,
            /* readonly */Field other, int die) {

        if (from < 25 && active.getChequers(25) > 0)
            return PartMoveResult.NO;
        if (active.getChequers(from) < 1)
            return PartMoveResult.NO;
        if (to > 0 && other.getChequers(25 - to) > 1)
            return PartMoveResult.NO;
        if (to > 0 && from - die != to)
            return PartMoveResult.NO;
        if (from - die > to)
            return PartMoveResult.NO;
        if (to == 0 && !active.allInLastQuarter(from))
            return PartMoveResult.NO;

        if (to == 0 && die > from - to) {
            // Test, if waste is allowed
            if (!active.isLastChequerOn(from))
                return PartMoveResult.NO;
        }

        boolean hit;
        if (to == 0) {
            hit = false;
        } else {
            assert (other.getChequers(25 - to) <= 1);
            hit = other.getChequers(25 - to) == 1;
        }

        return hit ? PartMoveResult.HIT : PartMoveResult.NO_HIT;
    }
    public static boolean canMoveAnyChequerWithDie(/* readonly */Field active, /* readonly */ Field other, int die) {
        if (active.getChequers(25) > 0) {
            // must move from bar
            return other.getChequers(die) <= 1;
        } else {
            for (int from = 24; from >= 1; --from) {
                int to = from - die;
                if (to < 0)
                    to = 0;
                if (canPartMove(from, to, active, other, die) != PartMoveResult.NO) {
                    return true;
                }
            }

            return false;
        }
    }

    /**
     * 
     * writes result to {@code out[offset]}, {@code out[offset + 1]}, ...,
     * {@code out[offset +
     * (n - 1)]}, and returns {@codee n}.
     * 
     */
    public static int allChequersMovableWithDie(/* readonly */Field active, /* readonly */Field other, int die,
            int[] out, int offset) {

        int end = offset;

        if (active.getChequers(25) > 0) {
            // must move from bar

            if (other.getChequers(die) <= 1) {
                out[end++] = 25;
            }
        } else {
            for (int from = 24; from >= 1; --from) {
                int to = from - die;
                if (to < 0)
                    to = 0;
                if (canPartMove(from, to, active, other, die) != PartMoveResult.NO) {
                    out[end++] = from;
                }
            }
        }

        return end - offset;
    }

    public static void allChequersMovableWithDie(/* readonly */Field active, /* readonly */Field other, int die,
            MutableIntArray out) {

        out.clear();

        if (active.getChequers(25) > 0) {
            // must move from bar

            if (other.getChequers(die) <= 1) {
                out.add(25);
            }
        } else {
            for (int from = 24; from >= 1; --from) {
                int to = from - die;
                if (to < 0)
                    to = 0;
                if (canPartMove(from, to, active, other, die) != PartMoveResult.NO) {
                    out.add(from);
                }
            }
        }
    }
}
