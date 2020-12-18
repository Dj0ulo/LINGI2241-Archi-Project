package com.archi;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class Client {

    public static void main(String[] args) throws InterruptedException {
        System.out.println("Starting client");

        int portNumber = 5678;
        Scanner stdIn = new Scanner(System.in);

        try (
                Socket socket = new Socket("localhost", portNumber);
                PrintWriter toServer = new PrintWriter(socket.getOutputStream(), true);
                BufferedReader fromServer = new BufferedReader(
                        new InputStreamReader(socket.getInputStream()));
        ) {
            System.out.println("Connected to the server");

            while(true){
                System.out.print("Send > ");
                String userLine = stdIn.nextLine();// read line from user
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
//                        if(n%1000 == 0)
//                            System.out.println(n);
                    }
                }
                long duration = System.currentTimeMillis() - start;
                System.out.println("* "+n+" result(s) in "+duration+" ms *");
                int limit = 5;
                results.stream().limit(limit).forEach(System.out::println);
                if(results.size() > limit)
                    System.out.println("...");
                System.out.println();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
