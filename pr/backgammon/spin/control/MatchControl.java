package pr.backgammon.spin.control;

import java.awt.GraphicsEnvironment;
import java.awt.MouseInfo;
import java.awt.Point;
import java.awt.Rectangle;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;

import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;

import pr.backgammon.control.AllMoves;
import pr.backgammon.control.Move;
import pr.backgammon.control.OwnMove;
import pr.backgammon.control.OwnMoveCb;
import pr.backgammon.control.RandomRoll;
import pr.backgammon.gnubg.model.BulkCommands;
import pr.backgammon.gnubg.model.GAccept;
import pr.backgammon.gnubg.model.GComment;
import pr.backgammon.gnubg.model.GDouble;
import pr.backgammon.gnubg.model.GMove;
import pr.backgammon.gnubg.model.GNewGame;
import pr.backgammon.gnubg.model.GNewMatch;
import pr.backgammon.gnubg.model.GReject;
import pr.backgammon.gnubg.model.GResign;
import pr.backgammon.gnubg.model.GSetDice;
import pr.backgammon.gnubg.model.GSetPlayerName;
import pr.backgammon.gnubg.model.GSetTurn;
import pr.backgammon.gnubg.model.GnuCmd;
import pr.backgammon.jokers.control.AllJokers;
import pr.backgammon.jokers.control.JokersUploader;
import pr.backgammon.jokers.control.Utils;
import pr.backgammon.model.Field;
import pr.backgammon.model.Match;
import pr.backgammon.model.Player;
import pr.backgammon.spin.control.workers.Calibrate;
import pr.backgammon.spin.control.workers.CalibrateRes;
import pr.backgammon.spin.control.workers.ClickBoardButton;
import pr.backgammon.spin.control.workers.ClickOwnMove;
import pr.backgammon.spin.control.workers.ClickRoll;
import pr.backgammon.spin.control.workers.OppDoubleReaction;
import pr.backgammon.spin.control.workers.OppRollOrDouble;
import pr.backgammon.spin.control.workers.OwnResign;
import pr.backgammon.spin.control.workers.StartMatch;
import pr.backgammon.spin.control.workers.StartMatchRes;
import pr.backgammon.spin.control.workers.WaitForFirstRoll;
import pr.backgammon.spin.control.workers.WaitForOppMove;
import pr.backgammon.spin.control.workers.WaitForOppMoveRes;
import pr.backgammon.spin.model.WorkerState;
import pr.backgammon.spin.view.HandsOffDlg;
import pr.backgammon.spin.view.MatchControlFrame;
import pr.backgammon.spin.view.MatchControlFrameListener;
import pr.backgammon.view.MatchView;
import pr.control.MyRobot;
import pr.control.MyWorker;
import pr.control.Searcher;
import pr.control.Tools;
import pr.model.MenuItem;
import pr.model.MutableIntArray;
import pr.view.Menu;
import pr.view.MenuListener;

public class MatchControl implements MenuListener, MatchControlFrameListener {
    private MatchView matchView;

    private final MatchControlFrame frame;
    private final Menu menu;
    private final MenuItem calibrate, start, roll, doDouble, reset, accept, reject, rescan, resign1, resign2, resign3;
    private CalibrationForSpin calWhite = null, calBlack = null, cal = null;
    private BoardSearchers bs;
    private SpinRolls spinRolls;
    private ScreenSearchers s;
    private final Rectangle screenRect;
    private final Match match = new Match();
    private final WorkerState workerState = new WorkerState();
    private int handsOffCount = 0;
    private HandsOffDlg handsOffDlg = null;
    private Point lastMouse;
    private final ArrayList<GnuCmd> commands = new ArrayList<>();
    private final StringBuilder tmpsb = new StringBuilder();
    private MyWorker<?, ?> runningWorker = null;
    private boolean autoroll;
    private OwnMove ownMove = null;
    private final AllJokers ownJokers, oppJokers;

    public MatchControl() throws IOException {
        ownJokers = new AllJokers();
        oppJokers = new AllJokers();
        matchView = new MatchView(null, true, false);
        menu = new Menu(true);
        calibrate = new MenuItem("Kalibrieren", "calibrate");
        start = new MenuItem("Match starten", "start");
        roll = new MenuItem("Würfeln", "roll");
        doDouble = new MenuItem("Doppeln", "double");
        reset = new MenuItem("Match stoppen", "stop");
        accept = new MenuItem("Annehmen", "accept");
        reject = new MenuItem("Ablehnen (Eigenen Zug evtl. abschließen!)", "reject");
        rescan = new MenuItem("Rescan", "rescan");
        resign1 = new MenuItem("Aufgabe einfach", "resign1");
        resign2 = new MenuItem("Aufgabe Gammon", "resign2");
        resign3 = new MenuItem("Aufgabe Backgammon", "resign3");

        frame = new MatchControlFrame(matchView, menu, ownJokers.getView(), oppJokers.getView(), this);
        menu.setListener(this);
        // We assume that all screens are horizontally aligned and sum up all widths
        int wsum = 0, hmax = 0;
        var devices = GraphicsEnvironment.getLocalGraphicsEnvironment().getScreenDevices();
        for (var device : devices) {
            var dm = device.getDisplayMode();
            wsum += dm.getWidth();
            hmax = Math.max(hmax, dm.getHeight());
        }
        screenRect = new Rectangle(0, 0, wsum, hmax);
        s = new ScreenSearchers(screenRect);
    }

