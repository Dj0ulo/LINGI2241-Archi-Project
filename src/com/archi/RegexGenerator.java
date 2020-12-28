package com.archi;

import java.io.FileWriter;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Utility class to create regex
 */
public class RegexGenerator {

    public static void main(String[] args) {
        System.out.println("Regex generator");
        OptiFileDataset dataset = new OptiFileDataset();
        dataset.load();
        System.out.println("Dataset loaded");

        int regexNumber = 500, // number of regex to be generated
                maxMatchLines = 100000; // maximum number of lines that the regex will match in the db
        for(int i=0;i<regexNumber;i++){
            Log.p(Log.BLUE+"Generating regex n°"+i);
            String regex = generateRegex(dataset, maxMatchLines);
            try (FileWriter myWriter = new FileWriter("regex-list"+maxMatchLines+".txt", true)) {
                myWriter.write(regex+"\n");
            } catch (IOException e) {
                e.printStackTrace();
            }

        }

    }

    /**
     * @param dataset the dataset
     * @param sizeMin minimum number of characters that contains the word
     * @param exceptions words that can not be returned
     * @return a random word that is in the top 10 most frequent ones
     */
    public static String randomMostFrequentWord(Dataset dataset, int sizeMin, List<String> exceptions) {
        List<Map.Entry<String, Integer>> words = dataset.wordFreq()
                .entrySet()
                .stream()
                .sorted((o1, o2) -> o2.getValue() - o1.getValue())
                .filter(e -> {
                    if(e.getKey().length()<sizeMin)
                        return false;
                    for (String exception : exceptions) {
                        if(e.getKey().equals(exception))
                            return false;
                    }
                    return true;
                })
                .limit(10)
                .collect(Collectors.toList());
        if(words.size() == 0)
            return "";
        return words.get((int)(Math.random()*words.size())).getKey();
    }

    /**
     * This function takes a random word in a dataset.  Then  it chooses  another  word  among  the  lines  of
     * the  dataset  that  contains  this  word  and  it  keeps  doing  that  until  the  number  of  lines  is
     * below  a  certain  threshold.   The  final  regex  looks  like  this  :.*firstword.*secondword.*thirdword.*
     * @return a regular expression that matches to max number of lines in a dataset
     */
    public static String generateRegex(OptimizedDataset dataset, int max){

        String word = dataset.randomWord(3);
        List<String> words = new ArrayList<>();
        String regex = ".*";
        int lines = 0;
        while (words.size() < 8){
            regex += word+".*";
            words.add(word);
            System.out.print(word+" ");
            dataset = new OptimizedDataset(dataset.match("", regex));
            dataset.load();

            lines = dataset.size();

            if(lines <= max){
                break;
            }
            word = randomMostFrequentWord(dataset, 2, words);
            if(word.equals(""))
                break;

        }
        Log.p("");
        Log.p(Log.RED+lines+Log.BLACK+"@@@"+Log.GREEN+regex);
        return lines+"@@@"+regex;
    }


}
