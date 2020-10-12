package com.newerty.derivedStats;

import com.google.common.collect.ImmutableMap;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.assertEquals;

public class DerivedExpressionEvaluatorTest {

    public final static String DUMMY_CATEGORY = "dummy";

    @Rule
    public ExpectedException thrown = ExpectedException.none();


    @Test
    public void getStatIds() {
        String testExpr = "foo = bar";
        DerivedExpressionEvaluator evaluator = DerivedExpressionEvaluator.build(testExpr, DUMMY_CATEGORY);
        ExpressionStatIdList statIds = evaluator.getStatIds();
        assertEquals("foo", statIds.derivedStatId.getStatId());
        assertEquals(1, statIds.dependentStatIds.size());
    }

    @Test
    public void getStatIdsWithWildcards() {
        String testExpr = "total = SUM(kills{map:\"*\"})";
        DerivedExpressionEvaluator evaluator = DerivedExpressionEvaluator.build(testExpr, DUMMY_CATEGORY);

        ExpressionStatIdList statIds = evaluator.getStatIds();
        assertEquals("total", statIds.derivedStatId.getStatId());

        assertEquals(statIds.getDependentStatIds().get(0),
                new ExpressionStatIdBuilder()
                        .setStatId("kills")
                        .setCategoryId(DUMMY_CATEGORY)
                        .addDimension("map", "*")
                        .build());
    }

    @Test
    public void simpleExpressionWithWildcardsTwoDimensions() {
        String testExpr = "foo = kills{map:\"*\"} / shots{map:\"*\"}";
        DerivedExpressionEvaluator evaluator = DerivedExpressionEvaluator.build(testExpr, DUMMY_CATEGORY);

        Map<ExpressionStatId, Double> values = new HashMap<>();
        values.put(new ExpressionStatId("kills", DUMMY_CATEGORY, ImmutableMap.of("map", "foo")), 1.0);
        values.put(new ExpressionStatId("shots", DUMMY_CATEGORY, ImmutableMap.of("map", "foo")), 20.0);
        values.put(new ExpressionStatId("shots", DUMMY_CATEGORY, ImmutableMap.of("map", "bar")), 50.0);


        assertEquals(1d/20d, evaluator.evaluate(values, ImmutableMap.of("map", "foo")), 1e-6);
    }

    @Test
    public void simpleExpressionWithWildcardsTwoDimensions2() {
        String testExpr = "foo{map:\"*\"} = kills{map:\"*\"} / shots{map:\"*\"}";
        DerivedExpressionEvaluator evaluator = DerivedExpressionEvaluator.build(testExpr, DUMMY_CATEGORY);

        Map<ExpressionStatId, Double> values = new HashMap<>();
        values.put(new ExpressionStatId("kills", DUMMY_CATEGORY, ImmutableMap.of("map", "foo")), 1.0);
        values.put(new ExpressionStatId("shots", DUMMY_CATEGORY, ImmutableMap.of("map", "foo")), 20.0);
        values.put(new ExpressionStatId("shots", DUMMY_CATEGORY, ImmutableMap.of("map", "bar")), 50.0);


        assertEquals(1d/20d, evaluator.evaluate(values, ImmutableMap.of("map", "foo")), 1e-6);
    }

    static String normalizeString(String rawString) {
        return rawString;
    }

    @Test
    public void constantExpression() {
        String testExpr = "foo = 99";
        DerivedExpressionEvaluator evaluator = DerivedExpressionEvaluator.build(testExpr, DUMMY_CATEGORY);

        ExpressionStatIdList statIds = evaluator.getStatIds();
        Map<ExpressionStatId, Double> values = Collections.emptyMap();

        assertEquals(normalizeString("foo = 99"), normalizeString(testExpr));
        assertEquals(99, evaluator.evaluate(values, ImmutableMap.of()), .1);
    }


