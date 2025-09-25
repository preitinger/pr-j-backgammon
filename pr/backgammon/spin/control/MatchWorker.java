package pr.backgammon.spin.control;

import pr.backgammon.spin.model.WorkerState;
import pr.control.MyWorker;

public abstract class MatchWorker<T> extends MyWorker<T, Void> {
    protected WorkerState state;

    public MatchWorker() {

    }

    public void setState(WorkerState state) {
        this.state = state;
    }
}
