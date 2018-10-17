package xyz.ronella.tools.sql.servant

import java.util.function.Predicate

interface IValidator<TYPE> extends Predicate<TYPE> {

}