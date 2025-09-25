package pr.backgammon.spin.control.workers;

import pr.backgammon.spin.control.BoardSearchers;
import pr.backgammon.spin.model.Player;
import pr.control.MyRobot;
import pr.control.MyWorker;
import pr.model.MutableIntArray;

public abstract class ClickOwnMove extends MyWorker<Void, Void> {
    private final BoardSearchers bs;
    private final /* readonly */MutableIntArray move;

    public ClickOwnMove(BoardSearchers bs, /* readonly */ MutableIntArray move) {
        this.bs = bs;
        this.move = move;
    }

    @Override
    public Void doIt() throws Exception {
        {
            // Achtung! Neues Prinzip: In Spin muss eigener Autowurf immer deaktiviert sein, damit WaitForOppMove & Co genug Zeit haben.
            SetAutoroll setAutoRoll = new SetAutoroll(bs, false) {
                @Override
                public void resultOnEventDispatchThread(Boolean result) {
                    // ignore
                }
            };
            setAutoRoll.doIt();
        }

        var cal = bs.cal;

        int n = move.length();

        if (n == 0)
            return null;

        Thread.sleep(500);

        for (int i = 0; i + 1 < n; i += 2) {
            int from = move.at(i);
            int to = move.at(i + 1);

            if (from != 25) {
                // also click on the source field
                int x = cal.centerXOfField(from);
                int y = cal.centerYOfField(from);
                MyRobot.click(x - 5, y - 5, 10, 10);
                Thread.sleep(100);

            } else {
                // Special case, chequer on bar is already selected and must not be clicked
                // just click to the destination field
            }

            int x = to == 0 ? cal.centerXOfOff(Player.OWN) : cal.centerXOfField(to);
            int y = to == 0 ? cal.centerYOfOff(Player.OWN) : cal.centerYOfField(to);
            MyRobot.click(x - 5, y - 5, 10, 10);
        }

        return null;
    }

}
