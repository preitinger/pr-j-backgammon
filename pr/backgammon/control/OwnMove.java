package pr.backgammon.control;

import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;

import pr.backgammon.model.Field;
import pr.backgammon.spin.model.WorkerState;
import pr.backgammon.view.MatchView;
import pr.backgammon.view.MatchViewListener;

public class OwnMove implements MatchViewListener {
    private final WorkerState state;
    private final MatchView matchView;
    private final OwnMoveCb cb;

    /**
     * state.match is changed temporarily but restored before any of the callback methods is called.
     */
    public OwnMove(WorkerState state, MatchView matchView, OwnMoveCb cb) {
        this.state = state;
        this.matchView = matchView;
        this.cb = cb;

        state.ongoingMove.move.clear();
        state.ongoingMove.hits.clear();
        state.ongoingMove.highlightedPip = -1;
        state.ongoingMove.hoveredField = -1;
        matchView.setMatch(state.match, state.ongoingMove);
        AllMoves.find(state.match, state.allMoves, state.findTaskArray);

        if (state.allMoves.length() == 1 && state.allMoves.at(0).length() == 0) {
            cb.done();
        } else {
            matchView.setListener(this);
        }
    }

    public void cancel() {
        matchView.setListener(null);
        undo();
    }

    @Override
    public void mouseClicked(MouseEvent e) {
        int x = e.getX();
        int y = e.getY();
        int field = matchView.containingField(x, y);
        if (field != -1) {
            pipClicked(matchView.getClockwise() ? field : 25 - field, e.getButton());
        }
    }

    @Override
    public void mouseMoved(MouseEvent e) {
        int x = e.getX();
        int y = e.getY();
        int field = matchView.containingField(x, y);
        if (state.ongoingMove.hoveredField != -1 && field != state.ongoingMove.hoveredField) {
            pipExited(state.ongoingMove.hoveredField);
            state.ongoingMove.hoveredField = -1;
        }
        if (state.ongoingMove.hoveredField == -1 && field != -1) {
            state.ongoingMove.hoveredField = field;
            pipEntered(matchView.getOwnWhite() ? 25 - state.ongoingMove.hoveredField : state.ongoingMove.hoveredField);
        }
    }

    /**
     * field in own's perspective (we play from 24 to 1)
     */
    public void pipClicked(int field, int button) {
        int bigDie = state.match.roll.die1();
        int smallDie = state.match.roll.die2();
        if (bigDie < smallDie) {
            int tmp = bigDie;
            bigDie = smallDie;
            smallDie = tmp;
        }
        int preferredDie, otherDie;
        if (button == MouseEvent.BUTTON1) {
            preferredDie = bigDie;
            otherDie = smallDie;
        } else {
            preferredDie = smallDie;
            otherDie = bigDie;
        }

        boolean chequerOnBar = state.match.getPlayer(state.match.own).getChequers(25) > 0;
        if (chequerOnBar) {
            int from = 25;
            int to = field;

            if (from - to == smallDie) {
                tryPartMove(from, smallDie);
            } else if (from - to == bigDie) {
                tryPartMove(from, bigDie);
            }
        } else {
            int from = field;
            if (tryPartMove(from, preferredDie)) {
                state.ongoingMove.highlightedPip = -1;
                pipEntered(field);
                return;
            }
            tryPartMove(from, otherDie);

        }
        // matchView.revalidate();
        pipEntered(field);

        // StringBuilder sb = new StringBuilder();
        // state.match.appendPosition(sb);
        // System.out.println("new position " + sb.toString());
    }

    private boolean tryPartMove(int from, int die) {
        int to = Math.max(0, from - die);
        var move = state.ongoingMove.move;
        var hits = state.ongoingMove.hits;
        if (move.length() == move.capacity()) {
            return false;
        }
        move.add(from);
        move.add(to);
        hits.add(to > 0 && state.match.getPlayer(1 - state.match.active).field.getChequers(25 - to) == 1 ? 1 : 0);

        if (!AllMoves.isValidStart(state.allMoves, move)) {
            System.out.println("is not a valid start with from " + from + " and die " + die);
            move.removeLast();
            move.removeLast();
            hits.removeLast();
            return false;
        }

        System.out.println("was a valid start with from " + from + " and die " + die);
        Field.runPartMove(state.match.getPlayer(state.match.active).field, state.match.getPlayer(1 - state.match.active).field, from, to);
        return true;

    }

