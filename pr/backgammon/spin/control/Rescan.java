package pr.backgammon.spin.control;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.ArrayList;

import javax.swing.JFrame;

import pr.backgammon.gnubg.model.GComment;
import pr.backgammon.gnubg.model.GNewMatch;
import pr.backgammon.gnubg.model.GSetBoardSimple;
import pr.backgammon.gnubg.model.GSetCrawford;
import pr.backgammon.gnubg.model.GSetCubeCenter;
import pr.backgammon.gnubg.model.GSetCubeOwner;
import pr.backgammon.gnubg.model.GSetCubeUse;
import pr.backgammon.gnubg.model.GSetCubeValue;
import pr.backgammon.gnubg.model.GSetDice;
import pr.backgammon.gnubg.model.GSetPlayerName;
import pr.backgammon.gnubg.model.GSetScore;
import pr.backgammon.gnubg.model.GSetTurn;
import pr.backgammon.gnubg.model.GnuCmd;
import pr.backgammon.model.Field;
import pr.backgammon.model.Match;
import pr.backgammon.spin.control.workers.ChatTextViaClipboard;
import pr.backgammon.spin.view.RescanDlg;

public class Rescan {
    private final ArrayList<GnuCmd> commands;
    private final Match match;
    private final BoardSearchers bs;
    private final SpinRolls spinRolls;
    private final TemplateSearchers ts;
    private final RescanCb cb;
    private final RescanDlg dlg;

    public Rescan(ArrayList<GnuCmd> commands, Match match, JFrame parent, BoardSearchers bs, SpinRolls spinRolls,
            TemplateSearchers ts,
            RescanCb cb) {
        this.commands = commands;
        this.match = match;
        this.bs = bs;
        this.spinRolls = spinRolls;
        this.ts = ts;
        this.cb = cb;
        dlg = new RescanDlg(parent);
        dlg.setVisible(true);
        dlg.scan.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent arg0) {
                try {
                    scanClicked();
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        });
        dlg.cancel.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                closeDlg();
                cb.canceled();
            }
        });
    }

    private void scanClicked() throws IOException {
        cb.onOk();
        closeDlg();

        var board = bs.boardShot()/* .getRaster() */;
        // var boardRaster = board.getRaster();
        FastChequerSearch chequers = new FastChequerSearch(bs.cal, ts);
        chequers.init(board);
        Field ownField = match.getPlayer(match.own).field;
        Field oppField = match.getPlayer(1 - match.own).field;
        chequers.getFields(ownField, oppField);
        spinRolls.detectFromBoardShot(board);

        if (!spinRolls.isOwnDice()) {
            cb.error("Eigener Spieler hat nicht gewürfelt wie vorausgesetzt.");
        }

        match.active = match.own;
        match.roll.die1 = spinRolls.die1();
        match.roll.die2 = spinRolls.die2();
        match.initialRoll = false;

        scanCube(board);

        cb.startWorker(new ChatTextViaClipboard(500) {
            @Override
            public void resultOnEventDispatchThread(String s) {
                System.out.println("rescan: result from chat text: '" + s + "'");
                final String state = "\n[server]: Spielstand ";
                final String end = "\n[server]: Endstand";
                final String crawford = "\n[server]: Aufgrund der Crawford-Regel";
                final int posState = s.lastIndexOf(state);
                final int posEnd = s.lastIndexOf(end);
                final int posCrawford = s.lastIndexOf(crawford);
                match.crawfordRound = posCrawford > posState && posCrawford > posEnd;

                System.out.println("posState " + posState + "  posEnd " + posEnd);

                if (posState == -1 || posEnd > posState) {
                    match.getPlayer(0).score = match.getPlayer(1).score = 0;
                    addCommands();
                    // closeDlg();
                    cb.done();
                    return;
                }

                assert (posState > posEnd);
                int after = posState + state.length();
                int colon = s.indexOf(':', after);
                if (colon == -1) {
                    // closeDlg();
                    cb.error("Unerwarteter Inhalt nach \"" + state + "\" im Chat.");
                    return;
                }
                int space = s.indexOf(' ', colon + 1);
                if (space == -1) {
                    // closeDlg();
                    cb.error("Unerwarteter Inhalt nach \"" + state + "\" im Chat.");
                    return;
                }
                String scoreWhiteStr = s.substring(after, colon);
                String scoreBlackStr = s.substring(colon + 1, space);
                try {
                    int scoreWhite = Integer.parseInt(scoreWhiteStr);
                    int scoreBlack = Integer.parseInt(scoreBlackStr);
                    int playerWhite = bs.cal.ownWhite ? match.own : 1 - match.own;
                    int playerBlack = bs.cal.ownWhite ? 1 - match.own : match.own;
                    match.getPlayer(playerWhite).score = scoreWhite;
                    match.getPlayer(playerBlack).score = scoreBlack;
                    addCommands();
                    cb.done();
                } catch (NumberFormatException ex) {
                    // closeDlg();
                    cb.error("Unerwartete Spielstandtexte im Chat: '" + scoreWhiteStr + "' und '"
                            + scoreBlackStr + "'.");
                    return;
                }
            }
        });

        // handsOffDlg.pack();
        // handsOffDlg.setVisible(true);

    }

    private void addCommands() {
        int scoreOpp = match.getPlayer(1 - match.own).score;
        int scoreOwn = match.getPlayer(match.own).score;

        commands.add(new GComment(""));
        commands.add(new GComment("Setze fort nach Rescan..."));
        commands.add(new GComment(""));
        commands.add(new GNewMatch(match.matchLen));
        commands.add(new GSetPlayerName(0, match.getPlayer(0).name));
        commands.add(new GSetPlayerName(1, match.getPlayer(1).name));
        commands.add(new GSetScore(scoreOpp, scoreOwn));
        commands.add(new GSetCrawford(match.crawfordRound));
        commands.add(new GSetTurn(1));
        commands.add(new GSetBoardSimple(match.getBoardSimple(null)));
        commands.add(new GSetCubeUse(match.cube.used));
        if (match.cube.used) {
            if (match.cube.owner == -1) {
                commands.add(new GSetCubeCenter());
            } else {
                commands.add(new GSetCubeOwner(match.cube.owner == match.own ? 1 : 0));
            }
            commands.add(new GSetCubeValue(match.cube.value));
        }
        commands.add(new GSetDice(match.roll.die1, match.roll.die2));
    }

    private void closeDlg() {
        if (dlg != null) {
            dlg.setVisible(false);
            dlg.dispose();
        }
    }

    private void scanCube(BufferedImage board) throws IOException {
        ScanUtils.scanCube(board, match, bs, ts);
        // Cube cube = match.cube;
        // cube.used = false;
        // cube.value = 1;
        // cube.owner = -1;
        // cube.offered = false;

        // if (bs.cubeEmpty.run(board) != null) {
        // cube.used = true;
        // return;
        // }

        // Searcher[] searchers = {
        // bs.cube2,
        // bs.cube4,
        // bs.cube8,
        // bs.cube16,
        // bs.cube32,
        // };
        // int[] vals = {
        // 2,
        // 4,
        // 8,
        // 16,
        // 32,
        // };

        // int midY = bs.cal.midY();

        // for (int i = 0; i < searchers.length; ++i) {
        // Point pos = searchers[i].run(board);
        // if (pos != null) {
        // cube.used = true;

        // if (pos.y < midY - bs.cal.dy * 3) {
        // // opponent owns the cube
        // cube.owner = 1 - match.own;
        // } else {
        // // own player owns the cube
        // cube.owner = match.own;
        // }

        // cube.value = vals[i];
        // return;
        // }
        // }
    }
}
