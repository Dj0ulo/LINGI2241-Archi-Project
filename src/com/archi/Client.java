package com.archi;

import java.util.concurrent.TimeUnit;

public class Client {

    public static void main(String[] args) throws InterruptedException {
        for (int i = 0; i < 50; i++) {
            System.out.println("Starting client");
            TimeUnit.SECONDS.sleep(1);
        }

    }
}
