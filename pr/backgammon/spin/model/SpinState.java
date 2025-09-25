package pr.backgammon.spin.model;

// TODO Wichtig auch erst zu pruefen ob eine Überdeckung des Feldes vorliegt durch einen Dialog in der Mitte wie bei Aufgabe oder Doppeln

@Deprecated
public enum SpinState {
    /**
     * Darauf warten, dass eigener Spieler Platz nimmt
     */
    CHOOSE_COLOR,
    /**
     * Warten dass eigener Spieler auf `Bereit` klickt.
     */
    UNREADY,
    /**
     * Initialer, erster Wurf sichtbar
     */
    INITIAL_ROLL,

    OPP_ROLL,

    OWN_ROLL,

    /**
     * Eigener Spieler klickt gerade "Annehmen".
     * Ob es ein Redoppel war erkennt man, ob der alte angezeigte Dopplerwert höher ist als der Cube im internen Match
     */
    OWN_TAKE,

    /**
     * Eigener Spieler klickt gerade "Verdoppeln"
     */
    OWN_REDOUBLE,

    /**
     * Eigener Spieler klickt gerade "Aufgeben".
     */
    OWN_DROP,
    /**
     * Achtung beim Initialdoppel ist der Dopplerwürfel ist in unterer Bretthälfte ohne eine Zahl solange der Gegner noch nicht angenommen hat.
     * Man sieht dass der eigene Spieler gedoppelt hat in der Statuszeile. Man kann nach dem Schnipsel `verdoppeln` suchen mit flexibler x-Position.
     * Bei Weiß ist der Doppler links, bei Schwarz rechts.
     */
    OWN_DOUBLE,

    OFFERED_RESIGN_TO_OPP,

    OFFERED_RESIGN_TO_OWN
}
