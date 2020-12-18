package com.archi;

import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.regex.Pattern;
import java.util.stream.Stream;

public class OptimizedDataset extends Dataset {
    private int[] indexTypes;
    private Cache cache = new Cache(50);

    @Override
    public long load(int number){
        long readTime = super.load(number);
        long start = System.currentTimeMillis();

        this.dataset.sort(Dataset.cmp);
        int maxType = Integer.parseInt(this.dataset.get(this.dataset.size()-1)[TYPE]);
        indexTypes = new int[maxType + 1];
        indexTypes[0] = 0;
        String prev = "0";
        for (int i = 0; i < this.dataset.size(); i++) {
            if(!this.dataset.get(i)[TYPE].equals(prev)){
                int cur = Integer.parseInt(this.dataset.get(i)[TYPE]);
                indexTypes[cur] = i;
                if(cur == maxType)
                    break;
                prev = this.dataset.get(i)[TYPE];

            }
        }

        return readTime + System.currentTimeMillis() -  start;
    }

    @Override
    public void match(PrintWriter out, String type, String regex) {
        Pattern pattern = Pattern.compile(regex);
        if(regex.equals(""))
            return;
        int intType = type.equals("") ? -1 : Integer.parseInt(type);
        int [] bounds = indexType(intType);
        List<Integer> indexes = new ArrayList<>();
        StringBuilder result = new StringBuilder();

        Integer[] cacheLines = cache.get(type, regex);
        if(cacheLines == null){
            for (int i = bounds[0]; i < bounds[1]; i++) {
                String[] line = this.dataset.get(i);
                if (pattern.matcher(line[1]).matches()) {
                    indexes.add(i);
                    result.append(line[0]).append("@@@").append(line[1]).append("\n");
                }
            }
            cache.add(type, regex, indexes.toArray(new Integer[0]));
        }else{
            for (Integer cacheLine : cacheLines) {
                String[] line = this.dataset.get(cacheLine);
                result.append(line[0]).append("@@@").append(line[1]).append("\n");
            }
        }
        out.write(result.toString());
    }

    public void freq(){
        int[] table = new int[256];
        this.dataset.forEach(line -> {
            for (int i = 0; i < line[1].length(); i++) {
                char c = line[1].charAt(i);
                if(c < 256)
                    table[c] ++;
            }
        });
        for (int i = 0; i < table.length; i++) {
            System.out.println(Character.toString(i)+" : "+table[i]);
        }
    }

    private Stream<String[]> filterType(int type) {
        int [] index = indexType(type);
        return this.dataset.subList(index[0], index[1]).stream();
    }

    private int[] indexType(int type){
        if (type == -1)
            return new int[]{0, this.dataset.size()};
        else if (0 <= type && type < this.indexTypes.length-1)
            return new int[]{this.indexTypes[type], this.indexTypes[type+1]};
        else if(type == indexTypes.length-1)
            return new int[]{this.indexTypes[type], this.dataset.size()};
        else
            return new int[]{0,0};
    }


}
