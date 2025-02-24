package com.phasmidsoftware.dsaipg.adt.pq;

import java.util.HashMap;
import java.util.Map;

public class FibonacciHeap<T extends Comparable<T>> {

    private Node<T> minNode;
    private int size;

    private static class Node<T> {
        T key;
        int degree;
        Node<T> parent, child, left, right;
        boolean marked;

        Node(T key) {
            this.key = key;
            left = right = this;
        }
    }

    public FibonacciHeap() {
        minNode = null;
        size = 0;
    }

    public void insert(T key) {
        Node<T> node = new Node<>(key);
        minNode = mergeLists(minNode, node);
        size++;
    }

    public T extractMin() {
        if (minNode == null) {
            System.out.println("⚠️ extractMin: minNode is null, returning...");
            return null;
        }

        System.out.println("🔹 extractMin: Removing minNode with key = " + minNode.key);
        size--;

        Node<T> oldMin = minNode;
        Node<T> rightNode = minNode.right;

        // 把 minNode 的所有子节点提升到根列表
        if (minNode.child != null) {
            Node<T> child = minNode.child;
            Node<T> start = child;
            do {
                child.parent = null;
                child = child.right;
            } while (child != start);
            System.out.println("🔹 extractMin: Merging children into root list...");
            minNode = mergeLists(minNode.right, minNode.child);
        } else {
            minNode = minNode.right;
        }

        // ✅ 确保 `minNode` 被正确移除
        removeNode(oldMin);

        // ✅ 如果 `minNode` 是最后一个元素，设置为 `null`
        if (oldMin == rightNode) {
            minNode = null;
        } else {
            System.out.println("🔹 extractMin: Calling consolidate()...");
            consolidate();
        }

        System.out.println("✅ extractMin: Returning " + oldMin.key);
        return oldMin.key;
    }

    private void consolidate() {
        System.out.println("🔄 consolidate: Starting consolidation...");
        Map<Integer, Node<T>> degreeMap = new HashMap<>();

        if (minNode == null) {
            System.out.println("⚠️ consolidate: minNode is null, skipping...");
            return;
        }

        // 获取所有根节点，防止无限循环
        Node<T>[] rootNodes = new Node[size];
        Node<T> start = minNode;
        Node<T> current = minNode;
        int count = 0;

        do {
            rootNodes[count++] = current;
            current = current.right;
        } while (current != start && count < size);

        // 合并相同度数的树
        for (int i = 0; i < count; i++) {
            Node<T> node = rootNodes[i];
            int degree = node.degree;

            while (degreeMap.containsKey(degree)) {
                Node<T> other = degreeMap.get(degree);
                if (node == other) break; // 避免 self-loop

                if (node.key.compareTo(other.key) > 0) {
                    Node<T> temp = node;
                    node = other;
                    other = temp;
                }

                link(other, node);
                degreeMap.remove(degree);
                degree++;
            }

            degreeMap.put(degree, node);
        }

        // 重新找到 minNode
        minNode = null;
        for (Node<T> node : degreeMap.values()) {
            minNode = mergeLists(minNode, node);
        }

        System.out.println("✅ consolidate: Finished consolidation.");
    }

    private void link(Node<T> child, Node<T> parent) {
        removeNode(child);
        child.parent = parent;
        child.right = child.left = child;
        parent.child = mergeLists(parent.child, child);
        parent.degree++;
        child.marked = false;
    }

    private void removeNode(Node<T> node) {
        if (node.right == node) return;
        node.left.right = node.right;
        node.right.left = node.left;
    }

    private Node<T> mergeLists(Node<T> first, Node<T> second) {
        if (first == null) return second;
        if (second == null) return first;
        Node<T> firstRight = first.right;
        first.right = second;
        second.left = first;
        firstRight.left = second.right;
        second.right = firstRight;
        return first.key.compareTo(second.key) < 0 ? first : second;
    }

    public boolean isEmpty() {
        return minNode == null;
    }

    public int size() {
        return size;
    }
}