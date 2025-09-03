package pr.backgammon;

public class Match {
    private final Player[] players = new Player[2];
    private final int own;
    private final int matchLen;
    private final Cube cube = new Cube();
    private int active = -1;
    private final boolean testBearoff;
    private boolean initialRoll = false;

    public Match(String[] playerNames, int indexOfOwn, int matchLen, boolean testBearoff) {
        if (playerNames.length != 2) {
            throw new IllegalArgumentException();
        }

        this.matchLen = matchLen;

        for (int i = 0; i < 2; ++i) {
            players[i] = new Player(playerNames[i]);
        }

        own = indexOfOwn;
        this.testBearoff = testBearoff;

        if (testBearoff) {
            players[0].testBearoff();
            players[1].testBearoff();
        }
    }

    public int getIndexOfOwn() {
        return own;
    }

    public boolean anyResignOffered() {
        return players[0].getOfferedResign() != 0 || players[1].getOfferedResign() != 0;
    }

    public void initialRoll(int owner, Roll roll) {
        if (owner < 0 || owner > 1) {
            throw new IllegalArgumentException();
        }

        if (active != -1 || cube.getValue() != 1 || players[0].getRoll() != null || players[1].getRoll() != null) {
            throw new IllegalStateException();
        }

        players[active = owner].setRoll(roll);
        initialRoll = true;
    }

    public void roll(Roll roll) {
        players[active].setRoll(roll);
        initialRoll = false;
    }

    public void offerDouble(int offering) {
        if (offering < 0 || offering > 1) {
            throw new IllegalArgumentException();
        }

        if (anyResignOffered()) {
            throw new IllegalStateException();
        }

        if (active != offering) {
            throw new IllegalStateException();
        }

        if (players[active].getRoll() != null) {
            throw new IllegalStateException("Double only allowed before rolling.");
        }

        int other = 1 - active;
        players[other].resetRoll();

        cube.offerDouble(offering);
        active = other;
    }

    public void take() {

        if (anyResignOffered()) {
            throw new IllegalStateException();
        }
        cube.take();
        System.out.println("active nach take " + active + "   own " + own);
        active = 1 - active;
    }

    public void drop() {

        if (anyResignOffered()) {
            throw new IllegalStateException();
        }

        if (!cube.isOffered()) {
            throw new IllegalStateException();
        }

        if (cube.getOwner() != active) {
            throw new IllegalStateException();
        }
        int points = cube.getValue();
        assert(points >= 2);
        points >>= 1;
        win(1 - active, points);
    }

    public void move(Move move) {
        if (move == null) {
            throw new IllegalArgumentException();
        }

        if (anyResignOffered()) {
            throw new IllegalStateException();
        }

        if (active == -1 || cube.isOffered() || players[active].getRoll() == null) {
            throw new IllegalStateException();
        }

        int other = 1 - active;
        Roll roll = players[active].getRoll();
        int big = roll.die1();
        int small = roll.die2();
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

        Field[] copiedFields = big == small ? null
                : new Field[] { players[active].copyField(), players[other].copyField() };

        for (PartMove pm : move) {
            int dist = pm.from - pm.to;
            Integer foundDie = availableDice.removeEqualOrGreater(dist);
            if (foundDie == null) {
                throw new IllegalStateException();
            }
            if (foundDie > dist) {
                if (pm.to != 0) {
                    // wasting only allowed in bear-off, and then only for a last checker in the
                    // field
                    throw new IllegalStateException();
                }
                // must check if pm.from is really the last field that contains a checker;
                // otherwise part move would be illegal
                if (!players[active].isLastCheckerOn(pm.from)) {
                    throw new IllegalStateException();
                }
            }
            if (pm.hit) {
                if (pm.to == 0) {
                    throw new IllegalArgumentException();
                }
                players[other].hit(25 - pm.to);
            } else {
                // must check if the opponent has no checker on `pm.to`.
                players[other].checkFree(25 - pm.to);
            }
            players[active].moveChecker(pm.from, pm.to);
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
                if (players[active].canMove(players[other], die)) {
                    throw new IllegalStateException();
                }
            }

            if (big != small && bigAvailable && Field.canMoveAnyCheckerWithDie(copiedFields[0], copiedFields[1], big)) {
                throw new IllegalStateException();
            }

        }

