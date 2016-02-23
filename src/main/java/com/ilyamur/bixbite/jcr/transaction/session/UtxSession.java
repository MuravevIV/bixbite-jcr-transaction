package com.ilyamur.bixbite.jcr.transaction.session;

import org.apache.jackrabbit.api.XASession;

/**
 * UtxSession.
 *
 * @author Ilya_Muravyev
 */
class UtxSession extends AbstractSessionProxy {

    UtxSession(XASession xaSession) {
        super(xaSession);
    }

    @Override
    public void logout() {
        // noop
    }

    void superLogout() {
        super.logout();
    }
}
