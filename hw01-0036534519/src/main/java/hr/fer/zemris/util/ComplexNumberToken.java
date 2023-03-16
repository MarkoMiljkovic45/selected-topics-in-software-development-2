package hr.fer.zemris.util;

public class ComplexNumberToken {

    private final double value;
    private final ComplexNumberTokenType type;

    public ComplexNumberToken(double value, ComplexNumberTokenType type) {
        this.value = value;
        this.type = type;
    }

    public double getValue() {
        return value;
    }

    public ComplexNumberTokenType getType() {
        return type;
    }
}
