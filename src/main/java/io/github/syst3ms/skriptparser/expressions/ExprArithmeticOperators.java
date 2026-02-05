package io.github.syst3ms.skriptparser.expressions;

import io.github.syst3ms.skriptparser.Parser;
import io.github.syst3ms.skriptparser.lang.Expression;
import io.github.syst3ms.skriptparser.lang.Literal;
import io.github.syst3ms.skriptparser.lang.TriggerContext;
import io.github.syst3ms.skriptparser.log.ErrorType;
import io.github.syst3ms.skriptparser.parsing.ParseContext;
import io.github.syst3ms.skriptparser.registration.PatternInfos;
import io.github.syst3ms.skriptparser.util.DoubleOptional;

import java.util.Optional;

/**
 * Various arithmetic expressions, including addition, subtraction, multiplication, division and exponentiation.
 * Notes :
 * <ul>
 *      <li>All of the operations will accommodate for the type of the two operands.
 *          <ul>
 *              <li>Two operands of the same type will yield a result of that type, except in the following special cases
 *                  <ul>
 *                      <li>Trying to divide anything by 0 will return {@literal 0} regardless of the original types.</li>
 *                  </ul>
 *              </li>
 *              <li>Adding a decimal type to an integer type will yield a decimal result.</li>
 *              <li>If any of the operands is an arbitrary precision number, the result will be of arbitrary precision</li>
 *          </ul>
 *      </li>
 *      <li>0<sup>0</sup> is defined to be 1</li>
 * </ul>
 *
 * @author Syst3ms
 * @name Arithmetic Operators
 * @pattern %number%[ ]+[ ]%number%
 * @pattern %number%[ ]-[ ]%number%
 * @pattern %number%[ ]*[ ]%number%
 * @pattern %number%[ ]/[ ]%number%
 * @pattern %number%[ ]^[ ]%number%
 * @since ALPHA
 */
public class ExprArithmeticOperators implements Expression<Number> {
    public static final PatternInfos<Operator> PATTERNS = new PatternInfos<>(new Object[][]{
        {"%number%[ ]+[ ]%number%", Operator.PLUS},
        {"%number%[ ]-[ ]%number%", Operator.MINUS},
        {"%number%[ ]*[ ]%number%", Operator.MULT},
        {"%number%[ ]/[ ]%number%", Operator.DIV},
        {"%number%[ ]^[ ]%number%", Operator.EXP},
    });

    static {
        Parser.getMainRegistration().newExpression(ExprArithmeticOperators.class, Number.class, true,
                PATTERNS.getPatterns())
            .name("Arithmetic Operators")
            .description("Performs arithmetic operations on two numbers.")
            .since("1.0.0")
            .register();
    }

    private Expression<? extends Number> first, second;
    private Operator op;

    @SuppressWarnings("unchecked")
    @Override
    public boolean init(Expression<?>[] exprs, int matchedPattern, ParseContext parseContext) {
        first = (Expression<? extends Number>) exprs[0];
        second = (Expression<? extends Number>) exprs[1];
        op = PATTERNS.getInfo(matchedPattern);
        if (op == Operator.DIV && second instanceof Literal) {
            Optional<? extends Number> value = ((Literal<? extends Number>) second).getSingle();
            if (value.filter(n -> n.doubleValue() == 0).isPresent()) {
                parseContext.getLogger().error(
                    "Cannot divide by 0!",
                    ErrorType.SEMANTIC_ERROR,
                    "Make sure the expression/variable you want to divide with does not represent 0, as dividing by 0 results in mathematical issues"
                );
                return false;
            }
        }
        return true;
    }

    @Override
    public Number[] getValues(TriggerContext ctx) {
        return DoubleOptional.ofOptional(first.getSingle(ctx), second.getSingle(ctx))
            .map(f -> (Number) f, s -> (Number) s)
            .mapToOptional((f, s) -> new Number[]{op.calculate(f, s)})
            .orElse(new Number[0]);
    }

    @Override
    public String toString(TriggerContext ctx, boolean debug) {
        return first.toString(ctx, debug) + " " + op + " " + second.toString(ctx, debug);
    }

    public enum Operator {
        PLUS('+') {
            @Override
            public Number calculate(Number left, Number right) {
                return switch (left) {
                    case Long ignored -> left.longValue() + right.longValue();
                    case Integer ignored -> left.intValue() + right.intValue();
                    case Double ignored -> left.doubleValue() + right.doubleValue();
                    default -> left.floatValue() + right.floatValue();
                };
            }
        },
        MINUS('-') {
            @Override
            public Number calculate(Number left, Number right) {
                return switch (left) {
                    case Long ignored -> left.longValue() - right.longValue();
                    case Integer ignored -> left.intValue() - right.intValue();
                    case Double ignored -> left.doubleValue() - right.doubleValue();
                    default -> left.floatValue() - right.floatValue();
                };
            }
        },
        MULT('*') {
            @Override
            public Number calculate(Number left, Number right) {
                return switch (left) {
                    case Long ignored -> left.longValue() * right.longValue();
                    case Integer ignored -> left.intValue() * right.intValue();
                    case Double ignored -> left.doubleValue() * right.doubleValue();
                    default -> left.floatValue() * right.floatValue();
                };
            }
        },
        DIV('/') {
            @Override
            public Number calculate(Number left, Number right) {
                return switch (left) {
                    case Long ignored -> left.longValue() / right.longValue();
                    case Integer ignored -> left.intValue() / right.intValue();
                    case Double ignored -> left.doubleValue() / right.doubleValue();
                    default -> left.floatValue() / right.floatValue();
                };
            }
        },
        EXP('^') {
            @Override
            public Number calculate(Number left, Number right) {
                return switch (left) {
                    case Long ignored -> left.longValue() ^ right.longValue();
                    default -> left.intValue() ^ right.intValue();
                };
            }
        };

        public final char sign;

        Operator(char sign) {
            this.sign = sign;
        }

        public abstract Number calculate(Number left, Number right);

        @Override
        public String toString() {
            return String.valueOf(sign);
        }
    }

}
