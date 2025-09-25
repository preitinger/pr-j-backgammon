package pr.backgammon;

import pr.control.Tools;
import pr.model.MutableArray;

@Deprecated
public class MatchWriter2 {
    public MatchWriter2(int len, String ownPlayer, String oppPlayer) {

        this.ownPlayer = ownPlayer;
        this.oppPlayer = oppPlayer;

        colRight = 36;
    }

    public StringBuilder appendMatch(StringBuilder sb, int len, String ownPlayer, String oppPlayer) {
        this.ownPlayer = ownPlayer;
        this.oppPlayer = oppPlayer;
        sb = ensureSb(sb);
        sb.append(len);
        sb.append(" point match\n");
        return sb;
    }

    public StringBuilder appendGame(StringBuilder sb, int game, int scoreOpp, int scoreOwn) {
        sb = ensureSb(sb);
        sb.append('\n');
        colRight = gameStart(sb, game, ownPlayer, oppPlayer, scoreOwn, scoreOpp);
        first = true;
        return sb;
    }

    public StringBuilder appendRoll(StringBuilder sb, boolean own, int die1, int die2) {
        sb = ensureSb(sb);

        if (first) {
            if (own) {
                for (int i = 0; i < colRight; ++i) {
                    sb.append(' ');
                }
            }
            first = false;
        }
        sb.append(' ');
        sb.append(die1);
        sb.append(die2);
        sb.append(": ");

        return sb;
    }

    public StringBuilder appendMove(StringBuilder sb, boolean own, Move move) {
        sb = ensureSb(sb);

        sb.append(move.toString());

        if (!own) {
            for (int i = sb.length(); i < colRight; ++i) {
                sb.append(' ');
            }
        } else {
            sb.append('\n');
        }

        return sb;
    }

    public StringBuilder appendMove(StringBuilder sb, boolean own, int die1, int die2, Move move) {
        sb = ensureSb(sb);

        if (first) {
            if (own) {
                for (int i = 0; i < colRight; ++i) {
                    sb.append(' ');
                }
            }
            first = false;
        }
        sb.append(' ');
        sb.append(die1);
        sb.append(die2);
        sb.append(": ");
        sb.append(move.toString());

        if (!own) {
            for (int i = sb.length(); i < colRight; ++i) {
                sb.append(' ');
            }
        } else {
            sb.append('\n');
        }

        return sb;
    }

    public StringBuilder appendMove(StringBuilder sb, boolean own, int die1, int die2, MutableArray<MutablePartMove> move) {
        sb = ensureSb(sb);

        if (first) {
            if (own) {
                for (int i = 0; i < colRight; ++i) {
                    sb.append(' ');
                }
            }
            first = false;
        }
        sb.append(' ');
        sb.append(die1);
        sb.append(die2);
        sb.append(": ");
        {
            int n = move.length();
            for (int i = 0; i < n; ++i) {
                MutablePartMove pm = move.at(i);
                pm.append(sb).append(' ');
            }
        }

        if (!own) {
            for (int i = sb.length(); i < colRight; ++i) {
                sb.append(' ');
            }
        } else {
            sb.append('\n');
        }

        return sb;
    }

    public StringBuilder appendWin(StringBuilder sb, boolean own, int points, int nextGame, int scoreOwn, int scoreOpp,
            boolean alsoMatch) {
        sb = ensureSb(sb);

        if (points < 1) {
            throw new IllegalArgumentException("points=" + points);
        }

        if (own) {
            Tools.appendSpaces(sb, colRight);
        } else {
            sb.append('\n');
        }

        sb.append("  Wins " + points + (points > 1 ? " points" : " point"));

        if (alsoMatch) {
            sb.append(" and the match\n");
        } else {
            sb.append("\n\n");
            gameStart(sb, nextGame, ownPlayer, oppPlayer, scoreOwn, scoreOpp);
        }

        first = true;

        return sb;
    }

    /**
     * @return colRight
     */
    private static int gameStart(StringBuilder sb, int nextGame, String ownPlayer, String oppPlayer, int scoreOwn,
            int scoreOpp) {
        sb.append("Game ").append(nextGame).append('\n');
        String partOpp = oppPlayer + " : " + scoreOpp + "  ";
        sb.append(partOpp);
        int slen = partOpp.length();
        int colRight = Math.max(slen, 36);

        for (int i = slen; i < colRight; ++i) {
            sb.append(' ');
        }

        sb.append(ownPlayer);
        sb.append(" : ").append(scoreOwn).append("\n\n");
        return colRight;
    }

    private StringBuilder ensureSb(StringBuilder sb) {
        if (sb == null) {
            sb = new StringBuilder();
        }
        return sb;
    }

    private String ownPlayer, oppPlayer;
    private int colRight;
    private boolean first = true;

}
