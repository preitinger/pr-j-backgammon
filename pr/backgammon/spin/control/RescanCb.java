package pr.backgammon.spin.control;

import pr.control.MyWorker;

public interface RescanCb {
    void onOk();
    void startWorker(MyWorker<?, ?> worker);
    void done();
    void canceled();
    void error(String error);
}
