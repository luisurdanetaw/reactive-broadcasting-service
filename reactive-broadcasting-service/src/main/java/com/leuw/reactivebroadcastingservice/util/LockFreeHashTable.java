package com.leuw.reactivebroadcastingservice.util;

import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;
import java.util.concurrent.atomic.AtomicReferenceArray;

//Experiment
//doesnt work
public class LockFreeHashTable<K, V> {

    private record Entry<K, V>(K key, V value) {}
    private final AtomicReferenceArray<Entry<K,V>> table;
    private final AtomicReference<AtomicReferenceArray<Entry<K, V>>> nextTable;
    private final AtomicBoolean resizing;
    private final AtomicInteger size;
    private final double loadFactor;
    private final int capacity;

    public LockFreeHashTable(int capacity) {
        this.capacity = capacity;
        this.table = new AtomicReferenceArray<>(capacity);
        this.nextTable = new AtomicReference<>(null);
        this.resizing = new AtomicBoolean(false);
        this.size = new AtomicInteger(0);
        this.loadFactor = 0.75;
    }

    public void put(K key, V value) {
        if (resizing.get()) {
            helpResize();
        }

        int hash = key.hashCode();
        int index = hash & (capacity - 1);

        Entry<K, V> newEntry = new Entry<>(key, value);

        for (int i = index; ; i = (i + 1) & (capacity - 1)) {
            Entry<K, V> cur = table.get(i);
            if (cur == null) {
                if (table.compareAndSet(i, null, newEntry)) {
                    size.incrementAndGet();
                    break;
                }
            } else if (cur.key.equals(key)) {
                if (table.compareAndSet(i, cur, newEntry)) {
                    break;
                }
            }
        }

        if ((double)size.get() / capacity > loadFactor) {
            resize();
        }
    }

    public V get(K key) {
        if (resizing.get()) {
            helpResize();
        }

        int hash = key.hashCode();
        int index = hash & (capacity - 1);

        for (int i = 0; i < capacity; i++) {
            Entry<K, V> cur = table.get(index);
            if (cur == null) {
                return null;
            }
            if (cur.key.equals(key)) {
                return cur.value;
            }
            index = (index + 1) & (capacity - 1);
        }
        return null;
    }

    private void resize() {
        if (resizing.compareAndSet(false, true)) {
            int newCapacity = capacity * 2;
            AtomicReferenceArray<Entry<K, V>> newTable = new AtomicReferenceArray<>(newCapacity);
            nextTable.set(newTable);

            for (int i = 0; i < capacity; i++) {
                Entry<K, V> entry = table.get(i);
                if (entry != null) {
                    int hash = entry.key.hashCode();
                    int index = hash & (newCapacity - 1);

                    while (true) {
                        Entry<K, V> cur = newTable.get(index);
                        if (cur == null) {
                            if (newTable.compareAndSet(index, null, entry)) {
                                break;
                            }
                        } else {
                            index = (index + 1) & (newCapacity - 1);
                        }
                    }
                }
            }

            nextTable.set(null);
            resizing.set(false);
        }
    }

    private void helpResize() {
        AtomicReferenceArray<Entry<K, V>> currentNextTable = nextTable.get();
        if (currentNextTable == null) {
            return;
        }

        for (int i = 0; i < capacity; i++) {
            Entry<K, V> entry = table.get(i);
            if (entry != null) {
                int hash = entry.key.hashCode();
                int index = hash & (currentNextTable.length() - 1);

                while (true) {
                    Entry<K, V> cur = currentNextTable.get(index);
                    if (cur == null) {
                        if (currentNextTable.compareAndSet(index, null, entry)) {
                            break;
                        }
                    } else {
                        index = (index + 1) & (currentNextTable.length() - 1);
                    }
                }
            }
        }
    }
}