    @Test
    public void simpleExpression() {
        String testExpr = "foo = bar +  keep +   calm";
        DerivedExpressionEvaluator evaluator = DerivedExpressionEvaluator.build(testExpr, DUMMY_CATEGORY);

        ExpressionStatIdList statIds = evaluator.getStatIds();

        Map<ExpressionStatId, Double> values = new HashMap<>();
        ExpressionStatId bar = statIds.getDependentStatIds().get(0);
        values.put(bar, 10d);
        ExpressionStatId keep = statIds.getDependentStatIds().get(1);
        values.put(keep, 20d);
        ExpressionStatId calm = statIds.getDependentStatIds().get(2);
        values.put(calm, 30d);

        assertEquals(60, evaluator.evaluate(values, ImmutableMap.of()), .1);
    }


    @Test
    public void complexArithmeticExpressionNoStatIds() {
        String testExpr = "foo = 10 + 20 * ( -5 + 15  )";
        DerivedExpressionEvaluator evaluator = DerivedExpressionEvaluator.build(testExpr, DUMMY_CATEGORY);
        assertEquals(210, evaluator.evaluate(), .1);
    }

    @Test
    public void complexArithmeticExpression() {
        String testExpr = "foo = A + B * (C + D)";
        DerivedExpressionEvaluator evaluator = DerivedExpressionEvaluator.build(testExpr, DUMMY_CATEGORY);

        ExpressionStatIdList statIds = evaluator.getStatIds();

        Map<ExpressionStatId, Double> values = new HashMap<>();
        List<ExpressionStatId> dependentStatIds = statIds.getDependentStatIds();
        dependentStatIds.forEach(s -> {
            switch (s.getStatId()) {
                case "A":
                    values.put(s, 25d);
                    break;

                case "B":
                    values.put(s, (double) -4);
                    break;

                case "C":
                    values.put(s, 5d);
                    break;

                case "D":
                    values.put(s, (double) -30);
            }
        });

        assertEquals(125, evaluator.evaluate(values, ImmutableMap.of()), .1);
    }


    @Test
    public void getAggregateStatIds() {
        String testExpr = "foo = SUM(kills{map:\"degaba\"}, kills{map:\"deathstar\"}, kills{map:\"alderaan\"})";
        DerivedExpressionEvaluator evaluator = DerivedExpressionEvaluator.build(testExpr, DUMMY_CATEGORY);

        ExpressionStatIdList statIds = evaluator.getStatIds();

        assertEquals("foo", statIds.derivedStatId.getStatId());
        assertEquals(3, statIds.dependentStatIds.size());
    }


    @Test
    public void simpleAggregateWithWildcards() {
        String testExpr = "foo = SUM(kills{map:\"*\"})";
        DerivedExpressionEvaluator evaluator = DerivedExpressionEvaluator.build(testExpr, DUMMY_CATEGORY);

        Map<ExpressionStatId, Double> values = new HashMap<>();
        values.put(new ExpressionStatId("kills", DUMMY_CATEGORY, ImmutableMap.of("map", "foo")), 20.0);
        values.put(new ExpressionStatId("kills", DUMMY_CATEGORY, ImmutableMap.of("map", "bar")), 10.0);


        assertEquals(30, evaluator.evaluate(values), .1);
    }

    @Test
    public void complexExpression() {
        String testExpr = "catA.weapon_accuracy{map:\"deathstar\",weapon:\"blaster\"} = catB.kills{map:\"deathstar\",weapon:\"blaster\"} / catC.shots{map:\"deathstar\",weapon:\"blaster\"}";
        DerivedExpressionEvaluator evaluator = DerivedExpressionEvaluator.build(testExpr, DUMMY_CATEGORY);

        ExpressionStatIdList statIds = evaluator.getStatIds();

        Map<ExpressionStatId, Double> values = new HashMap<>();
        ExpressionStatId kills = statIds.getDependentStatIds().get(0);
        ExpressionStatId shots = statIds.getDependentStatIds().get(1);
        values.put(kills, 10d);
        values.put(shots, 50d);

        assertEquals(.2, evaluator.evaluate(values), .01);
    }


