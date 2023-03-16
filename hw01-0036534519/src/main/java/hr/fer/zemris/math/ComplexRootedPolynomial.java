package hr.fer.zemris.math;

/**
 * Models complex polynomial with known roots
 */
public class ComplexRootedPolynomial {
    private final Complex constant;
    private final Complex[] roots;

    /**
     * Creates complex polynomial in form:
     *<p>
     * z0 * (z - z1) * (z - z2) * ... * (z - zn)
     *
     * @param constant Constant z0
     * @param roots All the roots of the polynomial
     */
    public ComplexRootedPolynomial(Complex constant, Complex ... roots) {
        this.constant = constant;
        this.roots = roots;
    }

    /**
     * Computes polynomial value at given point z
     *
     * @param z Point to calculate
     * @return Value of polynomial at point z
     */
    public Complex apply(Complex z) {
        return toComplexPolynomial().apply(z);
    }

    /**
     * Converts this representation to ComplexPolynomial form
     *
     * @return ComplexPolynomial representation of this polynomial
     */
    public ComplexPolynomial toComplexPolynomial() {
        ComplexPolynomial p = new ComplexPolynomial(constant);

        for (Complex root: roots) {
            p = p.multiply(new ComplexPolynomial(root.negate(), Complex.ONE));
        }

        return p;
    }

    /**
     * Finds the index of the closest root for the given complex number z that is within
     * the threshold.
     * <p>
     * If there is no such root -1 is returned
     *
     * @param z Complex number near one of the roots
     * @param threshold Maximum distance to root
     * @return Index of the root closest to z
     */
    public int indexOfClosestRootFor(Complex z, double threshold) {
        double minDistance = threshold;
        int closestRootIndex = -1;

        for (int i = 0; i < roots.length; i++) {
            double distanceToRoot = z.sub(roots[i]).module();
            if (distanceToRoot <= minDistance) {
                minDistance = distanceToRoot;
                closestRootIndex = i;
            }
        }

        return closestRootIndex;
    }

    @Override
    public String toString() {
        if (roots.length == 0) {
            return "(" + constant + ")";
        }

        StringBuilder sb = new StringBuilder();

        sb.append("(").append(constant).append(")*");

        for (int i = 0; i < roots.length; i++) {
            sb.append("(")
                .append("z-")
                .append("(")
                .append(roots[i])
                .append(")")
                .append(")");

            if (i != roots.length - 1) {
                sb.append("*");
            }
        }

        return sb.toString();
    }
}
