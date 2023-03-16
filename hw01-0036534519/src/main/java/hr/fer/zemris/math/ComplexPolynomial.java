package hr.fer.zemris.math;

import java.util.Arrays;

/**
 * Used to model a complex polynomial
 */
public class ComplexPolynomial {
    /**
     * Factors of the polynomial
     */
    private final Complex[] factors;

    /**
     * Creates a complex polynomial using provided factors
     * <p>
     * The order is z0, z1, z2, ..., zn where:
     * <p>
     * z0 + z1*z^1 + z2*^2 + ... + zn * z^n
     *
     * @param factors Factors of the polynomial
     */
    public ComplexPolynomial(Complex ...factors) {
        this.factors = factors;
    }

    /**
     * Order of polynomial is the highest power factor in the polynomial
     *
     * @return The order of the polynomial
     */
    public short order() {
        return (short) (factors.length - 1);
    }

    /**
     * Computes a new polynomial by multiplying this polynomial with p
     *
     * @param p Polynomial that will be multiplied with this polynomial
     * @return This complex polynomial multiplied by p
     */
    public ComplexPolynomial multiply(ComplexPolynomial p) {
        int newLength = order() + p.order() + 1;

        Complex[] newFactors = new Complex[newLength];

        for (int i = 0; i < newLength; i++) {
            newFactors[i] = Complex.ZERO;
        }

        for (int i = 0; i < factors.length; i++) {
            for (int j = 0; j < p.factors.length; j++) {
                Complex z1 = factors[i];
                Complex z2 = p.factors[j];

                Complex c = z1.multiply(z2);

                newFactors[i+j] = newFactors[i+j].add(c);
            }
        }

        return new ComplexPolynomial(newFactors);
    }

    /**
     * Computes derivative of this polynomial
     *
     * @return derivative of this polynomial
     */
    public ComplexPolynomial derive() {
        Complex[] newFactors = new Complex[factors.length - 1];

        for (int i = 1; i < factors.length; i++) {
            newFactors[i-1] = factors[i].multiply(new Complex(i, 0));
        }

        return new ComplexPolynomial(newFactors);
    }

    /**
     * Computes polynomial value at given point z
     *
     * @param z Point to calculate
     * @return Value of polynomial at point z
     */
    public Complex apply(Complex z) {
        if (factors.length == 0) {
            return Complex.ZERO;
        }

        Complex ans = factors[0];

        for (int i = 1; i < factors.length; i++) {
            ans = ans.add(factors[i].multiply(z.power(i)));
        }

        return ans;
    }

    @Override
    public String toString() {
        if (factors.length == 0) {
            return "(" + Complex.ZERO + ")";
        }

        StringBuilder sb = new StringBuilder();

        for (int i = factors.length - 1; i > 0; i--) {
            sb.append("(")
                .append(factors[i])
                .append(")")
                .append("*z^")
                .append(i)
                .append("+");
        }

        sb.append("(").append(factors[0]).append(")");

        return sb.toString();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ComplexPolynomial that = (ComplexPolynomial) o;
        return Arrays.equals(factors, that.factors);
    }

    @Override
    public int hashCode() {
        return Arrays.hashCode(factors);
    }
}
