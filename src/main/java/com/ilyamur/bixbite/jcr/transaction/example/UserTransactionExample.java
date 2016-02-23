package com.ilyamur.bixbite.jcr.transaction.example;

import com.ilyamur.bixbite.jcr.transaction.JcrValueStore;
import com.ilyamur.bixbite.jcr.transaction.JcrValueStoreImpl;
import com.ilyamur.bixbite.jcr.transaction.UserTransactionImpl;
import org.apache.jackrabbit.api.XASession;
import org.apache.jackrabbit.core.TransientRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.jcr.Repository;
import javax.jcr.RepositoryException;
import javax.jcr.Session;
import javax.jcr.SimpleCredentials;

/**
 * TransactionExample.
 *
 * @author Ilya_Muravyev
 */
public class UserTransactionExample {

    private static final Logger LOG = LoggerFactory.getLogger(UserTransactionExample.class);

    /**
     * main().
     */
    public static void main(String[] args) throws Exception {
        Repository repository = new TransientRepository();
        JcrValueStore store = new JcrValueStoreImpl();

        Session sessionO = getSession(repository);
        try {
            String messageO = store.load(sessionO);
            LOG.info("Session O load message: " + messageO);
        } finally {
            sessionO.logout();
        }

        Session sessionA = getSession(repository);
        try {
            String messageA = "testA";
            store.save(sessionA, messageA);
            LOG.info("Session A save message: " + messageA);
        } finally {
            sessionA.logout();
        }

        Session sessionB = getSession(repository);
        UserTransactionImpl userTransaction = new UserTransactionImpl((XASession) sessionB);
        Exception txException = null;
        userTransaction.begin();
        try {
            try {
                String messageB = "testB";
                store.save(sessionB, messageB);
                LOG.info("Session B save message: " + messageB);
            } catch (Exception e) {
                txException = e;
            }
            if (txException == null) {
                LOG.info("Roll back session B changes");
                userTransaction.rollback();
            } else {
                userTransaction.rollback();
                throw txException;
            }
        } finally {
            sessionB.logout();
        }

        Session sessionC = getSession(repository);
        try {
            String messageC = store.load(sessionC);
            LOG.info("Session C load message: " + messageC);
        } finally {
            sessionC.logout();
        }
    }

    private static Session getSession(Repository repository) throws RepositoryException {
        return repository.login(new SimpleCredentials("admin", "admin".toCharArray()));
    }
}
