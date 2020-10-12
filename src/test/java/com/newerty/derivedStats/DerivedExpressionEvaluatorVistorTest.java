package com.newerty.derivedStats;

import org.junit.Test;
import static org.junit.Assert.assertEquals;


import java.util.Collections;

public class DerivedExpressionEvaluatorVistorTest {

    public final static String DUMMY_CATEGORY = "dummy";

    @Test
    public void testSimpleMath() {
        String testSimpleAdd = "foo = 10 + 20";
        DerivedExpressionEvaluator evaluator = DerivedExpressionEvaluator.build(testSimpleAdd, DUMMY_CATEGORY);
        assertEquals(30, evaluator.evaluateList(Collections.emptyMap()), .1);

        String testSimpleMinus = "foo = 30 - 10";
        evaluator = DerivedExpressionEvaluator.build(testSimpleMinus, DUMMY_CATEGORY);
        assertEquals(20, evaluator.evaluateList(Collections.emptyMap()), .1);
    }

    @Test
    public void testSimpleUnary() {
        String testSimpleUnaryPlus = "foo = -10";
        DerivedExpressionEvaluator evaluator = DerivedExpressionEvaluator.build(testSimpleUnaryPlus, DUMMY_CATEGORY);
        assertEquals(-10, evaluator.evaluate(), .1);

        testSimpleUnaryPlus = "foo = 20";
        evaluator = DerivedExpressionEvaluator.build(testSimpleUnaryPlus, DUMMY_CATEGORY);
        assertEquals(20, evaluator.evaluate(), .1);

        String testSimpleUnaryMinus = "foo = +25";
        evaluator = DerivedExpressionEvaluator.build(testSimpleUnaryMinus, DUMMY_CATEGORY);
        assertEquals(25, evaluator.evaluate(), .1);
    }

    @Test
    public void testSimpleMulDiv() {
        String testSimpleMul = "foo = 10 * 30";
        DerivedExpressionEvaluator evaluator = DerivedExpressionEvaluator.build(testSimpleMul, DUMMY_CATEGORY);
        assertEquals(300, evaluator.evaluate(), .1);

        String testSimpleDiv = "foo = 30 / 10";
        evaluator = DerivedExpressionEvaluator.build(testSimpleDiv, DUMMY_CATEGORY);
        assertEquals(3, evaluator.evaluate(), .1);
    }

    @Test
    public void testSimpleGREATER() {
        String testSimpleGREATER = "foo = 20 > 10";
        DerivedExpressionEvaluator evaluator = DerivedExpressionEvaluator.build(testSimpleGREATER, DUMMY_CATEGORY);
        assertEquals(1, evaluator.evaluate(), .1);

        String testSimpleGREATERNot = "foo = 10 > 20";
        evaluator = DerivedExpressionEvaluator.build(testSimpleGREATERNot, DUMMY_CATEGORY);
        assertEquals(0, evaluator.evaluate(), .1);
    }


    @Test
    public void testSimpleAND() {
        String testSimpleAND = "foo = (20 >= 10) && (10 > 5)";
        DerivedExpressionEvaluator evaluator = DerivedExpressionEvaluator.build(testSimpleAND, DUMMY_CATEGORY);
        assertEquals(1, evaluator.evaluate(), .1);

        String testSimpleANDNot = "foo = (20 >= 10) && (10 < 5)";
        evaluator = DerivedExpressionEvaluator.build(testSimpleANDNot, DUMMY_CATEGORY);
        assertEquals(0, evaluator.evaluate(), .1);
    }

    @Test
    public void testSimpleOR() {
        String testSimpleOR = "foo = (20 >= 10) || (10 < 5)";
        DerivedExpressionEvaluator evaluator = DerivedExpressionEvaluator.build(testSimpleOR, DUMMY_CATEGORY);
        assertEquals(1, evaluator.evaluate(), .1);

        String testSimpleORNot = "foo = (20 <= 10) || (10 < 5)";
        evaluator = DerivedExpressionEvaluator.build(testSimpleORNot, DUMMY_CATEGORY);
        assertEquals(0, evaluator.evaluate(), .1);

        testSimpleOR = "foo = (20 <= 10) || (10 > 5)";
        evaluator = DerivedExpressionEvaluator.build(testSimpleOR, DUMMY_CATEGORY);
        assertEquals(1, evaluator.evaluate(), .1);
    }


    @Test
    public void testSimpleParens() {
        String testSimpleParens = "foo = 10 * (30 + 10)";
        DerivedExpressionEvaluator evaluator = DerivedExpressionEvaluator.build(testSimpleParens, DUMMY_CATEGORY);
        assertEquals(400, evaluator.evaluate(), .1);
    }

    @Test
    public void testSimpleLESS() {
        String testSimpleLESS = "foo = 10 < 20";
        DerivedExpressionEvaluator evaluator = DerivedExpressionEvaluator.build(testSimpleLESS, DUMMY_CATEGORY);
        assertEquals(1, evaluator.evaluate(), .1);

        String testSimpleLESSNot = "foo = 20 < 10";
        evaluator = DerivedExpressionEvaluator.build(testSimpleLESSNot, DUMMY_CATEGORY);
        assertEquals(0, evaluator.evaluate(), .1);
    }


