package hr.fer.zemris.util;

import hr.fer.zemris.math.Complex;

/**
 * Used for parsing string representations of complex numbers
 */
public class ComplexNumberParser {

    private final ComplexNumberLexer lexer;
    private ComplexNumberToken currentToken;
    private ComplexNumberTokenType currentTokenType;
    private double re;
    private double im;
    private boolean reSet;
    private boolean imSet;

    private ComplexNumberParser(String input) {
        this.lexer = new ComplexNumberLexer(input);
        this.reSet = false;
        this.imSet = false;
        setCurrentToken(lexer.nextToken());
    }

    private Complex parse() {
        while(currentTokenType != ComplexNumberTokenType.EOF) {
            if (currentTokenType == ComplexNumberTokenType.PLUS) {
                plus();
                continue;
            }
            
            if (currentTokenType == ComplexNumberTokenType.MINUS) {
                minus();
                continue;
            }
            
            if (currentTokenType == ComplexNumberTokenType.NUMBER) {
                number();
                continue;
            }
            
            if (currentTokenType == ComplexNumberTokenType.I) {
                i();
                continue;
            }

            throw new IllegalArgumentException("Unexpected token: " + currentTokenType);
        }

        return new Complex(reSet ? re : 0, imSet ? im : 0);
    }

    private void plus() {
        setCurrentToken(lexer.nextToken());

        if (currentTokenType == ComplexNumberTokenType.NUMBER) {
            number();
            return;
        }

        if (currentTokenType == ComplexNumberTokenType.I) {
            i();
            return;
        }

        throw new IllegalArgumentException("Unexpected token: " + currentTokenType);
    }

    private void minus() {
        setCurrentToken(lexer.nextToken());

        if (currentTokenType == ComplexNumberTokenType.NUMBER) {
            number();
            re *= -1;
            return;
        }

        if (currentTokenType == ComplexNumberTokenType.I) {
            i();
            im *= -1;
            return;
        }

        throw new IllegalArgumentException("Unexpected token: " + currentTokenType);
    }

    private void number() {
        if (reSet) {
            throw new IllegalArgumentException("Unexpected token: " + currentTokenType);
        }

        re = currentToken.getValue();
        reSet = true;
        setCurrentToken(lexer.nextToken());
    }

    private void i() {
        if (imSet) {
            throw new IllegalArgumentException("Unexpected token: " + currentTokenType);
        }

        setCurrentToken(lexer.nextToken());

        if (currentTokenType == ComplexNumberTokenType.NUMBER) {
            im = currentToken.getValue();
            setCurrentToken(lexer.nextToken());
        } else {
            im = 1;
        }

        imSet = true;
    }

    public void setCurrentToken(ComplexNumberToken token) {
        this.currentToken = token;
        this.currentTokenType = token.getType();
    }

    /**
     * General syntax for complex numbers is of form:
     * <ul>
     *     <li>a + ib</li>
     *     <li>a - ib</li>
     * </ul>
     * where parts that are zero can be dropped, but not both (empty string is not legal complex number)
     * <p>
     * For example, zero can be given as 0, i0, 0+i0, 0-i0.
     * <p>
     * If there is 'i' present but no b is given, it is assumed that b=1.
     *
     * @param input String representation of complex number
     * @return Parsed complex number
     * @throws IllegalArgumentException If input can't be parsed
     */
    public static Complex parse(String input) throws IllegalArgumentException {
        if (input.equals("")) {
            throw new IllegalArgumentException("Input can't be an empty string.");
        }

        ComplexNumberParser parser = new ComplexNumberParser(input);
        return parser.parse();
    }
}
