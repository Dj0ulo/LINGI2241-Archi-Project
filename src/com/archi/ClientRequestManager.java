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

/**
 * Static class with only one overloaded function that create a TCP connection and send one request
 */
public class ClientRequestManager {
    public static long makeRequest(String address, int port, String request){
        return makeRequest(address, port, request, false);
    }
    public static long makeRequest(String address, int port, String request, boolean print) {
        return makeRequest(address, port, request, print, null);
    }
    public static long makeRequest(String address, int port, String request, boolean print, EvaluateClient.RequestParams requestParams){
        List<String> results = new ArrayList<>();
        long duration = -1;
        try (
                Socket socket = new Socket(address, port); // creating tcp connection
                PrintWriter toServer = new PrintWriter(socket.getOutputStream(), true);
                InputStream inStream = socket.getInputStream();
                BufferedReader fromServer = new BufferedReader(new InputStreamReader(inStream));
        ) {
            if(print)
                System.out.println("* Connected - Sending request *");

            long start = System.currentTimeMillis(); // start time
            toServer.println(request); // sending

            // read received lines
            String serverLine;
            while ((serverLine = fromServer.readLine()) != null) {
                if (serverLine.equals(""))
                    break;
                else {
                    results.add(serverLine);
                }
            }

            // duration since the sending of the request = response time
            duration = System.currentTimeMillis() - start;

            if(requestParams != null){
                requestParams.responded(duration, results.size());
            }

            // print or not for manual debugging
            if(print){
                System.out.println("* " + results.size() + " result(s) in " + duration + " ms *");
                int limit = 5;
                results.stream().limit(limit).forEach(System.out::println);
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
