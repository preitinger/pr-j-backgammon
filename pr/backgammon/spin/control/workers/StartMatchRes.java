package pr.backgammon.spin.control.workers;

import pr.backgammon.model.Match;
import pr.backgammon.spin.control.BoardSearchers;
import pr.backgammon.spin.control.CalibrationForSpin;
import pr.backgammon.spin.control.SpinRolls;

public class StartMatchRes {
    public String error = null;
    public CalibrationForSpin cal = null;
    public BoardSearchers bs = null;
    public SpinRolls spinRolls = null;
    public Match match = null;
}
