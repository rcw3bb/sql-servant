package xyz.ronella.tools.sql.servant.impl

import xyz.ronella.tools.sql.servant.IStatus

class DefaultStatus implements IStatus {

    private boolean isSuccessful
    DefaultStatus(boolean isSuccessful) {
        this.isSuccessful = isSuccessful
    }

    @Override
    boolean isSuccessful() {
        return isSuccessful
    }
}
