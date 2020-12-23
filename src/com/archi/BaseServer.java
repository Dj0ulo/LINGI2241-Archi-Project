package com.archi;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

public abstract class BaseServer {
    protected static Dataset dataset;

    protected static void init(boolean opti) {
        if (opti)
            System.out.println("Starting *optimized* server");
        else
            System.out.println("Starting *simple* server");

        dataset = opti ? new OptiFileDataset() : new SimpleDataset();

        final long readTime = dataset.load();
        System.out.println("Dataset of " + dataset.size() + " lines read in " + readTime + " ms");
    }
}
