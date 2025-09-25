package pr.backgammon.spin.trackmove;

import java.awt.Rectangle;

import javax.swing.SwingWorker;

import pr.backgammon.model.Field;
import pr.backgammon.model.Match;
import pr.backgammon.spin.control.BoardSearchers;
import pr.backgammon.spin.control.CalibrationForSpin;
import pr.backgammon.spin.control.FastChequerSearch;

public class Worker extends SwingWorker<Void, Data> {
    private final CalibrationForSpin cal;
    private FastChequerSearch chequers = null;
    private Rectangle boardRect = null;
    private BoardSearchers bs = null;
    private Field ownField = new Field(), oppField = new Field(), lastOwnField = new Field(), lastOppField = new Field();

    public Worker(CalibrationForSpin cal) {
        this.cal = cal;
    }

    @Override
    protected Void doInBackground() throws Exception {
        chequers = new FastChequerSearch(cal);
        boardRect = chequers.boardScreenshotRect(null);
        bs = new BoardSearchers(cal, boardRect);
        // int num = 0;
        boolean first = true;

        do {
            Thread.sleep(1);

            if (isCancelled() || Thread.interrupted()) {
                return null;
            }

            var img = bs.boardShot();
            chequers.init(img.getRaster());
            chequers.getFields(ownField, oppField);

            if (!first && ownField.equals(lastOwnField) && oppField.equals(lastOppField)) {
                swapFields();
                continue;
            }

            first = false;
            Data data = new Data();

            Match m = data.match;
            m.reset(1, 3);
            m.getPlayer(0).field.set(oppField);
            m.getPlayer(1).field.set(ownField);
            data.shot = img;
            publish(data);

            // if (stopped.get()) {
            //     System.out.println("Stopping...");
            //     return null;
            // }
            // if (++num >= 16) {
            //     System.out.println("num " + num);
            //     return null;
            // }

            swapFields();
        } while (!isCancelled() && !Thread.interrupted());

        return null;
    }

    private void swapFields() {
        Field tmp = lastOppField;
        lastOppField = oppField;
        oppField = tmp;
        tmp = lastOwnField;
        lastOwnField = ownField;
        ownField = tmp;
    }
}
