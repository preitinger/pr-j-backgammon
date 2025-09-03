package pr.backgammon;

import java.util.Map;
import java.util.TreeMap;

public class DiceMultiSet {
    private TreeMap<Integer, Integer> m = new TreeMap<>();

    public void add(int die) {
        Integer old = m.get(die);
        m.put(die, (old == null ? 0 : old) + 1);
    }

    public Integer removeEqualOrGreater(int dist) {
        for (int i = dist; i <= 6; ++i) {
            if (tryRemove(i)) {
                return i;
            }
        }

        return null;
    }

    private boolean tryRemove(int die) {
        Integer old = m.get(die);
        if (old == null || old < 1)
            return false;

        if (old > 1) {
            m.put(die, old - 1);
        } else {
            m.remove(die);
        }
        return true;
    }

    public boolean isEmpty() {
        for (Map.Entry<Integer, Integer> entry : m.entrySet()) {
            Integer val = entry.getValue();
            if (val != null && val > 0) {
                return false;
            }
        }

        return true;
    }

    public boolean contains(int die) {
        Integer i = m.get(die);
        return i != null && i > 0;
    }
}
