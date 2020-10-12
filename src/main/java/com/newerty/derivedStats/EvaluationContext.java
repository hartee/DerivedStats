package com.newerty.derivedStats;

import org.checkerframework.checker.nullness.qual.NonNull;

import java.util.stream.DoubleStream;

public class EvaluationContext {

    // For most cases, we can use this as an accumulator and mutate the value
    // as we traverse the parse tree to avoid lots of unnecessary allocations.
    private double value;

    // For aggregation cases though, we have varargs thanks to wildcards and need
    // an arbitrary number of values. Streams let us squash them together with
    // flatMap, which simplifies the code a bit.
    private final DoubleStream valueStream;

    public EvaluationContext(double value) {
        this.value = value;
        this.valueStream = null;
    }

    public EvaluationContext(@NonNull DoubleStream valueStream) {
        this.value = Double.NaN;
        this.valueStream = valueStream;
    }

    public DoubleStream asStream() {
        return (valueStream != null) ? valueStream : DoubleStream.of(value);
    }

    public double getValue() {
        assert valueStream == null;
        return value;
    }

    public EvaluationContext add(EvaluationContext other) {
        assert valueStream == null;
        if (Double.isNaN(value) || Double.isNaN(other.getValue()) ) {
            value = Double.NaN;
        } else {
            value += other.getValue();
        }
        return this;
    }

    public EvaluationContext subtract(EvaluationContext other) {
        assert valueStream == null;
        value -= other.getValue();
        return this;
    }

    public EvaluationContext multiply(EvaluationContext other) {
        assert valueStream == null;
        value *= other.getValue();
        return this;
    }

    public EvaluationContext divide(EvaluationContext other) {
        assert valueStream == null;
        value /= other.getValue();
        return this;
    }

    public EvaluationContext negate() {
        assert valueStream == null;
        value *= -1;
        return this;
    }

    public EvaluationContext logicalAnd(EvaluationContext other) {
        assert valueStream == null;
        value = ((value != 0) && (other.getValue() != 0)) ? 1d : 0d;
        return this;
    }

    public EvaluationContext logicalOr(EvaluationContext other) {
        assert valueStream == null;
        value = ((value != 0) || (other.getValue() != 0)) ? 1d : 0d;
        return this;
    }

    public EvaluationContext relationalGT(EvaluationContext other) {
        assert valueStream == null;
        value = (value > other.getValue()) ? 1d : 0d;
        return this;
    }

    public EvaluationContext relationalGTE(EvaluationContext other) {
        assert valueStream == null;
        value = (value >= other.getValue()) ? 1d : 0d;
        return this;
    }

    public EvaluationContext relationalLT(EvaluationContext other) {
        assert valueStream == null;
        value = (value < other.getValue()) ? 1d : 0d;
        return this;
    }

    public EvaluationContext relationalLTE(EvaluationContext other) {
        assert valueStream == null;
        value = (value <= other.getValue()) ? 1d : 0d;
        return this;
    }

    public EvaluationContext relationalEQ(EvaluationContext other) {
        assert valueStream == null;

        if (Double.isNaN(value) || Double.isNaN(other.getValue())) {
            value = Double.NaN;
        } else {
            value = (value == other.getValue()) ? 1d : 0d;
        }

        return this;
    }

    public EvaluationContext relationalNEQ(EvaluationContext other) {
        assert valueStream == null;

        if (Double.isNaN(value) || Double.isNaN(other.getValue())) {
            value = Double.NaN;
        } else {
            value = (other.getValue() == value) ? 0d : 1d;
        }

        return this;
    }

    public DoubleStream getValueStream() {
        assert valueStream != null;
        return valueStream;
    }

}
