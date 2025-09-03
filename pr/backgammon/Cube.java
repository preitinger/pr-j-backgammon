package pr.backgammon;

/**
 * Methode drop() nicht notwendig, da reset() ausreicht.
 */
public class Cube {

    private int value = 1;
    /**
     * -1: no owner, i.e. initial cube value 1;
     * 0 to 1: player with index 0 or 1 owns the cube
     */
    private int owner = -1;
    private boolean offered = false;

    public void reset() {
        value = 1;
        owner = -1;
        offered = false;
    }

    public void offerDouble(int offering) {

        if (offering < 0 || offering > 1) {
            throw new IllegalArgumentException();
        }

        if (offered) {
            throw new IllegalStateException();
        }

        int newOwner = 1 - offering;

        if (owner == newOwner) {
            // opponent of `offering` owns the cube
            throw new IllegalStateException();
        }
        
        owner = newOwner;
        value <<= 1;
        offered = true;
    }

    public void take() {

        if (!offered) {
            throw new IllegalStateException();
        }

        offered = false;
    }

    public int getValue() {
        return value;
    }

    public boolean isOffered() {
        return offered;
    }

    public int getOwner() {
        return owner;
    }
}
