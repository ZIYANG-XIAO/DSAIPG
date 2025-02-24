package com.phasmidsoftware.dsaipg.adt.pq;

import java.util.Arrays;
import java.util.Comparator;
import java.util.Iterator;
import java.util.function.BiPredicate;
import java.util.function.Consumer;

/**
 * Priority Queue Data Structure which supports k-ary heaps.
 */
public class PriorityQueue<K> implements Iterable<K> {

    private final boolean max;
    private final int first;
    private final int k; // 支持 k-ary 堆
    private final Comparator<K> comparator;
    private final K[] binHeap;
    private int last;
    private final boolean floyd;

    /**
     * Primary constructor that takes the k-ary heap type, an actual array of elements, and a comparator.
     */
    public PriorityQueue(boolean max, Object[] binHeap, int first, int last, Comparator<K> comparator, boolean floyd, int k) {
        this.max = max;
        this.first = first;
        this.comparator = comparator;
        this.last = last;
        this.k = k; // 设定 k 叉堆
        //noinspection unchecked
        this.binHeap = (K[]) binHeap;
        this.floyd = floyd;
    }

    /**
     * Constructor with max capacity and comparator.
     */
    public PriorityQueue(int n, int first, boolean max, Comparator<K> comparator, boolean floyd, int k) {
        this(max, new Object[n + first], first, 0, comparator, floyd, k);
    }

    public boolean isEmpty() {
        return last == 0;
    }

    public int size() {
        return last;
    }

    public void give(K key) {
        if (last == binHeap.length - first) last--;
        binHeap[++last + first - 1] = key;
        swimUp(last + first - 1);
    }

    public K take() throws PQException {
        if (isEmpty()) throw new PQException("Priority queue is empty");
        return floyd ? doTake(this::snake) : doTake(this::sink);
    }

    private K doTake(Consumer<Integer> f) {
        K result = binHeap[first];
        swap(first, last-- + first - 1);
        f.accept(first);
        binHeap[last + first] = null;
        return result;
    }

    private void sink(int k) {
        doHeapify(k, (a, b) -> !unordered(a, b));
    }

    private void snake(int k) {
        swimUp(doHeapify(k, (a, b) -> !unordered(a, b)));
    }

    private void swimUp(int k) {
        int i = k;
        while (i > first && unordered(parent(i), i)) {
            swap(i, parent(i));
            i = parent(i);
        }
    }

    private boolean unordered(int i, int j) {
        return (comparator.compare(binHeap[i], binHeap[j]) > 0) ^ max;
    }

    private int doHeapify(int k, BiPredicate<Integer, Integer> p) {
        int i = k;
        while (firstChild(i) <= last + first - 1) {
            int j = firstChild(i);
            for (int child = j + 1; child < j + k && child <= last + first - 1; child++) {
                if (unordered(j, child)) j = child;
            }
            if (p.test(i, j)) break;
            swap(i, j);
            i = j;
        }
        return i;
    }

    private void swap(int i, int j) {
        K tmp = binHeap[i];
        binHeap[i] = binHeap[j];
        binHeap[j] = tmp;
    }

    private int parent(int k) {
        return (k - first - 1) / this.k + first;
    }

    private int firstChild(int k) {
        return k * k - k + first + 1;
    }

    public Iterator<K> iterator() {
        return Arrays.asList(Arrays.copyOf(binHeap, last + first)).iterator();
    }
}