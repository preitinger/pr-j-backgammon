package pr.backgammon;

import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.JFrame;

import pr.backgammon.ui.MatchView;

@SuppressWarnings("unused")
public class Test {
    private static void paintField1() throws InterruptedException {
        Match m = new Match(new String[] { "Gegner", "Ich" }, 1, 3, false);
        m.initialRoll(1, new Roll(6, 5));
        Move move = new Move();
        move.add(new PartMove(24, 18, false));
        move.add(new PartMove(18, 13, false));
        m.move(move);
        m.offerDouble(0);
        m.take();
        m.roll(new Roll(3, 1));
        move.clear();
        move.add(new PartMove(8, 5, false));
        move.add(new PartMove(6, 5, false));
        m.move(move);
        m.offerDouble(1);
        m.take();
        m.offerResign(1, 2);
        Utils.showMatch(m);
        m.acceptResign();
        Utils.showMatch(m);
    }

    private static void paintField2() {
        Match m = new Match(new String[] { "Gegner", "Ich" }, 1, 3, false);
        m.initialRoll(1, new Roll(6, 2));
        Move move = new Move();
        move.add(new PartMove(24, 18, false));
        move.add(new PartMove(13, 11, false));
        m.move(move);
        m.roll(new Roll(4, 6));
        move.clear();
        move.add(new PartMove(24, 18, false));
        move.add(new PartMove(18, 14, true));
        m.move(move);

        m.roll(new Roll(1, 1));
        move.clear();
        move.add(new PartMove(25, 24, false));
        move.add(new PartMove(24, 23, false));
        move.add(new PartMove(23, 22, false));
        move.add(new PartMove(22, 21, false));
        m.move(move);

        m.roll(new Roll(2, 3));
        move.clear();
        move.add(new PartMove(6, 4, true));
        move.add(new PartMove(4, 1, true));
        m.move(move);

        MatchView v = new MatchView(m, false);
        v.setSize(800, 600);
        JFrame f = new JFrame("paintField");
        f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        f.add(v);
        f.setSize(850, 650);
        f.setVisible(true);
    }

    private static void paint4OnOwnBar() {
        Match m = new Match(new String[] { "Gegner", "Ich" }, 1, 3, false);
        m.initialRoll(1, new Roll(1, 2));
        Move move = new Move();
        move.add(new PartMove(6, 5, false));
        move.add(new PartMove(6, 4, false));
        m.move(move);

        m.roll(new Roll(1, 2));
        move.clear();
        move.add(new PartMove(6, 5, false));
        move.add(new PartMove(6, 4, false));
        m.move(move);

        m.roll(new Roll(3, 4));
        move.clear();
        move.add(new PartMove(6, 3, false));
        move.add(new PartMove(6, 2, false));
        m.move(move);

        m.roll(new Roll(1, 1));
        move.clear();
        move.add(new PartMove(24, 23, true));
        move.add(new PartMove(23, 22, true));
        move.add(new PartMove(22, 21, true));
        move.add(new PartMove(21, 20, true));
        m.move(move);

        m.roll(new Roll(4, 6));
        move.clear();
        move.add(new PartMove(25, 21, true));
        m.move(move);

        m.roll(new Roll(6, 1));
        move.clear();
        move.add(new PartMove(25, 19, true));
        move.add(new PartMove(5, 4, true));
        m.move(move);

        m.roll(new Roll(6, 4));
        move.clear();
        move.add(new PartMove(25, 21, true));
        m.move(move);

        MatchView v = new MatchView(m, true);
        v.setSize(800, 600);
        JFrame f = new JFrame("paintField");
        f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        f.add(v);
        f.setSize(850, 650);
        f.setVisible(true);
    }

    private static void paint4OnOppBar() {
        Match m = new Match(new String[] { "Gegner", "Ich" }, 1, 3, false);
        m.initialRoll(0, new Roll(1, 2));
        Move move = new Move();
        move.add(new PartMove(6, 5, false));
        move.add(new PartMove(6, 4, false));
        m.move(move);

        m.roll(new Roll(1, 2));
        move.clear();
        move.add(new PartMove(6, 5, false));
        move.add(new PartMove(6, 4, false));
        m.move(move);

        m.roll(new Roll(3, 4));
        move.clear();
        move.add(new PartMove(6, 3, false));
        move.add(new PartMove(6, 2, false));
        m.move(move);

        m.roll(new Roll(1, 1));
        move.clear();
        move.add(new PartMove(24, 23, true));
        move.add(new PartMove(23, 22, true));
        move.add(new PartMove(22, 21, true));
        move.add(new PartMove(21, 20, true));
        m.move(move);

        m.roll(new Roll(4, 6));
        move.clear();
        move.add(new PartMove(25, 21, true));
        m.move(move);

        m.roll(new Roll(6, 1));
        move.clear();
        move.add(new PartMove(25, 19, true));
        move.add(new PartMove(5, 4, true));
        m.move(move);

        m.roll(new Roll(6, 4));
        move.clear();
        move.add(new PartMove(25, 21, true));
        m.move(move);

        MatchView v = new MatchView(m, false);
        v.setSize(800, 600);
        JFrame f = new JFrame("paintField");
        f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        f.add(v);
        f.setSize(850, 650);
        f.setVisible(true);
    }

    private static void testResign() throws Exception {
        Match m = new Match(new String[] { "Gegner", "Ich" }, 1, 3, false);
        m.initialRoll(0, new Roll(1, 2));
        Move move = new Move();
        move.add(new PartMove(6, 5, false));
        move.add(new PartMove(6, 4, false));
        m.move(move);
        m.offerResign(0, 1);
        Utils.showMatch(m);
        m.acceptResign();
        Utils.showMatch(m);

        m.initialRoll(1, new Roll(2, 4));
        Utils.showMatch(m);
        move.clear();
        move.add(8, 4, false);
        move.add(6, 4, false);
        m.move(move);
        Utils.showMatch(m);
    }

