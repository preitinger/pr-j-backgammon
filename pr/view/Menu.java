package pr.view;


import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.util.ArrayList;

import javax.swing.AbstractAction;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.SwingConstants;

import pr.model.MenuItem;

public class Menu extends JScrollPane {
    public Menu(boolean horizontal) {
        // sp.setPreferredSize(new Dimension(200, 50));
        panel.setLayout(new GridLayout(horizontal ? 1 : 0, horizontal ? 0 : 1, 5, 5));
        this.setViewportView(panel);
    }

    public void setListener(MenuListener l) {
        this.listener = l;
    }

    @Override
    public Dimension getPreferredSize() {
        Dimension size = panel.getPreferredSize();
        size.width += 20;
        size.height += 20;
        return size;
    }

    @Override
    public boolean isValidateRoot() {
        return false; // weil getPreferredSize() vom Inhalt abhaengt
    }

    @Override
    public Dimension getMinimumSize() {
        Dimension size = panel.getMinimumSize();
        size.width += 20;
        size.height += 20;
        return size;
    }

    public void setItems(MenuItem... items) {
        clear();
        
        for (MenuItem item : items) {
            add(item);
        }

        revalidate();
    }

    public void setItems(Iterable<MenuItem> items) {
        clear();
        
        for (MenuItem item : items) {
            add(item);
        }
    }

    public void add(MenuItem item) {
        items.add(item);
        JButton b = new JButton(new AbstractAction(item.label) {

            @Override
            public void actionPerformed(ActionEvent e) {
                if (listener != null) {
                    listener.onSelect(item.event);
                }
                
            }
            
        });
        b.setHorizontalAlignment(SwingConstants.LEFT);
        panel.add(b);

    }

    public void add(String label, String event) {
    }

    public void clear() {
        panel.removeAll();
        items.clear();
    }


    private final ArrayList<MenuItem> items = new ArrayList<>();
    private final JPanel panel = new JPanel();
    private MenuListener listener = null;
}
