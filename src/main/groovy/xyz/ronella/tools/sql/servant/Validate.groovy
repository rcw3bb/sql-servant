package xyz.ronella.tools.sql.servant

/**
 * The class that can does the validation.
 *
 * @param <TYPE> The type of the class to be validated.
 *
 * @author Ron Webb
 * @sinee 1.2.0
 */
class Validate<TYPE> {
    private IValidator<TYPE> validator

    /**
     * Creates an instance of the Validate.
     *
     * @param validator An implementation of IValidator.
     */
    Validate(IValidator<TYPE> validator) {
        this.validator = validator
    }

    /**
     * Does the actual checking of the expected type.
     *
     * @param item The type expected by the validate.
     * @return Returns true if everything is good.
     */
    boolean check(TYPE item) {
        return (validator ? validator.test(item) : true)
    }
}
