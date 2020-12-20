package com.archi;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.Random;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Arrays;


public class EvaluateClient extends BaseServer {

    public static long[] Times;
    public static int Finished;


    public static void main(String[] args) throws InterruptedException, IOException {

        dataset = new SimpleDataset();
        loadDataset();

        int portNumber = 5678;
        double lambda = 0.0003333333333333; // mean time between 2 arrivals is 1/lambda
        int min = 5;
        int max = 20;
        int step = 5;

        iterateOnNbRequests(min, max, step, portNumber, lambda);
    }


    public static void iterateOnNbRequests(int min, int max, int step, int portNumber, double lambda) throws InterruptedException, IOException { //iterate from min to max with step step on each iteration, these are used as NbRequests
        FileWriter myWriter = new FileWriter("MeanTimes.txt", false);
        myWriter.write(lambda+"\r\n");
        myWriter.close();
        for(int i = min; i <= max; i+=step){
            iteration(portNumber, i, lambda);
        }
    }

    public static void iteration(int portNumber, int NbRequests, double lambda) throws InterruptedException{ // Sends NbRequests requests to server at port portNumber and with arrivals following Poisson rule with lambda of lambda. Writes mean times to MeanTimes.txt

        MakeNRequests(portNumber, NbRequests,lambda);
        int sum = 0;
        for(int i = 0; i< Times.length; i++)
            sum +=Times[i];
        try {
            FileWriter myWriter = new FileWriter("MeanTimes.txt", true);
            myWriter.write(NbRequests+";"+sum/NbRequests+"\r\n");
            myWriter.close();
        } catch (IOException e) {
            System.out.println("error while writting to MeanTimes.txt");
            e.printStackTrace();
        }
    }

    public static void modifyTimes(long time, int index){
        Times[index] = time;
        Finished++;
    }
    public static  void MakeNRequests(int portNumber, int NbRequests, double lambda) throws InterruptedException{ // calculate the arrival times and  launches the thread following these timers

        double[] waitingTimes = getPoissons(lambda, NbRequests);

        String[] requests = new String[NbRequests];

        for(int i = 0; i < NbRequests; i++){
            requests[i] = RandomString();
        }
        Times = new long[NbRequests];
        Finished = 0;


        for(int i = 0; i < NbRequests; i++){
            MultiClientThread t = new MultiClientThread(requests[i], portNumber, i);
            t.start();

            Thread.sleep((int)waitingTimes[i]);
        }

        while(Finished < NbRequests)
            Thread.sleep(20);

        int i;
        System.out.print("Finished in : [");
        for(i = 0; i < Times.length-1; i++)
            System.out.print(Times[i]+", ");
        System.out.println(Times[i] + "] ms");

    }



    private static double[] getPoissons(double lambda, int nbRequest) { //returns a tab of the arrival timers following poisson rule
        double[] waitingTimes = new double[nbRequest];
        for(int i = 0; i < nbRequest; i++){
            waitingTimes[i] = getNext(lambda);
        }
        return waitingTimes;

    }

    public static double getNext(double lambda) { //Generates a waiting time (distribution is exponential with mean (1/lambda))
        Random rand = new Random();
        return  Math.log(1-rand.nextDouble())/(-lambda);
    }



    private static String RandomString(){ // returns a string respecting the format of requests specified in the project description. number of tags, tags, number of line and word in the line each follow a uniform distribution

        String userLine = "";


        int NbTags = RandomFrom0toN(4);
        List<Integer> tags = new ArrayList<Integer>();
        for (int i = 0; i<NbTags; i++) {

            int tag = RandomFrom0toN(5);

            if(tags.contains(tag)){
                i--;
            }
            else{
                tags.add(tag);
                if(tags.size()>= NbTags)
                    userLine= userLine+tag;
                else
                    userLine = userLine + tag + ",";
            }
        }

        String randomString = dataset.getRandomString();
        String purString = "";

        for(int i = 0; i<randomString.length();i++){
            if (randomString.charAt(i) =='.')
                purString = purString+"\\.";
            else
                purString = purString + randomString.charAt(i);
        }

        String[] words = purString.split(" ");
        purString = words[RandomFrom0toN(words.length-1)];


        userLine = userLine + ";.*"+ purString+".*";

        return userLine;
    }


    public static int RandomFrom0toN(int n){ // return a random int from 0 to n with uniform distribution
        Random random = new Random();
        int Nb = random.nextInt() %(n+1);
        if(Nb<0)
            Nb = - Nb;
        return Nb;
    }
}


