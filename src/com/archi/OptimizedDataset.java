package com.archi;

import java.io.PrintWriter;
import java.util.regex.Pattern;
import java.util.stream.Stream;

public class OptimizedDataset extends Dataset {

    @Override
    public void match(PrintWriter out, String type, String regex) {
        Pattern pattern = Pattern.compile(regex);
        filterType(type).forEach(line -> {
            if (pattern.matcher(line[1]).matches())
                out.println(line[0] + "@@@" + line[1]);
        });
    }

    public Stream<String[]> filterType(String type) {
        if (type.equals(""))
            return this.dataset.stream();
        else
            return this.dataset.stream().filter(strings -> strings[TYPE].equals(type));
    }
}
