package com.archi;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;


public class SimpleServer {
    private final static int TYPE = 0, SENTENCE = 1;
    private static List<String[]> dataset;

    public static void main(String[] args) throws InterruptedException {
        System.out.println("Starting server");

        System.out.println("Dataset read in " + readDataSet() + " ms");
        System.out.println(dataset.size());

        int portNumber = 5678;
        try (ServerSocket serverSocket = new ServerSocket(portNumber)) {
            ArrayList<MultiServerThread> threads = new ArrayList<>();
            while (true) {
                MultiServerThread t = new MultiServerThread(serverSocket.accept());
                threads.add(t);
                t.start();
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static long readDataSet() {
        return readDataSet(Integer.MAX_VALUE);
    }

    public static long readDataSet(int number) {
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

    public static List<String[]> matchInDataset(String type, String regex) {
        List<String[]> result = new ArrayList<>();
        Pattern pattern = Pattern.compile(regex);
        for (String[] line : dataset) {
            if ((type.equals("") || type.equals(line[0]))
                    && pattern.matcher(line[1]).matches()) {
                result.add(line);
            }
        }
        return result;
    }

}
