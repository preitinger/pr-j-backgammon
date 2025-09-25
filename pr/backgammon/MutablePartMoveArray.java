package pr.backgammon;

import pr.model.MutableArray;

@Deprecated
public class MutablePartMoveArray extends MutableArray<MutablePartMove> {

    public MutablePartMoveArray(int capacity) {
        super(capacity);
    }

    @Override
    protected MutablePartMove createInstance() {
        return new MutablePartMove();
    }
    
}
