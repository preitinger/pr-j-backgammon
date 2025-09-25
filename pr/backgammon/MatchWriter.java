package pr.backgammon;

import pr.control.Tools;

@Deprecated
public class MatchWriter {
    public MatchWriter(int len, String ownPlayer, String oppPlayer,
            MatchTextAppender appender) {

        this.appender = appender;
        this.ownPlayer = ownPlayer;
        this.oppPlayer = oppPlayer;

        StringBuilder sb = new StringBuilder();
        sb.append(len);
        sb.append(" point match\n");
        sb.append("\n");
        colRight = gameStart(sb, 1, ownPlayer, oppPlayer, 0, 0);

        appender.appendMatchText(sb.toString());
    }

    public void appendMove(boolean own, int die1, int die2, Move move) {
        StringBuilder sb = new StringBuilder();

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

        appender.appendMatchText(sb.toString());
    }

    public void appendWin(boolean own, int points, int nextGame, int scoreOwn, int scoreOpp, boolean alsoMatch) {
        if (points < 1) {
            throw new IllegalArgumentException("points=" + points);
        }

        StringBuilder sb = new StringBuilder();

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

        appender.appendMatchText(sb.toString());
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

    private final MatchTextAppender appender;
    private final String ownPlayer, oppPlayer;
    private final int colRight;
    private boolean first = true;
}
