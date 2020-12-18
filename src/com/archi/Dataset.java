package com.archi;

import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Stream;

public abstract class Dataset {
    public final static String FILENAME = "dbdata.txt";
    protected List<Entry> dataset;
    class Entry implements Comparable<Entry>{
        private final int type;
        private final String sentence;
        private int count;

        public Entry(int type, String sentence) {
            this.type = type;
            this.sentence = sentence;
            this.count = 1;
        }
        public Entry(String[] line) {
            this.type = Integer.parseInt(line[0]);
            this.sentence = line[1];
        }

        public Entry(int type, String sentence, int count) {
            this.type = type;
            this.sentence = sentence;
            this.count = count;
        }

        public int getType() {
            return type;
        }

        public String getSentence() {
            return sentence;
        }

        public int getCount() {
            return count;
        }

        public int compareTo(Entry e) {
            if (this.getType() == e.getType()) {
                return this.getSentence().compareTo(e.getSentence());
            }
            return this.getType() - e.getType();
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            Entry entry = (Entry) o;
            return type == entry.type && Objects.equals(sentence, entry.sentence);
        }

        @Override
        public int hashCode() {
            return Objects.hash(type, sentence);
        }

        @Override
        public String toString() {
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < getCount(); i++) {
                sb.append(this.getType()).append("@@@").append(this.getSentence()).append("\n");
            }
            return sb.toString();
        }
    }
    public long load() {
        return load(Integer.MAX_VALUE);
    }

    public long load(int number) {
        long start = System.currentTimeMillis();
        dataset = new ArrayList<>();

        try (Stream<String> stream = Files.lines(Paths.get(FILENAME))) {
            stream.limit(number).forEach(line -> dataset.add(new Entry(line.split("@@@"))));
        } catch (IOException e) {
            e.printStackTrace();
        }

        return System.currentTimeMillis() - start;
    }
    public String getRandomString(){
        Random rand2 = new Random();
        int index = rand2.nextInt() % dataset.size();
        if(index<0)
            index = - index;
        return dataset.get(index).getSentence();
    }
    public int size(){
        return dataset.size();
    }
    public abstract void match(PrintWriter out, String type, String regex);

}
