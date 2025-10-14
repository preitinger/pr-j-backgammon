package pr.backgammon.model;

import pr.model.Mutable;

public class Match implements Mutable<Match> {
    private static final long serialVersionUID = 1L;

    /**
     * final except for set(other)
     */
    public int own;
    /**
     * final except for set(other)
     */
    public int matchLen;
    /**
     * final except for set(other)
     */
    public boolean crawfordRule = true;
    /**
     * final except for set(other)
     */
    public boolean testBearoff;

    private final Player[] players = new Player[2];
    public final Roll roll = new Roll(0, 0);
    public final Cube cube = new Cube();
    public int active = -1;
    public boolean initialRoll = false;
    public boolean crawfordRound;
    // public boolean autoroll;

    public Match() {
        own = 1;
        matchLen = 1;
        crawfordRule = true;
        testBearoff = false;
        players[0] = new Player("Gegner", 0);
        players[1] = new Player("Ich", 0);
        crawfordRound = false;
        // autoroll = false;
    }
    
    public Match(String[] playerNames, int indexOfOwn, int matchLen, int scoreOpp, int scoreOwn, boolean testBearoff) {
        if (playerNames.length != 2) {
            throw new IllegalArgumentException();
        }
        if (matchLen < 1) {
            throw new IllegalArgumentException();
        }

        this.matchLen = matchLen;

        for (int i = 0; i < 2; ++i) {
            int score = i == indexOfOwn ? scoreOwn : scoreOpp;
            players[i] = new Player(playerNames[i], score);
        }

        own = indexOfOwn;
        this.testBearoff = testBearoff;

        if (testBearoff) {
            players[0].testBearoff();
            players[1].testBearoff();
        }
        crawfordRound = crawfordRule && matchLen == 1;
        // autoroll = false;
    }

    /**
     * deep copy from other to this
     */
    public void set(/* readonly */Match other) {
        own = other.own;
        matchLen = other.matchLen;
        crawfordRule = other.crawfordRule;
        testBearoff = other.testBearoff;

        for (int i = 0; i < players.length; ++i) {
            players[i].set(other.players[i]);
        }
        roll.set(other.roll);
        cube.set(other.cube);
        active = other.active;
        initialRoll = other.initialRoll;
        crawfordRound = other.crawfordRound;
        // autoroll = other.autoroll;
    }

    public int getIndexOfOwn() {
        return own;
    }

    public Player getPlayer(int i) {
        return players[i];
    }

    public boolean anyResignOffered() {
        return players[0].getOfferedResign() != 0 || players[1].getOfferedResign() != 0;
    }

    public void initialRoll(int owner, int die1, int die2) {
        if (owner < 0 || owner > 1) {
            throw new IllegalArgumentException();
        }

        if (active != -1 || cube.getValue() != 1 || roll.die1 != 0 || roll.die2 != 0) {
            throw new IllegalStateException();
        }

        active = owner;
        roll.die1 = die1;
        roll.die2 = die2;
        initialRoll = true;
    }

    public void roll(int die1, int die2) {
        if (active == -1) {
            throw new IllegalStateException("cannot roll when active is -1");
        }
        roll.die1 = die1;
        roll.die2 = die2;
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

        if (!roll.isEmpty()) {
            throw new IllegalStateException("Double only allowed before rolling.");

        }

        if (crawfordRound) {
            throw new IllegalStateException("Crawford Round");
        }

        cube.offerDouble(offering);
    }

    public void take() {

        if (anyResignOffered()) {
            throw new IllegalStateException();
        }
        cube.take();
    }

    public void drop() {

        if (anyResignOffered()) {
            throw new IllegalStateException();
        }

        if (!cube.isOffered()) {
            throw new IllegalStateException();
        }

        int winner = 1 - cube.getOwner();

        cube.drop();
        win(winner, 1);
    }

    // public void move(Move move) {
    //     if (move == null) {
    //         throw new IllegalArgumentException();
    //     }

    //     if (anyResignOffered()) {
    //         throw new IllegalStateException();
    //     }

    //     if (active == -1 || cube.isOffered() || roll.isEmpty()) {
    //         throw new IllegalStateException();
    //     }

    //     int other = 1 - active;
    //     int big = roll.die1;
    //     int small = roll.die2;
    //     if (big < small) {
    //         int tmp = big;
    //         big = small;
    //         small = tmp;
    //     }
    //     assert (big >= small);
    //     DiceMultiSet availableDice = new DiceMultiSet();
    //     availableDice.add(big);
    //     availableDice.add(small);
    //     if (big == small) {
    //         availableDice.add(big);
    //         availableDice.add(small);
    //     }

