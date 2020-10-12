package com.newerty.derivedStats;

import java.util.Map;

@FunctionalInterface
public interface EvaluationFunction {
    EvaluationContext apply(Map<ExpressionStatId, Double> statValues, Map<String, String> placeholders);
}
