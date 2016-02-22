package com.ilyamur.bixbite.jcr.transaction.example;

import org.apache.jackrabbit.core.TransientRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.jcr.Repository;
import javax.jcr.Session;

/**
 * First hop example. Logs in to a content repository and prints a
 * status message.
 *
 * @author Ilya_Muravyev
 */
public class FirstHopExample {

    private static final Logger LOG = LoggerFactory.getLogger(FirstHopExample.class);

    /**
     * The main entry point of the example application.
     *
     * @param args command line arguments (ignored)
     * @throws Exception if an error occurs
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
