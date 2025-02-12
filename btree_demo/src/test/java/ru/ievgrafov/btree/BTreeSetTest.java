package ru.ievgrafov.btree;

import org.junit.Test;
import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Random;
import java.util.TreeSet;

public class BTreeSetTest {
    private final Comparator<Integer> intComparator = Comparator.naturalOrder();

    @Test
    public void testEmptySet() {
        BTreeSet<Integer> set = new BTreeSet<>(intComparator);
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
    public void testRemoval() {
        BTreeSet<Integer> set = new BTreeSet<>(2, intComparator);
        List<Integer> values = List.of(2, 3, 5, 1, 6, 7, 4, 8);
        List<Integer> valuesToRemove = List.of(2, 3, 5, 1, 6, 4);

        values.forEach((value) -> set.add(value));
        valuesToRemove.forEach((value) -> set.remove(value));
        valuesToRemove.forEach((value) -> {
          assertEquals(
            "BTreeSet shouldn't contain removed values",
            false,
            set.contains(value)
          );
        });
        assertEquals(
          "BTreeSet shouldn't contain rest of values",
          true,
          set.contains(7)
        );
        assertEquals(
          "BTreeSet shouldn't contain rest of values",
          true,
          set.contains(8)
        );
    }

    @Test
    public void testConstructorWithCustomFactor() {
        BTreeSet<Integer> set = new BTreeSet<>(3, intComparator);
        assertTrue("New set with custom factor should be empty", set.isEmpty());
        assertEquals("New set with custom factor should have size 0", 0, set.size());
    }

    @Test
    public void testBTreeBehavesLikeTreeSet() {
        BTreeSet<Integer> set = new BTreeSet<>(30, intComparator);
        TreeSet<Integer> javaSet = new TreeSet<>(intComparator);
        Random generator = new Random(1);
        int iterations = 500000;

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

    @Test
    public void testBTreeBehavesLikeTreeSetWithFactor2() {
        BTreeSet<Integer> set = new BTreeSet<>(2, intComparator);
        TreeSet<Integer> javaSet = new TreeSet<>(intComparator);
        Random generator = new Random(1);
        int iterations = 50000;

        for (int i = 0; i < iterations; i++) {
          Integer value = generator.nextInt(0, iterations );
          assertEquals("BTreeSet and TreeSet should respond same way on insert", javaSet.add(value), set.add(value));
        }

        for (int i = 0; i < iterations; i++) {
          Integer value = generator.nextInt(0, iterations);
          assertEquals("BTreeSet and TreeSet should respond same way on remove", javaSet.remove(value), set.remove(value));
        }

        for (int i = 0; i < iterations; i++) {
          Integer value = generator.nextInt(0, iterations);
          assertEquals("BTreeSet and TreeSet should respond same way on lookup", javaSet.contains(value), set.contains(value));
        }

        assertEquals("BTreeSet and TreeSet should have same size after all ops", javaSet.size(), set.size());
    }

    @Test
    public void testBTreeBehavesLikeTreeSetAfterCompleteCleanUp() {
        BTreeSet<Integer> set = new BTreeSet<>(30, intComparator);
        TreeSet<Integer> javaSet = new TreeSet<>(intComparator);
        Random generator = new Random(1);
        int iterations = 500000;
        List<Integer> valuesToRemove= new ArrayList<>();

        for (int i = 0; i < iterations; i++) {
          Integer value = generator.nextInt(0, iterations * 2);
          javaSet.add(value);
          set.add(value);
          valuesToRemove.add(value);
        }

        valuesToRemove.forEach((value) -> {
          javaSet.remove(value);
          set.remove(value);
          assertEquals(
            "BTreeSet and TreeSet shouldn't respond true to contains after removal",
            javaSet.contains(value),
            set.contains(value)
          );
        });

        assertEquals("BTreeSet should be empty after all keys removed", 0, set.size());

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

    // @Test
    // public void testSparseTreeBehavesLikeTreeSet() {
    //     BTreeSet<Integer> set = new BTreeSet<>(30, intComparator);
    //     TreeSet<Integer> javaSet = new TreeSet<>(intComparator);
    //     Random generator = new Random(1);
    //     int targetSize = 100000;
    //     List<Integer> toBeRemoved = new ArrayList<>();

    //     while (javaSet.size() < targetSize) {
    //         for(int i = 0; i < 100000; i++) {
    //             Integer value = generator.nextInt(0, targetSize * 10);
    //             javaSet.add(value);
    //             set.add(value);

    //             if (generator.nextInt(0, 5) != 0) {
    //                 toBeRemoved.add(value);
    //             }
    //         }

    //         toBeRemoved.forEach((value) -> {
    //             javaSet.remove(value);
    //             set.remove(value);
    //         });
    //         toBeRemoved.clear();
    //     }

        
        
    //     System.out.println("Set size: " + javaSet.size() + "; BTree set size: " + set.size());
    //     Map<Integer, Integer> sizes = set.nodesCountByLevel();

    //     sizes.forEach((level, size) -> System.out.println(size + " nodes on level " + level));

    //     assertEquals("BTreeSet and TreeSet should have same size after all ops", javaSet.size(), set.size());
    // }
}