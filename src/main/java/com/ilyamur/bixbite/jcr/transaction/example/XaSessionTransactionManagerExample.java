package com.ilyamur.bixbite.jcr.transaction.example;

import com.ilyamur.bixbite.jcr.transaction.JcrValueStore;
import com.ilyamur.bixbite.jcr.transaction.JcrValueStoreImpl;
import com.ilyamur.bixbite.jcr.transaction.session.XaResourceUserTransaction;
import com.ilyamur.bixbite.jcr.transaction.session.XaSessionTransactionManager;
import org.apache.jackrabbit.api.XASession;
import org.apache.jackrabbit.core.TransientRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.jcr.Repository;
import javax.jcr.RepositoryException;
import javax.jcr.Session;
import javax.jcr.SimpleCredentials;

/**
 * XaSessionTransactionManagerExample.
 *
 * @author Ilya_Muravyev
 */
public class XaSessionTransactionManagerExample {

    private static final Logger LOG = LoggerFactory.getLogger(UserTransactionExample.class);

    /**
     * main().
     */
    public static void main(String[] args) throws Exception {
        Repository repository = new TransientRepository();
        JcrValueStore store = new JcrValueStoreImpl();

        XaSessionTransactionManager txManager = new XaSessionTransactionManager();

        Session sessionO = getSession(repository, txManager);
        try {
            String messageO = store.load(sessionO);
            LOG.info("Session O load message: " + messageO);
        } finally {
            sessionO.logout();
        }

        Session sessionA = getSession(repository, txManager);
        try {
            String messageA = "testA";
            store.save(sessionA, messageA);
            LOG.info("Session A save message: " + messageA);
        } finally {
            sessionA.logout();
        }

        LOG.info("Begin global transaction");
        txManager.begin();

        Session sessionB = getSession(repository, txManager);
        try {
            String messageB = "testB";
            store.save(sessionB, messageB);
            LOG.info("Session B save message: " + messageB);
        } finally {
            sessionB.logout();
        }

        LOG.info("Roll back global transaction");
        txManager.rollback();

        Session sessionC = getSession(repository, txManager);
        try {
            String messageC = store.load(sessionC);
            LOG.info("Session C load message: " + messageC);
        } finally {
            sessionC.logout();
        }
    }

    private static Session getSession(Repository repository,
                                      XaSessionTransactionManager txManager) throws RepositoryException {

        Session session = repository.login(new SimpleCredentials("admin", "admin".toCharArray()));
        return txManager.createProxySession((XASession) session);
    }
}
