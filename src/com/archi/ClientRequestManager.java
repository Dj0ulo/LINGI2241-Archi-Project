package com.archi;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class ClientRequestManager extends Thread {

    public static long makeRequest(String address, int port, String request){
        return makeRequest(address, port, request, false);
    }
    public static long makeRequest(String address, int port, String request, boolean print){
        List<String> results = new ArrayList<>();
        long duration = -1;
        try (
                Socket socket = new Socket(address, port);
                PrintWriter toServer = new PrintWriter(socket.getOutputStream(), true);
                BufferedReader fromServer = new BufferedReader(
                        new InputStreamReader(socket.getInputStream()));
        ) {
            if(print)
                System.out.println("* Connected - Sending request *");

            long start = System.currentTimeMillis(); // start time
            toServer.println(request); // sending

            String serverLine;
            while ((serverLine = fromServer.readLine()) != null) {
                if (serverLine.equals(""))
                    break;
                else {
                    results.add(serverLine);
                }
            }

            duration = System.currentTimeMillis() - start;
            if(print){
                System.out.println("* " + results.size() + " result(s) in " + duration + " ms *");
                int limit = 5;
                results.stream().limit(5).forEach(System.out::println);
                if (results.size() > limit)
                    System.out.println("...");
                System.out.println();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return duration;
    }
}
