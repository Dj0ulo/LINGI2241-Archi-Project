package com.archi;

import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

/*
 * Inspiration
 * https://stackoverflow.com/questions/21117636/how-to-implement-a-least-frequently-used-lfu-cache#23668899
 */

/**
 * A simple implemententaion of an LFU cache where ecah entry is identified by a type and a regex and contains the list
 * of line indexes in the dataset
 */

public class Cache {

    /**
     * The class that represents an entry in the cache
     */
    class Entry {
        private Integer[] lines;
        private int frequency;

        public Entry(Integer[] lines) {
            this.lines = lines;
            this.frequency = 0;
        }

        void incrementFrequency() {
            this.frequency++;
        }

        public Integer[] getLines() {
            return lines;
        }

        public int getFrequency() {
            return frequency;
        }

        @Override
        public String toString() {
            return "Entry{" +
                    "lines=" + lines.length +
                    ", frequency=" + frequency +
                    '}';
        }
    }

    private final LinkedHashMap<String, Entry> map;
    private final int size;

    public Cache(int size) {
        this.map = new LinkedHashMap<>();
        this.size = size;
    }

    /**
     * Add an entry
     */
    public void add(String type, String regex, Integer[] lines) {
        if (isFull())
            map.remove(getLFUKey());
        map.put(type+";"+regex, new Entry(lines));
    }

    /**
     * @return the least frequently used key
     */
    public String getLFUKey() {
        String key = "";
        int minFreq = Integer.MAX_VALUE;

        for (Map.Entry<String, Entry> entry : map.entrySet()) {
            if (minFreq > entry.getValue().getFrequency()) {
                key = entry.getKey();
                minFreq = entry.getValue().getFrequency();
            }
        }

        return key;
    }

    /**
     * @return the list of line indexes in the dataset corresponding to a regex and a type
     */
    public Integer[] get(String type, String regex) {
        String key = type+";"+regex;
        if (map.containsKey(key))  // cache hit
        {
            Entry entry = map.get(key);
            entry.incrementFrequency();
            map.put(key, entry);
            return entry.getLines();
        }
        return null; // cache miss
    }

    /**
     * @return true if the cache is full
     */
    public boolean isFull() {
        return map.size() == size;
    }

    @Override
    public String toString() {
        StringBuilder entryList = new StringBuilder();
        for (Map.Entry<String, Entry> entry : map.entrySet()) {
            entryList.append("\t").append(entry).append("\n");
        }
        return "Cache{\n" +
                "map=[\n" + entryList +
                "]}";
    }
}
