package com.newerty.derivedStats;

import com.google.common.base.Joiner;
import com.google.common.collect.ImmutableMap;
import com.newerty.derivedStats.DerivedExpressionParser.Numeric_entityContext;
import com.newerty.derivedStats.DerivedExpressionParser.StatIdContext;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.ParserRuleContext;
import org.antlr.v4.runtime.tree.ParseTree;
import org.antlr.v4.runtime.tree.ParseTreeWalker;

import java.util.Collections;
import java.util.List;
import java.util.Map;


public class ReplaceStatIdsListener extends DerivedExpressionBaseListener {

    private final String categoryId;
    private final DerivedExpressionParser parser;
    private Map<ExpressionStatId, List<Double>> values;

    public ReplaceStatIdsListener(DerivedExpressionParser parser, String categoryId) {
        this.parser = parser;
        this.categoryId = categoryId;
    }

    public String replace(Map<ExpressionStatId, List<Double>> values) {
        this.values = ImmutableMap.copyOf(values);
        parser.reset();
        ParseTree tree = parser.assignment();
        ParseTreeWalker walker = new ParseTreeWalker();
        walker.walk(this, tree);
        return tree.getText();
    }


    @Override
    public void exitStatId(StatIdContext ctx) {

        if (!ctx.getParent().getClass().isAssignableFrom(DerivedExpressionParser.AssignmentContext.class)) {

            ExpressionStatId dependentStatId = new ExpressionStatIdBuilder()
                    .from(ctx, categoryId)
                    .build();

            if (values.containsKey(dependentStatId)) {
                List<Double> replaceVals = values.get(dependentStatId);
                if ((replaceVals == null) || replaceVals.isEmpty()) {
                    replaceVals = Collections.singletonList(0.0);
                }

                ctx.getParent().removeLastChild();
                if (replaceVals.size() == 1) {
                    ctx.getParent().addChild(generateNumericContext(replaceVals.get(0)));
                } else {
                    if (isAggregateClause(ctx)) {
                        ctx.getParent().addChild(generateAggregateClause(replaceVals));
                    } else {
                       throw new ExpressionEvaluationException("Unrecoverable error during expression evaluation.");
                   }
                }
            }
        }
    }


    private static boolean isAggregateClause(ParserRuleContext ctx) {
        while (ctx.parent != null) {
            ctx = ctx.getParent();
            if (ctx.getClass().isAssignableFrom(DerivedExpressionParser.AggregateClauseContext.class)) {
                return true;
            }
        }
        return false;
    }


    private static Numeric_entityContext generateNumericContext(Double n) {
        return new DerivedExpressionParser(
                new CommonTokenStream(new DerivedExpressionLexer(CharStreams.fromString(n.toString())))).numeric_entity();
    }

    private static DerivedExpressionParser.AggregateClauseContext generateAggregateClause(List<Double> list) {
        return new DerivedExpressionParser(
                new CommonTokenStream(new DerivedExpressionLexer(CharStreams.fromString(Joiner.on(",").join(list))))).aggregateClause();
    }


}

