package com.archi;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

/**
 * Class to manually test a request on the server
 */
public class Client {

    public static void main(String[] args) {
        System.out.println("Starting manual client");

        String address = "2620:9b::193f:5de1";//ip address
        int portNumber = 5678;
        Scanner stdIn = new Scanner(System.in);
        System.out.println("Connection to the server");

        while (true) {
            System.out.print("Send > ");
            String userLine = stdIn.nextLine();// read line from user
            if (userLine.equals("quit"))
                break;

            ClientRequestManager.makeRequest(address, portNumber, userLine, true);
        }
    }
}
