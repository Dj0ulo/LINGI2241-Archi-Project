package com.archi;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.Random;


public class EvaluateClient extends BaseServer {

    public static long[] Times;
    public static int Finished = 0;

    public static void modifyTimes(long time, int index){
        Times[index] = time;
        Finished++;
    }

    public static void main(String[] args) throws InterruptedException {
        System.out.println("Starting client");

        int portNumber = 5678;
        int NbRequests = 5;       //arguments to modify
        int mean = 3000;



        Scanner stdIn = new Scanner(System.in);

        dataset = new SimpleDataset();
        loadDataset();


        int[] waitingTimes = getPoissons(mean, NbRequests);



        String[] requests = new String[NbRequests];

        for(int i = 0; i < NbRequests; i++){
            requests[i] = RandomString();
        }

        ArrayList<MultiClientThread> threads = new ArrayList<>();

        Times = new long[NbRequests];


        for(int i = 0; i < NbRequests; i++){
            MultiClientThread t = new MultiClientThread(requests[i], portNumber, i);
            threads.add(t);
            t.start();

            Thread.sleep(waitingTimes[i]);
        }

        while(Finished < NbRequests)
            Thread.sleep(20);

        int i;
        System.out.print("Finished in : [");
        for(i = 0; i < Times.length-1; i++)
            System.out.print(Times[i]+", ");
        System.out.println(Times[i] + "] ms");


    }

    private static int[] getPoissons(int mean, int nbRequest) {
        int[] waitingTimes = new int[nbRequest];
        for(int i = 0; i < nbRequest; i++){
            waitingTimes[i] = getPoissonRandom(mean);
        }
        return waitingTimes;

    }
    private static int getPoissonRandom(double mean) {
        Random r = new Random();
        double L = Math.exp(-mean);
        int k = 0;
        double p = 1.0;
        do {
            p = p * r.nextDouble();
            k++;
        } while (p > L);
        return k - 1;
    }




    private static String RandomString(){

        String userLine = "";

        Random random = new Random();
        int NbTags = random.nextInt() %5;
        if(NbTags<0)
            NbTags = - NbTags;

        List<Integer> tags = new ArrayList<Integer>();

        for (int i = 0; i<NbTags; i++) {
            Random rand2 = new Random();
            int tag = rand2.nextInt() % 6;
            if(tag<0)
                tag = - tag;

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


        userLine = userLine + ";.*"+ purString+".*";

        return userLine;
    }
}


