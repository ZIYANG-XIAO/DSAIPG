package com.phasmidsoftware.dsaipg.util;

import java.util.Comparator;
import java.util.Random;

import com.phasmidsoftware.dsaipg.adt.pq.FibonacciHeap;
import com.phasmidsoftware.dsaipg.adt.pq.PQException;
import com.phasmidsoftware.dsaipg.adt.pq.PriorityQueue;

public class PriorityQueueBenchmark {

    private static void benchmarkPriorityQueue(Integer[] data, int removeCount, boolean useFloyd, int k) {
        PriorityQueue<Integer> pq = new PriorityQueue<>(data.length, 1, true, Comparator.<Integer>naturalOrder(), useFloyd, k);

        long startTime = System.nanoTime();
        for (Integer num : data) {
            pq.give(num);
        }
        for (int i = 0; i < removeCount; i++) {
            try {
                pq.take();
            } catch (PQException e) {
                e.printStackTrace();
            }
        }
        long endTime = System.nanoTime();
        System.out.println("PriorityQueue (k=" + k + ", Floyd=" + useFloyd + ") took " + (endTime - startTime) / 1e6 + " ms");
    }

    private static void benchmarkFibonacciHeap(Integer[] data, int removeCount) {
        System.out.println("⚡ FibonacciHeap benchmark started...");
        FibonacciHeap<Integer> fh = new FibonacciHeap<>();
        long startTime = System.nanoTime();
        for (Integer num : data) {
            fh.insert(num);
        }
        for (int i = 0; i < removeCount; i++) {
            fh.extractMin();
        }
        long endTime = System.nanoTime();
        System.out.println("FibonacciHeap took " + (endTime - startTime) / 1e6 + " ms");
    }

    public static void main(String[] args) {
        
        int N = 16000;
        int removeCount = 4000;
        Integer[] randomData = new Integer[N];
        Random random = new Random();

        for (int i = 0; i < N; i++) {
            randomData[i] = random.nextInt(100000);
        }

        benchmarkPriorityQueue(randomData, removeCount, false, 2);
        benchmarkPriorityQueue(randomData, removeCount, true, 2);
        benchmarkPriorityQueue(randomData, removeCount, false, 4);
        benchmarkPriorityQueue(randomData, removeCount, true, 4);

        benchmarkFibonacciHeap(randomData, removeCount);
    }
}