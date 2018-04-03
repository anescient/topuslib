package net.chaosworship.topuslib;


public class IntPair implements Cloneable {

    public final int a;
    public final int b;
    private final int hashcode;

    public IntPair(int a, int b) {
        this.a = a;
        this.b = b;
        hashcode = this.a ^ (this.b * 997);
    }

    public static IntPair sorted(int a, int b) {
        if(a < b) {
            return new IntPair(a, b);
        } else {
            return new IntPair(b, a);
        }
    }

    @Override
    protected Object clone()
            throws CloneNotSupportedException {
        super.clone();
        return new IntPair(a, b);
    }

    @Override
    public boolean equals(Object rhs) {
        if(rhs == null || !this.getClass().equals(rhs.getClass())) {
            return false;
        }

        IntPair rhsPair = (IntPair)rhs;
        return a == rhsPair.a && b == rhsPair.b;
    }

    @Override
    public int hashCode() {
        return hashcode;
    }
}
