package lab8;

import java.util.Iterator;
import java.util.Set;

public class BSTMap<K extends Comparable<K>, V> implements Map61B<K, V> {
    private class Node<K, V> {
        private K key;
        private V value;
        private Node left;
        private Node right;

        private V getValue() {
            return this.value;
        }

        private K getKey() {
            return this.key;
        }

        private Node() {
            this.key = null;
            this.value = null;
            this.left = null;
            this.right = null;
        }

        private Node(K key, V value) {
            this.key = key;
            this.value = value;
            this.left = null;
            this.right = null;
        }

        private void print() {
            if (this == null) {
                System.out.println("");
            }
            this.left.print();
            System.out.println(this.value + " ");
            this.right.print();
        }
    }

    private Node root = null;
    private int size;

    public BSTMap() {
        this.root = null;
        this.size = 0;
    }

    @Override
    public void clear() {
        this.root = null;
        this.size = 0;
    }

    private boolean keyFinder(K key, Node node) {
        if (node == null) {
            return false;
        }
        int cmp = key.compareTo((K) node.getKey());

        if (cmp < 0) {
            return keyFinder(key, node.left);
        } else if (cmp > 0) {
            return keyFinder(key, node.right);
        } else {
            return key.equals((K) node.getKey());
        }
    }

    @Override
    public boolean containsKey(K key) {
        return keyFinder(key, this.root);
    }

    private V getHelper(K key, Node node) {
        if (node == null) {
            return null;
        }
        int cmp = key.compareTo((K) node.getKey());

        if (cmp < 0) {
            return getHelper(key, node.left);
        } else if (cmp > 0) {
            return getHelper(key, node.right);
        } else {
            V val = (V) node.getValue();
            return val;
        }
    }

    @Override
    public V get(K key) {
        V value = getHelper(key, this.root);
        return value;
    }

    @Override
    public int size() {
        return this.size;
    }

    private Node putHelp(K key, V value, Node node) {

        if (node == null) {
            size += 1;
            return new Node(key, value);
        }
        int cmp = key.compareTo((K) node.getKey());
        if (cmp < 0) {
            node.left = putHelp(key, value, node.left);
        } else if (cmp > 0) {
            node.right = putHelp(key, value, node.right);
        } else {
            node.value = value;
        }
        return node;
    }

    @Override
    public void put(K key, V value) {
        this.root = putHelp(key, value, this.root);
    }

    @Override
    public Set<K> keySet() {
        throw new UnsupportedOperationException();
    }

    @Override
    public V remove(K key) {
        throw new UnsupportedOperationException();
    }

    @Override
    public V remove(K key, V value) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Iterator<K> iterator() {
        throw new UnsupportedOperationException();
    }

    public void printInOrder() {
        root.print();
    }
}
