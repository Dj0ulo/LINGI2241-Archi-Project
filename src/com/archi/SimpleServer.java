package com.archi;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.concurrent.TimeUnit;

public class SimpleServer {
    final static int TYPE = 0, SENTENCE = 1;
    static List<String[]> dataset;

    public static void main(String[] args) throws InterruptedException {
        System.out.println("Starting server");

        System.out.println("Dataset read in " + readDataSet(1000) + " ms");
        System.out.println(dataset.get(0).length);
    }

    public static long readDataSet() {
        return readDataSet(-1);
    }

    public static long readDataSet(int number) {
        long start = System.currentTimeMillis();
        dataset = new ArrayList<String[]>();
        try {
            File file = new File("dbdata.txt");
            Scanner myReader = new Scanner(file);
            for (int i = 0; myReader.hasNextLine() && i != number; i++) {
                String[] data = myReader.nextLine().split("@@@");
                dataset.add(data);
            }
            myReader.close();
        } catch (FileNotFoundException e) {
            System.out.println("An error occurred.");
            e.printStackTrace();
        }
        return System.currentTimeMillis() - start;
    }

}
