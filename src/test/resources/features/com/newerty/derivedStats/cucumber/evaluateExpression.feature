@Parse
Feature: Evaluate expression

  Scenario Outline: Should correctly evaluate the SIMPLE expression
    When Evaluating a valid <expression>
    Then The expression should evaluate to  <expected>
    Examples:
      | expression        | expected |
      | 10 + 20           | 30       |
      | 1 - (10 - -100)   | -109     |
      | 1 / 10 * 10 / 100 | 0.01     |
      | 1 + 10 - 100      | -89      |
      | 1 - 10 + 100      | 91       |
      | 1 - 10 - -100     | 91       |
      | 1 / 1 * 100       | 100      |
      | 1 / (1 * 100)     | 0.01     |
      | 1 * 1 /100        | 0.01     |
      | 3+4               | 7        |
      | 3    +   4        | 7        |
      | 3+   -4           | -1       |
      | 3 + (-4)          | -1       |


  Scenario Outline: Should correctly evaluate the PRECEDENCE expression
    When Evaluating a valid <expression>
    Then The expression should evaluate to  <expected>
    Examples:
      | expression   | expected |
      | 3+4*4        | 19       |
      | 3 <  4*4     | 1        |
      | 3 > 4* 4     | 0        |
      | (3+   4) * 4 | 28       |


  Scenario Outline: Should correctly evaluate the SIGNED expression
    When Evaluating a valid <expression>
    Then The expression should evaluate to  <expected>
    Examples:
      | expression    | expected |
      | -2.02         | -2.02    |
      | +2.02         | 2.02     |
      | +2.02 + -1.01 | 1.01     |
      | -2.02 - +2.01 | -4.03    |
      | +2.02 + +1.01 | 3.03     |
      | 6*-1.1        | -6.6     |
      | 6*+1.1        | 6.6      |


  Scenario Outline: Should correctly evaluate the EQUALITY expression
    When Evaluating a valid <expression>
    Then The expression should evaluate to  <expected>
    Examples:
      | expression | expected |
      | 5 == 5     | 1        |
      | 5 != 5     | 0        |
      | 3 == -3    | 0        |


  Scenario Outline: Should correctly evaluate the LOGICAL expression
    When Evaluating a valid <expression>
    Then The expression should evaluate to  <expected>
    Examples:
      | expression   | expected |
      | 5 && 5       | 1        |
      | 5 && (3 + 2) | 1        |
      | 5 && 2       | 1        |
      | 0 && 0       | 0        |
      | -1 && -8     | 1        |
      | 1  \|\| 3    | 1        |
      | -8  \|\| 0   | 1        |
      | -8  \|\| -2  | 1        |
      | 0  \|\| 0    | 0        |


  Scenario Outline: Should correctly evaluate the RELATIONAL expression
    When Evaluating a valid <expression>
    Then The expression should evaluate to  <expected>
    Examples:
      | expression | expected |
      | 5 <= 5     | 1        |
      | 5 >= 5     | 1        |
      | 5 < 5      | 0        |
      | 5 > 5      | 0        |
      | 10 > 5     | 1        |


  Scenario Outline: Should correctly evaluate the TERNARY expression
    When Evaluating a valid <expression>
    Then The expression should evaluate to  <expected>
    Examples:
      | expression                                                                | expected |
      | 10 > 20 ? 3 : 7                                                           | 7        |
      | 10 > 20 ? (3) : (7)                                                       | 7        |
      | (99 > 10.0) ? (99 >= 100.0 ? 99 - 100 : 99) : (99 > 0.0 ? 99 : 0.0)       | 99       |
      | 99 > 10.0 ? 99 >= 100.0 ? 99 - 100 : 99 : 99 > 0.0 ? 99 : 0.0             | 99       |
      | (52.1125 > 11.12) && (52.1125 > -24.0) ? 11.12 >= 1.0? 75.9: 21.0 : 11.12 | 75.9     |
      | 150 > 10.0 ? 150 >= 100.0 ? 150 - 100 : 150 : 150 > 0.0 ? 150 : 0.0       | 50       |


  Scenario Outline: Should correctly evaluate the PARENTHETICAL expression
    When Evaluating a valid <expression>
    Then The expression should evaluate to  <expected>
    Examples:
      | expression | expected |
      | -(-0.2)    | 0.2      |
      | 1-(-0.2)   | 1.2      |
      | 1+(-0.2)   | 0.8      |
      | +(2.2)     | 2.2      |


  Scenario Outline: Should correctly evaluate the PATHOLOGICAL expression
    When Evaluating a valid <expression>
    Then The expression should evaluate to  <expected>
    Examples:
      | expression                                                                 | expected |
      | 5 > 3 > 2                                                                  | 0        |
      | ((1 + 2) * 1 / (2 + 1) + 5 * 3 * -15.23) / 56.123 - 1 + 1111132 + 4432 * 0 | 1111127  |
      | SUM(AVG(3.0,4.0,5.0),1.0,2.0)                                              | 7.0      |


  Scenario Outline: Should correctly evaluate the weird NUMERIC expression
    When Evaluating a valid <expression>
    Then The expression should evaluate to  <expected>
    Examples:
      | expression            | expected  |
      | 1 / Infinity          | 0.0       |
      | 1 / -Infinity         | -0.0      |
      | 1 - Infinity          | -Infinity |
      | 1 * Infinity          | Infinity  |
      | 1 - Infinity          | -Infinity |
      | 1 - Infinity          | -Infinity |
      | 1 + Infinity          | Infinity  |
      | 1 / NaN               | NaN       |
      | 1 * NaN               | NaN       |
      | 1 - NaN               | NaN       |
      | 1 + NaN               | NaN       |
      | Infinity / NaN        | NaN       |
      | Infinity - Infinity   | NaN       |
      | Infinity + -Infinity  | NaN       |
      | Infinity + Infinity   | Infinity  |
      | -Infinity + -Infinity | -Infinity |
      | 1 == NaN              | NaN       |
      | NaN == NaN            | NaN       |
      | 1 != NaN              | NaN       |
      | NaN != NaN            | NaN       |

