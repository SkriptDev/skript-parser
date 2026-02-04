package io.github.syst3ms.skriptparser.expressions;

import io.github.syst3ms.skriptparser.Parser;
import io.github.syst3ms.skriptparser.lang.Expression;
import io.github.syst3ms.skriptparser.lang.Literal;
import io.github.syst3ms.skriptparser.lang.TriggerContext;
import io.github.syst3ms.skriptparser.log.ErrorType;
import io.github.syst3ms.skriptparser.parsing.ParseContext;
import io.github.syst3ms.skriptparser.types.Type;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.function.Supplier;

public class ExprTypeValues implements Expression<Object> {

    static {
        Parser.getMainRegistration().newExpression(ExprTypeValues.class, Object.class, false,
                "[all] [type] values of %*type%")
            .name("Type Values")
            .description("Get all values of a certain type.")
            .examples("loop all type values of SomeType:")
            .since("1.0.0")
            .register();
    }

    private Type<?> type;
    private Supplier<? extends Iterator<?>> supplier;

    @Override
    public boolean init(Expression<?>[] expressions, int matchedPattern, ParseContext parseContext) {
        Object o = ((Literal<?>) expressions[0]).getSingle().orElse(null);
        if (o instanceof Type<?> t) {
            this.type = t;
            Supplier<? extends Iterator<?>> supplier = this.type.getSupplier();
            if (supplier == null) {
                parseContext.getLogger().error("Type " + this.type.getBaseName() + " has no supplier and cannot get all values.", ErrorType.SEMANTIC_ERROR);
                return false;
            }
            this.supplier = supplier;
            return true;
        } else {
            return false;
        }
    }

    @Override
    public Object[] getValues(TriggerContext ctx) {
        List<Object> listOfObjects = new ArrayList<>();
        this.supplier.get().forEachRemaining(listOfObjects::add);
        return listOfObjects.toArray((Object[]) Array.newInstance(this.type.getTypeClass(), listOfObjects.size()));
    }

    @Override
    public Iterator<?> iterator(TriggerContext ctx) {
        return this.supplier.get();
    }

    @Override
    public String toString(TriggerContext ctx, boolean debug) {
        return "all values of " + this.type.getBaseName();
    }

}
