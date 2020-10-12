package com.newerty.derivedStats;

import com.google.common.base.Preconditions;

import java.util.Collections;
import java.util.Map;
import java.util.Optional;
import java.util.TreeMap;
import java.util.stream.Collectors;


public class ExpressionStatId implements Comparable<ExpressionStatId> {

    private final String statId;
    private final String categoryId;
    private Map<String, String> dimensions = Collections.emptyMap();

    private final boolean wildcarded;

    public ExpressionStatId(String statId, String categoryId, Map<String, String> dimensions) {
        this.statId = Preconditions.checkNotNull(statId);
        this.categoryId = Preconditions.checkNotNull(categoryId);
        this.dimensions = (dimensions == null) ? Collections.emptyMap() : dimensions;
        this.wildcarded = checkWildcard();
    }

    public ExpressionStatId withDimensions(Map<String, String> dimensions) {
        return new ExpressionStatId(statId, categoryId, dimensions);
    }


    boolean checkWildcard() {
        if ((this.dimensions == null) || (this.dimensions.isEmpty())) {
            return false;
        } else {
            return getDimensions().entrySet()
                    .stream()
                    .anyMatch(x -> x.getValue().equals("*"));
        }

    }

    public String getStatId() {
        return statId;
    }

    public String getCategoryId() {
        return categoryId;
    }

    public Map<String, String> getDimensions() {
        return dimensions;
    }

    public boolean isWildcarded() {
        return wildcarded;
    }

    public void setDimensions(Map<String, String> dimensions) {
        this.dimensions = (dimensions == null) ? Collections.emptyMap() : dimensions;
    }

    @Override
    public int compareTo(ExpressionStatId other) {
        String id = categoryId + "." + statId;
        String otherId = other.categoryId + "." + other.statId;

        return id.compareTo(otherId);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ExpressionStatId that = (ExpressionStatId) o;

        if (!statId.equals(that.statId)) return false;
        if (categoryId != null ? !categoryId.equals(that.categoryId) : that.categoryId != null) return false;
        return dimensions != null ? dimensions.equals(that.dimensions) : that.dimensions == null;
    }

    public boolean keysEquals(ExpressionStatId that) {
        if (!statId.equals(that.statId)) return false;
        if (categoryId != null ? !categoryId.equals(that.categoryId) : that.categoryId != null) return false;
        return dimensions.keySet().equals(that.dimensions.keySet());
    }

    @Override
    public int hashCode() {
        int result = statId.hashCode();
        result = 31 * result + (categoryId != null ? categoryId.hashCode() : 0);
        result = 31 * result + (dimensions != null ? dimensions.hashCode() : 0);
        return result;
    }


    @Override
    public String toString() {
        Optional<String> catPhrase = Optional.empty();
        Optional<String> dimPhrase = Optional.empty();

        if (categoryId != null) {
            catPhrase = Optional.of(categoryId + ".");
        }

        if ((dimensions != null) && !dimensions.isEmpty()) {
            dimPhrase = Optional.of("{" + new TreeMap<String, String>(dimensions).entrySet().stream()
                    .map(n -> dimString(n))
                    .collect(Collectors.joining(",")) + "}");

        }
        return catPhrase.orElse("") + statId + dimPhrase.orElse("");
    }

    private static String dimString(Map.Entry<String, String> entry) {
        return entry.getKey() + ":" + "\"" + entry.getValue() + "\"";
    }


}
