package com.archi;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * Simple server single threaded
 */
public class SimpleServer extends BaseServer {
    public static void main(String[] args)  {
        init(false);

        int portNumber = 5666;
        try (ServerSocket serverSocket = new ServerSocket(portNumber)) {
            Socket client;
            while ((client = serverSocket.accept()) != null) {
                ServerRequestManager.respond(client, dataset, true);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
