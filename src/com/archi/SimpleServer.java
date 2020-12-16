package com.archi;

public class SimpleServer extends BaseServer {
    public static void main(String[] args)  {
        System.out.println("Starting simple server");
        dataset = new SimpleDataset();
        loadDataset();
        listen(5678);
    }
}
