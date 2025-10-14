package pr.backgammon.control;

import java.util.Comparator;

import pr.backgammon.DiceMultiSet;
import pr.backgammon.model.Field;
import pr.backgammon.model.Match;
import pr.backgammon.model.Player;
import pr.model.MutableArray;
import pr.model.MutableIntArray;

public class Move {
    public static void run(Match match, MutableIntArray move) {
        if (move == null) {
            throw new IllegalArgumentException();
        }

        if (match.anyResignOffered()) {
            throw new IllegalStateException();
        }

        if (match.active == -1 || match.cube.isOffered() || match.roll.isEmpty()) {
            throw new IllegalStateException("active=" + match.active + "   offered=" + match.cube.isOffered()
                    + "   roll.isEmpty()=" + match.roll.isEmpty());
        }

        int other = 1 - match.active;
        int big = match.roll.die1;
        int small = match.roll.die2;
        if (big < small) {
            int tmp = big;
            big = small;
            small = tmp;
        }
        assert (big >= small);
        DiceMultiSet availableDice = new DiceMultiSet();
        availableDice.add(big);
        availableDice.add(small);
        if (big == small) {
            availableDice.add(big);
            availableDice.add(small);
        }

        Player activePlayer = match.getPlayer(match.active);
        Player otherPlayer = match.getPlayer(other);

        Field copiedActive = new Field();
        copiedActive.set(activePlayer.field);
        Field copiedOther = new Field();
        copiedOther.set(otherPlayer.field);

        for (int pm = 0; pm + 1 < move.length(); pm += 2) {
            // for (PartMove pm : move) {
            int from = move.at(pm);
            int to = move.at(pm + 1);
            int dist = from - to;
            Integer foundDie = availableDice.removeEqualOrGreater(dist);
            if (foundDie == null) {
                throw new IllegalStateException();
            }
            if (foundDie > dist) {
                if (to != 0) {
                    // wasting only allowed in bear-off, and then only for a last chequer in the
                    // field
                    throw new IllegalStateException();
                }
                // must check if pm.from is really the last field that contains a chequer;
                // otherwise part move would be illegal
                if (!activePlayer.isLastChequerOn(from)) {
                    throw new IllegalStateException();
                }
            }

            otherPlayer.eventuallyHit(25 - to);
            activePlayer.moveChequer(from, to);
        }

        if (!availableDice.isEmpty()) {
            // Now, it's a bit tricky.
            // We must check if there is any valid part move with one of the remaining dice.
            // Then the move is illegal.
            // But, even if there is no valid part move with one of the remaining dice, if
            // only the smaller die of the roll has been used,
            // but instead of that also the bigger one could have been used for a part move,
            // the move is also illegal.

            boolean bigAvailable = availableDice.contains(big);

            Integer die;
            while ((die = availableDice.removeEqualOrGreater(other)) != null) {
                if (AllMoves.canMoveAnyChequerWithDie(activePlayer.field, otherPlayer.field, die)) {
                    throw new IllegalStateException();
                }
            }

            if (!activePlayer.bearoffComplete() && big != small && bigAvailable
                    && AllMoves.canMoveAnyChequerWithDie(copiedActive, copiedOther, big)) {
                throw new IllegalStateException();
            }

        }

        if (activePlayer.bearoffComplete()) {
            int winner = match.active;
            boolean isGammon = otherPlayer.getChequers(0) == 0;
            boolean isBackgammon = isGammon && otherPlayer.hasChequerBefore18();
            int gameVal = isBackgammon ? 3 : isGammon ? 2 : 1;
            match.win(winner, gameVal);
        } else {
            match.active = other;
            match.initialRoll = false;
            match.roll.setEmpty();
        }

    }

