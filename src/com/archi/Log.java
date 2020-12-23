package com.archi;

import java.io.FileWriter;
import java.io.IOException;
import java.util.Arrays;
import java.util.stream.LongStream;

public class Log {
    public static final String RESET = "\u001B[0m";
    public static final String BLACK = "\u001B[30m";
    public static final String RED = "\u001B[31m";
    public static final String GREEN = "\u001B[32m";
    public static final String YELLOW = "\u001B[33m";
    public static final String BLUE = "\u001B[34m";
    public static final String PURPLE = "\u001B[35m";
    public static final String CYAN = "\u001B[36m";
    public static final String WHITE = "\u001B[37m";

    public static void p(String str){
        System.out.println(str+Log.RESET);
    }
    public static void file(String filename, String str){
        try (FileWriter myWriter = new FileWriter(filename, false)) {
            myWriter.write(str);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
