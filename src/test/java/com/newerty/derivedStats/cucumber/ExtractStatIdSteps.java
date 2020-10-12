package com.newerty.derivedStats.cucumber;

import com.newerty.derivedStats.DerivedExpressionEvaluator;
import com.newerty.derivedStats.ExpressionStatIdList;

import io.cucumber.java.en.And;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import io.cucumber.datatable.DataTable;

import java.util.*;

import static com.newerty.derivedStats.TestHelpers.checkError;
import static org.junit.Assert.assertEquals;

public class ExtractStatIdSteps {

    private ExpressionStatIdList statIds;

    private Throwable expressionEvaluationError;


    @When("^Get dependent statIds for the following expression: \"(.*)\"$")
    public void getStatIdsForTheFollowingExpression(String expr) throws Throwable {
        DerivedExpressionEvaluator evaluator = DerivedExpressionEvaluator.build(expr, "dummy");
        statIds = evaluator.getStatIds();
    }

    @Then("^The get statIds response should contain these values$")
    public void theGetStatIdsResponseShouldContainTheseValues(DataTable expectedTable) throws Throwable {
        ArrayList<String> actualValues = new ArrayList<>();

        statIds.getDependentStatIds().forEach(v -> actualValues.add(v.toString()));
        compareStatIds(expectedTable.asList(), actualValues);
    }

    private static void compareStatIds(List<String> actual, List<String> expected) {
        assertEquals(new HashSet<>(expected), new HashSet<>(actual));
    }

    @And("^The get statIds response should contain this many statIds$")
    public void theGetStatIdsResponseShouldContainThisManyStatIds(String numIds) throws Throwable {
        assertEquals(Double.valueOf(numIds), statIds.getDependentStatIds().size(), .1);
    }

    @When("^Get dependent statIds for the following invalid expression: (.*)$")
    public void getDependentStatIdsForTheFollowingInvalidExpressionExpression(String expr) throws Throwable {
        try {
            DerivedExpressionEvaluator evaluator = DerivedExpressionEvaluator.build(expr, "dummy");
            statIds = evaluator.getStatIds();
        } catch (Exception e) {
            expressionEvaluationError = e;
        }
    }

    @Then("^Expression evaluation exception is thrown of type \"([^\"]*)\" containing (.*)$")
    public void checkExpressionEvaluationError(String errorType, String message) throws ClassNotFoundException {
        checkError(errorType, message, expressionEvaluationError, false);
    }
}