package com.archi;

import javax.xml.crypto.Data;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.stream.Stream;

public class OptiFileDataset extends OptimizedDataset{
    public OptiFileDataset() {
        super(fileToStream("dbdata.txt"));
    }
    public OptiFileDataset(String filename) {
        super(fileToStream(filename));
    }
    private static Stream <String> fileToStream(String filename){
        try {
            return Files.lines(Paths.get(filename));
        } catch (IOException e) {
            e.printStackTrace();
        }
        return Arrays.stream(new String[0]);
    }
}
