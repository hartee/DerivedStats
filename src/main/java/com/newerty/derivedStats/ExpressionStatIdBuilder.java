package com.newerty.derivedStats;

import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;
import java.util.stream.Collectors;

public class ExpressionStatIdBuilder {
    private String statId;
    private String categoryId;
    private Map<String, String> dimensions;

    ExpressionStatIdBuilder setStatId(String statId) {
        this.statId = statId;
        return this;
    }

    ExpressionStatIdBuilder setCategoryId(String categoryId) {
        this.categoryId = categoryId;
        return this;
    }

    ExpressionStatIdBuilder setDimensions(Map<String, String> dimensions) {
        this.dimensions = dimensions;
        return this;
    }

    ExpressionStatIdBuilder addDimension(String label, String value) {
        if (this.dimensions == null) {
            this.dimensions = new HashMap<>();
        }
        this.dimensions.put(label, value);
        return this;
    }

    ExpressionStatIdBuilder from(DerivedExpressionParser.StatIdContext ctx, String defaultCategoryId) {
        DerivedExpressionParser.BaseStatIdContext baseStatIdCtx;

        // get categoryId if present
        if (ctx.qualifiedStatId() != null) {
            this.categoryId = ctx.qualifiedStatId().categoryId().getText();
            baseStatIdCtx = ctx.qualifiedStatId().baseStatId();
        } else {
            this.categoryId = defaultCategoryId;
            baseStatIdCtx = ctx.baseStatId();
        }

        // get statId
        this.statId = baseStatIdCtx.IDENTIFIER().getText();

        // get dimensions if present
        if (baseStatIdCtx.dimensionList() == null) {
            this.dimensions = null;
        } else {
            this.dimensions = baseStatIdCtx.dimensionList().dimensionListItem().stream()
                    .collect(Collectors.toMap(item -> item.IDENTIFIER().getText(), item -> removeQuotes(item.dim_value_clause().DIM_VALUE().getText())));
            this.dimensions = new TreeMap<>(dimensions);
        }
        return this;
    }

    private String removeQuotes(String quotedString) {
        return quotedString.replace("\"", "");
    }

    public ExpressionStatId build() {
        return new ExpressionStatId(statId, categoryId, dimensions);
    }


}
