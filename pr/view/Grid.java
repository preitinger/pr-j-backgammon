package pr.view;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;

import javax.swing.JComponent;
import javax.swing.JPanel;

public class Grid  implements IGrid {
    public final JPanel panel;
    public final GridBagLayout layout;
    public final GridBagConstraints constraints;

    public Grid() {
        panel = new JPanel();
        layout = new GridBagLayout();
        constraints = new GridBagConstraints();
        panel.setLayout(layout);
    }

    @Override
    public JComponent asComponent() {
        return panel;
    }

    @Override
    public void add(JComponent comp) {
        layout.setConstraints(comp, constraints);
        panel.add(comp);
    }

    public void x(int v) {
        constraints.gridx = v;
    }

    public void y(int v) {
        constraints.gridy = v;
    }

    public void w(int v) {
        constraints.gridwidth = v;
    }

    public void h(int v) {
        constraints.gridheight = v;
    }

    public void rect(int x, int y, int w, int h) {
        constraints.gridx = x;
        constraints.gridy = y;
        constraints.gridwidth = w;
        constraints.gridheight = h;
    }

    public final IWeight weight = new Weight();

    public final IWeight weight() {
        return weight;
    }

    public final IAnchor anchor = new Anchor();

    public final IAnchor anchor() {
        return anchor;
    }

    public final IFill fill = new Fill();

    public final IFill fill() {
        return fill;
    }

    public final IInsets insets = new Insets();
    public final IInsets insets() {
        return insets;
    }

    public class Weight implements IGrid.IWeight {
        public void x(double v) {
            constraints.weightx = v;
        }

        public void y(double v) {
            constraints.weighty = v;
        }
    }

    public class Fill implements IGrid.IFill {
        public void none() {
            constraints.fill = GridBagConstraints.NONE;
        }

        public void horizontal() {
            constraints.fill = GridBagConstraints.HORIZONTAL;
        }

        public void vertical() {
            constraints.fill = GridBagConstraints.VERTICAL;
        }

        public void both() {
            constraints.fill = GridBagConstraints.BOTH;
        }
    }

    public class Insets implements IInsets {
        public void top(int v) {
            constraints.insets.top = v;
        }
        public void left(int v) {
            constraints.insets.left = v;
        }
        public void right(int v) {
            constraints.insets.right = v;
        }
        public void bottom(int v) {
            constraints.insets.bottom = v;
        }
    }

    public class Anchor implements IAnchor {
        @Override
        public void center() {
            constraints.anchor = GridBagConstraints.CENTER;
        }
        @Override
        public void east() {
            constraints.anchor = GridBagConstraints.EAST;
        }
        @Override
        public void north() {
            constraints.anchor = GridBagConstraints.NORTH;
        }
        @Override
        public void northEast() {
            constraints.anchor = GridBagConstraints.NORTHEAST;
        }
        @Override
        public void northWest() {
            constraints.anchor = GridBagConstraints.NORTHWEST;
        }
        @Override
        public void south() {
            constraints.anchor = GridBagConstraints.SOUTH;
        }
        @Override
        public void southEast() {
            constraints.anchor = GridBagConstraints.SOUTHEAST;
        }
        @Override
        public void west() {
            constraints.anchor = GridBagConstraints.WEST;
        }
        @Override
        public void southWest() {
            constraints.anchor = GridBagConstraints.SOUTHWEST;
        }
        
    }
}
