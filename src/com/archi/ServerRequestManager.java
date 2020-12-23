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

public class ServerRequestManager {
    public static void respond(Socket socket, Dataset dataset) {
        respond(socket, dataset, false);
    }

    public static void respond(Socket socket, Dataset dataset, boolean print) {
        try (
                PrintWriter out =
                        new PrintWriter(socket.getOutputStream(), true);
                BufferedReader in = new BufferedReader(
                        new InputStreamReader(socket.getInputStream()));
        ) {
            String request = in.readLine();
            if (print)
                Log.p(Log.BLUE + socket.getInetAddress().toString().substring(1) + Log.RESET + " requests : " + Log.GREEN + request);
            String[] ss = request.split(";", 2);
            long duration = 0;
            if (ss.length <= 2) {
                String[] types = (ss.length == 1) ? new String[]{""} : ss[0].split(",");
                String regex = (ss.length == 1) ? ss[0] : ss[1];

                for (String type : types) {
                    duration += dataset.match(out, type, regex);
                }
            }
            out.println("");
            if (print)
                Log.p(Log.GREEN + request + Log.RESET + " responded in " + Log.RED + duration + " ms");
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
