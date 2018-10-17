package xyz.ronella.tools.sql.servant

class Validate<TYPE> {
    private IValidator validator

    public Validate(IValidator validator) {
        this.validator = validator
    }

    boolean check(TYPE item) {
        return (validator ? validator.test(item) : true)
    }
}
