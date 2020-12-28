package com.archi;

import java.io.PrintWriter;
import java.util.*;
import java.util.regex.Pattern;
import java.util.stream.Stream;

/**
 * Dataset optimized with :
 * - Sorting by types and keeping the indexes of the first element with each type
 * - Removing duplicates and taking count of them in Dataset.Entry
 * - Caching results
 */
public class OptimizedDataset extends Dataset {
    private final Stream<String> stream;
    private final HashMap<Integer, BoundType> types = new HashMap<>();
    private final Cache cache = new Cache(50);

    private static class BoundType {
        private final int start;
        private final int end;

        public BoundType(int start, int end) {
            this.start = start;
            this.end = end;
        }

        public int getStart() {
            return start;
        }

        public int getEnd() {
            return end;
        }
    }

    public OptimizedDataset(Stream<String> stream) {
        this.stream = stream;
    }

    public OptimizedDataset(String content) {
        this.stream = content.lines();
    }

    @Override
    public long load(int number) {
        long start = System.currentTimeMillis();

        List<Dataset.Entry> tmpList = new ArrayList<>();

        stream.limit(number)
                .sorted()// sorted stream !
                .forEach(line -> {
                    if (!line.equals("")) tmpList.add(new Dataset.Entry(line.split("@@@")));
                });


        // remove duplicates
        for (int i = 0, k = 1; i < tmpList.size(); i++) {
            if (i == tmpList.size() - 1 || !tmpList.get(i).getSentence().equals(tmpList.get(i + 1).getSentence())) { // if next element not the same
                this.dataset.add(new Dataset.Entry(tmpList.get(i).getType(), tmpList.get(i).getSentence(), k));
                k = 1;
            } else {
                k++;
            }
        }

        // keep index of types in memory
        int maxType = this.dataset.size() == 0 ? -1 : this.dataset.get(this.dataset.size() - 1).getType();

        int prev = 0, prevIndex = 0;
        for (int i = 0; i < this.dataset.size(); i++) {
            if (this.dataset.get(i).getType() != prev) {
                types.put(prev, new BoundType(prevIndex, i));
                prevIndex = i;
                int cur = this.dataset.get(i).getType();
                if (cur == maxType) {
                    types.put(cur, new BoundType(i, this.dataset.size()));
                    break;
                }
                prev = this.dataset.get(i).getType();
            }
        }
        return System.currentTimeMillis() - start;
    }

    @Override
    public long match(PrintWriter out, String type, String regex) {
        long start = System.currentTimeMillis();
        out.write(match(type, regex));
        return System.currentTimeMillis() - start;
    }

    @Override
    public String match(String type, String regex) {
        Pattern pattern = compileRegex(regex);
        if (pattern != null && !regex.equals("")) {
            boolean all = regex.equals(".*"); // if the regex matches all sentences

            int intType = type.equals("") ? -1 : Integer.parseInt(type);
            BoundType bounds = indexType(intType);
            List<Integer> indexes = new ArrayList<>();
            StringBuilder result = new StringBuilder();

            Integer[] cacheLines = cache.get(type, regex);
            if (cacheLines == null) {// if the regex is not in the cache
                for (int i = bounds.start; i < bounds.end; i++) {
                    Dataset.Entry entry = this.dataset.get(i);
                    if (all || pattern.matcher(entry.getSentence()).matches()) {
                        indexes.add(i);
                        result.append(entry);
                    }
                }
                //add to cache
                cache.add(type, regex, indexes.toArray(new Integer[0]));
            } else {
                for (Integer cacheLine : cacheLines) {
                    result.append(this.dataset.get(cacheLine));
                }
            }
            return result.toString();
        }
        return "";
    }

    /**
     * @return the boundaries (indexes) of a type in the dataset
     */
    private BoundType indexType(int type) {
        if (type == -1)
            return new BoundType(0, this.dataset.size());
        else if (types.containsKey(type))
            return types.get(type);
        else
            return new BoundType(0, 0);
    }

    /**
     * @return the total number of entries (with duplicates)
     */
    public int entryNumber() {
        return match("", ".*").split("\n").length;
    }


}