    @Test
    public void getStatIdsFails() {
        String testExpr = "foo == bar";

        // expression has too many '='
        thrown.expect(ExpressionEvaluationException.class);

        DerivedExpressionEvaluator evaluator = DerivedExpressionEvaluator.build(testExpr, DUMMY_CATEGORY);

        ExpressionStatIdList statIds = evaluator.getStatIds();
    }

    @Test
    public void getStatIdsFailsDuringLexer() {
        String testExpr = "foo = bar ^ kat";

        // grammar doesn't recognize '^'
        thrown.expect(ExpressionEvaluationException.class);
        thrown.expectMessage("token recognition error at: '^'");

        DerivedExpressionEvaluator evaluator = DerivedExpressionEvaluator.build(testExpr, DUMMY_CATEGORY);

        ExpressionStatIdList statIds = evaluator.getStatIds();
    }

    @Test
    public void longExpression() {
        String testExpr = "a = ((b + c) * b / (c + b) + 5 * 3 * -15.23) / 56.123 - 1 + 1111132 + 4432 * 0";
        DerivedExpressionEvaluator evaluator = DerivedExpressionEvaluator.build(testExpr, DUMMY_CATEGORY);
        ExpressionStatIdList statIds = evaluator.getStatIds();

        Map<ExpressionStatId, Double> values = new HashMap<>();
        ExpressionStatId bar = statIds.getDependentStatIds().get(0);
        values.put(bar, 1d);
        ExpressionStatId cat = statIds.getDependentStatIds().get(1);
        values.put(cat, 2d);

        assertEquals(1111127, evaluator.evaluate(values), .1);
    }

    @Test
    public void pathologicallyLong() {
        DerivedExpressionEvaluator evaluator = DerivedExpressionEvaluator.build(SILLY_LONG_STRING, DUMMY_CATEGORY);
        assertEquals(1340, evaluator.evaluate(Collections.emptyMap()), .1);
    }

