package net.chaosworship.topuslib;


@SuppressWarnings("WeakerAccess")
public class IntTriple implements Cloneable {

    public final int a;
    public final int b;
    public final int c;
    private final int hashcode;

    public IntTriple(int a, int b, int c) {
        this.a = a;
        this.b = b;
        this.c = c;
        hashcode = this.a ^ (this.b * 137) ^ (this.c * 997);
    }

    public static IntTriple sorted(int a, int b, int c) {
        if(a < b) {
            if(b < c) {
                return new IntTriple(a, b, c);
            } else {
                if(a < c) {
                    return new IntTriple(a, c, b);
                } else {
                    return new IntTriple(c, a, b);
                }
            }
        } else {
            if(b > c) {
                return new IntTriple(c, b, a);
            } else {
                if(a > c) {
                    return new IntTriple(b, c, a);
                } else {
                    return new IntTriple(b, a, c);
                }
            }
        }
    }

    @Override
    protected Object clone()
            throws CloneNotSupportedException {
        super.clone();
        return new IntTriple(a, b, c);
    }

    @Override
    public boolean equals(Object rhs) {
        if(rhs == null || !this.getClass().equals(rhs.getClass())) {
            return false;
        }

        IntTriple rhsTriple = (IntTriple)rhs;
        return a == rhsTriple.a && b == rhsTriple.b && c == rhsTriple.c;
    }

    @Override
    public int hashCode() {
        return hashcode;
    }

    public boolean includes(int x) {
        return a == x || b == x || c == x;
    }

    public boolean includesPair(IntPair pair) {
        return includesPair(pair.a, pair.b);
    }

    public boolean includesPair(int i, int j) {
        return
            ((a == i) && (b == j || c == j)) ||
            ((b == i) && (a == j || c == j)) ||
            ((c == i) && (a == j || b == j));
    }

    public boolean includesAnyOver(int n) {
        return a > n || b > n || c > n;
    }

    public int getThird(IntPair firstAndSecond) {
        return getThird(firstAndSecond.a, firstAndSecond.b);
    }

    public int getThird(int first, int second) {
        if(!includesPair(first, second)) {
            throw new IllegalArgumentException();
        }
        if(first == a) {
            return second == b ? c : b;
        } else if(first == b) {
            return second == a ? c : a;
        } else if(first == c) {
            return second == a ? b : a;
        } else {
            throw new AssertionError();
        }
    }
}
