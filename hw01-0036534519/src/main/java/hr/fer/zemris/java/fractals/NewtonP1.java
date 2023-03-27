package hr.fer.zemris.java.fractals;

import hr.fer.zemris.java.fractals.viewer.FractalViewer;
import hr.fer.zemris.java.fractals.viewer.IFractalProducer;
import hr.fer.zemris.java.fractals.viewer.IFractalResultObserver;
import hr.fer.zemris.math.Complex;
import hr.fer.zemris.math.ComplexRootedPolynomial;
import hr.fer.zemris.math.NewtonRaphson;
import hr.fer.zemris.util.ArgumentParser;
import hr.fer.zemris.util.ComplexNumberParser;

import java.util.*;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Used for visualizing Newton Raphson iteration fractal
 * <p>
 * This version utilizes multi-threading
 */
public class NewtonP1 {

    private static final int DEFAULT_NUMBER_OF_WORKERS = Runtime.getRuntime().availableProcessors();
    private static final String WORKERS = "workers";
    private static final String[] WORKERS_PSEUDONYMS = {"workers", "w"};
    private static final String TRACKS = "tracks";
    private static final String[] TRACKS_PSEUDONYMS = {"tracks", "t"};

    public static void main(String[] args) {
        Map<String, Integer> params = parseArguments(args);

        int numberOfWorkers;
        int numberOfTracks;

        int argWorkers = params.get(WORKERS);
        int argTracks  = params.get(TRACKS);

        if (argWorkers > 0 && argWorkers < DEFAULT_NUMBER_OF_WORKERS) {
            numberOfWorkers = argWorkers;
        } else {
            numberOfWorkers = DEFAULT_NUMBER_OF_WORKERS;
        }

        if (argTracks > 0) {
            numberOfTracks = argTracks;
        } else {
            numberOfTracks = 4 * numberOfWorkers;
        }

        System.out.println("Welcome to Newton-Raphson iteration-based fractal viewer.");
        System.out.println("WORKERS = " + numberOfWorkers);
        System.out.println("Please enter at least two roots, one root per line. Enter 'done' when done.");

        List<Complex> roots = new ArrayList<>();

        try (Scanner sc = new Scanner(System.in)) {
            int rootIndex = 1;
            System.out.printf("Root %d> ", rootIndex++);
            String line = sc.nextLine();

            while(!line.equalsIgnoreCase("done")) {
                roots.add(ComplexNumberParser.parse(line));
                System.out.printf("Root %d> ", rootIndex++);
                line = sc.nextLine();
            }

            if (rootIndex <= 3) {
                System.out.println("You must provide at least two roots.");
                System.exit(0);
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
            System.exit(0);
        }

        ComplexRootedPolynomial crp = new ComplexRootedPolynomial(Complex.ONE, roots.toArray(new Complex[2]));

        System.out.println("Image of fractal will appear shortly. Thank you.");
        FractalViewer.show(new NewtonRaphsonProducer(crp, numberOfWorkers, numberOfTracks));
    }

    private static class CalculationJob implements Runnable {
        ComplexRootedPolynomial rootedPolynomial;
        double reMin, reMax, imMin, imMax;
        int width, height, yMin, yMax;
        short[] data;
        AtomicBoolean cancel;

        public CalculationJob(ComplexRootedPolynomial rootedPolynomial,double reMin,
                              double reMax, double imMin,
                              double imMax, int width, int height, int yMin, int yMax,
                              short[] data, AtomicBoolean cancel) {
            super();
            this.rootedPolynomial = rootedPolynomial;
            this.reMin = reMin;
            this.reMax = reMax;
            this.imMin = imMin;
            this.imMax = imMax;
            this.width = width;
            this.height = height;
            this.yMin = yMin;
            this.yMax = yMax;
            this.data = data;
            this.cancel = cancel;
        }

        @Override
        public void run() {
            NewtonRaphson.calculate(rootedPolynomial, reMin, reMax, imMin, imMax, width, height, yMin, yMax, data, cancel);
        }
    }

    private static class NewtonRaphsonProducer implements IFractalProducer {
        private final ComplexRootedPolynomial rootedPolynomial;
        private final int numberOfWorkers;
        private final int numberOfTracks;

        public NewtonRaphsonProducer(ComplexRootedPolynomial rootedPolynomial, int numberOfWorkers, int numberOfTracks) {
            this.rootedPolynomial = rootedPolynomial;
            this.numberOfWorkers = numberOfWorkers;
            this.numberOfTracks = numberOfTracks;
        }

        @Override
        public void setup() {
            //TODO Open thread pool and calculate parameters
        }

        @Override
        public void produce(double reMin, double reMax, double imMin, double imMax, int width, int height, long requestNo, IFractalResultObserver observer, AtomicBoolean cancel) {
            System.out.println("Generating image...");
            short[] data = new short[width * height];

            int numberOfTracks = Math.min(this.numberOfTracks, height);
            System.out.println("TRACKS = " + numberOfTracks);

            int yPerTrack = height / numberOfTracks;

            //TODO Move parts to setup

            for (int i = 0; i < numberOfTracks; i++) {
                int yMin = i * yPerTrack;
                int yMax;
                if(i == numberOfTracks - 1) {
                    yMax = height;
                } else {
                    yMax = (i+1) * yPerTrack;
                }

                CalculationJob job = new CalculationJob(rootedPolynomial, reMin, reMax, imMin, imMax, width, height, yMin, yMax, data, cancel);

                //TODO Add jobs to thread pool
            }


            //TODO Wait for jobs to finish

            System.out.println("Racunanje gotovo. Idem obavijestiti promatraca tj. GUI!");
            observer.acceptResult(data, (short)(rootedPolynomial.toComplexPolynomial().order() + 1), requestNo);
        }

        @Override
        public void close() {
            //TODO Close thread pool
        }
    }

    /**
     * Used to parse NewtonP1 arguments
     * @return mapping for param -> value
     */
    private static Map<String, Integer> parseArguments(String[] args) {
        Map<String, Integer> params = new HashMap<>();

        String[] formattedArgs = Stream.of(args)
                .map(arg -> arg.replace("--", "-"))
                .map(arg -> arg.replace("=", " "))
                .collect(Collectors.joining(" "))
                .split(" ");

        ArgumentParser argumentParser = new ArgumentParser(formattedArgs);

        params.put(WORKERS, getArgValue(argumentParser, WORKERS_PSEUDONYMS));
        params.put(TRACKS, getArgValue(argumentParser, TRACKS_PSEUDONYMS));

        return params;
    }

    private static int getArgValue(ArgumentParser parser, String...argPseudonyms) {
        List<String> argValues = new ArrayList<>();

        for (String argPseudonym: argPseudonyms) {
            argValues.addAll(parser.getArgumentValue(argPseudonym));
        }

        if (argValues.size() == 0) return 0;

        try {
            if (argValues.size() > 1) throw new IllegalArgumentException();
            return Integer.parseInt(argValues.get(0));
        } catch (Exception e) {
            System.out.println("Invalid parameters for argument: " + argPseudonyms[0]);
            System.exit(1);
        }

        throw new IllegalStateException("An unexpected error occurred");
    }
}

