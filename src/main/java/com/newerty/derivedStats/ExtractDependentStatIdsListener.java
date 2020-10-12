package com.newerty.derivedStats;

import org.antlr.v4.runtime.tree.ParseTree;
import org.antlr.v4.runtime.tree.ParseTreeWalker;


public class ExtractDependentStatIdsListener extends DerivedExpressionBaseListener {

    private final String categoryId;
    private final ExpressionStatIdList statIds;

    public ExtractDependentStatIdsListener(ParseTree parseTree, String categoryId) {
        this.categoryId = categoryId;
        statIds = new ExpressionStatIdList();

        ParseTreeWalker walker = new ParseTreeWalker();
        walker.walk(this, parseTree);
    }

    @Override
    public void enterStatId(DerivedExpressionParser.StatIdContext ctx) {
        ExpressionStatId expressionStatId = new ExpressionStatIdBuilder()
                .from(ctx, categoryId)
                .build();

        // if first one (i.e. derived stat)
        if (statIds.derivedStatId == null) {
            statIds.derivedStatId = expressionStatId;
        } else {
            statIds.dependentStatIds.add(expressionStatId);
        }
    }

    /*
    Returns the set of statIds found during a "walk"
     */
    public ExpressionStatIdList getStatIds() {
        return statIds;
    }
}

