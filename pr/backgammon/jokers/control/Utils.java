package pr.backgammon.jokers.control;

import pr.backgammon.model.Field;
import pr.backgammon.model.Match;
import pr.model.MutableIntArray;

public class Utils {

    /**
     * detects if the non-active player has a prime between shotSrc and shotDest.
     * The positions shotSrc and shotDest are from the perspective of the active
     * player
     * who wants to cross the opponent's prime between th
     */
    public static boolean brokenPrimeBetween(Match m, int shotSrc, int shotDest, int minNumPoints) {
        if (shotSrc <= shotDest) {
            throw new IllegalArgumentException();
        }

        var fieldOpp = m.getPlayer(1 - m.active).field;

        int points = 0;

        for (int pos = shotSrc - 1; pos > shotDest; --pos) {
            if (fieldOpp.getChequers(25 - pos) >= 2) {
                ++points;
            }
        }

        return (points >= minNumPoints);
    }

    /**
     * Find direct shots for the active player and insert their src and dest
     * positions as pairs to out.
     */
    public static void findDirectShots(Match m, MutableIntArray out) {
        var active = m.getPlayer(m.active).field;
        var opp = m.getPlayer(1 - m.active).field;

        if (active.getChequers(25) > 0) {
            for (int to = 24; to >= 19; --to) {
                int from = 25;
                if (opp.getChequers(25 - to) == 1) {
                    out.add(from);
                    out.add(to);
                }
            }
        } else {
            for (int from = 24; from >= 2; --from) {
                for (int to = Math.max(1, from - 1); to >= Math.max(1, from - 6); --to) {
                    if (active.getChequers(from) > 0 && opp.getChequers(25 - to) == 1) {
                        out.add(from);
                        out.add(to);
                    }
                }
            }
        }
    }

    /**
     * Find indirect shots for the active player and add them as tripples (from,
     * mid, to)
     * to out.
     */
    public static void findIndirectShots(Match m, MutableIntArray out) {
        var active = m.getPlayer(m.active).field;
        var opp = m.getPlayer(1 - m.active).field;

        int onBar = active.getChequers(25);

        if (onBar > 0) {
            if (onBar >= 2) {
                // no indirect shot possible, only direct or shot with doubles
                return;
            }
            int from = 25;
            for (int die1 = 1; die1 <= 6; ++die1) {
                for (int die2 = 1; die2 <= 6; ++die2) {
                    if (die2 == die1) {
                        continue;
                    }

                    int mid = from - die1;
                    int to = mid - die2;

                    if (opp.getChequers(25 - mid) < 2 && opp.getChequers(25 - to) == 1) {
                        out.add(from);
                        out.add(mid);
                        out.add(to);
                    }
                }
            }
        } else {
            for (int from = 24; from >= 2; --from) {
                if (active.getChequers(from) < 1) {
                    continue;
                }

                for (int die1 = 1; die1 <= 6; ++die1) {
                    int mid = from - die1;
                    if (mid < 2) {
                        break;
                    }

                    if (opp.getChequers(25 - mid) >= 2) {
                        continue; // blocked mid position
                    }

                    for (int die2 = 1; die2 <= 6; ++die2) {
                        if (die2 == die1) {
                            continue;
                        }
                        int to = mid - die2;

                        if (to < 1) {
                            break;
                        }

                        if (opp.getChequers(25 - to) == 1) {
                            out.add(from);
                            out.add(mid);
                            out.add(to);
                        }
                    }
                }
            }
        }

    }

    /**
     * Find shots with a double roll for the active player and add them as
     * quadruples
     * (die, numFromBar, from, to) to out.
     */
    public static void findShotsWithADouble(Match m, MutableIntArray out) {
        var active = m.getPlayer(m.active).field;
        var opp = m.getPlayer(1 - m.active).field;

        int onBar = active.getChequers(25);
        if (onBar >= 4) {
            return;
        }

        for (int die = 1; die <= 6; ++die) {

            if (onBar > 0 && opp.getChequers(25 - (25 - die)) >= 2) {
                continue;
            }
            for (int from = 24; from >= 2; --from) {
                if (active.getChequers(from) == 0 && (onBar == 0 || from != 25 - die)) {
                    continue;
                }

                if (from - die < 1) {
                    break;
                }

                if (opp.getChequers(25 - (from - die)) >= 2) {
                    // first step from `from` blocked, so continue
                    continue;
                }

                int to = onBar == 0 ? from - die : from;

                for (int used = Math.min(onBar, 1); used < 4; ++used) {
                    to -= die;

                    if (to < 1) {
                        break;
                    }

                    int numOpp = opp.getChequers(25 - to);

                    if (numOpp >= 2) {
                        break;
                    }

                    if (numOpp == 1) {
                        out.add(die);
                        out.add(onBar);
                        out.add(from);
                        out.add(to);
                    }
                }
            }
        }
    }

    private static void findShotsWithADouble1(Field active, Field opp, int numFromBar, MutableIntArray out) {
    }

}
