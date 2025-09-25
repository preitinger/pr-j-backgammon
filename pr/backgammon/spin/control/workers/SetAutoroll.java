package pr.backgammon.spin.control.workers;

import pr.backgammon.spin.control.BoardSearchers;
import pr.control.MyRobot;
import pr.control.MyWorker;

import java.awt.Point;
import java.awt.image.Raster;

public abstract class SetAutoroll extends MyWorker<Boolean, Void> {
    private final BoardSearchers s;
    private final boolean selected;

    public SetAutoroll(BoardSearchers s, boolean selected) {
        this.s = s;
        this.selected = selected;
    }

    @Override
    public Boolean doIt() throws Exception {
        Thread.sleep(500);
        Raster board = s.boardShot().getRaster();
        Point posSelected = s.autorollSelected.run(board);
        Point posDeselected = s.autorollDeselected.run(board);
        System.out.println("posSelected " + posSelected);
        System.out.println("posDeselected " + posDeselected);
        if (posSelected == null && posDeselected == null) {
            throw new IllegalStateException("Autoroll weder selektiert noch deselektiert");
        }
        if (posSelected != null && posDeselected != null) {
            throw new IllegalStateException("Autoroll sowohl selektiert als auch deselektiert?!");
        }
        int dx = s.boardRect().x;
        int dy = s.boardRect().y;
        
        if (selected) {
            if (posDeselected != null) {
                MyRobot.click(dx + posDeselected.x, dy + posDeselected.y, 31, 11);
                return true;
            }
            return false;
        }

        assert(!selected);
        if (posSelected != null) {
            MyRobot.click(dx + posSelected.x, dy + posSelected.y, 31, 11);
            return true;
        }
        return false;
    }
}
