package io.github.syst3ms.skriptparser.lang.entries;

import io.github.syst3ms.skriptparser.file.FileElement;
import io.github.syst3ms.skriptparser.lang.Expression;
import io.github.syst3ms.skriptparser.lang.Variable;
import io.github.syst3ms.skriptparser.log.ErrorType;
import io.github.syst3ms.skriptparser.log.SkriptLogger;
import io.github.syst3ms.skriptparser.parsing.ParserState;
import io.github.syst3ms.skriptparser.parsing.SyntaxParser;
import io.github.syst3ms.skriptparser.types.PatternType;

import java.util.Optional;

public class ExpressionLoader<T> extends OptionLoader {

    private final Class<T> typeClass;

    public ExpressionLoader(String key, Class<T> classType, boolean multiple, boolean optional) {
        super(key, multiple, optional);
        this.typeClass = classType;
    }

    @Override
    public boolean loadEntry(SectionConfiguration config, FileElement element, ParserState parserState, SkriptLogger logger) {
        // We will use the loaded values later
        if (!super.loadEntry(config, element, parserState, logger))
            return false;

        logger.setLine(element.getLine() - 1);
        String s;
        if (isMultiple()) {
            Optional<String[]> stringList = config.getStringList(this.key);
            if (stringList.isEmpty()) {
                logger.error("Key '" + this.key + "' does not exist.", ErrorType.SEMANTIC_ERROR);
                return false;
            }
            s = String.join(", ", stringList.get());
        } else {
            Optional<String> string = config.getString(this.key);
            if (string.isEmpty()) {
                logger.error("Key '" + this.key + "' does not exist.", ErrorType.SEMANTIC_ERROR);
                return false;
            }
            s = string.get();
        }
        Optional<String> string = config.getString(this.key);
        if (string.isEmpty()) {
            logger.error("Key '" + this.key + "' does not exist.", ErrorType.SEMANTIC_ERROR);
            return false;
        }

        PatternType<Object> type = isMultiple() ? SyntaxParser.OBJECTS_PATTERN_TYPE : SyntaxParser.OBJECT_PATTERN_TYPE;
        Optional<? extends Expression<?>> exprOptional = SyntaxParser.parseExpression(s, type, parserState, logger);
        if (exprOptional.isEmpty()) {
            logger.error("Couldn't parse '" + s + "' as an expression", ErrorType.SEMANTIC_ERROR);
            return false;
        }

        Expression<?> expression = exprOptional.get();
        if (expression instanceof Variable<?> var) {
            var.setReturnType(this.typeClass);
        }

        config.getData().put(this.key, expression);
        return true;
    }

}
