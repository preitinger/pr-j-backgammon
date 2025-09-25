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

        if (move.length() == 0 || isExtremeWaste(die1, die2, move)) {
            out.set(move);
            return;
        }

        var partMoves = tmp.at(0);

        {
            partMoves.clear();
            int n = (move.length() >> 1);
            for (int i = 0; i < n; ++i) {
                partMoves.add(i);
            }
            partMoves.sort(new Comparator<Integer>() {
                @Override
                public int compare(Integer i, Integer j) {
                    // compare from values of a i'th and j'th part move, greater first.
                    return move.at(j << 1) - move.at(i << 1);
                }
            });
        }
        out.clear();

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

        // out enthaelt nun genau den ersten Teilzug. Dieser wird geaendert, falls eine Verkettung moeglich ist.
        // Falls nicht, wird der naechste Teilzug in out hinzugefuegt und mit diesem fortgefahren usw.

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
                // Remove partMoves[then] by copying partMoves[then + 1..partMoves.length() - 1] to
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

    // public static boolean tryChain()
}