        if (players[active].bearoffComplete()) {
            int winner = active;
            boolean isGammon = players[other].getCheckers(0) == 0;
            boolean isBackgammon = isGammon && players[other].hasCheckerBefore18();
            int gameVal = isBackgammon ? 3 : isGammon ? 2 : 1;
            int points = gameVal * cube.getValue();
            win(winner, points);
        } else {
            active = other;
        }
    }

    private void win(int winner, int points) {
        players[winner].win(points);

        if (players[winner].getScore() >= matchLen) {
            setEndOfMatch();
        } else {
            for (Player p : players) {
                if (testBearoff) {
                    p.testBearoff();
                } else {
                    p.resetField();
                }
                p.resetRoll();
                p.resetResign();
            }
            cube.reset();
        }

        this.active = -1;
    }

    public void offerResign(int offeringPlayer, int val) {
        if (offeringPlayer < 0 || offeringPlayer > 1) {
            throw new IllegalArgumentException();
        }
        if (val < 1 || val > 3) {
            throw new IllegalArgumentException();
        }

        if (anyResignOffered()) {
            throw new IllegalStateException();
        }

        int acceptingPlayer = 1 - offeringPlayer;
        players[acceptingPlayer].offerResign(val);
    }

    public void acceptResign() {
        int winner, points;

        if (players[0].getOfferedResign() > 0) {
            winner = 0;
            points = players[0].acceptResign(cube.getValue());
        } else if (players[1].getOfferedResign() > 0) {
            winner = 1;
            points = players[1].acceptResign(cube.getValue());
        } else {
            throw new IllegalStateException();
        }

        if (players[winner].getScore() >= matchLen) {
            setEndOfMatch();
        } else {
            for (Player p : players) {
                if (testBearoff) {
                    p.testBearoff();
                } else {
                    p.resetField();
                }
                p.resetRoll();
                p.resetResign();
            }
            cube.reset();
        }

        this.active = -1;

        // TODO write win and start of next game or win of the match
        // There the points will be needed.
    }

    private void setEndOfMatch() {
        players[0].resetRoll();
        players[1].resetRoll();
    }

    public int getCubeOwner() {
        return cube.getOwner();
    }

    public int getCubeVal() {
        return cube.getValue();
    }

    /**
     * @return 0, if no checker is on `field`; positive value if own checkers are on
     *         `field`; negative value if opponent's checkers are on `field`.
     */
    public int getCheckers(int field) {
        if (field < 0 || field > 25) {
            throw new IllegalArgumentException();
        }

        int n = players[own].getCheckers(field);
        if (field > 0 && n > 0)
            return n;
        if (field == 25)
            return 0;
        n = players[1 - own].getCheckers(25 - field);
        return -n;
    }

    public boolean isCubeOffered() {
        return cube.isOffered();
    }

    public int getOfferedResign() {
        int resign = players[own].getOfferedResign();
        if (resign > 0)
            return resign;
        return -players[1 - own].getOfferedResign();
    }

    public int getScore(int player) {
        return players[player].getScore();
    }

    public String getPlayerName(int player) {
        return players[player].getName();
    }

    public void debugFields(int[] a) {
        int[][] fields = new int[2][26];
        fields[0][0] = 15;
        fields[1][0] = 15;

        for (int i = 0; i + 1 < a.length; i += 2) {
            int field = a[i];
            int val = a[i + 1];
            if (val < 0) {
                fields[0][field] += (-val);
                fields[0][0] += val;
            } else {
                fields[1][field] += val;
                fields[1][0] -= val;
            }
        }

        assert(fields[0][0] >= 0 && fields[1][0] >= 0);
        players[1 - own].debugField(fields[0]);
        players[own].debugField(fields[1]);
    }

    public int getActivePlayer() {
        return active;
    }

    public Roll getRoll() {
        if (active != -1) {
            return players[active].getRoll();
        }
        return null;
    }

    public boolean isInitialRoll() {
        return initialRoll;
    }
}
