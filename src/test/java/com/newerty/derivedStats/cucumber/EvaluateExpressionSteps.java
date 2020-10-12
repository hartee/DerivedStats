package com.newerty.derivedStats.cucumber;

import com.newerty.derivedStats.DerivedExpressionEvaluator;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

import java.util.Collections;


import static org.junit.Assert.assertEquals;

public class EvaluateExpressionSteps {

    private Double result;

    @When("^Evaluating a valid (.*)$")
    public void evaluateExpression(String expr) throws Throwable {
        String expression = "foo =" + expr;
        DerivedExpressionEvaluator evaluator = DerivedExpressionEvaluator.build(expression, "dummy");
        result = evaluator.evaluateList(Collections.emptyMap());
    }

    @Then("^The expression should evaluate to (.*)$")
    public void validateResult(String expected) throws Throwable {
        assertEquals(Double.valueOf(expected), result, .1);
    }

}

