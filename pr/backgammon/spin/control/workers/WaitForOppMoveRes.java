package pr.backgammon.spin.control.workers;

import pr.backgammon.spin.control.CalibrationForSpin;
import pr.backgammon.spin.control.FastChequerSearch;
import pr.backgammon.spin.control.SpinRolls;
import pr.model.MutableIntArray;

public class WaitForOppMoveRes {
    public CalibrationForSpin cal;
    public FastChequerSearch chequers;
    public SpinRolls spinRolls;
    public String error = null;
    /**
     * content readonly if not null!
     */
    public MutableIntArray move = null;
}
