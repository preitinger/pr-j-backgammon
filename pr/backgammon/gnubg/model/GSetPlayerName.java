package pr.backgammon.gnubg.model;

import pr.backgammon.model.Match;

public class GSetPlayerName extends GnuCmd {
    private int player;
    private String name;

    public GSetPlayerName(int player, String name) {
        this.player = player;
        this.name = name;
    }

    // @Override
    public void runOn(Match m) {
        m.getPlayer(player).setName(name);
    }

    @Override
    protected void safeAppend(StringBuilder sb) {
        sb.append("set player ").append(player).append(" name ").append(name);
    }

}
