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
    private static Dataset dataset;

    public static void main(String[] args) {
        // init random class
        random = new Random();

        // load dataset to make random request based on it
        dataset = new SimpleDataset();
        dataset.load();
        System.out.println("Dataset loaded");

        // server info
        address = "2620:9b::193f:5de1";
        port = 5678;

        // distribution parameters
        double lambda = 0.0003333333333333; // mean time between 2 arrivals is 1/lambda
        int min = 5;
        int max = 20;
        int step = 5;

        iterateOnNbRequests(min, max, step, lambda);
    }

    /**
     * iterate from min to max with step step on each iteration, these are used as NbRequests
     */
    public static void iterateOnNbRequests(int min, int max, int step, double lambda) {
        try (FileWriter myWriter = new FileWriter("MeanTimes.txt", false)) {
            myWriter.write(lambda + "\n");


            for (int i = min; i <= max; i += step) {// i = number of requests for that iteration
                // Sends NbRequests requests to server at port portNumber and with arrivals following Poisson rule with lambda of lambda.
                // Writes mean times to MeanTimes.txt
                long[] durations = makeNRequests(i, lambda);
                long sum = LongStream.of(durations).sum(); // make the sum of the array

                // print durations
                Log.p(Log.PURPLE + "Durations" + Log.RESET + " = " + Arrays.toString(durations) + " ms :" +
                        " mean = " + Log.RED + (sum / i) + " ms\n");
                myWriter.write(i + "," + (sum / i) + "\n");
            }
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }

    }


    /**
     * calculate the arrival times and  launches the thread following these timers
     */
    public static long[] makeNRequests(int nbRequests, double lambda) throws InterruptedException {
        Log.p(Log.BLUE + "* Making " + nbRequests + " requests *");
        double[] waitingTimes = getPoissons(lambda, nbRequests);

        String[] requests = new String[nbRequests];

        for (int i = 0; i < nbRequests; i++) {
            requests[i] = randomString();
        }


        ExecutorService executorService = Executors.newFixedThreadPool(nbRequests); // thread pool manager
        CompletionService<Long> completionService = new ExecutorCompletionService<Long>(executorService); // to wait for thread
        for (int i = 0; i < nbRequests; i++) {
            final String request = requests[i];

            completionService.submit(() -> ClientRequestManager.makeRequest(address, port, request));
            Log.p("Thread n°" + (i + 1) + " launched. Waiting " + (int) waitingTimes[i] + " ms...");

            for (int k=(int) waitingTimes[i]/1000;k>=0;k--){
                System.out.print("Waiting " + k + " s...\r");
                Thread.sleep(1000);
            }
            Thread.sleep(((int) waitingTimes[i])%1000);
        }

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

        return durations;
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
     * @return a string respecting the format of requests specified in the project description.
     * number of tags, tags, number of line and word in the line each follow a uniform distribution
     */
    private static String randomString() {

        StringBuilder request = new StringBuilder();


        int NbTags = random.nextInt(5);
        List<Integer> tags = new ArrayList<Integer>();
        for (int i = 0; i < NbTags; i++) {

            int tag = random.nextInt(6); // choose tag from 0 to 5

            if (tags.contains(tag)) {
                i--;
            } else {
                tags.add(tag);
                if (tags.size() >= NbTags)
                    request.append(tag);
                else
                    request.append(tag).append(",");
            }
        }

        String randomString = dataset.getRandomString();
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
}


