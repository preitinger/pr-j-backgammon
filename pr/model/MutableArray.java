package pr.model;

/**
 * The implementation suppresses "unchecked" warnings, but invalid down casts
 * are impossible by design because of the following facts.
 * The internal array vals is private, so can only be written by this class.
 * All writing methods write always instances of T into vals.
 * 
 * This class is designed for efficiency, so the effort for all methods is
 * constant.
 */
abstract public class MutableArray<T extends Mutable<T>> implements Mutable<MutableArray<T>> {
    private static final long serialVersionUID = 1L;

    private int n;
    private final Mutable<T>[] vals;

    abstract protected T createInstance();

    @SuppressWarnings("unchecked")
    public MutableArray(int capacity) {
        vals = new Mutable[capacity];
        for (int i = 0; i < vals.length; ++i) {
            vals[i] = createInstance();
        }
        n = 0;
    }

    public int length() {
        return n;
    }

    @SuppressWarnings("unchecked")
    public T at(int i) {
        if (i < 0 || i >= n)
            throw new IndexOutOfBoundsException(i);
        return (T) vals[i];
    }

    public void clear() {
        n = 0;
    }

    /**
     * deep copy
     */
    @SuppressWarnings("unchecked")
    @Override
    public void set(MutableArray<T> other) {
        n = other.n;
        for (int i = 0; i < n; ++i) {
            (vals[i]).set((T) other.vals[i]);
        }
    }

    /**
     * increments the length, and then returns the new last element to be modified
     */
    @SuppressWarnings("unchecked")
    public T add() {
        return (T) vals[n++];
    }

    @SuppressWarnings("unchecked")
    public T removeLast() {
        if (n == 0)
            throw new IllegalStateException("Empty!");
        return (T) vals[--n];
    }

    public T removeLastSwapping(T swappedIn) {
        @SuppressWarnings("unchecked")
        T result = (T) vals[--n];
        vals[n] = swappedIn;
        return result;
    }

    /**
     * swaps indices i and length - 1, and then decrements the length by 1.
     */
    public void swapOut(int i) {
        if (i < 0 || i >= n)
            throw new IndexOutOfBoundsException(i); // also always when n == 0

        if (i < n - 1) {
            Mutable<T> tmp = vals[i];
            vals[i] = vals[n - 1];
            vals[n - 1] = tmp;
        }

        --n;
    }

    public void swap(int ownIndex, MutableArray<T> other, int otherIndex) {
        Mutable<T> tmp = vals[ownIndex];
        vals[ownIndex] = other.vals[otherIndex];
        other.vals[otherIndex] = tmp;
    }

    /**
     * Moves the element in {@code this} at {@code ownSrcIdx} to the end of {@code dest}.
     * May only be called if this is not empty (n > 0), and ownSrcIdx >= 0 && ownSrcIdx <
     * this.n and dest is not full (dest.n < dest.vals.length).
     * Then the effect is equal the following sequence.
     * {@code
     * dest.add();
     * this.swap(ownSrcIdx, dest, dest.length() - 1);
     * this.swapOut(ownSrcIdx);
     * }
     */
    public void move(int ownSrcIdx, MutableArray<T> dest) {
        --n;
        Mutable<T> tmp = vals[ownSrcIdx];
        vals[ownSrcIdx] = vals[n];
        vals[n] = dest.vals[dest.n];
        dest.vals[dest.n] = tmp;
        ++dest.n;
    }

    public int capacity() {
        return this.vals.length;
    }
}