    //     Field[] copiedFields = big == small ? null
    //             : new Field[] { players[active].copyField(), players[other].copyField() };

    //     for (PartMove pm : move) {
    //         int dist = pm.from - pm.to;
    //         Integer foundDie = availableDice.removeEqualOrGreater(dist);
    //         if (foundDie == null) {
    //             throw new IllegalStateException();
    //         }
    //         if (foundDie > dist) {
    //             if (pm.to != 0) {
    //                 // wasting only allowed in bear-off, and then only for a last chequer in the
    //                 // field
    //                 throw new IllegalStateException();
    //             }
    //             // must check if pm.from is really the last field that contains a chequer;
    //             // otherwise part move would be illegal
    //             if (!players[active].isLastChequerOn(pm.from)) {
    //                 throw new IllegalStateException();
    //             }
    //         }
    //         if (pm.hit) {
    //             if (pm.to == 0) {
    //                 throw new IllegalArgumentException();
    //             }
    //             players[other].hit(25 - pm.to);
    //         } else {
    //             // must check if the opponent has no chequer on `pm.to`.
    //             players[other].checkFree(25 - pm.to);
    //         }
    //         players[active].moveChequer(pm.from, pm.to);
    //     }

    //     if (!availableDice.isEmpty()) {
    //         // Now, it's a bit tricky.
    //         // We must check if there is any valid part move with one of the remaining dice.
    //         // Then the move is illegal.
    //         // But, even if there is no valid part move with one of the remaining dice, if
    //         // only the smaller die of the roll has been used,
    //         // but instead of that also the bigger one could have been used for a part move,
    //         // the move is also illegal.

    //         boolean bigAvailable = availableDice.contains(big);

    //         Integer die;
    //         while ((die = availableDice.removeEqualOrGreater(other)) != null) {
    //             if (players[active].canMove(players[other], die)) {
    //                 throw new IllegalStateException();
    //             }
    //         }

    //         if (big != small && bigAvailable && Field.canMoveAnyChequerWithDie(copiedFields[0], copiedFields[1], big)) {
    //             throw new IllegalStateException();
    //         }

    //     }

    //     if (players[active].bearoffComplete()) {
    //         int winner = active;
    //         boolean isGammon = players[other].getChequers(0) == 0;
    //         boolean isBackgammon = isGammon && players[other].hasChequerBefore18();
    //         int gameVal = isBackgammon ? 3 : isGammon ? 2 : 1;
    //         win(winner, gameVal);
    //     } else {
    //         active = other;
    //     }
    // }

    // public void move(MutableIntArray move) {
    //     if (move == null) {
    //         throw new IllegalArgumentException();
    //     }

    //     if (anyResignOffered()) {
    //         throw new IllegalStateException();
    //     }

    //     if (active == -1 || cube.isOffered() || roll.isEmpty()) {
    //         throw new IllegalStateException();
    //     }

    //     int other = 1 - active;
    //     int big = roll.die1;
    //     int small = roll.die2;
    //     if (big < small) {
    //         int tmp = big;
    //         big = small;
    //         small = tmp;
    //     }
    //     assert (big >= small);
    //     DiceMultiSet availableDice = new DiceMultiSet();
    //     availableDice.add(big);
    //     availableDice.add(small);
    //     if (big == small) {
    //         availableDice.add(big);
    //         availableDice.add(small);
    //     }

    //     Field[] copiedFields = big == small ? null
    //             : new Field[] { players[active].copyField(), players[other].copyField() };

    //     for (int pm = 0; pm + 1 < move.length(); pm += 2) {
    //         // for (PartMove pm : move) {
    //         int from = move.at(pm);
    //         int to = move.at(pm + 1);
    //         int dist = from - to;
    //         Integer foundDie = availableDice.removeEqualOrGreater(dist);
    //         if (foundDie == null) {
    //             throw new IllegalStateException();
    //         }
    //         if (foundDie > dist) {
    //             if (to != 0) {
    //                 // wasting only allowed in bear-off, and then only for a last chequer in the
    //                 // field
    //                 throw new IllegalStateException();
    //             }
    //             // must check if pm.from is really the last field that contains a chequer;
    //             // otherwise part move would be illegal
    //             if (!players[active].isLastChequerOn(from)) {
    //                 throw new IllegalStateException();
    //             }
    //         }

