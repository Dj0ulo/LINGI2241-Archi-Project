package com.archi;

import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;
import java.util.stream.Stream;

public class SimpleDataset extends Dataset{
    private final String filename;

    public SimpleDataset(){
        this.filename = "dbdata.txt";
    }

    @Override
    public long load(int number) {
        long start = System.currentTimeMillis();

        try (Stream<String> stream = Files.lines(Paths.get(filename))) {
            stream.limit(number).forEach(line -> dataset.add(new Dataset.Entry(line.split("@@@"))));
        } catch (IOException e) {
            e.printStackTrace();
        }

        return System.currentTimeMillis() - start;
    }

    @Override
    public String match(String type, String regex) {
        StringBuilder result = new StringBuilder();
        Pattern pattern = compileRegex(regex);
        if(pattern != null){
            int intType = type.equals("") ? -1 : Integer.parseInt(type);

            for (Dataset.Entry line : this.dataset) {
                if ((intType == -1 || intType == line.getType())
                        && pattern.matcher(line.getSentence()).matches()) {
                    result.append(line).append("\n");
                }
            }
        }
        return result.toString();
    }
    @Override
    public long match(PrintWriter out, String type, String regex){
        long start = System.currentTimeMillis();
        out.write(match(type, regex));
        return System.currentTimeMillis() - start;
    }
}
