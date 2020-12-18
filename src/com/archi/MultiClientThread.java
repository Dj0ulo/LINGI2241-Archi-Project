package com.archi;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class MultiClientThread extends Thread {
    private String request = "";
    private int portNumber = 0;
    private int nbThread = 0;
    public MultiClientThread(String request, int portNumber, int nbThread) {
        super("MultiClientThread");
        this.request = request;
        this.portNumber = portNumber;
        this.nbThread = nbThread;
    }

    public void run() {
        System.out.println("Thread nb "+nbThread+" launched");
        try (
                Socket socket = new Socket("localhost", portNumber);
                PrintWriter toServer = new PrintWriter(socket.getOutputStream(), true);
                BufferedReader fromServer = new BufferedReader(
                        new InputStreamReader(socket.getInputStream()))
        ) {

            if(request.equals("quit"))
                return;

            toServer.println(request); // sending
            long start = System.currentTimeMillis(); // start time

            String serverLine;
            List<String> results = new ArrayList<>();
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

            EvaluateClient.modifyTimes(duration, nbThread);

            System.out.println("* thread "+nbThread+" : "+n+" result(s) for "+ request +" in "+duration+" ms *");
            /*int limit = 30;
            results.stream().limit(limit).forEach(System.out::println);

            if(results.size() > limit)
                System.out.println("...");*/
            System.out.println();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
