package pr.backgammon.spin.control.workers;

import pr.backgammon.spin.control.BoardSearchers;
import pr.control.MyWorker;
import pr.control.Searcher;

public abstract class ClickBoardButton extends MyWorker<Boolean, Void> {
    private final BoardSearchers bs;
    private final Searcher searcher;

    public ClickBoardButton(BoardSearchers bs, Searcher searcher) {
        this.bs = bs;
        this.searcher = searcher;
    }
    
    @Override
    public Boolean doIt() throws Exception {
        var board = bs.boardShot().getRaster();
        return searcher.runAndClick(board, bs.boardRect().x, bs.boardRect().y);
    }
}
