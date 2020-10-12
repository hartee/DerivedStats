package com.newerty.derivedStats;

import org.junit.Test;
import static org.junit.Assert.assertEquals;

public class ExpressionStatIdTest {

    @Test
    public void simpleStatId() {
        ExpressionStatId weapon_accuracy = new ExpressionStatIdBuilder()
                .setStatId("weapon_accuracy")
                .setCategoryId("catA")
                .addDimension("B", "deathstar")
                .addDimension("C", "blaster")
                .addDimension("A", "bounty_hunter")
                .build();

        // test toString()
        assertEquals("catA.weapon_accuracy{A:\"bounty_hunter\",B:\"deathstar\",C:\"blaster\"}", weapon_accuracy.toString());
    }
}
