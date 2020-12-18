package com.archi;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.Random;

public class EvaluateClient extends BaseServer {

    public static void main(String[] args) throws InterruptedException {
        System.out.println("Starting client");

        int portNumber = 5678;
        Scanner stdIn = new Scanner(System.in);

        dataset = new SimpleDataset();
        loadDataset();

        connection(portNumber);
    }

    private static void connection(int portNumber){
        try (
                Socket socket = new Socket("localhost", portNumber);
                PrintWriter toServer = new PrintWriter(socket.getOutputStream(), true);
                BufferedReader fromServer = new BufferedReader(
                        new InputStreamReader(socket.getInputStream()))
        ) {
            System.out.println("Connected to the server");

            String userLine = RandomString();

            System.out.println("Send > "+userLine+ ";.*Transport.*");


            if(userLine.equals("quit"))
                return;

            System.out.println("* Sending request *");

            toServer.println(userLine); // sending
            long start = System.currentTimeMillis(); // start time

            String serverLine;
            List <String> results = new ArrayList<>();
            int n = 0;
            while ((serverLine = fromServer.readLine()) != null) {
                if(serverLine.equals(""))
                    break;
                else {
                    results.add(serverLine);
                    n++;
                }
            }
            long duration = System.currentTimeMillis() - start;
            System.out.println("* "+n+" result(s) in "+duration+" ms *");
            int limit = 30;
            results.stream().limit(limit).forEach(System.out::println);

            if(results.size() > limit)
                System.out.println("...");
            System.out.println();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static String RandomString(){

        String userLine = "";

        Random random = new Random();
        int NbTags = random.nextInt() %5;
        if(NbTags<0)
            NbTags = - NbTags;

        List<Integer> tags = new ArrayList<Integer>();

        NbTags = 1;

        for (int i = 0; i<NbTags; i++) {
            Random rand2 = new Random();
            int tag = rand2.nextInt() % 6;
            if(tag<0)
                tag = - tag;

            if(tags.contains(tag)){
                i--;
            }
            else{
                tags.add(tag);
                if(tags.size()>= NbTags)
                    userLine= userLine+tag;
                else
                    userLine = userLine + tag + ",";
            }
        }

        System.out.println(dataset.getRandomString());





        userLine = userLine+ ";.*Transport.*";

        return userLine;
    }
}


