package com.phasmidsoftware.dsaipg.sort.benchmark;

import java.io.IOException;
import java.util.Arrays;
import java.util.Random;

import com.phasmidsoftware.dsaipg.sort.elementary.InsertionSortComparator;
import com.phasmidsoftware.dsaipg.util.Config;
import com.phasmidsoftware.dsaipg.util.Config_Benchmark;
import com.phasmidsoftware.dsaipg.util.Timer;

public class BenchmarkSort {
    public static void main(String[] args) {
        int[] sizes = {1000, 2000, 4000, 8000, 16000}; // 倍增法
        Random random = new Random();

        System.out.println("Insertion Sort Benchmark:");
        System.out.println("Size\tRandom\tOrdered\tPartially-Ordered\tReversed");

        for (int n : sizes) {
            Integer[] randomArray = generateRandomArray(n, random);
            Integer[] orderedArray = generateOrderedArray(n);
            Integer[] partiallyOrderedArray = generatePartiallyOrderedArray(n, random);
            Integer[] reversedArray = generateReversedArray(n);

            double timeRandom = runBenchmark(randomArray);
            double timeOrdered = runBenchmark(orderedArray);
            double timePartiallyOrdered = runBenchmark(partiallyOrderedArray);
            double timeReversed = runBenchmark(reversedArray);

            System.out.printf("%d\t%.3fms\t%.3fms\t%.3fms\t%.3fms\n", 
                n, timeRandom, timeOrdered, timePartiallyOrdered, timeReversed);
        }
    }

    private static double runBenchmark(Integer[] array) {
        Config config;
        try {
            config = Config.load(InsertionSortComparator.class);
            if (config == null) {
                System.err.println("⚠ Warning: Config is null! Using default configuration.");
                config = Config_Benchmark.setupConfigFixes();
            }
        } catch (IOException e) {
            throw new RuntimeException("Failed to load config", e);
        }

        InsertionSortComparator<Integer> sorter = new InsertionSortComparator<>(Integer::compareTo, array.length, 1, config);
        Timer timer = new Timer();
        return timer.repeat(5, () -> {
            Integer[] copy = Arrays.copyOf(array, array.length);
            sorter.sort(copy, 0, copy.length);
            return null;
        });
    }

    private static Integer[] generateRandomArray(int n, Random random) {
        Integer[] arr = new Integer[n];
        for (int i = 0; i < n; i++) arr[i] = random.nextInt(10000);
        return arr;
    }

    private static Integer[] generateOrderedArray(int n) {
        Integer[] arr = new Integer[n];
        for (int i = 0; i < n; i++) arr[i] = i;
        return arr;
    }

    private static Integer[] generatePartiallyOrderedArray(int n, Random random) {
        Integer[] arr = generateOrderedArray(n);
        for (int i = 0; i < n / 10; i++) { // 10% 的元素进行交换，而不是赋新值
            int index1 = random.nextInt(n);
            int index2 = random.nextInt(n);
            int temp = arr[index1];
            arr[index1] = arr[index2];
            arr[index2] = temp;
        }
        return arr;
    }

    private static Integer[] generateReversedArray(int n) {
        Integer[] arr = new Integer[n];
        for (int i = 0; i < n; i++) arr[i] = n - i;
        return arr;
    }
}
