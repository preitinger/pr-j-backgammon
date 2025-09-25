package pr.backgammon.jokers.view;

import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.TableModel;

public class JokersView extends JScrollPane {

    private final JTable table;

    public JokersView(TableModel tableModel) {
        super();
        setViewportView(table = new JTable(tableModel));
    }
}
