package xyz.ronella.tools.sql.servant

/**
 * The blueprint of implementing the status of the task.
 *
 * @author Ron Webb
 * @since 2018-10-07
 */
interface IStatus {

    /**
     * Must hold the actual success logic.
     *
     * @return Must return true if successful.
     */
    boolean isSuccessful()
}