package com.archi;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.io.FileWriter;
import java.io.IOException;
import java.util.concurrent.*;
import java.util.stream.LongStream;


public class EvaluateClient {

    private static Random random;
    private static long[] durations;

    private static String address;
    private static int port;
    private static Dataset dataset;

    public static void main(String[] args) throws InterruptedException, IOException {
        // init random class
        random = new Random();

        // load dataset to make random request based on it
        dataset = new SimpleDataset();
        dataset.load();

        // server info
        address = "localhost";
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
            myWriter.write(lambda + "\r\n");


            for (int i = min; i <= max; i += step) {// i = number of requests for that iteration
                // Sends NbRequests requests to server at port portNumber and with arrivals following Poisson rule with lambda of lambda.
                // Writes mean times to MeanTimes.txt
                makeNRequests(i, lambda);
                long sum = LongStream.of(durations).sum(); // make the sum of the array
                myWriter.write(i + ";" + sum / i + "\r\n");
            }
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }

    }

    /**
     * calculate the arrival times and  launches the thread following these timers
     */
    public static void makeNRequests(int nbRequests, double lambda) throws InterruptedException {

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
            System.out.println("Thread nb "+i+" launched");
            Thread.sleep((int) waitingTimes[i]);
        }

        // wait for thread and retrieve durations
        durations = new long[nbRequests];
        for (int i = 0; i < nbRequests; i++) {
            Future<Long> future = completionService.take();
            try {
                durations[i] = future.get(); // waiting for the result of a thread
            } catch (ExecutionException e) {
                e.printStackTrace();
            }
        }

        // print durations
        int i;
        System.out.print("Finished in : [");
        for (i = 0; i < durations.length - 1; i++)
            System.out.print(durations[i] + ", ");
        System.out.println(durations[i] + "] ms");

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

        StringBuilder userLine = new StringBuilder();


        int NbTags = random.nextInt(5);
        List<Integer> tags = new ArrayList<Integer>();
        for (int i = 0; i < NbTags; i++) {

            int tag = random.nextInt(6); // choose tag from 0 to 5

            if (tags.contains(tag)) {
                i--;
            } else {
                tags.add(tag);
                if (tags.size() >= NbTags)
                    userLine.append(tag);
                else
                    userLine.append(tag).append(",");
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

        userLine.append(";.*").append(purString).append(".*");

        return userLine.toString();
    }
}


