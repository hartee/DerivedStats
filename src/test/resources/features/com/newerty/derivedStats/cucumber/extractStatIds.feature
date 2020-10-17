@Parse
Feature: Extract StatIds

  Scenario: Should extract dependent statIds from expression
    When Get dependent statIds for the following expression: "catA.weapon_accuracy{map:"deathstar",weapon:"blaster"} = catB.kills{map:"deathstar",weapon:"blaster"} / catC.shots{map:"deathstar",weapon:"blaster"}"
    Then The get statIds response should contain these values
      | catB.kills{map:"deathstar",weapon:"blaster"} |
      | catC.shots{map:"deathstar",weapon:"blaster"} |


  Scenario: Should extract dependent statIds from expression
    When Get dependent statIds for the following expression: "foo = SUM(kills{map:"deathstar"})+ shots{map:"*"}"
    Then The get statIds response should contain these values
      | dummy.kills{map:"deathstar"} |
      | dummy.shots{map:"*"}         |

  Scenario: Should extract dependent statIds from expression
    When Get dependent statIds for the following expression: "foo = a + b + (b + c) + c + (d + a)"
    Then The get statIds response should contain these values
      | dummy.a |
      | dummy.b |
      | dummy.c |
      | dummy.d |
    And The get statIds response should contain this many statIds
      | 4 |

  Scenario Outline: Invalid expressions aren't allowed
    When Get dependent statIds for the following invalid expression: <expression>
    Then Expression evaluation exception is thrown of type "com.newerty.derivedStats.ExpressionEvaluationException" containing <message>
    Examples:
      | expression                  | message              |
      | foo = XYZ(kills{map : "*"}) | mismatched input '(' |
      | foo = kills(map : "dust2"}  | mismatched input '(' |
      | foo = shots + 3.0)          | extraneous input ')' |
      | foo = shots => 3.0          | mismatched input '=' |
      | foo = shots =< 3.0          | mismatched input '=' |


