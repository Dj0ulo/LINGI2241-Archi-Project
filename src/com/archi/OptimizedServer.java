package com.archi;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;

public class OptimizedServer extends BaseServer{
    public static void main(String[] args)  {
        init(true);
        /*
         * Thread pool tutorial
         * https://www.baeldung.com/thread-pool-java-and-guava
         */
        int threadNumber = 4;
        ThreadPoolExecutor executor = (ThreadPoolExecutor) Executors.newFixedThreadPool(threadNumber);

        int portNumber = 5678;
        try (ServerSocket serverSocket = new ServerSocket(portNumber)){
            Socket client;
            while ((client = serverSocket.accept()) != null) {
                final Socket currentClient = client;
                executor.submit(() -> ServerRequestManager.respond(currentClient, dataset, true));
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            executor.shutdown();
        }
    }
}
