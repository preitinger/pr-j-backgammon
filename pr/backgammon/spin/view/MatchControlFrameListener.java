package pr.backgammon.spin.view;

public interface MatchControlFrameListener {
    void onAutowurfChanged(boolean selected);
    void onClose();
    void onAlternativeControlChanged(boolean b);
}