    private static void testBearoff() throws Exception {
        Match m = new Match(new String[] { "Gegner", "Ich" }, 1, 3, true);
        m.initialRoll(0, new Roll(6, 6));
        // Utils.showMatch(m);
        Move move = new Move();
        move.add(new PartMove(6, 0, false));
        move.add(new PartMove(6, 0, false));
        m.move(move);
        // Utils.showMatch(m);

        m.initialRoll(0, new Roll(1, 2));
        move.clear();
        move.add(6, 5, false);
        move.add(6, 4, false);
        m.move(move);
        // Utils.showMatch(m);
        m.offerDouble(1);
        // Utils.showMatch(m);
        m.take();
        // Utils.showMatch(m);

        m.roll(new Roll(5, 3));
        move.clear();
        move.add(6, 3, false);
        move.add(6, 1, false);
        m.move(move);
        // Utils.showMatch(m);

        m.roll(new Roll(4, 1));
        move.clear();
        move.add(5, 1, false);
        move.add(1, 0, false);
        m.move(move);

        m.roll(new Roll(5, 2));
        // Utils.showMatch(m);
        move.clear();
        move.add(3, 0, false);
        move.add(1, 0, false);
        m.move(move);
        Utils.showMatch(m);

    }

    private static void testDrop() throws Exception {
        Match m = new Match(new String[] { "Gegner", "Ich" }, 1, 3, true);
        m.initialRoll(0, new Roll(1, 2));
        Move move = new Move();
        move.add(new PartMove(6, 5, false));
        move.add(new PartMove(6, 4, false));
        m.move(move);

        m.offerDouble(1);
        Utils.showMatch(m);

        m.drop();
        Utils.showMatch(m);

        m.initialRoll(0, new Roll(1, 2));
        move.clear();
        move.add(new PartMove(6, 5, false));
        move.add(new PartMove(6, 4, false));
        m.move(move);

        m.offerDouble(1);
        Utils.showMatch(m);

        m.drop();
        Utils.showMatch(m);

        m.initialRoll(1, new Roll(1, 2));
        move.clear();
        move.add(new PartMove(6, 5, false));
        move.add(new PartMove(6, 4, false));
        m.move(move);

        m.offerResign(1, 1);
        Utils.showMatch(m);
        m.acceptResign();
        Utils.showMatch(m);

        m.initialRoll(1, new Roll(1, 2));
        move.clear();
        move.add(new PartMove(6, 5, false));
        move.add(new PartMove(6, 4, false));
        m.move(move);

        Utils.showMatch(m);

        m.offerDouble(0);
        Utils.showMatch(m);

        m.drop();
        Utils.showMatch(m);
    }

    private static void testCrawford() throws Exception {
        Match m = new Match(new String[] { "Gegner", "Ich" }, 1, 3, true);
        m.initialRoll(0, new Roll(1, 2));
        Move move = new Move();
        move.add(new PartMove(6, 5, false));
        move.add(new PartMove(6, 4, false));
        m.move(move);

        m.offerDouble(1);
        // Utils.showMatch(m);

        m.drop();
        // Utils.showMatch(m);

        m.initialRoll(0, new Roll(1, 2));
        move.clear();
        move.add(new PartMove(6, 5, false));
        move.add(new PartMove(6, 4, false));
        m.move(move);

        m.offerDouble(1);
        Utils.showMatch(m);

        m.drop();
        Utils.showMatch(m);

        // Now, crawford round.

        m.initialRoll(1, new Roll(1, 2));
        move.clear();
        move.add(new PartMove(6, 5, false));
        move.add(new PartMove(6, 4, false));
        m.move(move);
        Utils.showMatch(m);

        try {
            // expected to throw IllegalStateException:
            m.offerDouble(0);
            // not expected to reach this line, because exception expected
            throw new Exception("Expected IllegalStateException missing");
        } catch (IllegalStateException ex) {
            // expected, ignore
        }
        // Utils.showMatch(m);

        m.offerResign(1, 1);
        m.acceptResign();
        // Utils.showMatch(m);

        // Now, post-crawford.

        m.initialRoll(1, new Roll(1, 2));
        move.clear();
        move.add(new PartMove(6, 5, false));
        move.add(new PartMove(6, 4, false));
        m.move(move);

        Utils.showMatch(m);

        m.offerDouble(0); // must not throw because of post-crawford round.
        Utils.showMatch(m);

        m.take();
        Utils.showMatch(m);

        m.roll(new Roll(4, 4));
        Utils.showMatch(m);
        move.clear();
        move.add(6, 2, false);
        move.add(6, 2, false);
        move.add(2, 0, false);
        move.add(2, 0, false);
        m.move(move);
        Utils.showMatch(m);

    }

    public static void main(String[] args) throws Exception {
        // paintField1();
        // paint4OnOwnBar();
        // paint4OnOppBar();
        // testResign();
        // testBearoff();
        // testDrop();
        testCrawford();
    }

}

class Utils {
    static void showMatch(Match m) throws InterruptedException {
        Object sync = new Object();
        boolean[] closed = { false };

        MatchView v = new MatchView(m, true);
        v.setSize(800, 600);
        JFrame f = new JFrame("paintField");
        f.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        f.add(v);
        f.setSize(850, 650);
        f.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosed(WindowEvent e) {
                synchronized (sync) {
                    closed[0] = true;
                    sync.notifyAll();
                }
            }
        });
        f.setVisible(true);

        synchronized (sync) {
            while (!closed[0]) {
                sync.wait();
            }
        }
    }
}