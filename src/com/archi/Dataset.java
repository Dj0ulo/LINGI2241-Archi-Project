package com.archi;

import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.stream.Stream;

public abstract class Dataset {
    public final static int TYPE = 0, SENTENCE = 1;
    protected List<String[]> dataset;
    public long read() {
        return read(Integer.MAX_VALUE);
    }

    public long read(int number) {
        long start = System.currentTimeMillis();
        String fileName = "dbdata.txt";
        dataset = new ArrayList<>();

        try (Stream<String> stream = Files.lines(Paths.get(fileName))) {
            stream.limit(number).forEach(line -> dataset.add(line.split("@@@")));
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
        return dataset.get(index)[1];
    }
    public int size(){
        return dataset.size();
    }
    public abstract void match(PrintWriter out, String type, String regex);
}
