package com.archi;

import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

public class SimpleDataset extends Dataset{

    public List<String[]> match(String type, String regex) {
        List<String[]> result = new ArrayList<>();
        Pattern pattern = Pattern.compile(regex);
        for (String[] line : this.dataset) {
            if ((type.equals("") || type.equals(line[0]))
                    && pattern.matcher(line[1]).matches()) {
                result.add(line);
            }
        }
        return result;
    }
    @Override
    public void match(PrintWriter out, String type, String regex){
        List<String[]> list = this.match(type, regex);
        list.forEach(line -> out.println(line[0]+"@@@"+line[1]));
    }
}
