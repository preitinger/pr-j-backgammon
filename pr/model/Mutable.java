package pr.model;

import java.io.Serializable;

public interface Mutable<T> extends Serializable {
    /**
     * must do a deep copy of other to this.
     */
    void set(T other);
}
