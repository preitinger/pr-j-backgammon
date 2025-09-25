package pr.backgammon.gnubg.model;

public class BulkCommands extends GnuCmd {
    private final String commandLines;

    public BulkCommands(String commandLines) {
        this.commandLines = commandLines;
    }

    @Override
    protected void safeAppend(StringBuilder sb) {
        sb.append(commandLines);
        sb.append('\n');
    }
}
