package pr.model;

import java.util.Arrays;
import java.util.Comparator;

public class MutableIntArray implements Mutable<MutableIntArray> {
    private static final long serialVersionUID = 1L;

    private int n;
    private final int[] vals;

    @Override
    public void set(MutableIntArray other) {
        n = other.n;
        for (int i = 0; i < n; ++i) {
            vals[i] = other.vals[i];
        }
    }

    public MutableIntArray(int capacity) {
        vals = new int[capacity];
        n = 0;
    }

    public int capacity() {
        return vals.length;
    }

    public int length() {
        return n;
    }

    public int at(int i) {
        if (i < 0 || i >= n)
            throw new IndexOutOfBoundsException(i);
        return vals[i];
    }

    public void clear() {
        n = 0;
    }

    /**
     * increments the length, and then sets the new last element to val.
     */
    public void add(int val) {
        vals[n++] = val;
    }

    public void addAll(MutableIntArray toAdd) {
        int n = toAdd.length();
        for (int i = 0; i < n; ++i) {
            vals[n++] = toAdd.vals[i];
        }
    }

    public void addRange(MutableIntArray toAdd, int begin, int end) {
        for (int i = begin; i < end; ++i) {
            vals[n++] = toAdd.vals[i];
        }
    }

    // Dies ist eher sogar langsamer als addRange, aber kaum messbar:
    public void addRange2(MutableIntArray toAdd, int begin, int end) {
        var valsToAdd = toAdd.vals;
        for (int i = begin; i < end; ++i) {
            vals[n++] = valsToAdd[i];
        }
    }

    public int removeLast() {
        if (n == 0)
            throw new IllegalStateException("Empty!");
        return vals[--n];
    }

    /**
     * replaces the value at index i by val.
     */
    public void set(int i, int val) {
        if (i < 0 || i >= n)
            throw new IndexOutOfBoundsException(i);
        vals[i] = val;
    }

    /**
     * swaps indices i and length - 1, and then decrements the length by 1.
     */
    public void swapOut(int i) {
        if (i < 0 || i >= n)
            throw new IndexOutOfBoundsException(i); // also always when n == 0

        if (i < n - 1) {
            int tmp = vals[i];
            vals[i] = vals[n - 1];
            vals[n - 1] = tmp;
        }

        --n;
    }

    public StringBuilder append(StringBuilder sb) {
        if (sb == null) {
            sb = new StringBuilder();
        }
        sb.append('[');
        for (int i = 0; i < n; ++i) {
            sb.append(vals[i]);
            if (i + 1 < n) {
                sb.append(", ");
            }
        }
        return sb.append(']');
    }

    public boolean equals(MutableIntArray other) {
        if (n != other.n) {
            return false;
        }

        for (int i = 0; i < n; ++i) {
            if (vals[i] != other.vals[i]) {
                return false;
            }
        }

        return true;
    }

    public void sort(Comparator<Integer> cmp) {
        Integer[] arr = Arrays.stream(vals, 0, n).boxed().toArray(Integer[]::new);
        Arrays.sort(arr, cmp);
        for (int i = 0; i < n; ++i) {
            vals[i] = arr[i];
        }
    }
}
