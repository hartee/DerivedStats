package com.newerty.derivedStats;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class ExpressionStatIdList {
    ExpressionStatId derivedStatId;
    Set<ExpressionStatId> dependentStatIds;

    ExpressionStatIdList() {
        this.dependentStatIds = new HashSet<>();
    }

    public ExpressionStatId getDerivedStatId() {
        return derivedStatId;
    }

    public List<ExpressionStatId> getDependentStatIds() {
        return new ArrayList<>(dependentStatIds);
    }
}
