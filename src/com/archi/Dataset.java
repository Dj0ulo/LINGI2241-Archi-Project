package com.archi;

import java.io.PrintWriter;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

/**
 * Abstract class having a lot of utility functions
 */
public abstract class Dataset {

    protected List<Dataset.Entry> dataset = new ArrayList<>();

    /**
     * Class describing an entry in the dataset, each entry has a type, a sentence and a count that reprensents
     * the number of times it appears in the database. Count is always = 1 by default
     */
    protected static class Entry implements Comparable<Entry> {
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

    /**
     * Load all the lines in the database
     * @return the duration of the loading
     */
    public long load() {
        return load(Integer.MAX_VALUE);
    }

    /**
     * @param number number of lines in the database to be loaded
     * @return the duration of the loading
     */
    public abstract long load(int number);

    /**
     * @return a random sentence in the dataset
     */
    public String getRandomString() {
        Random rand2 = new Random();
        int index = rand2.nextInt() % dataset.size();
        if (index < 0)
            index = -index;
        return dataset.get(index).getSentence();
    }

    /**
     * @return the number of different entries
     */
    public int size() {
        return dataset.size();
    }

    /**
     * @return a Java Pattern based on a regex
     */
    protected Pattern compileRegex(String regex) {
        try {
            return Pattern.compile(regex);
        } catch (PatternSyntaxException e) {
            Log.p(Log.RED + e.getMessage());
        }
        return null;
    }

    /**
     * Print in the stream out the lines of the database that matches the type and the regex
     * @return the time it took
     */
    public abstract long match(PrintWriter out, String type, String regex);

    /**
     * @return the lines of the database that matches the type and the regex
     */
    public abstract String match(String type, String regex);

    /**
     * Static function that returns an array of words in a sentence
     */
    private static String[] words(String sentence) {
        return words(sentence, 1);
    }
    private static String[] words(String sentence, int sizeMin) {
        Pattern p = Pattern.compile("[a-zA-Z]{"+sizeMin+",}");

        List<String> list = new ArrayList<>();
        Matcher matcher = p.matcher(sentence);
        while (matcher.find()) {
            list.add(matcher.group());
        }
        return list.toArray(new String[0]);
    }

    /**
     * @return random entry
     */
    public Entry random() {
        return dataset.get((int) (Math.random() * dataset.size()));
    }

    public String randomWord(int sizeMin) {
        String[] words;
        do {
            words = words(random().getSentence(), sizeMin);
        } while (words.length == 0);
        return words[(int) (Math.random() * words.length)];
    }

    public int randomType() {
        return random().getType();
    }

    /**
     * @return an Hashmap with key corresponding to the words in the database and their count
     */
    public HashMap<String, Integer> wordFreq() {
        HashMap<String, Integer> f = new HashMap<>();
        this.dataset.forEach(entry -> {
            String[] words = words(entry.getSentence());
            for (String word : words) {
                if (f.containsKey(word)) {
                    f.put(word, f.get(word) + entry.getCount());
                } else {
                    f.put(word, entry.getCount());
                }
            }
        });
        return f;
    }
}