    @Test
    public void testSimpleLESSEQ() {
        String testSimpleLESS = "foo = 10 <= 20";
        DerivedExpressionEvaluator evaluator = DerivedExpressionEvaluator.build(testSimpleLESS, DUMMY_CATEGORY);
        assertEquals(1, evaluator.evaluate(), .1);

        String testSimpleLESSEQ = "foo = 10 <= 10";
        evaluator = DerivedExpressionEvaluator.build(testSimpleLESSEQ, DUMMY_CATEGORY);
        assertEquals(1, evaluator.evaluate(), .1);

        String testSimpleLESSNot = "foo = 11 <= 10";
        evaluator = DerivedExpressionEvaluator.build(testSimpleLESSNot, DUMMY_CATEGORY);
        assertEquals(0, evaluator.evaluate(), .1);
    }

    @Test
    public void testSimpleGREATEREQ() {
        String testSimpleGREATER = "foo = 20 >= 10";
        DerivedExpressionEvaluator evaluator = DerivedExpressionEvaluator.build(testSimpleGREATER, DUMMY_CATEGORY);
        assertEquals(1, evaluator.evaluate(), .1);

        String testSimpleGREATEREQ = "foo = 10 >= 10";
        evaluator = DerivedExpressionEvaluator.build(testSimpleGREATEREQ, DUMMY_CATEGORY);
        assertEquals(1, evaluator.evaluate(), .1);

        String testSimpleGREATERNot = "foo = 10 >= 11";
        evaluator = DerivedExpressionEvaluator.build(testSimpleGREATERNot, DUMMY_CATEGORY);
        assertEquals(0, evaluator.evaluate(), .1);
    }


    @Test
    public void testSimpleEQ() {
        String testSimpleEQ = "foo = (20 + 10) == (5 + 5 + 20)";
        DerivedExpressionEvaluator evaluator = DerivedExpressionEvaluator.build(testSimpleEQ, DUMMY_CATEGORY);
        assertEquals(1, evaluator.evaluate(), .1);

        String testSimpleEQNot = "foo = (20 + 10) == (5 + 5 + 99)";
        evaluator = DerivedExpressionEvaluator.build(testSimpleEQNot, DUMMY_CATEGORY);
        assertEquals(0, evaluator.evaluate(), .1);
    }

    @Test
    public void testSimpleNEQ() {
        String testSimpleNEQ = "foo = (20 + 10) != (5 + 5 + 99)";
        DerivedExpressionEvaluator evaluator = DerivedExpressionEvaluator.build(testSimpleNEQ, DUMMY_CATEGORY);
        assertEquals(1, evaluator.evaluate(), .1);

        String testSimpleNEQNot = "foo = (20 + 10) != (5 + 5 + 20)";
        evaluator = DerivedExpressionEvaluator.build(testSimpleNEQNot, DUMMY_CATEGORY);
        assertEquals(0, evaluator.evaluate(), .1);
    }

    @Test
    public void testSimpleSUM() {
        String testSimpleSUM = "foo = SUM(10,20,30)";
        DerivedExpressionEvaluator evaluator = DerivedExpressionEvaluator.build(testSimpleSUM, DUMMY_CATEGORY);
        assertEquals(60, evaluator.evaluate(), .1);
    }

    @Test
    public void testSimpleAVG() {
        String testSimpleAVG = "foo = AVG(10,20,30)";
        DerivedExpressionEvaluator evaluator = DerivedExpressionEvaluator.build(testSimpleAVG, DUMMY_CATEGORY);
        assertEquals(20, evaluator.evaluate(), .1);
    }

    @Test
    public void testSimpleMIN() {
        String testSimpleMIN = "foo = MIN(10,20,30)";
        DerivedExpressionEvaluator evaluator = DerivedExpressionEvaluator.build(testSimpleMIN, DUMMY_CATEGORY);
        assertEquals(10, evaluator.evaluate(), .1);
    }

    @Test
    public void testSimpleMAX() {
        String testSimpleMAX = "foo = MAX(10,20,30)";
        DerivedExpressionEvaluator evaluator = DerivedExpressionEvaluator.build(testSimpleMAX, DUMMY_CATEGORY);
        assertEquals(30, evaluator.evaluate(), .1);
    }


    @Test
    public void testSimpleTernary() {
        String testSimpleTernary = "foo = (20 >= 10) ? 100 : -100";
        DerivedExpressionEvaluator evaluator = DerivedExpressionEvaluator.build(testSimpleTernary, DUMMY_CATEGORY);
        assertEquals(100, evaluator.evaluate(), .1);

        // any positive value evaluates to True
        testSimpleTernary = "foo = (10) ? 100 : -100";
        evaluator = DerivedExpressionEvaluator.build(testSimpleTernary, DUMMY_CATEGORY);
        assertEquals(100, evaluator.evaluate(), .1);

        testSimpleTernary = "foo = (10 > SUM(10,20,30)) ? 10 : 99";
        evaluator = DerivedExpressionEvaluator.build(testSimpleTernary, DUMMY_CATEGORY);
        assertEquals(99, evaluator.evaluate(), .1);
    }
}
