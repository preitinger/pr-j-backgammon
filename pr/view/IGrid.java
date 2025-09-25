package pr.view;

import javax.swing.JComponent;

public interface IGrid {
    public static IGrid create() {
        return new Grid();
    }

    JComponent asComponent();
    void add(JComponent comp);
    void x(int v);
    void y(int v);
    void w(int v);
    void h(int v);
    void rect(int x, int y, int w, int h);
    public IWeight weight();
    public IFill fill();
    public IInsets insets();

    interface IWeight {
        void x(double v);
        void y(double v);
    }

    interface IFill {
        void none();
        void horizontal();
        void vertical();
        void both();
    }

    interface IInsets {
        void top(int v);
        void left(int v);
        void right(int v);
        void bottom(int v);
    }
}
