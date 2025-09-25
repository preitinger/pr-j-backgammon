package pr.backgammon.jokers.model;

import javax.swing.table.AbstractTableModel;

public class JokerTableModel extends AbstractTableModel {
    private final Joker[] jokers;

    public JokerTableModel(Joker[] jokers) {
        this.jokers = jokers;
    }

    @Override
    public int getColumnCount() {
        return 5;
    }

    @Override
    public int getRowCount() {
        return jokers.length;
    }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        Joker joker = jokers[rowIndex];

        switch (columnIndex) {
            case 0:
                return joker.name;
            case 1:
                return joker.pos;
            case 2:
                return joker.neg;
            case 3:
                return joker.pos + joker.neg == 0 ? null : joker.pos / (joker.pos + joker.neg);
            case 4:
                return joker.prob;
            default:
                throw new IllegalArgumentException();
        }
    }

    

    @Override
    public String getColumnName(int column) {
        switch (column) {
            case 0:
                return "Name";
            case 1:
                return "Pos.";
            case 2:
                return "Neg.";
            case 3:
                return "Häufigk.";
            case 4:
                return "Wahrsch.";
            default:
                return "No column";
        }
    }

    public void firePosUpdate(int rowIndex) {
        this.fireTableCellUpdated(rowIndex, 1);
        this.fireTableCellUpdated(rowIndex, 3);
    }

    public void fireNegUpdate(int rowIndex) {
        this.fireTableCellUpdated(rowIndex, 2);
        this.fireTableCellUpdated(rowIndex, 3);
    }
}
