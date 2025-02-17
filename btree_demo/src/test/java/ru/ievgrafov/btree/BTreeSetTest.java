package ru.ievgrafov.btree;

import org.junit.Test;
import static org.junit.Assert.*;

import java.util.Comparator;
import java.util.Iterator;
import java.util.List;

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
    public void testRemovalThroughIterator() {
        BTreeSet<Integer> set = new BTreeSet<>(2, intComparator);
        List<Integer> values = List.of(2, 3, 5, 1, 6, 7, 4, 8);
        List<Integer> valuesToRemove = List.of(2, 3, 5, 1, 6, 4);

        values.forEach((value) -> set.add(value));
        Iterator<Integer> iterator = set.iterator();

        while (iterator.hasNext()) {
          if (valuesToRemove.contains(iterator.next())) iterator.remove();
        }

        assertArrayEquals("BTreeSet should contain only elements that weren't removed", new Integer[]{7, 8}, set.toArray());
    }

    @Test
    public void testConstructorWithCustomFactor() {
        BTreeSet<Integer> set = new BTreeSet<>(3, intComparator);
        assertTrue("New set with custom factor should be empty", set.isEmpty());
        assertEquals("New set with custom factor should have size 0", 0, set.size());
    }
}
