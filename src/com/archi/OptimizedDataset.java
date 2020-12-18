package com.archi;

import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.util.regex.Pattern;
import java.util.stream.Stream;

public class OptimizedDataset extends Dataset {
    private int[] indexTypes;
    private final Cache cache = new Cache(50);

    @Override
    public long load(int number) {
        long start = System.currentTimeMillis();

        dataset = new ArrayList<>();
        List <Entry> tmpList = new ArrayList<>();
        try (Stream<String> stream = Files.lines(Paths.get(FILENAME))) {
            stream.limit(number).forEach(line -> tmpList.add(new Entry(line.split("@@@"))));
        } catch (IOException e) {
            e.printStackTrace();
        }

        tmpList.sort(Entry::compareTo); // sort dataset

        // remove duplicates
        for (int i = 0, k = 1; i < tmpList.size(); i++) {
            if (i == tmpList.size()-1 || !tmpList.get(i).getSentence().equals(tmpList.get(i + 1).getSentence())) { // if next element not the same
                this.dataset.add(new Entry(tmpList.get(i).getType(), tmpList.get(i).getSentence(), k));
                k=1;
            }else{
                k++;
            }
        }

        // keep index of types in memory
        int maxType = this.dataset.get(this.dataset.size() - 1).getType();
        indexTypes = new int[maxType + 1];
        indexTypes[0] = 0;
        int prev = 0;
        for (int i = 0; i < this.dataset.size(); i++) {
            if (this.dataset.get(i).getType() != prev) {
                int cur = this.dataset.get(i).getType();
                indexTypes[cur] = i;
                if (cur == maxType)
                    break;
                prev = this.dataset.get(i).getType();
            }
        }

        return System.currentTimeMillis() - start;
    }

    @Override
    public void match(PrintWriter out, String type, String regex) {
        Pattern pattern = Pattern.compile(regex);
        if (regex.equals(""))
            return;
        int intType = type.equals("") ? -1 : Integer.parseInt(type);
        int[] bounds = indexType(intType);
        List<Integer> indexes = new ArrayList<>();
        StringBuilder result = new StringBuilder();

        Integer[] cacheLines = cache.get(type, regex);
        if (cacheLines == null) {
            for (int i = bounds[0]; i < bounds[1]; i++) {
                Entry entry = this.dataset.get(i);
                if (pattern.matcher(entry.getSentence()).matches()) {
                    indexes.add(i);
                    result.append(entry);
                }
            }
            cache.add(type, regex, indexes.toArray(new Integer[0]));
        } else {
            for (Integer cacheLine : cacheLines) {
                result.append(this.dataset.get(cacheLine));
            }
        }
        out.write(result.toString());
    }

    public void freq() {
        int[] table = new int[256];
        this.dataset.forEach(line -> {
            for (int i = 0; i < line.getSentence().length(); i++) {
                char c = line.getSentence().charAt(i);
                if (c < 256)
                    table[c]++;
            }
        });
        for (int i = 0; i < table.length; i++) {
            System.out.println(Character.toString(i) + " : " + table[i]);
        }
    }

    private int[] indexType(int type) {
        if (type == -1)
            return new int[]{0, this.dataset.size()};
        else if (0 <= type && type < this.indexTypes.length - 1)
            return new int[]{this.indexTypes[type], this.indexTypes[type + 1]};
        else if (type == indexTypes.length - 1)
            return new int[]{this.indexTypes[type], this.dataset.size()};
        else
            return new int[]{0, 0};
    }


}
