package io.github.syst3ms.skriptparser;

import io.github.syst3ms.skriptparser.lang.Expression;
import io.github.syst3ms.skriptparser.log.SkriptLogger;
import io.github.syst3ms.skriptparser.parsing.ParserState;
import io.github.syst3ms.skriptparser.parsing.SyntaxParser;
import io.github.syst3ms.skriptparser.types.PatternType;
import io.github.syst3ms.skriptparser.types.TypeManager;
import org.junit.Test;

import static org.junit.Assert.*;

public class ClaudeDebugTest {
    static {
        TestRegistration.register();
    }

    @Test
    public void testClaudePattern() {
        var logger = new SkriptLogger(true);
        var parserState = new ParserState();

        // Test case 1: with space between numbers
        String input1 = "claude test 1 2";
        var type = TypeManager.getByClass(String.class).orElseThrow();
        var patternType = new PatternType<>(type, true);

        System.out.println("Testing: " + input1);
        var result1 = SyntaxParser.parseExpression(input1, patternType, parserState, logger);

        if (result1.isEmpty()) {
            System.out.println("FAILED to parse: " + input1);
            fail("Failed to parse: " + input1);
        } else {
            System.out.println("SUCCESS: " + input1);
            Expression<?> expr = result1.get();
            System.out.println("  Parsed expression: " + expr.getClass().getSimpleName());
        }

        // Test case 2: with "of" between numbers
        logger.clearErrors();
        logger.clearLogs();
        String input2 = "claude test 1 of 2";
        System.out.println("\nTesting: " + input2);
        var result2 = SyntaxParser.parseExpression(input2, patternType, parserState, logger);

        if (result2.isEmpty()) {
            System.out.println("FAILED to parse: " + input2);
            fail("Failed to parse: " + input2);
        } else {
            System.out.println("SUCCESS: " + input2);
            Expression<?> expr = result2.get();
            System.out.println("  Parsed expression: " + expr.getClass().getSimpleName());
        }
    }
}
