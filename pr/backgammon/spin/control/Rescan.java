package pr.backgammon.spin.control;

import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.Raster;
import java.util.ArrayList;

import javax.swing.JFrame;

import pr.backgammon.gnubg.model.GComment;
import pr.backgammon.gnubg.model.GNewMatch;
import pr.backgammon.gnubg.model.GSetBoardSimple;
import pr.backgammon.gnubg.model.GSetCrawford;
import pr.backgammon.gnubg.model.GSetCubeCenter;
import pr.backgammon.gnubg.model.GSetCubeOwner;
import pr.backgammon.gnubg.model.GSetCubeValue;
import pr.backgammon.gnubg.model.GSetDice;
import pr.backgammon.gnubg.model.GSetScore;
import pr.backgammon.gnubg.model.GSetTurn;
import pr.backgammon.gnubg.model.GnuCmd;
import pr.backgammon.model.Cube;
import pr.backgammon.model.Field;
import pr.backgammon.model.Match;
import pr.backgammon.spin.control.workers.ChatTextViaClipboard;
import pr.backgammon.spin.view.HandsOffDlg;
import pr.backgammon.spin.view.RescanDlg;
import pr.control.Searcher;

public class Rescan {
    private final ArrayList<GnuCmd> commands;
    private final Match match;
    private final BoardSearchers bs;
    private final SpinRolls spinRolls;
    private final RescanCb cb;
    private final RescanDlg dlg;
    private HandsOffDlg handsOffDlg = null;

    public Rescan(ArrayList<GnuCmd> commands, Match match, JFrame parent, BoardSearchers bs, SpinRolls spinRolls,
            RescanCb cb) {
        this.commands = commands;
        this.match = match;
        this.bs = bs;
        this.spinRolls = spinRolls;
        this.cb = cb;
        dlg = new RescanDlg(parent);
        dlg.setVisible(true);
        dlg.scan.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent arg0) {
                scanClicked();
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

    private void scanClicked() {
        var board = bs.boardShot().getRaster();
        FastChequerSearch chequers = new FastChequerSearch(bs.cal);
        chequers.init(board);
        Field ownField = match.getPlayer(match.own).field;
        Field oppField = match.getPlayer(1 - match.own).field;
        chequers.getFields(ownField, oppField);
        spinRolls.detectFromBoardShot(board);

        if (!spinRolls.isOwnDice()) {
            closeDlg();
            cb.error("Eigener Spieler hat nicht gewürfelt wie vorausgesetzt.");
        }

        match.active = match.own;
        match.roll.die1 = spinRolls.die1();
        match.roll.die2 = spinRolls.die2();
        match.initialRoll = false;

        scanCube(board);

        handsOffDlg = new HandsOffDlg(dlg, new Runnable() {
            @Override
            public void run() {
                // on ok
                new ChatTextViaClipboard(500) {
                    @Override
                    public void resultOnEventDispatchThread(String s) {
                        handsOffDlg.setVisible(false);
                        handsOffDlg.dispose();
                        handsOffDlg = null;

                        final String state = "\n[server]: Spielstand ";
                        final String end = "\n[server]: Endstand";
                        final String crawford = "\n[server]: Aufgrund der Crawford-Regel";
                        final int posState = s.lastIndexOf(state);
                        final int posEnd = s.lastIndexOf(end);
                        final int posCrawford = s.lastIndexOf(crawford);
                        match.crawfordRound = posCrawford > posState && posCrawford > posEnd;

                        if (posState == -1 || posEnd > posState) {
                            match.getPlayer(0).score = match.getPlayer(1).score = 0;
                            addCommands();
                            return;
                        }

                        assert (posState > posEnd);
                        int after = posState + state.length();
                        int colon = s.indexOf(':', after);
                        if (colon == -1) {
                            closeDlg();
                            cb.error("Unerwarteter Inhalt nach \"" + state + "\" im Chat.");
                            return;
                        }
                        int space = s.indexOf(' ', colon + 1);
                        if (space == -1) {
                            closeDlg();
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
                        } catch (NumberFormatException ex) {
                            closeDlg();
                            cb.error("Unerwartete Spielstandtexte im Chat: '" + scoreWhiteStr + "' und '"
                                    + scoreBlackStr + "'.");
                            return;
                        }
                    }
                }.execute();
            }
        }, new Runnable() {
            @Override
            public void run() {
                
            }
        });
        handsOffDlg.pack();
        handsOffDlg.setVisible(true);

    }

    private void addCommands() {
        int scoreOpp = match.getPlayer(1 - match.own).score;
        int scoreOwn = match.getPlayer(match.own).score;

        commands.add(new GComment(""));
        commands.add(new GComment("Faden verloren, setze neu auf..."));
        commands.add(new GComment(""));
        commands.add(new GNewMatch(match.matchLen));
        commands.add(new GSetScore(scoreOpp, scoreOwn));
        commands.add(new GSetCrawford(match.crawfordRound));
        commands.add(new GSetTurn(1));
        commands.add(new GSetBoardSimple(match.getBoardSimple(null)));
        if (match.cube.used) {
            if (match.cube.owner == -1) {
                commands.add(new GSetCubeCenter());
            } else {
                commands.add(new GSetCubeOwner(match.cube.owner == match.own ? 1 : 0));
            }
            commands.add(new GSetCubeValue(match.cube.value));
        }
        commands.add(new GSetDice(match.roll.die1, match.roll.die2));
        closeDlg();
        cb.done();
    }

    private void closeDlg() {
        if (dlg != null) {
            dlg.setVisible(false);
            dlg.dispose();
        }
    }

    private void scanCube(Raster board) {
        Cube cube = match.cube;
        cube.used = false;
        cube.value = 1;
        cube.owner = -1;
        cube.offered = false;

        if (bs.cubeEmpty.run(board) != null) {
            cube.used = true;
            return;
        }

        Searcher[] searchers = {
                bs.cube2,
                bs.cube4,
                bs.cube8,
                bs.cube16,
                bs.cube32,
        };
        int[] vals = {
                2,
                4,
                8,
                16,
                32,
        };

        int midY = bs.cal.midY();

        for (int i = 0; i < searchers.length; ++i) {
            Point pos = searchers[i].run(board);
            if (pos != null) {
                cube.used = true;
                
                if (pos.y < midY - bs.cal.dy * 3) {
                    // opponent owns the cube
                    cube.owner = 1 - match.own;
                } else {
                    // own player owns the cube
                    cube.owner = match.own;
                }

                cube.value = vals[i];
                return;
            }
        }
    }
}
