package com.archi;

public class OptimizedServer extends BaseServer{
    public static void main(String[] args)  {
        System.out.println("Starting optimized server");
        dataset = new OptimizedDataset();
        loadDataset();
        listen(5678);
    }

}
