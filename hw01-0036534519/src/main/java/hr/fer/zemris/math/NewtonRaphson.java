package hr.fer.zemris.math;

import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Used to calculate Newton-Raphson iterations for a complex polynomial over a range of
 * complex plain points
 */
public class NewtonRaphson {

    public static final double CONVERGENCE_THRESHOLD = 0.001;
    public static final double ROOT_THRESHOLD = 0.002;
    public static final int ITERATIONS_MAX = 64;

    public static void calculate(ComplexRootedPolynomial rootedPolynomial, double reMin, double reMax, double imMin, double imMax, int width, int height, int yMin, int yMax, short[] data, AtomicBoolean cancel) {

        ComplexPolynomial polynomial = rootedPolynomial.toComplexPolynomial();
        ComplexPolynomial derived = polynomial.derive();

        int offset = yMin * width;

        for (int y = yMin; y < yMax; y++) {
            if (cancel.get()) break;
            for (int x = 0; x < width; x++) {
                Complex zn = mapToComplexPlain(x, y, width, height, reMin, reMax, imMin, imMax);
                Complex znOld;
                Complex numerator;
                Complex denominator;
                Complex fraction;
                double module;
                int iterations = 0;
                do {
                    numerator = polynomial.apply(zn);
                    denominator = derived.apply(zn);
                    znOld = zn;
                    fraction = numerator.divide(denominator);
                    zn = zn.sub(fraction);
                    module = znOld.sub(zn).module();
                    iterations++;
                } while(module > CONVERGENCE_THRESHOLD && iterations < ITERATIONS_MAX);

                int index = rootedPolynomial.indexOfClosestRootFor(zn, ROOT_THRESHOLD);
                data[offset++] = (short)index;
            }
        }
    }

    private static Complex mapToComplexPlain(int x, int y, int width, int height, double reMin, double reMax, double imMin, double imMax) {
        double re = x / (width - 1.0) * (reMax - reMin) + reMin;
        double im = (1 - y / (height - 1.0)) * (imMax - imMin) + imMin;
        return new Complex(re, im);
    }
}
