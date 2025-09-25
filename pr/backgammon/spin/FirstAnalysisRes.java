package pr.backgammon.spin;

import pr.backgammon.model.Field;
import pr.backgammon.model.Roll;

public class FirstAnalysisRes {
    public static enum Type {
        /**
         * weder top12 noch top24 gefunden
         */
        BOARD_NOT_FOUND,

        /**
         * Can enter as white or black or any of them, i.e. at least one of the enter
         * chequers is visible.
         */
        CAN_ENTER,

        /**
         * Ready button is visible and can be clicked
         */
        CAN_READY,

        /**
         * Irgendein Spieldialog ist gerade offen (Aufgabe oder Doppeln, hier egal).
         * Das heißt nämlich, dass das Brett noch nicht analysiert werden kann, da
         * Chequer verdeckt sein können.
         */
        DIALOG_OPEN,

        /**
         * Button `Verlassen` visible, d.h. zwar bereit, aber Gegner noch nicht.
         * Sonst haette CAN_READY Vorrang vor CAN_LEAVE
         */
        CAN_LEAVE,

        /**
         * Match aktiv, und wir spielen mit.
         */
        OWN_MATCH,

        /**
         * Wenn weder der Button `Verlassen` noch `Aufgeben` noch der Farbdialog
         * sichtbar ist, bleibt nur
         * die letzte Möglichkeit, dass 2 andere spielen und wir nur zusehen.
         */
        OTHERS_MATCH
    }

    public final Type type;
    /**
     * if true, calibration for white is needed to continue, otherwise calibration
     * for black
     */
    public final boolean calWhite;
    public final int[] chequersAsForMatchDebug;
    public final Roll initialRoll, ownRoll, oppRoll;
    /**
     * Initial: 1. Im eigenen Besitz: > 1. Im gegnerischen Besitz: < -1.
     */
    public final int cubeVal;

    FirstAnalysisRes(Type type, boolean calWhite) {
        this.type = type;
        this.calWhite = calWhite;
        chequersAsForMatchDebug = null;
        this.initialRoll = null;
        this.ownRoll = null;
        this.oppRoll = null;
        this.cubeVal = 1;
    }

    public FirstAnalysisRes(Type type, boolean calWhite, int[] chequersAsForMatchDebug, Roll initialRoll,
            Roll ownRoll,
            Roll oppRoll,
            int cubeVal) {
        this.type = type;
        this.calWhite = calWhite;
        this.chequersAsForMatchDebug = chequersAsForMatchDebug;
        this.initialRoll = initialRoll;
        this.ownRoll = ownRoll;
        this.oppRoll = oppRoll;
        this.cubeVal = cubeVal;
    }

    public StringBuilder append(StringBuilder sb) {
        if (sb == null) {
            sb = new StringBuilder();
        }
        sb.append(type.toString()).append('\n');
        if (chequersAsForMatchDebug != null) {
            Field tmpField = new Field();
            tmpField.debugField(chequersAsForMatchDebug);
            tmpField.appendPosition(sb);
        }
        appendRoll(sb, "initial roll", initialRoll);
        appendRoll(sb, "opp roll", oppRoll);
        appendRoll(sb, "own roll", ownRoll);
        return sb;
    }

    private void appendRoll(StringBuilder sb, String name, Roll roll) {
        if (roll != null) {
            sb.append(name).append("    ").append(roll.die1()).append(roll.die2()).append('\n');
        }
    }
}
