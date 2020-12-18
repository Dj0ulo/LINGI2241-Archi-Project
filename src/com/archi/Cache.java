package com.archi;

import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

/*
 * Inspiration
 * https://stackoverflow.com/questions/21117636/how-to-implement-a-least-frequently-used-lfu-cache#23668899
 */


public class Cache {
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

    public void add(String type, String regex, Integer[] lines) {
        if (isFull())
            map.remove(getLFUKey());
        map.put(type+";"+regex, new Entry(lines));
    }

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
