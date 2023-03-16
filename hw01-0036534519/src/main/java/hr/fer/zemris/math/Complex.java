package hr.fer.zemris.math;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Models complex numbers
 */
public class Complex {
    /**
     * Real part of the complex number
     */
    private final double re;
    /**
     * Imaginary part of the complex number
     */
    private final double im;

    public static final Complex ZERO = new Complex(0,0);
    public static final Complex ONE = new Complex(1,0);
    public static final Complex ONE_NEG = new Complex(-1,0);
    public static final Complex IM = new Complex(0,1);
    public static final Complex IM_NEG = new Complex(0,-1);

    /**
     * Returns complex plain origin
     */
    public Complex() {
        this(0, 0);
    }

    public Complex(double re, double im) {
        this.re = re;
        this.im = im;
    }

    /**
     * Distance of this complex number from the origin of the complex plain
     *
     * @return Module of complex number
     */
    public double module() {
        return Math.sqrt(re * re + im * im);
    }

    /**
     * Multiplies this complex number with c
     * <p>
     * z1 = a + ib
     * <p>
     * z2 = c + id
     * <p>
     * z1 * z2 = (ac - bd) + i(ad + bc)
     *
     * @param c Complex number that this will be multiplied with
     * @return This complex number multiplied by c
     */
    public Complex multiply(Complex c) {
        return new Complex(
                re * c.re - im * c.im,
                re * c.im + im * c.re
        );
    }

    /**
     * Divides this complex number with c
     * <p>
     * z1 = a + ib
     * <p>
     * z2 = c + id
     * <p>
     * z1 / z2 = (ac + bd)/(c^2 + d^2) + i(-ad + bc)/(c^2 + d^2)
     *
     * @param c Complex number that this will be divided by
     * @return This complex number divided by c
     */
    public Complex divide(Complex c) {
        double denominator = c.re * c.re + c.im * c.im;

        return new Complex(
                (re * c.re + im * c.im) / denominator,
                (im * c.re - re * c.im) / denominator
        );
    }

    /**
     * Sums this complex number and c
     * <p>
     * z1 = a + ib
     * <p>
     * z2 = c + id
     * <p>
     * z1 + z2 = (a + c) + i(b + d)
     *
     * @param c Complex number that will be added to this
     * @return Sum of this number and c
     */
    public Complex add(Complex c) {
        return new Complex(
                re + c.re,
                im + c.im
        );
    }

    /**
     * Subtracts c from this complex number
     * <p>
     * z1 = a + ib
     * <p>
     * z2 = c + id
     * <p>
     * z1 - z2 = (a - c) + i(b - d)
     *
     * @param c Complex number that this will be reduced by
     * @return This complex number subtracted by c
     */
    public Complex sub(Complex c) {
        return add(c.negate());
    }

    /**
     * Negates this complex number
     * <p>
     * z1 = a + ib
     * <p>
     * -z1 = -a - ib
     *
     * @return This complex number multiplied by -1
     */
    public Complex negate() {
        return multiply(ONE_NEG);
    }

    /**
     * Raises this complex number to the n-th power (n must be positive)
     * <p>
     * If n is less than or equal to 0 throws IllegalArgumentException
     *
     * @param n Power to be raised by
     * @return This raised to the n-th power
     * @throws IllegalArgumentException if n is less than or equal to 0
     */
    public Complex power(int n) {
        if (n <= 0) {
            throw new IllegalArgumentException("n must be positive.");
        }

        Complex base = this;
        Complex ans = ONE;

        for (int i = 0; i < n; i++) {
            ans = ans.multiply(base);
        }

        return ans;
    }

    /**
     * Takes the n-th root of this complex number (n must be positive)
     * <p>
     * If n is less than or equal to 0 throws IllegalArgumentException
     *
     * @param n The root
     * @return The n-th root of this complex number
     * @throws IllegalArgumentException if n is less than or equal to 0
     */
    public List<Complex> root(int n) {
        if (n <= 0) {
            throw new IllegalArgumentException("n must be positive.");
        }

        List<Complex> ans = new ArrayList<>();

        double rootR = Math.pow(module(), 1f / n);
        double theta = Math.atan(im / re);

        for (int k = 0; k < n; k++) {
            double phi = (theta + 2*Math.PI*k) / n;

            ans.add(new Complex(rootR * Math.cos(phi),rootR * Math.sin(phi)));
        }

        return ans;
    }

    @Override
    public String toString() {
        return im >= 0 ? re + "+i" + im : re + "-i" + im * -1;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Complex complex = (Complex) o;
        return Math.abs(complex.re - re) < 1e-12 && Math.abs(complex.im - im) < 1e-12;
    }

    @Override
    public int hashCode() {
        return Objects.hash(re, im);
    }
}
