package net.chaosworship.topuslib;

import net.chaosworship.topuslib.tuple.IntPair;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;


@SuppressWarnings("WeakerAccess")
public class CollectionTester {

    private CollectionTester() {}

    public static boolean hasDuplicateReferences(List list) {
        for(int i = 0; i < list.size(); i++) {
            for(int j = i + 1; j < list.size(); j++) {
                if(list.get(i) == list.get(j)) {
                    return true;
                }
            }
        }
        return false;
    }

    // return number of occurrences of reference to object
    public static int countReferences(Iterable iterable, Object object) {
        int count = 0;
        for(Object o : iterable) {
            if(o == object) {
                count++;
            }
        }
        return count;
    }

    public static <T> boolean orderedReferencesEqual(List<T> a, List<T> b) {
        if(a.size() != b.size()) {
            return false;
        }
        for(int i = 0; i < a.size(); i++) {
            if(a.get(i) != b.get(i)) {
                return false;
            }
        }
        return true;
    }

    // check that two lists have identical contents regardless of order
    public static <T> boolean unorderedReferencesEqual(List<T> a, List<T> b) {
        if(a.size() != b.size()) {
            return false;
        }
        for(Object o : a) {
            if(countReferences(a, o) != countReferences(b, o)) {
                return false;
            }
        }
        return true;
    }

    // check that two arrays have identical contents regardless of order
    public static <T> boolean unorderedReferencesEqual(T[] a, T[] b) {
        return unorderedReferencesEqual(Arrays.asList(a), Arrays.asList(b));
    }

    public static boolean intSetsEqual(Iterable<Integer> a, Iterable<Integer> b) {
        HashSet<Integer> aSet = new HashSet<>();
        for(Integer aInt : a) {
            if(aSet.contains(aInt)) {
                return false; // not a set
            }
            aSet.add(aInt);
        }

        HashSet<Integer> bSet = new HashSet<>();
        for(Integer bInt : b) {
            if(bSet.contains(bInt)) {
                return false; // not a set
            }
            if(!aSet.contains(bInt)) {
                return false;
            }
            bSet.add(bInt);
        }

        return aSet.size() == bSet.size();
    }

    public static boolean intPairSetsEqual(Iterable<IntPair> pa, Iterable<IntPair> pb) {
        HashSet<IntPair> aSet = new HashSet<>();
        HashSet<IntPair> bSet = new HashSet<>();
        for(IntPair p : pa) {
            aSet.add(p);
        }
        for(IntPair p : pb) {
            bSet.add(p);
        }
        return aSet.equals(bSet);
    }
}