    public void run() {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                menu.setItems(calibrate);
                frame.setVisible(true);
                // s = new Sear
            }
        });
    }

    @Override
    public void onSelect(String event) {

        switch (event) {
            case "calibrate":
                exeWorker(new Calibrate(s) {
                    @Override
                    public void resultOnEventDispatchThread(CalibrateRes res) {
                        if (res.error != null) {
                            System.out.println(res.error);
                        } else {
                            if (res.calWhite.ownWhite && !res.calBlack.ownWhite) {
                                calWhite = res.calWhite;
                                calBlack = res.calBlack;
                                FastChequerSearch chequers = new FastChequerSearch(calWhite);
                                try {
                                    bs = new BoardSearchers(calWhite, chequers.boardScreenshotRect(null));
                                    menu.setItems(start);
                                } catch (IOException ex) {
                                    ex.printStackTrace();
                                }
                            }
                        }
                    }
                });
                break;
            case "start":
                if (calWhite == null || calBlack == null) {
                    JOptionPane.showMessageDialog(frame, "Kalibrierung prüfen!");
                    return;
                }
                exeWorker(new StartMatch(calWhite, calBlack, bs) {

                    @Override
                    public void resultOnEventDispatchThread(StartMatchRes result) {
                        if (result.error != null) {
                            frame.appendText("FEHLER: " + result.error + "\n");
                        } else {
                            commands.clear();
                            ownJokers.reset();
                            oppJokers.reset();
                            addCmd(new GNewMatch(result.match.matchLen));
                            workerState.game = 1;
                            addCmd(new GSetPlayerName(0, result.match.getPlayer(0).name));
                            addCmd(new GSetPlayerName(1, result.match.getPlayer(1).name));
                            try {
                                // BufferedReader r = new BufferedReader(new FileReader(null))
                                BufferedReader r = new BufferedReader(new InputStreamReader(Tools.readResourceFile(
                                        result.match.cube.used ? "pr/res/gnubg/gnubg-mit-doppeln.gnubg"
                                                : "pr/res/gnubg/gnubg-ohne-doppeln.gnubg")));
                                tmpsb.setLength(0);
                                r.lines().forEach(l -> tmpsb.append(l).append('\n'));
                                addCmd(new BulkCommands(tmpsb.toString()));
                            } catch (IOException ex) {
                                ex.printStackTrace();
                            }
                            match.set(result.match);

                            frame.setAutoroll(autoroll = true);
                            frame.setAutorollEnabled(true);
                            cal = result.cal;
                            bs = result.bs;
                            spinRolls = result.spinRolls;
                            matchView.setOwnWhite(cal.ownWhite);
                            matchView.setClockwise(!cal.ownWhite);
                            matchView.setMatch(match, workerState.ongoingMove);

                            if (match.active == -1) {
                                // Rescan notwendig
                                JOptionPane.showMessageDialog(frame, "Rescan notwendig, da Match!");
                                menu.setItems(rescan, reset);
                            } else {

                                StringBuilder sb = new StringBuilder("Initialer Wurf: ");
                                result.match.roll.append(sb).append('\n');
                                frame.appendText(sb.toString());

                                if (result.match.active == 1 - match.own) {
                                    addCmd(new GSetTurn(0));
                                    addCmd(new GSetDice(match.roll.die1, match.roll.die2));
                                    oppMove();
                                } else if (result.match.active == match.own) {
                                    addCmd(new GSetTurn(1));
                                    addCmd(new GSetDice(match.roll.die1, match.roll.die2));
                                    ownMove();
                                } else {
                                    throw new IllegalStateException(
                                            "match.active unexpected during initial roll: " + match.active);
                                }
                            }

                        }
                    }

                });
                break;
            case "stop":
                stopMatch();
                break;

            case "accept":
                if (ownPlayer().resign > 0) {
                    exeWorker(new ClickBoardButton(bs, bs.jaUnpressed) {
                        @Override
                        public void resultOnEventDispatchThread(Boolean successful) {
                            if (!successful) {
                                frame.appendText("FEHLER: Button Ja  nicht gefunden!");
                                return;
                            }

                            match.acceptResign();
                            matchView.setMatch(match, workerState.ongoingMove);
                            addCmd(new GAccept());

                            if (match.finished()) {
                                closeMatch();
                                return;
                            }

                            addCmd(new GComment(""));
                            addCmd(new GComment("Spiel " + ++workerState.game));
                            addCmd(new GComment(""));
                            addCmd(new GNewGame());
                            waitForFirstRoll();
                        }
                    });
                } else if (match.cube.offered) {
                    if (match.cube.owner != match.own) {
                        throw new IllegalStateException("We were not offered a double?!");
                    }
                    // Achtung, kann Take fuer normales oder Redoppel sein!
                    exeWorker(new ClickBoardButton(bs, bs.annehmenUnpressed) {
                        @Override
                        public void resultOnEventDispatchThread(Boolean result) {
                            if (!result) {
                                frame.appendText("FEHLER: Button Annehmen nicht gefunden!");
                                return;
                            }
                            match.take();
                            matchView.setMatch(match, workerState.ongoingMove);
                            // konnte redouble gewesen sein
                            if (match.active == match.own) {
                                // ja war redoppel auf unser doppel
                                addCmd(new GAccept());
                                addCmd(new GSetTurn(1));
                                menu.setItems(rescan, reset);
                                clickOwnRoll(false);
                            } else {
                                // war normales take auf normales doppel vom gegner
                                addCmd(new GAccept());
                                menu.setItems(rescan, reset);
                                oppRollOrDouble(); // eigentlich nur roll aber passt erst recht
                            }
                        }
                    });
                }
                break;
            case "reject":
                if (ownPlayer().resign > 0) {
                    exeWorker(new ClickBoardButton(bs, bs.neinUnpressed) {
                        @Override
                        public void resultOnEventDispatchThread(Boolean successful) {
                            if (!successful) {
                                frame.appendText("FEHLER: Button Nein  nicht gefunden!");
                                return;
                            }

                            match.resetResign();
                            addCmd(new GReject());

                            menu.setItems(rescan, reset);

                            if (match.active == match.own) {
                                if (match.roll.isEmpty()) {
                                    ownRoll(false);
                                } else {
                                    addCmd(new GSetDice(match.roll.die1, match.roll.die2));
                                    matchView.setMatch(match, workerState.ongoingMove);
                                    ownMove();
                                }
                            } else if (match.active == 1 - match.own) {
                                matchView.setMatch(match, workerState.ongoingMove);

                                if (match.roll.isEmpty()) {
                                    oppRollOrDouble();
                                } else {
                                    addCmd(new GSetDice(match.roll.die1, match.roll.die2));
                                    oppMove();
                                }
                            }

                        }
                    });
                } else if (match.cube.offered) {
                    if (match.cube.owner != match.own) {
                        throw new IllegalStateException("We were not offered a double?!");
                    }
                    exeWorker(new ClickBoardButton(bs, bs.aufgebenUnpressed) {
                        @Override
                        public void resultOnEventDispatchThread(Boolean result) {
                            if (!result) {
                                frame.appendText("FEHLER: Button Aufgeben nicht gefunden!");
                                return;
                            }

                            match.drop();
                            matchView.setMatch(match, workerState.ongoingMove);
                            addCmd(new GReject());

                            addCmd(new GComment(""));
                            addCmd(new GComment("Spiel " + ++workerState.game));
                            addCmd(new GComment(""));
                            addCmd(new GNewGame());

                            waitForFirstRoll();
                        }
                    });

                }
                break;
            case "roll":
                menu.setItems(rescan, reset);
                if (match.active == match.own && match.roll.isEmpty()) {
                    clickOwnRoll(false);
                }
                break;
            case "double":
                System.out.println("own double");
                menu.setItems(rescan, reset);
                if (match.active == match.own && match.roll.isEmpty() && match.cube.owner != 1 - match.own) {
                    Searcher searcher = bs.cubeSearcher(match.cube.value);
                    exeWorker(new ClickBoardButton(bs, searcher) {
                        @Override
                        public void resultOnEventDispatchThread(Boolean result) {
                            System.out.println("result from ClickBoardButton for double: " + result);
                            if (result) {
                                match.cube.owner = 1 - match.own;
                                match.cube.value <<= 1;
                                match.cube.offered = true;
                                addCmd(new GDouble());
                                matchView.setMatch(match, workerState.ongoingMove);
                                System.out.println("starting OppDoubleReaction");
                                exeMatchWorker(new OppDoubleReaction(bs, spinRolls) {
                                    @Override
                                    public void resultOnEventDispatchThread(Void result) {
                                        System.out.println("OppDoubleReaction returned");
                                        matchView.setMatch(match, workerState.ongoingMove);
                                        if (ownPlayer().resign > 0) {
                                            ownResignResponse();
                                        } else if (match.cube.offered) {
                                            // opp has redoubled
                                            System.out.println("opp has redoubled");
                                            if (match.cube.owner != match.own) {
                                                throw new IllegalStateException(
                                                        "we do not own the cube after redouble from opp?!");
                                            }
                                            addCmd(new GAccept());
                                            // addCmd(new GSetCubeOwner(1));
                                            // addCmd(new GSetCubeValue(match.cube.value));
                                            addCmd(new GSetTurn(0));
                                            addCmd(new GDouble());

                                            ownDoubleResponse();
                                        } else if (match.cube.owner == 1 - match.own) {
                                            // opp has taken
                                            System.out.println("opp has taken");
                                            addCmd(new GAccept());
                                            clickOwnRoll(false);
                                        } else {
                                            // Opponent has dropped. New game, or because spin is allowing dead cubes,
                                            // end of the match
                                            System.out.println("opp has dropped");
                                            addCmd(new GReject());
                                            waitForFirstRoll();
                                        }
                                    }
                                });
                            } else {
                                System.out.println(
                                        "Achtung! Klick auf den Doppel-Button konnte seltsamerweise nicht ausgefuehrt werden. Versuche eigenen Wurf...");
                                ownRoll(false);
                            }

                        }
                    });
                } else {
                    System.out.println("may not double?!");
                }
                break;

            case "rescan":

                int oldCommands = commands.size();
                new Rescan(this.commands, match, frame, bs, spinRolls, new RescanCb() {
                    @Override
                    public void onOk() {
                        workerState.ongoingMove.move.clear();
                        workerState.ongoingMove.hits.clear();
                        workerState.ongoingMove.highlightedPip = -1;
                        workerState.ongoingMove.hoveredField = -1;
                        matchView.setListener(null);

                        if (runningWorker != null) {
                            runningWorker.cancel(true);
                            try {
                                runningWorker.resultNow();
                            } catch (Exception ex) {
                                ex.printStackTrace();
                                try {
                                    runningWorker.exceptionNow();
                                } catch (Exception ex2) {
                                    ex2.printStackTrace();
                                }
                            }
                        }

                        handsOffCount = 0;

                    }

                    @Override
                    public void canceled() {
                        frame.appendText("Rescan wurde abgebrochen.\n");
                    }

                    public void startWorker(pr.control.MyWorker<?, ?> worker) {
                        exeWorker(worker);
                    }

                    @Override
                    public void done() {

                        frame.appendText("Rescan abgeschlossen.\n");
                        tmpsb.setLength(0);

                        for (int i = oldCommands; i < commands.size(); ++i) {
                            commands.get(i).append(tmpsb);
                        }

                        String toAppend = tmpsb.toString();
                        frame.appendText(toAppend);
                        frame.appendCmd(toAppend);

                        ownMove();
                    }

                    @Override
                    public void error(String error) {
                        frame.appendText("\nFEHLER beim Rescan: " + error + "\n\n");
                    }
                });
                break;

            case "resign1":
                ownResign(1);
                break;
            case "resign2":
                ownResign(2);
                break;
            case "resign3":
                ownResign(3);
                break;
        }
    }

    private boolean stopMatch() {
        if (match.active == -1) {
            return true;
        }

        int option = JOptionPane.showOptionDialog(frame,
                "Match beenden\n1 - Match beenden und Protokoll, Würfe und Joker speichern\n2 - Match abbrechen ohne Speichern\n3 - Match nicht beenden, weiter spielen",
                "pr-j-backgammon", 2,
                JOptionPane.QUESTION_MESSAGE, null,
                new String[] { "1 - Match beenden und speichern",
                        "2 - Match abbrechen ohne Speichern ", "3 - Weiter spielen" },
                null);

        if (option == 2) {
            return false;
        }
        if (ownMove != null) {
            ownMove.cancel();
            ownMove = null;
        }
        if (runningWorker != null) {
            runningWorker.cancel(true);
        }
        matchView.setListener(null);
        if (option == 0) {
            closeMatch();
        }
        menu.setItems(start);
        return true;
    }

    private void ownResign(int val) {
        if (match.getPlayer(0).field.isInitial() && match.getPlayer(1).field.isInitial()) {
            JOptionPane.showMessageDialog(frame,
                    "Eigene Aufgabe in Initialposition der Einfachheit halber nicht erlaubt!");
            return;
        }

        if (ownMove != null) {
            ownMove.cancel();
            ownMove = null;
        }

        exeMatchWorker(new OwnResign(bs, val) {
            @Override
            public void resultOnEventDispatchThread(Void result) {
                addCmd(new GSetTurn(1));
                addCmd(new GResign(val));

                if (match.finished()) {
                    addCmd(new GAccept());
                    closeMatch();
                    return;
                }
                if (match.gameStarting()) {
                    // Der Sicherheit halber set turn auf eigenen spieler
                    addCmd(new GAccept());
                    addCmd(new GComment(""));
                    addCmd(new GComment("Spiel " + ++workerState.game));
                    addCmd(new GComment(""));
                    addCmd(new GNewGame());
                    assert (state == workerState);
                    addCmd(new GComment(""));
                    waitForFirstRoll();
                    return;
                }

                // Gegner hat unsere Aufgabe abgelehnt
                JOptionPane.showMessageDialog(frame, "Gegner lehnte die Aufgabe ab.");
                ownMove();

            }
        });
    }

    private void waitForFirstRoll() {
        // New game is starting, wait for the spin server to send the first roll which
        // can be an initial roll or an own roll or an opponent roll because it is spin
        // ... ;-)
        menu.setItems(rescan, reset);
        exeMatchWorker(new WaitForFirstRoll(bs, spinRolls) {
            @Override
            public void resultOnEventDispatchThread(Void result) {
                System.out.println("result from WaitForFirstroll");
                matchView.setMatch(match, workerState.ongoingMove);

                if (ownPlayer().resign > 0) {
                    ownResignResponse();
                } else if (match.active == 1 - match.own) {
                    addCmd(new GSetTurn(0));
                    addCmd(new GSetDice(match.roll.die1, match.roll.die2));
                    oppMove();
                } else if (match.active == 1) {
                    addCmd(new GSetTurn(1));
                    addCmd(new GSetDice(match.roll.die1, match.roll.die2));
                    ownMove();
                }
            }
        }, false);
    }

    private Player ownPlayer() {
        return match.getPlayer(match.own);
    }

    private Player oppPlayer() {
        return match.getPlayer(1 - match.own);
    }

    private void ownResignResponse() {
        System.out.println("ownResignResponse");
        int resign = match.getPlayer(match.own).resign;
        addCmd(new GResign(resign));
        matchView.setMatch(match, workerState.ongoingMove);

        String val;
        switch (resign) {
            case 1:
                val = "einfach";
                break;
            case 2:
                val = "Gammon";
                break;
            case 3:
                val = "Backgammon";
                break;
            default:
                throw new IllegalStateException(
                        "Illegal value for resign: "
                                + match.getPlayer(match.own).resign);
        }
        frame.appendText("Gegner bietet Aufgabe an: " + val + "\n");

        menu.setItems(accept, reject, rescan, reset);

        // next event: onSelect / case "accept" or "reject"
    }

    private void ownDoubleResponse() {
        menu.setItems(accept, reject, rescan, reset);
    }

    private void oppMove() {
        menu.setItems(rescan, reset);

        logShots("Gegnerische Schüsse:");

        oppJokers.count(match, workerState.tmp);
        exeMatchWorker(new WaitForOppMove(cal, bs,
                spinRolls) {
            @Override
            public void resultOnEventDispatchThread(WaitForOppMoveRes result) {
                System.out.println("result from WaitForOppMoveRes");
                if (result.error != null) {
                    frame.appendText("FEHLER: " + result.error + "\n");
                } else if (ownPlayer().resign > 0) {
                    ownResignResponse();
                } else if (result.move != null) {
                    addCmd(new GMove(result.move));
                    matchView.setMatch(match, workerState.ongoingMove);
                    // result.match, also match enthaelt schon den Zustand nach result.move

                    if (match.finished()) {
                        closeMatch();
                    } else if (match.gameStarting()) {
                        addCmd(new GComment(""));
                        addCmd(new GComment("Spiel " + ++workerState.game));
                        addCmd(new GComment(""));
                        addCmd(new GNewGame());
                        assert (state == workerState);
                        addCmd(new GComment(""));
                        waitForFirstRoll();
                    } else if (match.closedOut(match.own)) {
                        System.out.println(
                                "Skip own roll and move because closed out and spin does not let us double, then.");
                        // Spin skips our roll and our move completely
                        // and puts the opponent immediatly on roll, again.
                        RandomRoll.create(match.roll);
                        addCmd(new GSetDice(match.roll.die1, match.roll.die2));
                        AllMoves.find(match, workerState.allMoves, workerState.findTaskArray);
                        if (workerState.allMoves.length() != 1 || workerState.allMoves.at(0).length() != 0) {
                            throw new IllegalStateException("closed out and not only empty move?!");
                        }
                        addCmd(new GMove(workerState.allMoves.at(0)));
                        Move.run(match, workerState.allMoves.at(0));

                        oppRollOrDouble();
                    } else {
                        if (match.roll.isEmpty()) {
                            System.out.println("roll is empty");
                            // ownRoll muss so schnell wie moeglich sein, falls der eigene Spieler gerade
                            // tanzt.
                            // System.out.println("match.roll was still empty and autoroll was " +
                            // match.autoroll);
                            ownRoll(false);
                        } else {
                            System.out.println("roll is NOT empty");
                            addCmd(new GSetDice(match.roll.die1, match.roll.die2));
                            ownMove();
                        }
                    }

                }

            }
        }, false);

    }

    private void clickOwnRoll(boolean waitUntilNoOwnRollVisible) {
        System.out.println("start ClickRoll ...");
        exeMatchWorker(new ClickRoll(bs, spinRolls, waitUntilNoOwnRollVisible) {
            @Override
            public void resultOnEventDispatchThread(Void result) {
                System.out.println("ClickRoll done.");
                matchView.setMatch(match, workerState.ongoingMove);

                if (ownPlayer().resign > 0) {
                    ownResignResponse();
                } else {
                    addCmd(new GSetDice(match.roll.die1, match.roll.die2));
                    ownMove();
                }
            }
        });
    }

    private void ownRoll(boolean waitUntilNoOwnRollVisible) {
        if (match.active != match.own) {
            throw new IllegalStateException("Eigener Spieler nicht am Wurf!");
        }

        if (autoroll) {
            menu.setItems(rescan, reset);
            clickOwnRoll(waitUntilNoOwnRollVisible);
        } else {
            if (match.cube.used && (!match.crawfordRule || !match.crawfordRound) && match.cube.owner != 1 - match.own
                    && ownPlayer().score + match.cube.value < match.matchLen) {
                menu.setItems(roll, doDouble, rescan, reset);
            } else {
                menu.setItems(rescan, reset);
                clickOwnRoll(waitUntilNoOwnRollVisible);
            }

        }

        matchView.setMatch(match, workerState.ongoingMove);
    }

    private void closeMatch() {
        frame.appendText("\n\n**** Match zuende!\n\n");

        String now = DateTimeFormatter.ofPattern("yyyy-MM-dd_HH-mm-ss").format(ZonedDateTime.now());
        tmpsb.setLength(0);
        tmpsb.append(now).append("__").append(match.getPlayerName(0)).append("__").append(match.getPlayerName(1))
                .append(".gnubg");
        try {
            BufferedWriter w = new BufferedWriter(new FileWriter(tmpsb.toString()));
            for (var cmd : commands) {
                tmpsb.setLength(0);
                cmd.append(tmpsb);
                w.write(tmpsb.toString());
            }
            w.close();
            menu.setItems(start);
        } catch (IOException ex) {
            ex.printStackTrace();
        }

        try {
            JokersUploader uploader = new JokersUploader();
            String name1 = match.getPlayerName(match.own);
            String name2 = match.getPlayerName(1 - match.own);
            AllJokers jokers1 = ownJokers;
            AllJokers jokers2 = oppJokers;
            uploader.send(name1, name2, jokers1, jokers2);
            uploader.close();

        } catch (Exception ex) {
            ex.printStackTrace();
        }

        match.active = -1; // to show that no match is running
    }

    private void logShots(String header) {
        StringBuilder sb = new StringBuilder("\n").append(header).append("\n");

        workerState.tmp.clear();
        var shots = workerState.tmp.add();
        int n;

        shots.clear();
        Utils.findDirectShots(match, shots);
        n = shots.length();
        for (int i = 0; i + 1 < n; i += 2) {
            sb.append("Direkter Schuss: ").append(shots.at(i)).append('/').append(shots.at(i + 1)).append("*\n");
        }

        shots.clear();
        Utils.findIndirectShots(match, shots);
        n = shots.length();
        for (int i = 0; i + 2 < n; i += 3) {
            sb.append("Indirekter Schuss: ").append(shots.at(i)).append('/').append(shots.at(i + 1)).append('/')
                    .append(shots.at(i + 2)).append("*\n");
        }

        shots.clear();
        Utils.findShotsWithADouble(match, shots);
        n = shots.length();
        for (int i = 0; i + 3 < n;) {
            int die = shots.at(i++);
            int fromBar = shots.at(i++);
            int from = shots.at(i++);
            int to = shots.at(i++);
            sb.append("Pasch-Schuss:");

            for (int j = 0; j < fromBar; ++j) {
                sb.append(" Bar/").append(25 - die);
            }

            sb.append(' ').append(from);

            for (int pos = from - die; pos >= to; pos -= die) {
                sb.append('/').append(pos);
            }

            sb.append("*\n");
        }

        frame.appendText(sb.toString());
    }

    private void ownMove() {
        if (match.getPlayer(0).field.isInitial() && match.getPlayer(1).field.isInitial()) {
            // Aufgabe nicht erlaubt, da Implementierung von OwnResign viel einfacher, wenn
            // dieser Fall ausgeschlossen wird
            menu.setItems(rescan, reset);
        } else {
            menu.setItems(resign1, resign2, resign3, rescan, reset);
        }

        workerState.match.set(match);
        ownJokers.count(match, workerState.tmp);

        logShots("Eigene Schüsse:");

        ownMove = new OwnMove(workerState, matchView, new OwnMoveCb() {
            @Override
            public void done() {
                ownMove = null;
                System.out.println("OwnMove.done");
                // match.ongoingMove.move enthaelt einen gueltigen Zug, aber match ist noch im
                // Zustand vor diesem Zug.
                match.set(workerState.match);
                assert (false);
                addCmd(new GMove(workerState.ongoingMove.move));
                MutableIntArray aggregatedMove = new MutableIntArray(8);
                Field otherField = oppPlayer().field;
                Move.aggregate(match.roll.die1, match.roll.die2, workerState.ongoingMove.move, otherField,
                        aggregatedMove, workerState.tmp);
                // tmpsb.setLength(0);
                // tmpsb.append("\n\noriginal move ");
                // workerState.ongoingMove.move.append(tmpsb);
                // tmpsb.append("\naggregated move ");
                // aggregatedMove.append(tmpsb).append("\n\n");
                // frame.appendText(tmpsb.toString());
                Move.run(match, workerState.ongoingMove.move);
                workerState.ongoingMove.move.clear();
                workerState.ongoingMove.hits.clear();
                matchView.setMatch(match, workerState.ongoingMove);

                Runnable afterMove = new Runnable() {
                    @Override
                    public void run() {

                        if (match.finished()) {
                            closeMatch();
                            return;
                        }

                        if (match.gameStarting()) {
                            frame.appendText("Du hast das Spiel gewonnen. Nächste Runde...\n");

                            addCmd(new GComment(""));
                            addCmd(new GComment("Spiel " + ++workerState.game));
                            addCmd(new GComment(""));
                            addCmd(new GNewGame());

                            waitForFirstRoll();
                            return;
                        }

                        if (match.closedOut(1 - match.own)) {
                            // This is a situation where the behavior of the spin server is undefined.
                            // There are the following possibilities:
                            // a) The opponent cannot roll because he is closed out. Instead, we are
                            // immediately on roll after
                            // our last move.
                            // b) The opponent can roll even though he is closed out. We see the opponent's
                            // roll for
                            // a split second and then we are on roll, again.
                            // c) The opponent can roll and it is delayed because he has not set autoroll.

                            // So what shall be done?
                            // First of all, we must wait until the roll of our own last move has really
                            // disappeared.
                            // If an opp roll is visible, wait until it is invisible and then click for our
                            // roll.
                            // If no roll is visible, click only once, wait up to one second for our roll to
                            // become visible.
                            // If not visible after one second, click again and repeat.

                            // Skip opp roll and double in spin browser, because spin does not let him roll
                            // or double when
                            // closed out.

                            // But roll here!
                            if (match.active == match.own) {
                                throw new IllegalStateException("After own move, opponent must be active!");
                            }
                            RandomRoll.create(match.roll);
                            addCmd(new GSetDice(match.roll.die1, match.roll.die2));
                            AllMoves.find(match, workerState.allMoves, workerState.findTaskArray);
                            if (workerState.allMoves.length() != 1 || workerState.allMoves.at(0).length() != 0) {
                                throw new IllegalStateException("closed out and not only empty move?!");
                            }
                            addCmd(new GMove(workerState.allMoves.at(0)));
                            Move.run(match, workerState.allMoves.at(0));

                            ownRoll(true);
                        } else {
                            oppRollOrDouble();
                        }

                    }
                };

                if (aggregatedMove.length() == 0) {

                    // Spin wartet nicht, also hier auch nicht blockieren sondern lieber schleunigst
                    // den gegnerischen Zug erfassen!
                    afterMove.run();

                } else {

                    exeWorker(new ClickOwnMove(bs, aggregatedMove) {
                        @Override
                        public void resultOnEventDispatchThread(Void result) {
                            afterMove.run();
                        }
                    });

                }
            }
        });
    }

    private void oppRollOrDouble() {
        if (match.active != 1 - match.own) {
            throw new IllegalStateException();
        }
        exeMatchWorker(new OppRollOrDouble(bs, spinRolls) {
            @Override
            public void resultOnEventDispatchThread(Void result) {
                System.out.println("OppRollOrDouble returned");
                matchView.setMatch(match, workerState.ongoingMove);

                if (ownPlayer().resign > 0) {
                    ownResignResponse();
                } else if (!match.roll.isEmpty()) {
                    addCmd(new GSetDice(match.roll.die1, match.roll.die2));
                    oppMove();
                } else if (match.cube.offered) {
                    System.out.println("cube offered");
                    if (match.cube.owner != match.own || match.active != 1 - match.own) {
                        throw new IllegalStateException(
                                "Opp offered cube, but is not active or we do not own the cube?! match.cube.owner="
                                        + match.cube.owner + "  match.active=" + match.active);
                    }
                    addCmd(new GDouble());
                    ownDoubleResponse();
                } else {
                    throw new IllegalStateException(match.appendPosition(null).toString());
                }
            }
        }, false);
    }

    private void addCmd(GnuCmd... commands) {
        tmpsb.setLength(0);

        for (GnuCmd cmd : commands) {
            this.commands.add(cmd);
            cmd.append(tmpsb);
        }

        String text = tmpsb.toString();
        frame.appendText(text);
        frame.appendCmd(text);
    }

    private <T, V> void exeWorker(MyWorker<T, V> w) {
        handsOff(new Runnable() {
            @Override
            public void run() {
                // workerState.match.set(match);

                runningWorker = new MyWorker<T, V>() {
                    @Override
                    public T doIt() throws Exception {
                        return w.doIt();
                    }

                    @Override
                    public void resultOnEventDispatchThread(T result) {
                        runningWorker = null;
                        // match.set(workerMatch);
                        w.resultOnEventDispatchThread(result);
                        handsOn();
                    }
                };
                runningWorker.execute();
            }
        }, onHandsOffCancel);
    }

    private final Runnable onHandsOffCancel = new Runnable() {
        @Override
        public void run() {
            if (runningWorker != null) {
                runningWorker.cancel(true);
            }
            if (handsOffDlg != null) {
                handsOffDlg.setVisible(false);
                handsOffDlg.dispose();
                handsOffDlg = null;
            }
            frame.setEnabled(true);
            handsOffCount = 0;
        }
    };

    private <T> void exeMatchWorker(MatchWorker<T> matchWorker) {
        exeMatchWorker(matchWorker, true);
    }

    private <T> void exeMatchWorker(MatchWorker<T> matchWorker, boolean withHandsOff) {
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                workerState.match.set(match);
                matchWorker.setState(workerState);
                runningWorker = new MyWorker<T, Void>() {
                    @Override
                    public T doIt() throws Exception {
                        return matchWorker.doIt();
                    }

                    @Override
                    public void resultOnEventDispatchThread(T result) {
                        runningWorker = null;
                        match.set(workerState.match);
                        matchWorker.resultOnEventDispatchThread(result);
                        if (withHandsOff) {
                            handsOn();

                        }
                    }
                };
                runningWorker.execute();

            }
        };
        if (withHandsOff) {
            handsOff(runnable, onHandsOffCancel);
        } else {
            runnable.run();
        }
    }

    @Override
    public void onAutowurfChanged(boolean selected) {
        autoroll = selected;
        // System.out.println("onAutowurfChanged " + selected);
        // SwingUtilities.invokeLater(new Runnable() {
        // public void run() {
        // if (match != null) {
        // match.autoroll = selected;
        // exeWorker(
        // new SetAutoroll(bs, selected) {
        // @Override
        // public void resultOnEventDispatchThread(Boolean result) {
        // if (!result) {
        // frame.appendText("Autowurf war schon umgestellt?!\n");
        // }
        // }
        // });
        // }
        // }
        // });
    }

    private void handsOff(Runnable afterDlg, Runnable onCancel) {
        if (handsOffCount++ == 0) {
            lastMouse = MouseInfo.getPointerInfo().getLocation();
            frame.setEnabled(false);
            handsOffDlg = new HandsOffDlg(frame, afterDlg, onCancel);
            handsOffDlg.pack();
            handsOffDlg.setVisible(true);
            // JOptionPane.showMessageDialog(frame, "Ich übernehme die Kontrolle über Maus
            // und Tastatur...",
            // "Hände weg! ;-)",
            // JOptionPane.INFORMATION_MESSAGE);
        } else {
            afterDlg.run();
        }
    }

    private void handsOn() {
        if (--handsOffCount == 0) {
            if (handsOffDlg != null) {
                handsOffDlg.setVisible(false);
                handsOffDlg.dispose();
                handsOffDlg = null;
            }
            MyRobot.move(lastMouse.x, lastMouse.y);
            frame.setEnabled(true);
        }
    }

    // @Override
    // public void pipClicked(int field) {
    // // TODO Auto-generated method stub
    // throw new UnsupportedOperationException("Unimplemented method 'pipClicked'");
    // }

    // @Override
    // public void dragStarted(int field) {
    // // TODO Auto-generated method stub
    // throw new UnsupportedOperationException("Unimplemented method
    // 'dragStarted'");
    // }

    // @Override
    // public void dragContinued(int field) {
    // // TODO Auto-generated method stub
    // throw new UnsupportedOperationException("Unimplemented method
    // 'dragContinued'");
    // }

    // @Override
    // public void dragEnded() {
    // // TODO Auto-generated method stub
    // throw new UnsupportedOperationException("Unimplemented method 'dragEnded'");
    // }

    // @Override
    // public void pipEntered(int field) {
    // debug("pipEntered " + field);
    // if (match == null || match.active != match.own || match.roll.isEmpty()) {
    // return;
    // }

    // ongoingMove.highlightedPip = -1;
    // ongoingMove.move.add(field);
    // if (AllMoves.isValidStart(match.allMoves, ongoingMove.move)) {
    // ongoingMove.highlightedPip = field;
    // }
    // ongoingMove.move.removeLast();
    // matchView.setOngoingMove(ongoingMove);
    // }

    // @Override
    // public void pipExited(int field) {
    // debug("pipExited " + field);
    // if (match == null || match.active != match.own || match.roll.isEmpty()) {
    // return;
    // }

    // ongoingMove.highlightedPip = -1;
    // matchView.setOngoingMove(ongoingMove);
    // }

    private void debug(String msg) {
        frame.appendText(msg + "\n");
    }

    @Override
    public void onClose() {
        if (stopMatch()) {
            System.out.println("gonna exit");
            System.exit(0);
        }
    }
}
