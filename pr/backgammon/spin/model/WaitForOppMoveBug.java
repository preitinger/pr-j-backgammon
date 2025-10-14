package pr.backgammon.spin.model;

import java.io.File;
import java.io.FileInputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.Arrays;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JScrollPane;
import javax.swing.ListSelectionModel;
import javax.swing.SwingUtilities;

import pr.backgammon.model.Field;
import pr.backgammon.model.Match;
import pr.backgammon.view.MatchView;
import pr.model.Mutable;
import pr.view.IGrid;

public class WaitForOppMoveBug implements Mutable<WaitForOppMoveBug> {
    private static final long serialVersionUID = 1L;

    public final Match match = new Match();
    public final Match matchCopy = new Match();
    public final Field newOwn = new Field();
    public final Field newOpp = new Field();

    @Override
    public void set(WaitForOppMoveBug other) {
        match.set(other.match);
        matchCopy.set(other.matchCopy);
        newOwn.set(other.newOwn);
        newOpp.set(other.newOpp);
    }

    public static void main(String[] args) {
        // File[] files = Arrays.stream(findBugFiles()).map(name -> new
        // File(name)).toArray(File[]::new);
        String[] bugFiles = findBugFiles();
        Arrays.sort(bugFiles);

        SwingUtilities.invokeLater(() -> {
            JList<String> list = new JList<>(bugFiles);
            list.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
            list.addListSelectionListener(e -> {
                if (!e.getValueIsAdjusting()) {
                    String selected = list.getSelectedValue();
                    loadAndShow(selected);
                }
            });

            JFrame frame = new JFrame("WaitForOppMoveBug");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.add(new JScrollPane(list));
            frame.setSize(400, 300);
            frame.setLocationRelativeTo(null);
            frame.setVisible(true);

        });
    }

    private static String[] findBugFiles() {
        File dir = new File(".");
        return dir.list(new FilenameFilter() {
            @Override
            public boolean accept(File dir, String name) {
                return name.startsWith("WaitForOppMoveBug_");
            }
        });
    }


    // private static void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException {
    //     in.defaultReadObject();
    //     int v = in.
    // }

    private static void loadAndShow(String file) {
        try {
            ObjectInputStream is = new ObjectInputStream(new FileInputStream(file));
            try {
                WaitForOppMoveBug bug = (WaitForOppMoveBug) is.readObject();
                Match newMatch = new Match();
                newMatch.getPlayer(0).field.set(bug.newOpp);
                newMatch.getPlayer(1).field.set(bug.newOwn);

                var grid = IGrid.create();

                grid.rect(0, 0, 1, 1);
                grid.add(new JLabel("match"));

                grid.rect(1, 0, 1, 1);
                grid.add(new JLabel("matchCopy"));

                grid.rect(2, 0, 1, 1);
                grid.add(new JLabel("new fields"));

                grid.rect(0, 1, 1, 1);
                grid.fill().both();
                grid.weight().x(1);
                grid.weight().y(1);
                grid.add(new MatchView(bug.match, true, false));

                grid.rect(1, 1, 1, 1);
                // grid.fill().both();
                // grid.weight().x(1);
                // grid.weight().y(1);
                grid.add(new MatchView(bug.matchCopy, true, false));

                grid.rect(2, 1, 1, 1);
                grid.add(new MatchView(newMatch, true, false));

                var f = new JFrame();
                f.add(grid.asComponent());
                f.setBounds(100, 100, 800, 400);
                f.setVisible(true);

            } finally {
                is.close();
            }
        } catch (IOException ex) {
            ex.printStackTrace();
        } catch (ClassNotFoundException ex) {
            ex.printStackTrace();
        }

    }
}
