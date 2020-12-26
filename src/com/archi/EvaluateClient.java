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

    private static String address;
    private static int port;
    private static OptiFileDataset regexDataset;

    private static int minWords, maxWords, minResults, maxResults;

    public static class RequestParams{
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
            if(r[0].equals("") && nbTypes == 1){
                nbTypes = 6;
            }
            this.nbWords = r[1].split("\\.\\*").length - 2;
        }
        void responded(long responseTime, int nbLines){
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

        // load dataset to make random request based on it
        regexDataset = new OptiFileDataset("regex-list50.txt");
        regexDataset.load(280);
        System.out.println("Dataset with " + regexDataset.entryNumber() + " regex loaded");

        // server info
        address = "2620:9b::192c:f4e4";//2620:9b::193f:5de1";//""25.44.244.228";
        port = 5678;

        // distribution parameters
        double lambda = 1.0 / 500; // mean time between 2 arrivals is 1/lambda

        //params regex
        minWords = 1;
        maxWords = 5;
        minResults = 1;
        maxResults = 50;

        int min = 1;
        int max = 1;
        int step = 4;
        int iterPerNbIter = 50;

        iterateOnNbRequests(min, max, step, lambda, iterPerNbIter);
    }

    /**
     * iterate from min to max with step step on each iteration, these are used as NbRequests
     */
    public static void iterateOnNbRequests(int min, int max, int step, double lambda, int iterPerNbIter) {
        String filename = "lambda.txt";
        try (FileWriter myWriter = new FileWriter(filename, false)) {
            myWriter.write(lambda + "\n");
        } catch (Exception e) {
            e.printStackTrace();
        }

        for (int i = min; i <= max; i += step) {// i = number of requests for that iteration
            for (int j = 0; j < iterPerNbIter; j++) {
                try (FileWriter myWriter = new FileWriter(filename, true)) {
                    // Sends NbRequests requests to server at port portNumber and with arrivals following Poisson rule with lambda of lambda.
                    // Writes mean times to MeanTimes.txt
                    RequestParams [] requestParams = makeNRequests(i, lambda);

                    // print durations
//                    Log.p(Log.PURPLE + "Durations" + Log.RESET + " = " + Arrays.toString(durations) + " ms");

                    for (RequestParams rp : requestParams)
                        myWriter.write(rp+"\n");
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }


    }


    /**
     * calculate the arrival times and  launches the thread following these timers
     */
    public static RequestParams [] makeNRequests(int nbRequests, double lambda) throws Exception {
        Log.p(Log.BLUE + "* Making " + nbRequests + " requests *");
        double[] waitingTimes = getPoissons(lambda, nbRequests);

        String[] requests = new String[nbRequests];

        RequestParams [] requestParams = new RequestParams[nbRequests];

        for (int i = 0; i < nbRequests; i++) {
            requests[i] = randomTypes() +";"+ chooseRegex(minWords, maxWords, minResults, maxResults);
            requestParams[i] = new RequestParams(requests[i], (long)waitingTimes[i]);
        }


        ExecutorService executorService = Executors.newFixedThreadPool(nbRequests); // thread pool manager
        CompletionService<Long> completionService = new ExecutorCompletionService<Long>(executorService); // to wait for thread
        for (int i = 0; i < nbRequests; i++) {
            final RequestParams rp = requestParams[i];

            completionService.submit(() -> ClientRequestManager.makeRequest(address, port, rp.getRequest(), false, rp));
            Log.p("N°" + (i + 1) + " " + Log.GREEN + requests[i] + Log.RED + " Waiting " + (int) waitingTimes[i] + " ms...");

            if(i < nbRequests-1) {
                for (int k = (int) waitingTimes[i] / 1000; k >= 0; k--) {
                    System.out.print(".");
                    Thread.sleep(1000);
                }
                Thread.sleep(((int) waitingTimes[i]) % 1000);
                System.out.print("\r");
            }
        }
        System.out.println("Waiting responses...");
        // wait for thread and retrieve durations
        long[] durations = new long[nbRequests];
        for (int i = 0; i < nbRequests; i++) {
            Future<Long> future = completionService.take();
            try {
                durations[i] = future.get(); // waiting for the result of a thread
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
     * @return a string respecting the format of requests specified in the project description.
     * number of tags, tags, number of line and word in the line each follow a uniform distribution
     */
    private static String randomString() {

        StringBuilder request = new StringBuilder().append(randomTypes());

        String randomString = regexDataset.getRandomString();
        StringBuilder purString = new StringBuilder();

        for (int i = 0; i < randomString.length(); i++) {
            if (randomString.charAt(i) == '.')
                purString.append("\\.");
            else
                purString.append(randomString.charAt(i));
        }

        String[] words = purString.toString().split(" ");
        purString = new StringBuilder(words[random.nextInt(words.length)]);

        request.append(";.*").append(purString).append(".*");

        return request.toString();
    }

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
