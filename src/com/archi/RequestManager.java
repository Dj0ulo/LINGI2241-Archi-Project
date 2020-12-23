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

public class RequestManager {

    public static void respond(Socket socket, Dataset dataset) {
        try (
                PrintWriter out =
                        new PrintWriter(socket.getOutputStream(), true);
                BufferedReader in = new BufferedReader(
                        new InputStreamReader(socket.getInputStream()));
        ) {
            String request = in.readLine();
            String[] ss = request.split(";", 2);
            if (ss.length <= 2) {
                String[] types = (ss.length == 1) ? new String[]{""} : ss[0].split(",");
                String regex = (ss.length == 1) ? ss[0] : ss[1];
                for (String type : types) {
                    dataset.match(out, type, regex);
                }
            }
            out.println("");
        } catch (IOException ignored) {
        } finally {
            try {
                socket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
