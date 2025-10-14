package pr.backgammon.spin.control.workers;

import pr.backgammon.spin.control.BoardSearchers;
import pr.backgammon.spin.control.TemplateSearchers;
import pr.control.MyWorker;
import pr.control.Searcher;
import pr.control.TemplateSearcher;

public abstract class ClickBoardButton extends MyWorker<Boolean, Void> {
    private final BoardSearchers bs;
    private final TemplateSearchers ts;
    private final Searcher searcher;
    private final TemplateSearcher templateSearcher;

    public ClickBoardButton(BoardSearchers bs, TemplateSearchers ts, Searcher searcher) {
        this.bs = bs;
        this.ts = ts;
        this.searcher = searcher;
        this.templateSearcher = null;
    }

    public ClickBoardButton(BoardSearchers bs, TemplateSearchers ts, TemplateSearcher templateSearcher) {
        this.bs = bs;
        this.ts = ts;
        this.searcher = null;
        this.templateSearcher = templateSearcher;
    }

    @Override
    public Boolean doIt() throws Exception {
        System.out.println("\n***** CLICK BOARD BUTTON\n");
        if (searcher != null) {
            var board = bs.boardShot().getRaster();
            return searcher.runAndClick(board, bs.boardRect().x, bs.boardRect().y);
        }

        if (templateSearcher != null) {
            ts.waitAndClick(bs, templateSearcher);
            // var pos = templateSearcher.search(bs.boardShot());
            // boolean found = pos != null;

            // if (!found) {
            //     return false;
            // }
            
            // Rectangle rect = new Rectangle();
            // rect.x = (int) Math.round(pos.x);
            // rect.y = (int) Math.round(pos.y);
            // rect.width = templateSearcher.templateWidth();
            // rect.height = templateSearcher.templateHeight();
            // MyRobot.click(rect);
            return true;
        }

        return false;
    }
}
