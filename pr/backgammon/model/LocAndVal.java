package pr.backgammon.model;

import java.util.Comparator;

public class LocAndVal implements Comparable<LocAndVal> {
        public static final Comparator<LocAndVal> sortRow = new Comparator<LocAndVal>() {
            @Override
            public int compare(LocAndVal a, LocAndVal b) {
                return a.row - b.row;
            }
        };
        public static final Comparator<LocAndVal> sortCol = new Comparator<LocAndVal>() {
            @Override
            public int compare(LocAndVal a, LocAndVal b) {
                return a.col - b.col;
            }
        };

        public int row;
        public int col;
        public float val;
        public Object custom;

        public LocAndVal() {
        }

        public LocAndVal(int row, int col, float val) {
            this.row = row;
            this.col = col;
            this.val = val;
        }

        public String toString() {
            return "[row=" + row + ", col=" + col + ", val=" + val + "]";
        }

        @Override
        public int compareTo(LocAndVal other) {
            return this.val < other.val ? -1 : this.val > other.val ? 1 : 0;
        }

        /**
         * @param tmp - must have width = template.width() and height =
         *            template.height(). x and y will be set in the function
         */
        public boolean overlapsWith(LocAndVal other, int templateWidth, int templateHeight) {
            return ((col <= other.col && other.col < col + templateWidth)
                    || (other.col <= col && col < other.col + templateWidth)) &&
                    ((row <= other.row && other.row < row + templateHeight)
                            || (other.row <= row && row < other.row + templateHeight));
        }
    }
