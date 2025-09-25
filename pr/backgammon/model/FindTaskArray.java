package pr.backgammon.model;

import pr.model.MutableArray;

public class FindTaskArray extends MutableArray<FindTask> {
    public FindTaskArray(int capacity) {
        super(capacity);
    }

    @Override
    protected FindTask createInstance() {
        return new FindTask();
    }
}