    private static final String SILLY_LONG_STRING =
            new StringBuilder()
                    .append("foo = 1.0 + 1.0 + 1.0 + 1.0 + 1.0 + 1.0 + 1.0 + 1.0 + 1.0 + 1.0 + 1.0 + 1.0 + 1.0 + 1.0 + 1.0 + 1.0 + 1.0 + 1.0 + 1.0 + 1.0 +")
                    .append("1.0 + 1.0 + 1.0 + 1.0 + 1.0 + 1.0 + 1.0 + 1.0 + 1.0 + 1.0 + 1.0 + 1.0 + 1.0 + 1.0 + 1.0 + 1.0 + 1.0 + 1.0 + 1.0 + 1.0 +")
                    .append("1.0 + 1.0 + 1.0 + 1.0 + 1.0 + 1.0 + 1.0 + 1.0 + 1.0 + 1.0 + 1.0 + 1.0 + 1.0 + 1.0 + 1.0 + 1.0 + 1.0 + 1.0 + 1.0 + 1.0 +")
                    .append("1.0 + 1.0 + 1.0 + 1.0 + 1.0 + 1.0 + 1.0 + 1.0 + 1.0 + 1.0 + 1.0 + 1.0 + 1.0 + 1.0 + 1.0 + 1.0 + 1.0 + 1.0 + 1.0 + 1.0 +")
                    .append("1.0 + 1.0 + 1.0 + 1.0 + 1.0 + 1.0 + 1.0 + 1.0 + 1.0 + 1.0 + 1.0 + 1.0 + 1.0 + 1.0 + 1.0 + 1.0 + 1.0 + 1.0 + 1.0 + 1.0 +")
                    .append("1.0 + 1.0 + 1.0 + 1.0 + 1.0 + 1.0 + 1.0 + 1.0 + 1.0 + 1.0 + 1.0 + 1.0 + 1.0 + 1.0 + 1.0 + 1.0 + 1.0 + 1.0 + 1.0 + 1.0 +")
                    .append("1.0 + 1.0 + 1.0 + 1.0 + 1.0 + 1.0 + 1.0 + 1.0 + 1.0 + 1.0 + 1.0 + 1.0 + 1.0 + 1.0 + 1.0 + 1.0 + 1.0 + 1.0 + 1.0 + 1.0 +")
                    .append("1.0 + 1.0 + 1.0 + 1.0 + 1.0 + 1.0 + 1.0 + 1.0 + 1.0 + 1.0 + 1.0 + 1.0 + 1.0 + 1.0 + 1.0 + 1.0 + 1.0 + 1.0 + 1.0 + 1.0 +")
                    .append("1.0 + 1.0 + 1.0 + 1.0 + 1.0 + 1.0 + 1.0 + 1.0 + 1.0 + 1.0 + 1.0 + 1.0 + 1.0 + 1.0 + 1.0 + 1.0 + 1.0 + 1.0 + 1.0 + 1.0 +")
                    .append("1.0 + 1.0 + 1.0 + 1.0 + 1.0 + 1.0 + 1.0 + 1.0 + 1.0 + 1.0 + 1.0 + 1.0 + 1.0 + 1.0 + 1.0 + 1.0 + 1.0 + 1.0 + 1.0 + 1.0 +")
                    .append("1.0 + 1.0 + 1.0 + 1.0 + 1.0 + 1.0 + 1.0 + 1.0 + 1.0 + 1.0 + 1.0 + 1.0 + 1.0 + 1.0 + 1.0 + 1.0 + 1.0 + 1.0 + 1.0 + 1.0 +")
                    .append("1.0 + 1.0 + 1.0 + 1.0 + 1.0 + 1.0 + 1.0 + 1.0 + 1.0 + 1.0 + 1.0 + 1.0 + 1.0 + 1.0 + 1.0 + 1.0 + 1.0 + 1.0 + 1.0 + 1.0 +")
                    .append("1.0 + 1.0 + 1.0 + 1.0 + 1.0 + 1.0 + 1.0 + 1.0 + 1.0 + 1.0 + 1.0 + 1.0 + 1.0 + 1.0 + 1.0 + 1.0 + 1.0 + 1.0 + 1.0 + 1.0 +")
                    .append("1.0 + 1.0 + 1.0 + 1.0 + 1.0 + 1.0 + 1.0 + 1.0 + 1.0 + 1.0 + 1.0 + 1.0 + 1.0 + 1.0 + 1.0 + 1.0 + 1.0 + 1.0 + 1.0 + 1.0 +")
                    .append("1.0 + 1.0 + 1.0 + 1.0 + 1.0 + 1.0 + 1.0 + 1.0 + 1.0 + 1.0 + 1.0 + 1.0 + 1.0 + 1.0 + 1.0 + 1.0 + 1.0 + 1.0 + 1.0 + 1.0 +")
                    .append("1.0 + 1.0 + 1.0 + 1.0 + 1.0 + 1.0 + 1.0 + 1.0 + 1.0 + 1.0 + 1.0 + 1.0 + 1.0 + 1.0 + 1.0 + 1.0 + 1.0 + 1.0 + 1.0 + 1.0 +")
                    .append("1.0 + 1.0 + 1.0 + 1.0 + 1.0 + 1.0 + 1.0 + 1.0 + 1.0 + 1.0 + 1.0 + 1.0 + 1.0 + 1.0 + 1.0 + 1.0 + 1.0 + 1.0 + 1.0 + 1.0 +")
                    .append("1.0 + 1.0 + 1.0 + 1.0 + 1.0 + 1.0 + 1.0 + 1.0 + 1.0 + 1.0 + 1.0 + 1.0 + 1.0 + 1.0 + 1.0 + 1.0 + 1.0 + 1.0 + 1.0 + 1.0 +")
                    .append("1.0 + 1.0 + 1.0 + 1.0 + 1.0 + 1.0 + 1.0 + 1.0 + 1.0 + 1.0 + 1.0 + 1.0 + 1.0 + 1.0 + 1.0 + 1.0 + 1.0 + 1.0 + 1.0 + 1.0 +")
                    .append("1.0 + 1.0 + 1.0 + 1.0 + 1.0 + 1.0 + 1.0 + 1.0 + 1.0 + 1.0 + 1.0 + 1.0 + 1.0 + 1.0 + 1.0 + 1.0 + 1.0 + 1.0 + 1.0 + 1.0 +")
                    .append("1.0 + 1.0 + 1.0 + 1.0 + 1.0 + 1.0 + 1.0 + 1.0 + 1.0 + 1.0 + 1.0 + 1.0 + 1.0 + 1.0 + 1.0 + 1.0 + 1.0 + 1.0 + 1.0 + 1.0 +")
                    .append("1.0 + 1.0 + 1.0 + 1.0 + 1.0 + 1.0 + 1.0 + 1.0 + 1.0 + 1.0 + 1.0 + 1.0 + 1.0 + 1.0 + 1.0 + 1.0 + 1.0 + 1.0 + 1.0 + 1.0 +")
                    .append("1.0 + 1.0 + 1.0 + 1.0 + 1.0 + 1.0 + 1.0 + 1.0 + 1.0 + 1.0 + 1.0 + 1.0 + 1.0 + 1.0 + 1.0 + 1.0 + 1.0 + 1.0 + 1.0 + 1.0 +")
                    .append("1.0 + 1.0 + 1.0 + 1.0 + 1.0 + 1.0 + 1.0 + 1.0 + 1.0 + 1.0 + 1.0 + 1.0 + 1.0 + 1.0 + 1.0 + 1.0 + 1.0 + 1.0 + 1.0 + 1.0 +")
                    .append("1.0 + 1.0 + 1.0 + 1.0 + 1.0 + 1.0 + 1.0 + 1.0 + 1.0 + 1.0 + 1.0 + 1.0 + 1.0 + 1.0 + 1.0 + 1.0 + 1.0 + 1.0 + 1.0 + 1.0 +")
                    .append("1.0 + 1.0 + 1.0 + 1.0 + 1.0 + 1.0 + 1.0 + 1.0 + 1.0 + 1.0 + 1.0 + 1.0 + 1.0 + 1.0 + 1.0 + 1.0 + 1.0 + 1.0 + 1.0 + 1.0 +")
                    .append("1.0 + 1.0 + 1.0 + 1.0 + 1.0 + 1.0 + 1.0 + 1.0 + 1.0 + 1.0 + 1.0 + 1.0 + 1.0 + 1.0 + 1.0 + 1.0 + 1.0 + 1.0 + 1.0 + 1.0 +")
                    .append("1.0 + 1.0 + 1.0 + 1.0 + 1.0 + 1.0 + 1.0 + 1.0 + 1.0 + 1.0 + 1.0 + 1.0 + 1.0 + 1.0 + 1.0 + 1.0 + 1.0 + 1.0 + 1.0 + 1.0 +")
                    .append("1.0 + 1.0 + 1.0 + 1.0 + 1.0 + 1.0 + 1.0 + 1.0 + 1.0 + 1.0 + 1.0 + 1.0 + 1.0 + 1.0 + 1.0 + 1.0 + 1.0 + 1.0 + 1.0 + 1.0 +")
                    .append("1.0 + 1.0 + 1.0 + 1.0 + 1.0 + 1.0 + 1.0 + 1.0 + 1.0 + 1.0 + 1.0 + 1.0 + 1.0 + 1.0 + 1.0 + 1.0 + 1.0 + 1.0 + 1.0 + 1.0 +")
                    .append("1.0 + 1.0 + 1.0 + 1.0 + 1.0 + 1.0 + 1.0 + 1.0 + 1.0 + 1.0 + 1.0 + 1.0 + 1.0 + 1.0 + 1.0 + 1.0 + 1.0 + 1.0 + 1.0 + 1.0 +")
                    .append("1.0 + 1.0 + 1.0 + 1.0 + 1.0 + 1.0 + 1.0 + 1.0 + 1.0 + 1.0 + 1.0 + 1.0 + 1.0 + 1.0 + 1.0 + 1.0 + 1.0 + 1.0 + 1.0 + 1.0 +")
                    .append("1.0 + 1.0 + 1.0 + 1.0 + 1.0 + 1.0 + 1.0 + 1.0 + 1.0 + 1.0 + 1.0 + 1.0 + 1.0 + 1.0 + 1.0 + 1.0 + 1.0 + 1.0 + 1.0 + 1.0 +")
                    .append("1.0 + 1.0 + 1.0 + 1.0 + 1.0 + 1.0 + 1.0 + 1.0 + 1.0 + 1.0 + 1.0 + 1.0 + 1.0 + 1.0 + 1.0 + 1.0 + 1.0 + 1.0 + 1.0 + 1.0 +")
                    .append("1.0 + 1.0 + 1.0 + 1.0 + 1.0 + 1.0 + 1.0 + 1.0 + 1.0 + 1.0 + 1.0 + 1.0 + 1.0 + 1.0 + 1.0 + 1.0 + 1.0 + 1.0 + 1.0 + 1.0 +")
                    .append("1.0 + 1.0 + 1.0 + 1.0 + 1.0 + 1.0 + 1.0 + 1.0 + 1.0 + 1.0 + 1.0 + 1.0 + 1.0 + 1.0 + 1.0 + 1.0 + 1.0 + 1.0 + 1.0 + 1.0 +")
                    .append("1.0 + 1.0 + 1.0 + 1.0 + 1.0 + 1.0 + 1.0 + 1.0 + 1.0 + 1.0 + 1.0 + 1.0 + 1.0 + 1.0 + 1.0 + 1.0 + 1.0 + 1.0 + 1.0 + 1.0 +")
                    .append("1.0 + 1.0 + 1.0 + 1.0 + 1.0 + 1.0 + 1.0 + 1.0 + 1.0 + 1.0 + 1.0 + 1.0 + 1.0 + 1.0 + 1.0 + 1.0 + 1.0 + 1.0 + 1.0 + 1.0 +")
                    .append("1.0 + 1.0 + 1.0 + 1.0 + 1.0 + 1.0 + 1.0 + 1.0 + 1.0 + 1.0 + 1.0 + 1.0 + 1.0 + 1.0 + 1.0 + 1.0 + 1.0 + 1.0 + 1.0 + 1.0 +")
                    .append("1.0 + 1.0 + 1.0 + 1.0 + 1.0 + 1.0 + 1.0 + 1.0 + 1.0 + 1.0 + 1.0 + 1.0 + 1.0 + 1.0 + 1.0 + 1.0 + 1.0 + 1.0 + 1.0 + 1.0 +")
                    .append("1.0 + 1.0 + 1.0 + 1.0 + 1.0 + 1.0 + 1.0 + 1.0 + 1.0 + 1.0 + 1.0 + 1.0 + 1.0 + 1.0 + 1.0 + 1.0 + 1.0 + 1.0 + 1.0 + 1.0 +")
                    .append("1.0 + 1.0 + 1.0 + 1.0 + 1.0 + 1.0 + 1.0 + 1.0 + 1.0 + 1.0 + 1.0 + 1.0 + 1.0 + 1.0 + 1.0 + 1.0 + 1.0 + 1.0 + 1.0 + 1.0 +")
                    .append("1.0 + 1.0 + 1.0 + 1.0 + 1.0 + 1.0 + 1.0 + 1.0 + 1.0 + 1.0 + 1.0 + 1.0 + 1.0 + 1.0 + 1.0 + 1.0 + 1.0 + 1.0 + 1.0 + 1.0 +")
                    .append("1.0 + 1.0 + 1.0 + 1.0 + 1.0 + 1.0 + 1.0 + 1.0 + 1.0 + 1.0 + 1.0 + 1.0 + 1.0 + 1.0 + 1.0 + 1.0 + 1.0 + 1.0 + 1.0 + 1.0 +")
                    .append("1.0 + 1.0 + 1.0 + 1.0 + 1.0 + 1.0 + 1.0 + 1.0 + 1.0 + 1.0 + 1.0 + 1.0 + 1.0 + 1.0 + 1.0 + 1.0 + 1.0 + 1.0 + 1.0 + 1.0 +")
                    .append("1.0 + 1.0 + 1.0 + 1.0 + 1.0 + 1.0 + 1.0 + 1.0 + 1.0 + 1.0 + 1.0 + 1.0 + 1.0 + 1.0 + 1.0 + 1.0 + 1.0 + 1.0 + 1.0 + 1.0 +")
                    .append("1.0 + 1.0 + 1.0 + 1.0 + 1.0 + 1.0 + 1.0 + 1.0 + 1.0 + 1.0 + 1.0 + 1.0 + 1.0 + 1.0 + 1.0 + 1.0 + 1.0 + 1.0 + 1.0 + 1.0 +")
                    .append("1.0 + 1.0 + 1.0 + 1.0 + 1.0 + 1.0 + 1.0 + 1.0 + 1.0 + 1.0 + 1.0 + 1.0 + 1.0 + 1.0 + 1.0 + 1.0 + 1.0 + 1.0 + 1.0 + 1.0 +")
                    .append("1.0 + 1.0 + 1.0 + 1.0 + 1.0 + 1.0 + 1.0 + 1.0 + 1.0 + 1.0 + 1.0 + 1.0 + 1.0 + 1.0 + 1.0 + 1.0 + 1.0 + 1.0 + 1.0 + 1.0 +")
                    .append("1.0 + 1.0 + 1.0 + 1.0 + 1.0 + 1.0 + 1.0 + 1.0 + 1.0 + 1.0 + 1.0 + 1.0 + 1.0 + 1.0 + 1.0 + 1.0 + 1.0 + 1.0 + 1.0 + 1.0 +")
                    .append("1.0 + 1.0 + 1.0 + 1.0 + 1.0 + 1.0 + 1.0 + 1.0 + 1.0 + 1.0 + 1.0 + 1.0 + 1.0 + 1.0 + 1.0 + 1.0 + 1.0 + 1.0 + 1.0 + 1.0 +")
                    .append("1.0 + 1.0 + 1.0 + 1.0 + 1.0 + 1.0 + 1.0 + 1.0 + 1.0 + 1.0 + 1.0 + 1.0 + 1.0 + 1.0 + 1.0 + 1.0 + 1.0 + 1.0 + 1.0 + 1.0 +")
                    .append("1.0 + 1.0 + 1.0 + 1.0 + 1.0 + 1.0 + 1.0 + 1.0 + 1.0 + 1.0 + 1.0 + 1.0 + 1.0 + 1.0 + 1.0 + 1.0 + 1.0 + 1.0 + 1.0 + 1.0 +")
                    .append("1.0 + 1.0 + 1.0 + 1.0 + 1.0 + 1.0 + 1.0 + 1.0 + 1.0 + 1.0 + 1.0 + 1.0 + 1.0 + 1.0 + 1.0 + 1.0 + 1.0 + 1.0 + 1.0 + 1.0 +")
                    .append("1.0 + 1.0 + 1.0 + 1.0 + 1.0 + 1.0 + 1.0 + 1.0 + 1.0 + 1.0 + 1.0 + 1.0 + 1.0 + 1.0 + 1.0 + 1.0 + 1.0 + 1.0 + 1.0 + 1.0 +")
                    .append("1.0 + 1.0 + 1.0 + 1.0 + 1.0 + 1.0 + 1.0 + 1.0 + 1.0 + 1.0 + 1.0 + 1.0 + 1.0 + 1.0 + 1.0 + 1.0 + 1.0 + 1.0 + 1.0 + 1.0 +")
                    .append("1.0 + 1.0 + 1.0 + 1.0 + 1.0 + 1.0 + 1.0 + 1.0 + 1.0 + 1.0 + 1.0 + 1.0 + 1.0 + 1.0 + 1.0 + 1.0 + 1.0 + 1.0 + 1.0 + 1.0 +")
                    .append("1.0 + 1.0 + 1.0 + 1.0 + 1.0 + 1.0 + 1.0 + 1.0 + 1.0 + 1.0 + 1.0 + 1.0 + 1.0 + 1.0 + 1.0 + 1.0 + 1.0 + 1.0 + 1.0 + 1.0 +")
                    .append("1.0 + 1.0 + 1.0 + 1.0 + 1.0 + 1.0 + 1.0 + 1.0 + 1.0 + 1.0 + 1.0 + 1.0 + 1.0 + 1.0 + 1.0 + 1.0 + 1.0 + 1.0 + 1.0 + 1.0 +")
                    .append("1.0 + 1.0 + 1.0 + 1.0 + 1.0 + 1.0 + 1.0 + 1.0 + 1.0 + 1.0 + 1.0 + 1.0 + 1.0 + 1.0 + 1.0 + 1.0 + 1.0 + 1.0 + 1.0 + 1.0 +")
                    .append("1.0 + 1.0 + 1.0 + 1.0 + 1.0 + 1.0 + 1.0 + 1.0 + 1.0 + 1.0 + 1.0 + 1.0 + 1.0 + 1.0 + 1.0 + 1.0 + 1.0 + 1.0 + 1.0 + 1.0 +")
                    .append("1.0 + 1.0 + 1.0 + 1.0 + 1.0 + 1.0 + 1.0 + 1.0 + 1.0 + 1.0 + 1.0 + 1.0 + 1.0 + 1.0 + 1.0 + 1.0 + 1.0 + 1.0 + 1.0 + 1.0 +")
                    .append("1.0 + 1.0 + 1.0 + 1.0 + 1.0 + 1.0 + 1.0 + 1.0 + 1.0 + 1.0 + 1.0 + 1.0 + 1.0 + 1.0 + 1.0 + 1.0 + 1.0 + 1.0 + 1.0 + 1.0 +")
                    .append("1.0 + 1.0 + 1.0 + 1.0 + 1.0 + 1.0 + 1.0 + 1.0 + 1.0 + 1.0 + 1.0 + 1.0 + 1.0 + 1.0 + 1.0 + 1.0 + 1.0 + 1.0 + 1.0 + 1.0 +")
                    .append("1.0 + 1.0 + 1.0 + 1.0 + 1.0 + 1.0 + 1.0 + 1.0 + 1.0 + 1.0 + 1.0 + 1.0 + 1.0 + 1.0 + 1.0 + 1.0 + 1.0 + 1.0 + 1.0 + 1.0 +")
                    .append("1.0 + 1.0 + 1.0 + 1.0 + 1.0 + 1.0 + 1.0 + 1.0 + 1.0 + 1.0 + 1.0 + 1.0 + 1.0 + 1.0 + 1.0 + 1.0 + 1.0 + 1.0 + 1.0 + 1.0 +")
                    .append("1.0 + 1.0 + 1.0 + 1.0 + 1.0 + 1.0 + 1.0 + 1.0 + 1.0 + 1.0 + 1.0 + 1.0 + 1.0 + 1.0 + 1.0 + 1.0 + 1.0 + 1.0 + 1.0 + 1.0 ")
                    .toString();


}
