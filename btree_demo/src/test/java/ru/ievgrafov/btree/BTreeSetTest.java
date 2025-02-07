package ru.ievgrafov.btree;

import org.junit.Test;
import static org.junit.Assert.*;
import java.util.Comparator;
import java.util.Random;
import java.util.TreeSet;

public class BTreeSetTest {
    private final Comparator<Integer> intComparator = Comparator.naturalOrder();

    @Test
    public void testEmptySet() {
        BTreeSet<Integer> set = new BTreeSet<Integer>(intComparator);
        assertTrue("New set should be empty", set.isEmpty());
        assertEquals("New set size should be 0", 0, set.size());
    }

    @Test
    public void testAddSingleElement() {
        BTreeSet<String> set = new BTreeSet<String>(Comparator.naturalOrder());
        assertTrue("First add should return true", set.add("test"));
        assertTrue("Set should contain added element", set.contains("test"));
    }

    @Test
    public void testAddDuplicateElement() {
        BTreeSet<Integer> set = new BTreeSet<>(intComparator);
        set.add(42);
        assertFalse("Duplicate add should return false", set.add(42));
    }

    @Test
    public void testContainsAfterMultipleAdds() {
        BTreeSet<Integer> set = new BTreeSet<>(2, intComparator);
        for (int i = 0; i < 10; i++) {
            set.add(i);
        }
        assertTrue("Should contain first element", set.contains(0));
        assertTrue("Should contain last element", set.contains(9));
        assertFalse("Should not contain unadded element", set.contains(10));
    }

    @Test
    public void testCustomComparatorBehavior() {
        Comparator<String> caseInsensitive = String.CASE_INSENSITIVE_ORDER;
        BTreeSet<String> set = new BTreeSet<>(caseInsensitive);
        set.add("Apple");
        assertTrue("Should respect case-insensitive comparison", set.contains("apple"));
    }

    @Test
    public void testReverseOrderInsertion() {
        BTreeSet<Integer> set = new BTreeSet<>(2, intComparator);
        for (int i = 5; i > 0; i--) {
            set.add(i);
        }
        for (int i = 1; i <= 5; i++) {
            assertTrue("Should contain all reversed elements", set.contains(i));
        }
    }

    @Test
    public void testConstructorWithCustomFactor() {
        BTreeSet<Integer> set = new BTreeSet<Integer>(3, intComparator);
        assertTrue("New set with custom factor should be empty", set.isEmpty());
        assertEquals("New set with custom factor should have size 0", 0, set.size());
    }

    @Test
    public void testTreeBehavesLikeTreeSet() {
        BTreeSet<Integer> set = new BTreeSet<Integer>(30, intComparator);
        TreeSet<Integer> javaSet = new TreeSet<Integer>(intComparator);
        Random generator = new Random(1);
        int iterations = 50000;

        for (int i = 0; i < iterations; i++) {
          Integer value = generator.nextInt(0, iterations * 2);
          assertEquals("BTreeSet and TreeSet should respond same way on insert", javaSet.add(value), set.add(value));
        }

        for (int i = 0; i < iterations; i++) {
          Integer value = generator.nextInt(0, iterations * 2);
          assertEquals("BTreeSet and TreeSet should respond same way on remove", javaSet.remove(value), set.remove(value));
        }

        for (int i = 0; i < iterations; i++) {
          Integer value = generator.nextInt(0, iterations * 2);
          assertEquals("BTreeSet and TreeSet should respond same way on lookup", javaSet.contains(value), set.contains(value));
        }

        assertEquals("BTreeSet and TreeSet should have same size after all ops", javaSet.size(), set.size());
    }

}