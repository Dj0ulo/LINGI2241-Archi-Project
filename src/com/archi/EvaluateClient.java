package com.archi;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.io.FileWriter;
import java.io.IOException;
import java.util.concurrent.*;
import java.util.stream.LongStream;


public class EvaluateClient {

    private static Random random;

    private static String serv;
    private static String address;
    private static int port;
    private static OptiFileDataset regexDataset;

    private static int minWords, maxWords, minResults, maxResults;

    /**
     * Class to log a request and how it performed
     */
    public static class RequestParams {
        private final String request;
        private final int nbWords;
        private int nbTypes;
        private int nbLines;
        private final long waitingTime;
        private long responseTime;

        public RequestParams(String request, long waitingTime) {
            this.request = request;
            this.waitingTime = waitingTime;

            String[] r = request.split(";");
            this.nbTypes = r[0].split(",").length;
            if (r[0].equals("") && nbTypes == 1) {
                nbTypes = 6;
            }
            this.nbWords = r[1].split("\\.\\*").length - 2;
        }

        void responded(long responseTime, int nbLines) {
            this.responseTime = responseTime;
            this.nbLines = nbLines;
        }

        public String getRequest() {
            return request;
        }

        @Override
        public String toString() {
            return request + ';' + nbTypes + ';' + nbWords + ';' + waitingTime + ';' + responseTime + ';' + nbLines;
        }
    }

    public static void main(String[] args) {
        // init random class
        random = new Random();

        //params regex
        minWords = 1;
        maxWords = 8;
        minResults = 0;
        maxResults = 10000;

        // load dataset to make random request based on it
        regexDataset = new OptiFileDataset("regex-list" + maxResults + ".txt");
        regexDataset.load();
        System.out.println("Dataset with " + regexDataset.entryNumber() + " regex loaded");

        // server info
        address = "2620:9b::192c:f4e4";//""25.44.244.228";


        serv = "opti";
        if (serv.equals("opti"))
            port = 5678;
        else
            port = 5666;

        // distribution parameters
        double lambda = 1.0 / 1010; // mean time between 2 arrivals is 1/lambda

        int nbRequests = 1;
        int iterations = 200;

        iterateOnNbRequests(nbRequests, lambda, iterations);
    }

    /**
     * iterate from min to max with step step on each iteration, these are used as NbRequests
     */
    public static void iterateOnNbRequests(int nbRequests, double lambda, int iterations) {
        String filename = "tests/" +
                "rate-" + serv + "-l=" + (int) (1 / lambda) + "-maxwords=" + maxWords + "-maxres=" + maxResults +"-nbreq="+nbRequests+".csv";
        try (FileWriter myWriter = new FileWriter(filename, false)) {
            myWriter.write(lambda + "\n");
        } catch (Exception e) {
            e.printStackTrace();
        }

        for (int i = 0; i < iterations; i++) {
            try (FileWriter myWriter = new FileWriter(filename, true)) {
                // Sends NbRequests requests to server at port portNumber and with arrivals following Poisson rule.
                RequestParams[] requestParams = makeNRequests(nbRequests, lambda);

                for (RequestParams rp : requestParams)
                    myWriter.write(rp + "\n");
            } catch (Exception e) {
                e.printStackTrace();
            }
        }


    }


    /**
     * Make nbRequests with a time Poisson distribution betwwen them
     */
    public static RequestParams[] makeNRequests(int nbRequests, double lambda) throws Exception {
        Log.p(Log.BLUE + "* Making " + nbRequests + " requests *");
        double[] waitingTimes = getPoissons(lambda, nbRequests);

        String[] requests = new String[nbRequests];

        RequestParams[] requestParams = new RequestParams[nbRequests];

        // creating the string of a request
        for (int i = 0; i < nbRequests; i++) {
            requests[i] = randomTypes() + ";" + chooseRegex(minWords, maxWords, minResults, maxResults);
            requestParams[i] = new RequestParams(requests[i], (long) waitingTimes[i]);
        }


        ExecutorService executorService = Executors.newFixedThreadPool(nbRequests); // thread pool manager
        CompletionService<Long> completionService = new ExecutorCompletionService<Long>(executorService); // to wait for thread
        for (int i = 0; i < nbRequests; i++) {
            final RequestParams rp = requestParams[i];

            // creating the thread that sends a request
            completionService.submit(() -> ClientRequestManager.makeRequest(address, port, rp.getRequest(), false, rp));
            Log.p("N°" + (i + 1) + " " + Log.GREEN + requests[i] + Log.RED + " Waiting " + (int) waitingTimes[i] + " ms...");

            if (i < nbRequests - 1) {
                for (int k = (int) waitingTimes[i] / 1000; k >= 0; k--) {
                    System.out.print(".");
                    Thread.sleep(1000);
                }
                Thread.sleep(((int) waitingTimes[i]) % 1000);
                System.out.print("\r");
            }
        }
        System.out.println("Waiting responses...");
        // wait for threads
        for (int i = 0; i < nbRequests; i++) {
            Future<Long> future = completionService.take();
            try {
                future.get(); // waiting for the en of a thread
            } catch (ExecutionException e) {
                e.printStackTrace();
            }
        }

        executorService.shutdown();

        return requestParams;
    }

    /**
     * @return an array of arrival timers following poisson rule
     */
    private static double[] getPoissons(double lambda, int n) {
        double[] waitingTimes = new double[n];
        for (int i = 0; i < n; i++) {
            waitingTimes[i] = Math.log(1 - random.nextDouble()) / (-lambda);
        }
        return waitingTimes;
    }

    /**
     * @return between 0 and 4 types (types = 0|1|2|3|4|5)
     */
    private static String randomTypes() {
        StringBuilder types = new StringBuilder();

        int NbTags = random.nextInt(5);
        List<Integer> tags = new ArrayList<Integer>();
        for (int i = 0; i < NbTags; i++) {

            int tag = random.nextInt(6); // choose tag from 0 to 5

            if (tags.contains(tag)) {
                i--;
            } else {
                tags.add(tag);
                if (tags.size() >= NbTags)
                    types.append(tag);
                else
                    types.append(tag).append(",");
            }
        }
        return types.toString();
    }

    /**
     * Choose a regex in the file of regex loaded
     * @param minWords minimum number words in the regex
     * @param maxWords maximum number words in the regex
     * @param minResults minimum number of lines that the regex will match
     * @param maxResults maximum number of lines
     * @return a regex
     * @throws Exception if no regex corresponds to this parameters in the file
     */
    private static String chooseRegex(int minWords, int maxWords, int minResults, int maxResults) throws Exception {
        String tot = "";
        for (int i = 0; i < maxResults - minResults + 1; i++)
            tot += regexDataset.match("" + (minResults + i), "^(\\.\\*\\w+){" + minWords + "," + maxWords + "}\\.\\*$");
        OptimizedDataset tmp = new OptimizedDataset(tot);
        tmp.load();
        if (tmp.size() == 0)
            throw new Exception("Regex with these parameters not found");
        return tmp.random().getSentence();
    }
}
