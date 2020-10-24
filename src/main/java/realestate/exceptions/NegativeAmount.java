package realestate.exceptions;

import org.apache.commons.lang3.exception.ContextedRuntimeException;
import org.apache.commons.lang3.exception.DefaultExceptionContext;

public class NegativeAmount extends ContextedRuntimeException {
    public NegativeAmount(double amount, String entity) {
        super(
            "Amount cannot be negative",
            null,
            new DefaultExceptionContext()
                .addContextValue("Amount", amount)
                .addContextValue("Entity", entity)
        );
    }
}
