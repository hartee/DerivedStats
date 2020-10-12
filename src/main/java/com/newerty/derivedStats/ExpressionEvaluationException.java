package com.newerty.derivedStats;

import com.google.common.collect.ImmutableList;

import java.util.List;
import java.util.concurrent.CancellationException;


public class ExpressionEvaluationException extends CancellationException {
    private static final long serialVersionUID = -8838128786291231741L;

    public List<String> getRuleStack() {
        return ruleStack;
    }

    private final List<String> ruleStack;

    public ExpressionEvaluationException(String message) {
        super(message);
        ruleStack = null;
    }

    public ExpressionEvaluationException(String message, List<String> stack) {
        super(message);
        this.ruleStack = ImmutableList.copyOf(stack);
    }
}


