package com.archi;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
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
                InputStream inStream = socket.getInputStream();
                BufferedReader fromServer = new BufferedReader(new InputStreamReader(inStream));
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

//            byte[] buffer = inStream.readAllBytes();

            duration = System.currentTimeMillis() - start;

//            results.addAll(Arrays.asList(new String(buffer, StandardCharsets.UTF_8).split("\n")));



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
