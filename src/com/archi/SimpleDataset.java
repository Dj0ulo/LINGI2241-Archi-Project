package com.archi;

import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

public class SimpleDataset extends Dataset{

    public List<Entry> match(String type, String regex) {
        List<Entry> result = new ArrayList<>();
        Pattern pattern = Pattern.compile(regex);
        int intType = type.equals("") ? -1 : Integer.parseInt(type);

        for (Entry line : this.dataset) {
            if ((intType == -1 || intType == line.getType())
                    && pattern.matcher(line.getSentence()).matches()) {
                result.add(line);
            }
        }
        return result;
    }
    @Override
    public long match(PrintWriter out, String type, String regex){
        long start = System.currentTimeMillis();
        List<Entry> list = this.match(type, regex);
        list.forEach(line -> out.println(line.getType()+"@@@"+line.getSentence()));
        return System.currentTimeMillis() - start;
    }
}
