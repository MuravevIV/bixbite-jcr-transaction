package com.ilyamur.bixbite.jcr.transaction;

import org.apache.jackrabbit.core.TransientRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.jcr.Repository;
import javax.jcr.Session;

/**
 * Application.
 *
 * @author Ilya_Muravyev
 */
public class Application {

    private static final Logger LOG = LoggerFactory.getLogger(Application.class);

    /**
     * Main method.
     */
    public static void main(String[] args) throws Exception {
        Repository repository = new TransientRepository();
        Session session = repository.login();
        try {
            String user = session.getUserID();
            String name = repository.getDescriptor(Repository.REP_NAME_DESC);
            String txSupport = repository.getDescriptor(Repository.OPTION_TRANSACTIONS_SUPPORTED);
            LOG.info("Logged in as " + user + " to a " + name + " repository. "
                    + "Is transactions supported?: " + txSupport);
        } finally {
            session.logout();
        }
    }
}
