package pr.backgammon.jokers.control;

import javax.json.Json;
import javax.json.JsonArray;

import pr.backgammon.jokers.model.Joker;
import pr.backgammon.jokers.model.JokerTableModel;
import pr.backgammon.jokers.view.JokersView;
import pr.backgammon.model.Match;

public class AllJokers {
    private static final BaseJoker[] jokerControls = createJokerControls();
    
    private static final BaseJoker[] createJokerControls() {
        BaseJoker[] r = new BaseJoker[1 + 6];
        r[0] = new Prime5FromBarEscape();
        for (int die = 1; die <= 6; ++die) {
            r[die] = new DoubleFromBar(die);
        }

        // TODO Append more jokers here.
        // Also see the activity addJoker in pr-home/app/_lib/count-jokers/CountJokersAdminPage.tsx
        // to add a new joker to the MongoDB.

        return r;
    }
    private final Joker[] jokers;
    private final JokerTableModel tableModel;
    private final JokersView view;

    public AllJokers() {
        jokers = new Joker[jokerControls.length];

        for (int i = 0; i < jokerControls.length; ++i) {
            jokers[i] = new Joker();
            jokerControls[i].init(jokers[i]);
        }

        this.tableModel = new JokerTableModel(jokers);
        this.view = new JokersView(tableModel);
    }

    public JokersView getView() {
        return view;
    }

    public void count(Match m) {
        for (int i = 0; i < jokerControls.length; ++i) {
            var control = jokerControls[i];
            var joker = jokers[i];
            switch (control.evaluate(m, joker)) {
                case POSITIVE:
                    tableModel.firePosUpdate(i);
                    break;
                case NEGATIVE:
                    tableModel.fireNegUpdate(i);
                    break;
                default:
                    break;
            }
        }
    }

    public JsonArray toJson() {
        var b = Json.createArrayBuilder();
        
        for (var x : jokers) {
            b.add(Json.createObjectBuilder().add("name", x.name).add("pos", x.pos).add("neg", x.neg));
        }

        return b.build();
    }

    public void reset() {
        for (var joker : jokers) {
            joker.pos = 0;
            joker.neg = 0;
        }
        tableModel.fireTableDataChanged();
    }
}
