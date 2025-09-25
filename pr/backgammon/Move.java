package pr.backgammon;

import java.util.ArrayList;

@Deprecated
public class Move extends ArrayList<PartMove> {
    public void add(int from, int to, boolean hit) {
        add(new PartMove(from, to, hit));
    }

    public String toString() {
        StringBuilder sb = new StringBuilder();
        int n = this.size();

        for (int i = 0; i < n; ++i) {
            PartMove pm = this.get(i);
            if (i > 0)
                sb.append(' ');
            pm.append(sb);
        }

        return sb.toString();
    }
}