    /**
     * An eventually aggregated move is split into valid single part moves, if
     * necessary.
     * If it is possible to split into an either hitting or non-hitting variant, the
     * non-hitting variant is taken because this is the most intuitive case.
     * If both variants are equally hitting or non-hitting, the smaller die is used
     * first, then the bigger die.
     * If move is invalid, also out will be invalid.
     */
    public static void split(int die1, int die2, MutableIntArray move, /* readonly */ Field otherField,
            MutableIntArray out) {
        out.clear();

        int n = move.length();
        int next = 0;

        while (next < n) {
            int from = move.at(next++);
            int to = move.at(next++);
            // { next pointing to move after from/to or to the end }

            if (from - to < die1 + die2) {
                // Unsplittable
                out.add(from);
                out.add(to);
                continue;
            }

            if (die1 == die2) {
                while (from - die1 > to) {
                    out.add(from);
                    from -= die1;
                    out.add(from);
                }

                out.add(from);
                out.add(to);
                continue;
            }

            if (die1 > die2) {
                int tmpDie = die1;
                die1 = die2;
                die2 = tmpDie;
            }
            // { die1 < die2 }
            int mid1 = from - die1;
            int mid2 = from - die2;
            int other1 = otherField.getChequers(25 - mid1);
            int other2 = otherField.getChequers(25 - mid2);
            boolean possible1 = other1 <= 1;
            boolean possible2 = other2 <= 1;
            boolean hit1 = other1 == 1;
            boolean hit2 = other2 == 1;
            if (!possible1 || (possible2 && hit1 && !hit2)) {
                // Bigger die first
                out.add(from);
                out.add(mid2);
                out.add(mid2);
                out.add(to);
            } else {
                // Smaller die first
                out.add(from);
                out.add(mid1);
                out.add(mid1);
                out.add(to);
            }
        }
    }

    // Actually specific for spin.de. So, should be moved into
    // pr.backgammon.spin.control, or the like.
    public static void aggregate(int die1, int die2, /* readonly */MutableIntArray move,
            /* readonly */ Field otherField,
            MutableIntArray out, MutableArray<MutableIntArray> tmp) {

        final String tmpCondition = "tmp must have a capacity >= 1 and its first array must have a capacity >= 4.";

        if (tmp.capacity() < 1) {
            throw new IllegalArgumentException(tmpCondition);
        }
        tmp.clear();
        if (tmp.add().capacity() < 4) {
            throw new IllegalArgumentException(tmpCondition);
        }

        /**
         * Actually, isExtremeWaste is always a special case of isBearoff or in other
         * words: from isExtremeWaste(die1, die2, move) follows always isBearoff(die1,
         * die2, move)
         * So, as optimization isExtremeWaste is removed
         */
        if (move.length() == 0 /* || isExtremeWaste(die1, die2, move) */ || isBearoff(die1, die2, move)) {
            out.set(move);
            return;
        }

        out.clear();
        var partMoves = tmp.at(0);

        {
            partMoves.clear();
            int n = (move.length() >> 1);
            for (int i = 0; i < n; ++i) {
                if (i + 1 < n && move.at((i + 1) << 1) == 25) {
                    // Spin.de does not allow bear-in moves to be aggregated. So actually this
                    // function should be moved to the package pr.backgammon.spin
                    // If here, the next part move also does bear-in a chequer. (Because of sorted
                    // order, also this move.)
                    // Then, we add this bear-in move directly to out because it must not be
                    // aggregated.
                    // Only, after all bear-ins have been done aggregation is allowed by spin.de
                    out.add(move.at(i << 1));
                    out.add(move.at((i << 1) + 1));
                } else {
                    partMoves.add(i);
                }
            }
            partMoves.sort(new Comparator<Integer>() {
                @Override
                public int compare(Integer i, Integer j) {
                    // compare from values of a i'th and j'th part move, greater first.
                    return move.at(j << 1) - move.at(i << 1);
                }
            });
        }

        int bigDie = die1;
        int smallDie = die2;
        if (bigDie < smallDie) {
            int tmpi = bigDie;
            bigDie = smallDie;
            smallDie = tmpi;
        }

        out.add(fromOfPartMove(move, partMoves, 0));
        out.add(toOfPartMove(move, partMoves, 0));
        removePartMove(partMoves, 0);

        // out enthaelt nun genau den ersten Teilzug. Dieser wird geaendert, falls
        // eine Verkettung moeglich ist.
        // Falls nicht, wird der naechste Teilzug in out hinzugefuegt und mit diesem
        // fortgefahren usw.

        while (0 < partMoves.length()) {
            // dump(partMoves, out);
            int n = partMoves.length();
            int then = -1;
            for (int i = 0; i < n; ++i) {
                int from1 = out.at(out.length() - 2);
                int to1 = out.at(out.length() - 1);
                int from2 = fromOfPartMove(move, partMoves, i);
                int to2 = toOfPartMove(move, partMoves, i);
                if (canChain(from1, to1, from2, to2, otherField, bigDie, smallDie)) {
                    then = i;
                    break;
                }
            }

            if (then != -1) {
                // Extend out[out.length - 1] to the to field of partMoves[then].
                // Remove partMoves[then] by copying partMoves[then + 1..partMoves.length() - 1]
                // to
                // partMoves[then..partMoves.length() - 2] and removing the last.
                out.set(out.length() - 1, toOfPartMove(move, partMoves, then));
                removePartMove(partMoves, then);
                // done bleibt gleich!
            } else {
                // The last part move in out cannot be extended by chaining, any more.
                // So add partMoves[0] to out, remove it from partMoves and continue.
                out.add(fromOfPartMove(move, partMoves, 0));
                out.add(toOfPartMove(move, partMoves, 0));
                removePartMove(partMoves, 0);
            }
        }
    }

