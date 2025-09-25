package pr.backgammon.spin.control;

public interface RescanCb {
    void done();
    void canceled();
    void error(String error);
}
