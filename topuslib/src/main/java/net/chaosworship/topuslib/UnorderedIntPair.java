package net.chaosworship.topuslib;


public class UnorderedIntPair {

    public int a;
    public int b;

    public UnorderedIntPair(int a, int b) {
        this.a = a;
        this.b = b;
    }

    @Override
    public boolean equals(Object rhs) {
        if(rhs == null || !this.getClass().equals(rhs.getClass())) {
            return false;
        }

        UnorderedIntPair rhsPair = (UnorderedIntPair)rhs;
        return (a == rhsPair.a && b == rhsPair.b) ||
                (a == rhsPair.b && b == rhsPair.a);
    }

    @Override
    public int hashCode() {
        return a ^ 137 * b;
    }

    public boolean includes(int x) {
        return a == x || b == x;
    }
}
