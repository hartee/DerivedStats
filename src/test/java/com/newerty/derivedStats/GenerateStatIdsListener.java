package com.newerty.derivedStats;

import com.newerty.derivedStats.DerivedExpressionBaseListener;
import com.newerty.derivedStats.DerivedExpressionParser;
import com.newerty.derivedStats.ExpressionStatId;
import com.newerty.derivedStats.ExpressionStatIdBuilder;
import org.antlr.v4.runtime.tree.ParseTree;
import org.antlr.v4.runtime.tree.ParseTreeWalker;


public class GenerateStatIdsListener extends DerivedExpressionBaseListener {

    private final String categoryId;
    private ExpressionStatId statId;

    public GenerateStatIdsListener(DerivedExpressionParser parser, String categoryId) {
        this.categoryId = categoryId;
        ParseTree tree = parser.statId();
        ParseTreeWalker walker = new ParseTreeWalker();
        walker.walk(this, tree);
    }

    @Override
    public void enterStatId(DerivedExpressionParser.StatIdContext ctx) {
        statId = new ExpressionStatIdBuilder()
                .from(ctx, categoryId)
                .build();
    }

    public ExpressionStatId getStatId(String categoryId) {
        if (this.statId.getCategoryId() == null) {
            return new ExpressionStatId(this.statId.getStatId(), categoryId, this.statId.getDimensions());
        } else {
            return this.statId;
        }
    }


}