    //         players[other].eventuallyHit(25 - to);
    //         players[active].moveChequer(from, to);
    //     }

    //     if (!availableDice.isEmpty()) {
    //         // Now, it's a bit tricky.
    //         // We must check if there is any valid part move with one of the remaining dice.
    //         // Then the move is illegal.
    //         // But, even if there is no valid part move with one of the remaining dice, if
    //         // only the smaller die of the roll has been used,
    //         // but instead of that also the bigger one could have been used for a part move,
    //         // the move is also illegal.

    //         boolean bigAvailable = availableDice.contains(big);

    //         Integer die;
    //         while ((die = availableDice.removeEqualOrGreater(other)) != null) {
    //             if (players[active].canMove(players[other], die)) {
    //                 throw new IllegalStateException();
    //             }
    //         }

    //         if (big != small && bigAvailable && Field.canMoveAnyChequerWithDie(copiedFields[0], copiedFields[1], big)) {
    //             throw new IllegalStateException();
    //         }

    //     }

    //     if (players[active].bearoffComplete()) {
    //         int winner = active;
    //         boolean isGammon = players[other].getChequers(0) == 0;
    //         boolean isBackgammon = isGammon && players[other].hasChequerBefore18();
    //         int gameVal = isBackgammon ? 3 : isGammon ? 2 : 1;
    //         win(winner, gameVal);
    //     } else {
    //         active = other;
    //     }
    // }

    public void win(int winner, int undoubledPoints) {
        int points = undoubledPoints * cube.getValue();
        players[winner].win(points);

        if (players[winner].getScore() >= matchLen) {
            setEndOfMatch();
        } else {
            for (Player p : players) {
                if (testBearoff) {
                    p.testBearoff();
                } else {
                    p.setInitialField();
                }
                p.resetResign();
            }
            roll.die1 = roll.die2 = 0;
            cube.reset();
            crawfordRound = crawfordRule
                    && (players[1 - winner].getScore() != matchLen - 1 && players[winner].getScore() == matchLen - 1);
            this.active = -1;
        }

    }

    public boolean finished() {
        return players[0].score >= matchLen || players[1].score >= matchLen;
    }
    
    public boolean gameStarting() {
        return active == -1;
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
        int winner;
        int resign;

        if ((resign = players[0].getOfferedResign()) > 0) {
            winner = 0;
            players[0].resetResign();
        } else if ((resign = players[1].getOfferedResign()) > 0) {
            winner = 1;
            players[1].resetResign();
        } else {
            throw new IllegalStateException();
        }

        win(winner, resign);

        if (players[winner].getScore() >= matchLen) {
            setEndOfMatch();
        } else {
            for (Player p : players) {
                if (testBearoff) {
                    p.testBearoff();
                } else {
                    p.setInitialField();
                }
                p.resetResign();
            }
            roll.die1 = roll.die2 = 0;
            cube.reset();
        }

        this.active = -1;
    }

    private void setEndOfMatch() {
        roll.die1 = roll.die2 = 0;
        // sonst nix? Wohl noch nie verwendet.
    }

    public int getCubeOwner() {
        return cube.getOwner();
    }

    public int getCubeVal() {
        return cube.getValue();
    }

