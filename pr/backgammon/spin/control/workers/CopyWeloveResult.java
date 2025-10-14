package pr.backgammon.spin.control.workers;

import pr.control.MyWorker;

public abstract class CopyWeloveResult extends MyWorker<String, Void> {

    @Override
    public String doIt() throws Exception {
        String s = new ChatTextViaClipboard(0) {
            @Override
            public void resultOnEventDispatchThread(String result) {
            }
        }.doIt();

        final String end = "\n[server]: Endstand ";
        final String lastGame = " - Letztes Spiel";
        int posEnd = s.lastIndexOf(end);
        int posLastGame = s.lastIndexOf(lastGame);

        if (posEnd == -1 || posLastGame == -1 || posEnd >= posLastGame) {
            return "";
        }

        return s.substring(posEnd + end.length(), posLastGame);
    }

}
