package com.newerty.derivedStats;

import com.google.common.annotations.VisibleForTesting;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.tree.ParseTree;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.IntStream;


public class DerivedExpressionEvaluator implements Evaluator {

    private static final Logger LOG = LoggerFactory.getLogger(DerivedExpressionEvaluator.class);

    private final String categoryId;

    private final ExpressionStatIdList statIds;
    private final EvaluationFunction function;

    private final static char WILDCARD_CHAR = '*';

    DerivedExpressionEvaluator(String expr, String categoryId) {
        this.categoryId = categoryId;

        DerivedExpressionLexer lexer = new DerivedExpressionLexer(CharStreams.fromString(expr));
        lexer.removeErrorListeners();
        lexer.addErrorListener(ExpressionErrorListener.INSTANCE);

        CommonTokenStream tokens = new CommonTokenStream(lexer);

        DerivedExpressionParser parser = new DerivedExpressionParser(tokens);
        parser.removeErrorListeners();
        parser.addErrorListener(ExpressionErrorListener.INSTANCE);

        ParseTree parseTree = parser.assignment();

        ExtractDependentStatIdsListener extractor
                = new ExtractDependentStatIdsListener(parseTree, categoryId);
        this.statIds = extractor.getStatIds();
        this.function = new DerivedExpressionEvaluatorVisitor(categoryId).visit(parseTree);
    }

    /**
     * Returns all of the statIds in the evaluator's expression
     *
     * @return An ExpressionStatIdList containing the derived statId and the dependent statIds in the
     * evaluator's expression
     */
    @Override
    public ExpressionStatIdList getStatIds() {
        return this.statIds;
    }

    /**
     * Evaluate the evaluator's expression by first replacing the statIds with provided values in the map
     *
     * @param values A map of the statIds in the expression and their values
     * @return Returns the value of the evaluated expression
     */
    @Override
    public double evaluate(Map<ExpressionStatId, Double> values, Map<String, String> placeholders) {
        return function.apply(values, placeholders).getValue();
    }

    @Override
    public double evaluate(Map<ExpressionStatId, Double> values) {
        return evaluate(values, getPlaceholder());
    }

    private Map<String, String> getPlaceholder() {
        if (getStatIds().derivedStatId.isWildcarded()) {
            return getStatIds().derivedStatId.getDimensions();
        } else {
            return Collections.emptyMap();
        }
    }


    public double evaluateList(Map<ExpressionStatId, List<Double>> values) {
        Map<ExpressionStatId, Double> nMap = new HashMap<>();

        // is this a placeholder expression? e.g. foo{dim:*} = a{dim:*} + b{dim:*}
        Map<String, String> placeholder = getPlaceholder();
        if (placeholder.isEmpty()) {
            values.forEach((statId, valueList) -> {
                nMap.putAll(normalizeStatId(statId, valueList));
            });
        } else {
            values.forEach((statId, valueList) -> {
                if (valueList.size() > 1) {
                    throw new IllegalArgumentException("Placeholder value must contain exactly one element");
                }
                nMap.put(statId, valueList.get(0));
            });
        }

        return evaluate(nMap, placeholder);
    }

    private Map<ExpressionStatId, Double> normalizeStatId(ExpressionStatId statId, List<Double> values) {
        Map<ExpressionStatId, Double> vMap = new HashMap<>();
        IntStream.range(0, values.size()).forEach(idx -> {
            ExpressionStatIdBuilder builder = new ExpressionStatIdBuilder();
            builder.setCategoryId(statId.getCategoryId());
            builder.setStatId(statId.getStatId());
            statId.getDimensions().forEach((dimName, dimValue) -> {
                builder.addDimension(dimName, dimValue.replace("*", "foo_" + idx));
            });
            vMap.put(builder.build(), values.get(idx));
        });

        return vMap;
    }



    public static DerivedExpressionEvaluator build(String expr, String categoryId) {
        if ((expr == null) || expr.isEmpty()) {
            throw new IllegalArgumentException("Expression is required");
        }

        if ((categoryId == null) || categoryId.isEmpty()) {
            throw new IllegalArgumentException("CategoryId is required");
        }

        return new DerivedExpressionEvaluator(expr, categoryId);
    }

    @VisibleForTesting
    @NonNull
    Double evaluate() {
        return function.apply(Collections.emptyMap(), Collections.emptyMap()).getValue();
    }
}
