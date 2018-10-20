package xyz.ronella.tools.sql.servant

import java.util.function.Predicate

/**
 * The template to be used when implementing a validator that can be processed by the Validate class.
 *
 * @param <TYPE> The expected type to be validated.
 *
 * @author Ron Webb
 * @since 1.2.0
 */
interface IValidator<TYPE> extends Predicate<TYPE> {

}