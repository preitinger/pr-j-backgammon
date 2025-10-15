package pr.backgammon.spin.control.workers;

import pr.model.MutableIntArray;

public class WaitForOppMoveRes {
    // public CalibrationForSpin cal;
    // public FastChequerSearch chequers;
    // public SpinRolls spinRolls;
    public String error = null;
    /**
     * content readonly if not null!
     */
    public MutableIntArray move = null;
}
