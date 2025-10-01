package pr.backgammon.control;

public interface OwnMoveCb {
    /**
     * The match.ongoingMove can be used to mirror the move in the spin browser, and
     * then ongoingMove can be cleared. The player fields of match will already
     * contain the state after the ongoingMove.
     */
    void done();
}
