package com.archi;

/*
 * Inspiration
 * https://docs.oracle.com/javase/tutorial/displayCode.html?code=https://docs.oracle.com/javase/tutorial/networking/sockets/examples/KKMultiServerThread.java
 */

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.List;

public class MultiServerThread extends Thread {
    private Socket socket = null;

    public MultiServerThread(Socket socket) {
        super("MultiServerThread");
        this.socket = socket;
    }
    public void run() {
        try (
                PrintWriter out =
                        new PrintWriter(socket.getOutputStream(), true);
                BufferedReader in = new BufferedReader(
                        new InputStreamReader(socket.getInputStream()));
        ) {
            String inputLine;
            String[] ss;
            List<String[]> list;
            while((inputLine = in.readLine()) != null){
                ss = inputLine.split(";", 2);
                if(ss.length == 2){
                    list = SimpleServer.matchInDataset(ss[0], ss[1]);
                    list.forEach(line -> out.println(line[0]+"@@@"+line[1]));
                }
                out.println("");
            }

        } catch (IOException e) {
//            e.printStackTrace();
        } finally {
            try {
                socket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }
}
