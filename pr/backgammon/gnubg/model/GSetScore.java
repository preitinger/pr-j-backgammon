package pr.backgammon.gnubg.model;

/**
 * Der Befehl set score in GNU Backgammon setzt den Matchstand für das aktuelle
 * Spiel.
 * Achtung, es reicht nicht den Score zu setzen um nach verpassten Zügen einen
 * definierten Fortsetzungszustand zu setzen.
 * Stattdessen muss folgendes gemacht werden:
 * resign 1
 * accept
 * new game
 * NUN: set score <opponent> <own>
 * set turn
 * ...
 * set dice ...
 * move ...
 * ...
 */
public class GSetScore extends GnuCmd {
    private final int scoreOpp, scoreOwn;

    public GSetScore(int scoreOpp, int scoreOwn) {
        this.scoreOpp = scoreOpp;
        this.scoreOwn = scoreOwn;
    }

    @Override
    protected void safeAppend(StringBuilder sb) {
        sb.append("set score ").append(scoreOpp).append(' ').append(scoreOwn);
    }
}