    private static void dump(MutableIntArray partMoves, MutableIntArray out) {
        StringBuilder sb = new StringBuilder();
        sb.append("partMoves: ");
        partMoves.append(sb);
        sb.append("\nout: ");
        out.append(sb);
        System.out.println(sb);
    }

    private static void removePartMove(MutableIntArray partMoves, int indexToRemove) {
        int n = partMoves.length();
        for (int i = indexToRemove; i + 1 < n; ++i) {
            partMoves.set(i, partMoves.at(i + 1));
        }
        partMoves.removeLast();
    }

    private static boolean canChain(int from1, int to1, int from2, int to2,
            Field otherField, int bigDie, int smallDie) {

        // int from1 = fromOfPartMove(move, partMoves, first);
        // int to1 = toOfPartMove(move, partMoves, first);
        // int from2 = fromOfPartMove(move, partMoves, then);

        if (to1 != from2) {
            return false;
        }

        // If equal, further checks:

        if (bigDie == smallDie) {
            // Doubles, if here, always chain, because it cannot be interpreted wrongly by
            // spin.de.
            return true;
        }

        if (otherField.getChequers(25 - to1) == 1) {
            // hit
            return false;
        }

        int alternativeTo = (from1 - bigDie == to1 ? from1 - smallDie : from1 - bigDie);

        if (otherField.getChequers(25 - alternativeTo) == 1) {
            // alternative hit
            return false;
        }

        return true;
    }

    private static int fromOfPartMove(MutableIntArray move, MutableIntArray partMoves, int partMoveIdx) {
        return move.at(partMoves.at(partMoveIdx) << 1);
    }

    private static int toOfPartMove(MutableIntArray move, MutableIntArray partMoves, int partMoveIdx) {
        return move.at((partMoves.at(partMoveIdx) << 1) + 1);
    }

    /**
     * The validity of move for the given roll is assumed and not checked here.
     */
    public static boolean isExtremeWaste(int die1, int die2, /* readonly */ MutableIntArray move) {
        int from = move.at(0);
        return move.length() == 2 && (from <= die1 || from <= die2);
    }

    public static boolean isBearoff(int die1, int die2, /* readonly */ MutableIntArray move) {
        int n = move.length();
        for (int i = 1; i < n; i += 2) {
            if (move.at(i) == 0) {
                return true;
            }
        }

        return false;
    }

    // public static boolean tryChain()
}
