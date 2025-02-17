package ru.ievgrafov.btree;

import java.util.Comparator;

public class CountingComparatorWrapper<T> implements Comparator<T> {
    private final Comparator<T> comparator;
    private int compareCount = 0;

    public CountingComparatorWrapper(Comparator<T> comparator) {
        this.comparator = comparator;
    }

    @Override
    public int compare(T o1, T o2) {
        compareCount++;
        return comparator.compare(o1, o2);
    }

    public int getCompareCount() {
        return compareCount;
    }

    public void resetCompareCount() {
        compareCount = 0;
    }
}
