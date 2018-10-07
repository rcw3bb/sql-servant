package xyz.ronella.tools.sql.servant.impl

import xyz.ronella.tools.sql.servant.IStatus

/**
 * The default implementation of IStatus.
 *
 * @author Ron Webb
 * @since 2018-10-07
 */
class DefaultStatus implements IStatus {

    private boolean isSuccessful

    /**
     * Creates an instance of DefaultStatus
     *
     * @param isSuccessful The status to return if successful or not.
     */
    DefaultStatus(boolean isSuccessful) {
        this.isSuccessful = isSuccessful
    }

    /**
     * Check if the status of success.
     *
     * @return true if successful.
     */
    @Override
    boolean isSuccessful() {
        return isSuccessful
    }
}