    public void dragStarted(int field) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'dragStarted'");
    }

    public void dragContinued(int field) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'dragContinued'");
    }

    public void dragEnded() {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'dragEnded'");
    }

    /**
     * field in own's perspective (we play from 24 to 1)
     */
    public void pipEntered(int field) {
        if (state.match == null || state.match.active != state.match.own || state.match.roll.isEmpty()) {
            throw new IllegalStateException();
        }

        var ongoingMove = state.ongoingMove;
        ongoingMove.highlightedPip = -1;

        if (ongoingMove.move.length() == 8) {
            matchView.setMatch(state.match, state.ongoingMove);
            return;
        }

        boolean chequerOnBar = state.match.getPlayer(state.match.own).field.getChequers(25) > 0;

        if (chequerOnBar) {
            ongoingMove.move.add(25);
        }

        ongoingMove.move.add(field);

        if (AllMoves.isValidStart(state.allMoves, ongoingMove.move)) {
            ongoingMove.highlightedPip = matchView.getOwnWhite() ? 25 - field : field;
        }

        ongoingMove.move.removeLast();

        if (chequerOnBar) {
            ongoingMove.move.removeLast();
        }

        matchView.setMatch(state.match, state.ongoingMove);
    }

    public void pipExited(int field) {
        if (state.match == null || state.match.active != state.match.own || state.match.roll.isEmpty()) {
            throw new IllegalStateException();
        }

        var ongoingMove = state.ongoingMove;
        ongoingMove.highlightedPip = -1;
        matchView.setMatch(state.match, state.ongoingMove);
    }

    private void undo() {
        undoWithoutClear();

        var move = state.ongoingMove.move;
        var hits = state.ongoingMove.hits;
        move.clear();
        hits.clear();

        if (state.ongoingMove.hoveredField != -1) {
            pipEntered(matchView.getOwnWhite() ? 25 - state.ongoingMove.hoveredField : state.ongoingMove.hoveredField);
        }

        matchView.repaint();
    }

    private void undoWithoutClear() {
        var move = state.ongoingMove.move;
        var hits = state.ongoingMove.hits;
        assert (move.length() == hits.length());

        for (int i = hits.length() - 1; i >= 0; --i) {
            Field active = state.match.getPlayer(state.match.active).field;
            Field other = state.match.getPlayer(1 - state.match.active).field;
            int activeFrom = move.at(i * 2);
            int activeTo = move.at(i * 2 + 1);
            int otherTo = 25 - activeTo;
            moveChequer(active, activeTo, activeFrom);

            if (hits.at(i) != 0) {
                moveChequer(other, 25, otherTo);
            }
        }

    }

    /**
     * @param ownFrom - index from own perspective
     * @param ownTo   - index from own perspective
     */
    private void moveChequer(Field own, int ownFrom, int ownTo) {
        if (own.getChequers(ownFrom) < 1) {
            throw new IllegalArgumentException("own contains no chequer on ownFrom");
        }

        changeField(own, ownFrom, -1);
        changeField(own, ownTo, 1);
    }

    private void changeField(Field f, int index, int diff) {
        f.set(index, f.getChequers(index) + diff);
    }

    @Override
    public void mouseEntered(MouseEvent e) {
    }

    @Override
    public void mouseExited(MouseEvent e) {
    }

    @Override
    public void mousePressed(MouseEvent e) {
        matchView.requestFocus();
    }

    @Override
    public void mouseReleased(MouseEvent e) {
    }

    @Override
    public void mouseDragged(MouseEvent e) {
    }

    @Override
    public void keyPressed(KeyEvent e) {
        System.out.println("keypressed");
        switch (e.getKeyCode()) {
            case KeyEvent.VK_ESCAPE:
                undo();
                break;

            case KeyEvent.VK_ENTER:
                System.out.println("check if valid");
                if (AllMoves.isValid(state.allMoves, state.ongoingMove.move)) {
                    System.out.println("yes, valid");
                    matchView.setListener(null);
                    undoWithoutClear();
                    // Move.run(state.match, state.ongoingMove.move); // incl. evtl. neuem spiel oder state.matchende
                    cb.done();
                } else {
                    System.out.println("no, not valid");
                }
                break;
        }
    }

    @Override
    public void keyReleased(KeyEvent e) {
    }

    @Override
    public void keyTyped(KeyEvent e) {
    }
}
