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
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Used for visualizing Newton Raphson iteration fractal
 * <p>
 * This version utilizes multi-threading
 */
public class NewtonP2
{
    private static final String MINTRACKS = "mintracks";
    private static final String[] MINTRACKS_PSEUDONYMS = {"mintracks", "m"};
    private static final int MINTRACKS_DEFAULT = 16;

    public static void main(String[] args) {
        Map<String, Integer> params = parseArguments(args);

        int minTracks;
        int argMinTracks = params.get(MINTRACKS);

        if (argMinTracks > 0) {
            minTracks = argMinTracks;
        } else {
            minTracks = MINTRACKS_DEFAULT;
        }

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
        FractalViewer.show(new NewtonRaphsonProducer(crp, minTracks));
    }

    private static class CalculationJob extends RecursiveAction {
        ComplexRootedPolynomial rootedPolynomial;
        double reMin, reMax, imMin, imMax;
        int minTracks, width, height, yMin, yMax;
        short[] data;
        AtomicBoolean cancel;

        public CalculationJob(ComplexRootedPolynomial rootedPolynomial, int minTracks,double reMin,
                              double reMax, double imMin,
                              double imMax, int width, int height, int yMin, int yMax,
                              short[] data, AtomicBoolean cancel) {
            super();
            this.rootedPolynomial = rootedPolynomial;
            this.minTracks = minTracks;
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
        public void compute() {
            int trackHeight = yMax - yMin;

            if (height / trackHeight <= minTracks) {
                NewtonRaphson.calculate(rootedPolynomial, reMin, reMax, imMin, imMax, width, height, yMin, yMax, data, cancel);
                return;
            }

            int yMid = trackHeight / 2 + yMin;
            CalculationJob job1 = new CalculationJob(rootedPolynomial, minTracks, reMin, reMax, imMin, imMax, width, height, yMin, yMid, data, cancel);
            CalculationJob job2 = new CalculationJob(rootedPolynomial, minTracks, reMin, reMax, imMin, imMax, width, height, yMid, yMax, data, cancel);

            invokeAll(job1, job2);
        }
    }

    private static class NewtonRaphsonProducer implements IFractalProducer {
        private final ComplexRootedPolynomial rootedPolynomial;
        private final int minTracks;
        private ForkJoinPool pool;

        public NewtonRaphsonProducer(ComplexRootedPolynomial rootedPolynomial, int minTracks) {
            this.rootedPolynomial = rootedPolynomial;
            this.minTracks        = minTracks;
        }

        @Override
        public void setup() {
            pool = new ForkJoinPool();
        }

        @Override
        public synchronized void produce(double reMin, double reMax, double imMin, double imMax, int width, int height, long requestNo, IFractalResultObserver observer, AtomicBoolean cancel) {
            System.out.println("Generating image...");
            short[] data = new short[width * height];

            int actualMinTracks = Math.min(minTracks, height);
            System.out.println("MINTRACKS = " + actualMinTracks);

            CalculationJob job = new CalculationJob(rootedPolynomial, actualMinTracks, reMin, reMax, imMin, imMax, width, height, 0, height, data, cancel);
            pool.invoke(job);

            System.out.println("Racunanje gotovo. Idem obavijestiti promatraca tj. GUI!");
            observer.acceptResult(data, (short)(rootedPolynomial.toComplexPolynomial().order() + 1), requestNo);
        }

        @Override
        public void close() {
            pool.shutdown();
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

        params.put(MINTRACKS, getArgValue(argumentParser, MINTRACKS_PSEUDONYMS));

        return params;
    }

    private static int getArgValue(ArgumentParser parser, String...argPseudonyms) {
        List<String> argValues = new ArrayList<>();

        for (String argPseudonym: argPseudonyms) {
            List<String> values = parser.getArgumentValue(argPseudonym);
            if (values != null) argValues.addAll(values);
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

