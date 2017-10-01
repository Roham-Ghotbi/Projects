package lab9;

import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Set;

public class MyHashMap<K, V> implements Map61B<K, V> {

    private class Pair<K, V> {
        private K key;
        private V value;

        Pair(K key, V value) {
            this.key = key;
            this.value = value;
        }

        public K getKey() {
            return this.key;
        }

        public V getValue() {
            return this.value;
        }

        public void setValue(V newValue) {
            this.value = newValue;
        }
    }

    private final double loadFactor;
    private int numBuckets;
    private int numItems;
    private HashSet<K> keySet;
    private LinkedList<Pair>[] buckets;

    private int hashCreator(K key, int m) {
        return (key.hashCode() & 0x7fffffff) % m;
    }

    public MyHashMap() {
        this.numBuckets = 50;
        this.loadFactor = 1.5;
        this.keySet = new HashSet<>();
        this.numItems = 0;
        this.buckets = new LinkedList[50];

    }

    public MyHashMap(int initialSize) {
        this.numBuckets = initialSize;
        this.loadFactor = 1.5;
        this.keySet = new HashSet<>();
        this.numItems = 0;
        this.buckets = new LinkedList[initialSize];

    }

    public MyHashMap(int initialSize, double loadFactor) {
        this.numBuckets = initialSize;
        this.loadFactor = loadFactor;
        this.keySet = new HashSet<>();
        this.numItems = 0;
        this.buckets = new LinkedList[initialSize];

    }

    private Pair getPair(K key) {
        if (this.containsKey(key)) {
            int hashedKey = hashCreator(key, this.numBuckets);
            LinkedList<Pair> list = this.buckets[hashedKey];

            for (Pair pair : list) {
                if (key.equals(pair.getKey())) {
                    return pair;
                }
            }
        }
        return null;
    }

    /* Removes all of the mappings from this map. */
    @Override
    public void clear() {
        this.numBuckets = 50;
        this.keySet = new HashSet<>();
        this.numItems = 0;
        this.buckets = new LinkedList[50];
    }

    /* Returns true if this map contains a mapping for the specified key. */
    @Override
    public boolean containsKey(K key) {
        return keySet.contains(key);
    }

    /* Returns the value to which the specified key is mapped, or null if this
     * map contains no mapping for the key.*/
    @Override
    public V get(K key) {
        Pair pair = getPair(key);
        if (pair != null) {
            return (V) pair.getValue();
        }

        return null;
    }


    /* Returns the number of key-value mappings in this map. */
    @Override
    public int size() {
        return keySet.size();
    }

    /* Associates the specified value with the specified key in this map. */
    @Override
    public void put(K key, V value) {

        Pair item = new Pair(key, value);
        int temp = this.numBuckets * 2;

        if (this.containsKey(key)) {
            this.getPair(key).setValue(value);
        } else {
            if ((double) this.numItems / this.numBuckets >= this.loadFactor) {
                LinkedList<Pair>[] newBuckets = new LinkedList[this.numBuckets * 2];

                for (K mapKey : this.keySet) {
                    if (newBuckets[hashCreator(mapKey, temp)] == null) {
                        newBuckets[hashCreator(mapKey, temp)] = new LinkedList<>();
                    }
                    newBuckets[hashCreator(mapKey, temp)].add(new Pair(key, this.get(key)));
                }
                this.buckets = newBuckets;
                this.numBuckets = temp;
            }

            if (this.buckets[hashCreator(key, this.numBuckets)] == null) {
                this.buckets[hashCreator(key, this.numBuckets)] = new LinkedList<>();
            }
            this.buckets[hashCreator(key, this.numBuckets)].add(item);
            this.keySet.add(key);
            this.numItems += 1;
        }
    }

    /* Returns a Set view of the keys contained in this map. */
    @Override
    public Set<K> keySet() {
        return this.keySet;
    }

    /* Removes the mapping for the specified key from this map if present.
     * Not required for Lab 8. If you don't implement this, throw an
     * UnsupportedOperationException. */
    @Override
    public V remove(K key) {
        throw new UnsupportedOperationException();
    }

    /* Removes the entry for the specified key only if it is currently mapped to
         * the specified value. Not required for Lab 8. If you don't implement this,
         * throw an UnsupportedOperationException.*/
    @Override
    public V remove(K key, V value) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Iterator<K> iterator() {
        return this.keySet.iterator();
    }

    public static void main(String[] args) {
        MyHashMap<String, Integer> map = new MyHashMap();
        int x = 0;
        map.put("Number", 1);
        map.put("Number", 1);
        map.put("Number", 1);
        map.put("Number", 1);
        map.put("Number", 1);
        map.put("Number", 1);
        map.put("Number", 1);
        map.put("Number", 1);
        map.put("Number", 1);
        map.put("Number", 1);
        x = map.size();
        map.put("Number2", 2);
        x = map.size();
        map.put("Number3", 3);
        x = map.size();
        map.put("Number4", 4);
        x = map.size();
        map.put("Number5", 5);
        map.put("Number6", 6);
        map.put("Number7", 7);
        map.put("Number8", 8);
        map.put("Number9", 9);
        map.put("Number10", 10);
        map.get("salam");
        /*Set keys = map.keySet();
        for (String key: map) {
            System.out.println(key + " ");
        }*/
        System.out.println(map.size());
    }
}
