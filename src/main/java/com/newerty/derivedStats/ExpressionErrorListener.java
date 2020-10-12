package com.newerty.derivedStats;

import org.antlr.v4.runtime.*;

import java.util.Collections;
import java.util.List;

public class ExpressionErrorListener extends BaseErrorListener {

    public final static ExpressionErrorListener INSTANCE = new ExpressionErrorListener();

    @Override
    public void syntaxError(Recognizer<?,?> recognizer,
                            Object offendingSymbol,
                            int line,
                            int charPositionInLine,
                            String msg,
                            RecognitionException e) {

        if (recognizer instanceof Parser) {
                List<String> stack = ((Parser)recognizer).getRuleInvocationStack();
                Collections.reverse(stack);
                throw new ExpressionEvaluationException("line " + line + ":" + charPositionInLine + " " + msg, stack);
        } else if (recognizer instanceof Lexer) {
            throw new ExpressionEvaluationException(msg);
        } else {
            throw new ExpressionEvaluationException("Unrecoverable error during expression evaluation. " + msg);
        }
    }
}