package hr.fer.zemris.java.fractals;

import hr.fer.zemris.java.fractals.viewer.FractalViewer;
import hr.fer.zemris.java.fractals.viewer.IFractalProducer;
import hr.fer.zemris.java.fractals.viewer.IFractalResultObserver;
import hr.fer.zemris.math.Complex;
import hr.fer.zemris.math.ComplexRootedPolynomial;
import hr.fer.zemris.math.NewtonRaphson;
import hr.fer.zemris.util.ComplexNumberParser;

import java.util.*;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Used for visualizing Newton Raphson iteration fractal
 * <p>
 * This version utilizes multi-threading
 */
public class NewtonP1 {

    public static final int DEFAULT_NUMBER_OF_WORKERS;

    static {
        DEFAULT_NUMBER_OF_WORKERS = Runtime.getRuntime().availableProcessors();
    }

    public static void main(String[] args) {
        Map<String, Integer> params = Collections.emptyMap();

        try {
            params = getParams(args);
        } catch (IllegalArgumentException e) {
            System.out.println(e.getMessage());
            System.exit(0);
        } catch (Exception e) {
            System.out.println("Unable to read program arguments.");
            System.exit(0);
        }

        int numberOfWorkers;
        int numberOfTracks;

        Integer argWorkers = params.get("workers");
        Integer argTracks  = params.get("tracks");

        if (argWorkers != null && argWorkers < DEFAULT_NUMBER_OF_WORKERS) {
            numberOfWorkers = argWorkers;
        } else {
            numberOfWorkers = DEFAULT_NUMBER_OF_WORKERS;
        }

        numberOfTracks = Objects.requireNonNullElseGet(argTracks, () -> 4 * numberOfWorkers);

        System.out.println("Welcome to Newton-Raphson iteration-based fractal viewer.");
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
        double reMin;
        double reMax;
        double imMin;
        double imMax;
        int width;
        int height;
        int yMin;
        int yMax;
        short[] data;
        AtomicBoolean cancel;
        public static CalculationJob NO_JOB = new CalculationJob();

        private CalculationJob() {
        }

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
            //TODO
        }

        @Override
        public void produce(double reMin, double reMax, double imMin, double imMax, int width, int height, long requestNo, IFractalResultObserver observer, AtomicBoolean cancel) {
            System.out.println("Zapocinjem izracun...");
            short[] data = new short[width * height];

            int numberOfTracks = Math.min(this.numberOfTracks, height);

            System.out.println("Efektivan broj dretvi: " + numberOfWorkers);
            System.out.println("Efektivan broj poslova: " + numberOfTracks);

            int yPerTrack = height / numberOfTracks;

            final BlockingQueue<CalculationJob> queue = new LinkedBlockingQueue<>();

            Thread[] workers = new Thread[numberOfWorkers];

            for(int i = 0; i < workers.length; i++) {
                workers[i] = new Thread(() -> {
                    while(true) {
                        CalculationJob p;
                        try {
                            p = queue.take();
                            if(p==CalculationJob.NO_JOB) break;
                        } catch (InterruptedException e) {
                            continue;
                        }
                        p.run();
                    }
                });
            }

            for (Thread worker : workers) {
                worker.start();
            }

            for (int i = 0; i < numberOfTracks; i++) {
                int yMin = i * yPerTrack;
                int yMax;
                if(i == numberOfTracks - 1) {
                    yMax = height;
                } else {
                    yMax = (i+1) * yPerTrack;
                }

                CalculationJob job = new CalculationJob(rootedPolynomial, reMin, reMax, imMin, imMax, width, height, yMin, yMax, data, cancel);

                while(true) {
                    try {
                        queue.put(job);
                        break;
                    } catch (InterruptedException ignorable) {
                        //Ignore
                    }
                }
            }

            for (int i = 0; i < workers.length; i++) {
                while(true) {
                    try {
                        queue.put(CalculationJob.NO_JOB);
                        break;
                    } catch (InterruptedException ignorable) {
                        //Ignore
                    }
                }
            }

            for (Thread worker : workers) {
                while (true) {
                    try {
                        worker.join();
                        break;
                    } catch (InterruptedException ignorable) {
                        //Ignore
                    }
                }
            }

            System.out.println("Racunanje gotovo. Idem obavijestiti promatraca tj. GUI!");
            observer.acceptResult(data, (short)(rootedPolynomial.toComplexPolynomial().order() + 1), requestNo);
        }

        @Override
        public void close() {
            //TODO
        }
    }

    /**
     * Used to read program arguments
     *
     * @param args Program arguments
     * @return Map of program parameters
     * @throws IllegalArgumentException If any parameter is defined more than once or has an invalid value
     * @throws NumberFormatException If parameter value is not a whole number
     * @throws IndexOutOfBoundsException If parameter value not defined
     */
    public static Map<String, Integer> getParams(String[] args) {
        Map<String, Integer> params = new HashMap<>();

        for (int i = 0; i < args.length; i++) {
            String arg = args[i];

            if (arg.contains("--workers=")) {
                if (params.get("workers") != null) {
                    throw new IllegalArgumentException("Each argument can only be defined once.");
                }

                int argValue = Integer.parseInt(arg.split("=")[1]);
                if (argValue < 1) {
                    throw new IllegalArgumentException("Argument values must be greater or equal to 1.");
                }
                params.put("workers", argValue);
            }

            if (arg.contains("--tracks=")) {
                if (params.get("tracks") != null) {
                    throw new IllegalArgumentException("Each argument can only be defined once.");
                }

                int argValue = Integer.parseInt(arg.split("=")[1]);
                if (argValue < 1) {
                    throw new IllegalArgumentException("Argument values must be greater or equal to 1.");
                }
                params.put("tracks", argValue);
            }

            if (arg.equals("-w")) {
                if (params.get("workers") != null) {
                    throw new IllegalArgumentException("Each argument can only be defined once.");
                }

                int argValue = Integer.parseInt(args[++i]);
                if (argValue < 1) {
                    throw new IllegalArgumentException("Argument values must be greater or equal to 1.");
                }
                params.put("workers", argValue);
            }

            if (arg.equals("-t")) {
                if (params.get("tracks") != null) {
                    throw new IllegalArgumentException("Each argument can only be defined once.");
                }

                int argValue = Integer.parseInt(args[++i]);
                if (argValue < 1) {
                    throw new IllegalArgumentException("Argument values must be greater or equal to 1.");
                }
                params.put("tracks", argValue);
            }
        }

        return params;
    }

    private void parseArg(String arg, String argName, String argKey, Map<String, Integer> params) {
        if (arg.contains("--workers=")) {
            if (params.get("workers") != null) {
                throw new IllegalArgumentException("Each argument can only be defined once.");
            }

            int argValue = Integer.parseInt(arg.split("=")[1]);
            if (argValue < 1) {
                throw new IllegalArgumentException("Argument values must be greater or equal to 1.");
            }
            params.put("workers", argValue);
        }
    }

    private void parseArg(String arg, String argName, Map<String, Integer> params) {
        parseArg(arg, argName, argName, params);
    }
}

