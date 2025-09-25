package pr.backgammon.model;

import pr.model.Mutable;

/**
 * Methode drop() nicht notwendig, da reset() ausreicht.
 */
public class Cube implements Mutable<Cube> {
    private static final long serialVersionUID = 1L;
    
    public boolean used = true;

    /**
     * valid values are (1 << n) for any n in 0, 1, 2, ...
     */
    public int value = 1;
    /**
     * -1: no owner, value must not be 1 (for example automatic doubles)
     * 0 to 1: player with index 0 or 1 owns the cube
     */
    public int owner = -1;
    public boolean offered = false;

    public void reset() {
        used = true;
        value = 1;
        owner = -1;
        offered = false;
    }

    /**
     * deep copy from other to this
     */
    public void set(/* readonly */Cube other) {
        this.used = other.used;
        this.value = other.value;
        this.owner = other.owner;
        this.offered = other.offered;
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

    public void drop() {
        if (!offered || value < 2 || owner == -1) {
            throw new IllegalStateException();
        }

        value >>= 1;
        owner = -1;
        offered = false;
    }

    public void debug(int cubeVal, int own, boolean offered) {
        value = Math.abs(cubeVal);
        owner = value == 1 ? -1 : cubeVal > 0 ? own : 1 - own;
        this.offered = offered;
    }
}
