package hr.fer.zemris.util;

import java.util.regex.Pattern;

public class ComplexNumberLexer {

    private final StringSlicer slicer;

    protected ComplexNumberLexer(String input) {
        this.slicer = new StringSlicer(input);
    }

    protected ComplexNumberToken nextToken() {
        slicer.skip(c -> !Pattern.matches("\\s*", c.toString()));            //Skip whitespace

        if (slicer.hasNext()) {
            if (slicer.read(c -> c != '+').equals("+")) {
                return new ComplexNumberToken(0, ComplexNumberTokenType.PLUS);
            }

            if (slicer.read(c -> c != '-').equals("-")) {
                return new ComplexNumberToken(0, ComplexNumberTokenType.MINUS);
            }

            if (slicer.read(c -> c != 'i').equals("i")) {
                return new ComplexNumberToken(0, ComplexNumberTokenType.I);
            }

            String number = slicer.read(c -> !Character.isDigit(c) && c != '.');
            try {
                double value = Double.parseDouble(number);
                return new ComplexNumberToken(value, ComplexNumberTokenType.NUMBER);
            } catch (NumberFormatException e) {
                throw new IllegalArgumentException("Unexpected input: " + number);
            }
        }

        return new ComplexNumberToken(0, ComplexNumberTokenType.EOF);
    }
}
