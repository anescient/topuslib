package net.chaosworship.topuslib;

import org.junit.Test;

import java.util.ArrayList;

import static net.chaosworship.topuslib.CollectionTester.*;
import static org.junit.Assert.*;


public class CollectionTesterTest {

    @Test
    public void hasDuplicateReferencesTest() {
        ArrayList<Object> list = new ArrayList<>();
        assertFalse(hasDuplicateReferences(list));
        list.add(new Object());
        list.add(new Object());
        assertFalse(hasDuplicateReferences(list));
        list.add(list.get(0));
        assertTrue(hasDuplicateReferences(list));
    }

    @Test
    public void countReferencesTest() {
        ArrayList<Object> list = new ArrayList<>();
        Object a = new Object();
        Object b = new Object();
        assertEquals(countReferences(list, a), 0);
        list.add(b);
        assertEquals(countReferences(list, a), 0);
        list.add(a);
        assertEquals(countReferences(list, a), 1);
        list.add(b);
        assertEquals(countReferences(list, a), 1);
        assertEquals(countReferences(list, b), 2);
        assertEquals(countReferences(list, null), 0);
        list.add(null);
        list.add(null);
        list.add(null);
        assertEquals(countReferences(list, null), 3);
    }

    @Test
    public void unorderedReferencesEqualTest() {
        ArrayList<Object> list1 = new ArrayList<>();
        ArrayList<Object> list2 = new ArrayList<>();
        assertTrue(unorderedReferencesEqual(list1, list2));
        list1.add(new Object());
        assertFalse(unorderedReferencesEqual(list1, list2));
        list2.add(new Object());
        assertFalse(unorderedReferencesEqual(list1, list2));
        list1.add(list2.get(0));
        assertFalse(unorderedReferencesEqual(list1, list2));
        list2.add(list1.get(0));
        assertTrue(unorderedReferencesEqual(list1, list2));
        list1.add(list1.get(0));
        assertFalse(unorderedReferencesEqual(list1, list2));
    }

    @Test
    public void intSetsEqualTest() {
        ArrayList<Integer> a = new ArrayList<>();
        ArrayList<Integer> b = new ArrayList<>();
        assertTrue(intSetsEqual(a, b));
        a.add(1);
        assertFalse(intSetsEqual(a, b));
        b.add(1);
        assertTrue(intSetsEqual(a, b));
        a.add(1);
        assertFalse(intSetsEqual(a, b));
        b.add(1);
        assertFalse(intSetsEqual(a, b));
        a.clear();
        b.clear();
        a.add(1);
        b.add(2);
        assertFalse(intSetsEqual(a, b));
        b.add(1);
        assertFalse(intSetsEqual(a, b));
        a.add(2);
        assertTrue(intSetsEqual(a, b));
        a.add(3);
        assertFalse(intSetsEqual(a, b));
        b.add(3);
        a.add(4);
        assertFalse(intSetsEqual(a, b));
        b.add(4);
        assertTrue(intSetsEqual(a, b));
    }
}