    /**
     * 
     * @return 0, if no chequer is on `field`; positive value if own chequers are on
     *         `field`; negative value if opponent's chequers are on `field`.
     */
    public int getChequers(int field) {
        if (field < 0 || field > 25) {
            throw new IllegalArgumentException();
        }

        int n = players[own].getChequers(field);
        if (field > 0 && n > 0)
            return n;
        if (field == 25)
            return 0;
        n = players[1 - own].getChequers(25 - field);
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

    /**
     * @param chequers - list of pairs of int, where the first int of each pair
     *                 defines a
     *                 field (25 downto 0) and the second int of each pair defines
     *                 the
     *                 chequers on the field, positive for the own player, and
     *                 negative for
     *                 the opponent.
     */
    public void debug(int[] chequers, Roll initialRoll, Roll oppRoll, Roll ownRoll, int cubeVal, boolean cubeOffered) {
        int[][] fields = new int[2][26];
        fields[0][0] = 15;
        fields[1][0] = 15;

        for (int i = 0; i + 1 < chequers.length; i += 2) {
            int field = chequers[i];
            int val = chequers[i + 1];
            if (val < 0) {
                fields[0][25 - field] += (-val);
                fields[0][0] += val;
            } else {
                fields[1][field] += val;
                fields[1][0] -= val;
            }
        }

        if (initialRoll != null) {
            this.initialRoll = true;
            roll.set(initialRoll);
            if (initialRoll.die1 > initialRoll.die2) {
                active = 1 - own;
            } else {
                active = own;
            }
        } else if (oppRoll != null) {
            this.initialRoll = false;
            active = 1 - own;
            roll.set(oppRoll);
        } else if (ownRoll != null) {
            this.initialRoll = false;
            active = own;
            roll.set(ownRoll);
        } else {
            this.initialRoll = false;
            roll.setEmpty();
        }

        assert (fields[0][0] >= 0 && fields[1][0] >= 0);
        players[1 - own].debug(fields[0]);
        players[own].debug(fields[1]);

        cube.debug(cubeVal, own, cubeOffered);
    }

    public int getActivePlayer() {
        return active;
    }

    public boolean isInitialRoll() {
        return initialRoll;
    }

    public boolean isCrawfordRound() {
        return crawfordRound;
    }

    public boolean isCrawfordRule() {
        return crawfordRule;
    }

    public StringBuilder appendPosition(StringBuilder sb) {
        if (sb == null)
            sb = new StringBuilder();
        return players[1].appendPositionAndCube(
                players[0].appendPositionAndCube(sb,
                        cube.getOwner() == 0 ? cube.getValue() : 0)
                        .append('\n'),

                cube.getOwner() == 1 ? cube.getValue() : 0

        );
    }

    // public void findAllMoves(MutableArray<MutableIntArray> out, FindTaskArray todo) {
    //     todo.clear();
    //     if (active == -1)
    //         return;

    //     int other = 1 - active;
    //     players[active].findAllMoves(players[other], roll.die1, roll.die2, out, todo);
    // }

    /**
     * erstellt ein Array wie es direkt an den Konstruktor von
     * pr.backgammon.gnubg.GSetBoardSimple übergeben werden kann.
     * Siehe Kommantar zu GSetBoardSimple(int[] board).
     */
    public int[] getBoardSimple(int[] board) {
        if (active == -1) {
            throw new IllegalStateException(
                    "Can only be called when a player is active because the result is from the perspective of the active player");
        }

        if (board == null || board.length != 26) {
            board = new int[26];
        }

        int other = 1 - active;
        return Field.getBoardSimple(players[active].getField(), players[other].getField(), board);

        // board[0] = players[active].getChequers(25);
        // board[25] = players[other].getChequers(25);

        // for (int i = 1; i <= 24; ++i) {
        //     int chequersActive = players[active].getChequers(i);
        //     int chequersOther = players[other].getChequers(25 - i);
        //     if (chequersActive > 0) {
        //         if (chequersOther > 0) {
        //             throw new IllegalStateException("Field " + i + " contains chequers from both players?!");
        //         }
        //         board[i] = chequersActive;
        //     } else {
        //         board[i] = -chequersOther;
        //     }
        // }

        // return board;
    }

    public void reset(int indexOfOwn, int matchLen) {
        this.own = indexOfOwn;
        this.matchLen = matchLen;
        this.testBearoff = false;
        players[1 - own].reset("Gegner", 0);
        players[own].reset("Ich", 0);
        cube.reset();
        active = -1;
        initialRoll = false;
        crawfordRound = false;
    }

    /**
     * wirft IllegalStateException, falls gerade Aufgabe angeboten wird, damit es
     * sich so verhaelt wie gnubg.
     * Aus dem gleichen Grund wird ein ggf. bestehender Wurf zurueckgesetzt, ebenso
     * wie ein ggf. angebotene Verdopplung.
     * 
     * @param player - -1 means no active player; 0 means first player is active; 1
     *               means second player is active;
     */
    public void setTurn(int player) {
        if (player < -1 || player > 1)
            throw new IllegalArgumentException("active must be between -1 and 1");
        roll.setEmpty();
        active = player;
    }

    public void resetResign() {
        players[0].resetResign();
        players[1].resetResign();
    }

    /**
     * @return - true iff player has a chequer on the bar and the other player has
     *         a full board.
     */
    public boolean closedOut(int player) {
        if (player < 0 || player > 1) {
            throw new IllegalArgumentException();
        }
        var field = players[player].field;
        var otherField = players[1 - player].field;
        if (field.getChequers(25) == 0) {
            return false;
        }

        for (int i = 1; i <= 6; ++i) {
            if (otherField.getChequers(i) < 2) {
                return false;
            }
        }

        return true;
    }
}
