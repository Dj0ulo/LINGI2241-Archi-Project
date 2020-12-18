package com.archi;

/*
 * Inspiration
 * https://docs.oracle.com/javase/tutorial/displayCode.html?code=https://docs.oracle.com/javase/tutorial/networking/sockets/examples/KKMultiServerThread.java
 */

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class MultiServerThread extends Thread {
    private Socket socket = null;
    private final Dataset dataset;

    public MultiServerThread(Dataset dataset, Socket socket) {
        super("MultiServerThread");
        this.socket = socket;
        this.dataset = dataset;
    }
    public void run() {
        try (
                PrintWriter out =
                        new PrintWriter(socket.getOutputStream(), true);
                BufferedReader in = new BufferedReader(
                        new InputStreamReader(socket.getInputStream()));
        ) {
            String inputLine;
            String[] ss, types;
            while((inputLine = in.readLine()) != null){
                ss = inputLine.split(";", 2);
                if(ss.length == 2){
                    types = ss[0].split(",");
                    for (String type : types) {
                        this.dataset.match(out, type, ss[1]);
                    }
                }
                out.println("");
            }

        } catch (IOException e) {
        } finally {
            try {
                socket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }
}
