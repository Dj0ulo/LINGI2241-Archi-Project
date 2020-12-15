package com.archi;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;


public class SimpleServer {
    final static int TYPE = 0, SENTENCE = 1;
    static List<String[]> dataset;

    public static void main(String[] args) throws InterruptedException {
        System.out.println("Starting server");

        System.out.println("Dataset read in " + readDataSet() + " ms");
        System.out.println(dataset.size());

        int portNumber = 5678;
        try (
            ServerSocket serverSocket = new ServerSocket(portNumber);
            Socket clientSocket = serverSocket.accept();
            PrintWriter out =
                    new PrintWriter(clientSocket.getOutputStream(), true);
            BufferedReader in = new BufferedReader(
                    new InputStreamReader(clientSocket.getInputStream()));
        ) {

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static long readDataSet() {
        return readDataSet(-1);
    }

    public static long readDataSet(int number) {
        long start = System.currentTimeMillis();
        String fileName = "dbdata.txt";
        dataset = new ArrayList<>();

        try (Stream<String> stream = Files.lines(Paths.get(fileName))) {
            stream.forEach(line -> dataset.add(line.split("@@@")));
        } catch (IOException e) {
            e.printStackTrace();
        }

        return System.currentTimeMillis() - start;
    }

}
