package pr.backgammon.spin.view;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.StringSelection;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JTextField;

import pr.view.IGrid;

public class WeloveResultDlg extends JDialog {
    public WeloveResultDlg(JFrame parent, String result) {
        super(parent, "Generierte Welove-Ergebnismeldung zum Kopieren");
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);

        setLayout(new BorderLayout(10, 10));
        IGrid grid = IGrid.create();
        grid.rect(0, 0, 1, 1);
        grid.fill().both();
        grid.weight().x(1);
        grid.weight().y(1);
        JTextField tf = new JTextField(result);
        // tf.setBorder(new EmptyBorder(20, 20, 20, 20));
        tf.setEditable(false);
        Dimension prefSize = tf.getPreferredSize();
        prefSize.width = 200;
        tf.setPreferredSize(prefSize);
        grid.add(tf);
        
        JButton copy = new JButton("Kopieren");
        grid.rect(1, 0, 1, 1);
        grid.fill().none();
        grid.anchor().west();
        grid.add(copy);

        copy.addActionListener((e) -> {
            Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
            if (clipboard.isDataFlavorAvailable(DataFlavor.stringFlavor)) {
                StringSelection stringSelection = new StringSelection(result);
                clipboard.setContents(stringSelection, stringSelection);
                copy.setText("OK, kopiert.");
            }

        });

        add(grid.asComponent());

        setSize(400, 200);
    }
}
