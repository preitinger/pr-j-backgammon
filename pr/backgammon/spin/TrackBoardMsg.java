package pr.backgammon.spin;

public class TrackBoardMsg {
    public String msg;

    public static TrackBoardMsg msg(String msg) {
        return new TrackBoardMsg(msg);
    }

    private TrackBoardMsg(String msg) {
        this.msg = msg;
    }
}
