package xyz.ronella.tools.sql.servant;

/**
 * The class that can does the validation.
 *
 * @param <TYPE> The type of the class to be validated.
 *
 * @author Ron Webb
 * @sinee 1.2.0
 */
public class Validate<TYPE> {
    private final IValidator<TYPE> validator;

    /**
     * Creates an instance of the Validate.
     *
     * @param validator An implementation of IValidator.
     */
    public Validate(final IValidator<TYPE> validator) {
        this.validator = validator;
    }

    /**
     * Does the actual checking of the expected type.
     *
     * @param item The type expected by the validate.
     * @return Returns true if everything is good.
     */
    public boolean check(TYPE item) {
        return (validator == null || validator.test(item));
    }
}
