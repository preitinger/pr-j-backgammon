package pr.backgammon.model;

import pr.model.Mutable;
import pr.model.MutableIntArray;

public class FindTask implements Mutable<FindTask> {
    private static final long serialVersionUID = 1L;

    public final MutableIntArray move = new MutableIntArray(8);
    public final MutableIntArray availableDice = new MutableIntArray(4);
    public final Field active = new Field(), other = new Field();

    @Override
    public void set(FindTask other) {
        move.set(other.move);
        availableDice.set(other.availableDice);
        this.active.set(other.active);
        this.other.set(other.other);
    }

    public StringBuilder append(StringBuilder sb) {
        if (sb == null) {
            sb = new StringBuilder();
        }

        sb.append("move:");
        move.append(sb);
        sb.append('\n');
        sb.append("available Dice: ");
        availableDice.append(sb);
        sb.append('\n');
        sb.append("active field:\n");
        active.append(sb);
        sb.append("\nother field:\n");
        return other.append(sb);
    }
    
}

