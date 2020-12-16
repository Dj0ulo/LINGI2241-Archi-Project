package com.archi;

import java.io.IOException;
import java.net.ServerSocket;
import java.util.ArrayList;

public abstract class BaseServer {
    protected static Dataset dataset;

    protected static void loadDataset(){
        long readTime = dataset.read();
        System.out.println("Dataset of "+dataset.size()+" lines read in " + readTime + " ms");
    }
    protected static void listen(int portNumber){
        try (ServerSocket serverSocket = new ServerSocket(portNumber)) {
            ArrayList<MultiServerThread> threads = new ArrayList<>();
            while (true) {
                MultiServerThread t = new MultiServerThread(dataset, serverSocket.accept());
                threads.add(t);
                t.start();
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
