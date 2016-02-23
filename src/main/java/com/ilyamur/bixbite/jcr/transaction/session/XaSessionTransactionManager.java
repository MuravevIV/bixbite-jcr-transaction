package com.ilyamur.bixbite.jcr.transaction.session;

import org.apache.jackrabbit.api.XASession;

import java.util.ArrayList;
import java.util.List;

import javax.jcr.Session;
import javax.transaction.HeuristicMixedException;
import javax.transaction.HeuristicRollbackException;
import javax.transaction.NotSupportedException;
import javax.transaction.RollbackException;
import javax.transaction.Status;
import javax.transaction.SystemException;
import javax.transaction.UserTransaction;
import javax.transaction.xa.XAResource;


/**
 * XaSessionTransactionManager.
 *
 * @author Ilya_Muravyev
 */
public class XaSessionTransactionManager implements UserTransaction {

    private boolean isActiveTransaction = false;
    private XaResourceUserTransaction userTransaction = null;
    private List<UtxSession> utxSessions = new ArrayList<>();

    public synchronized Session createProxySession(XASession xaSession) {
        if (isActiveTransaction) {
            XAResource xaResource = xaSession.getXAResource();
            if (userTransaction == null) {
                userTransaction = new XaResourceUserTransaction(xaResource, true);
                try {
                    userTransaction.begin();
                } catch (NotSupportedException | SystemException e) {
                    throw new RuntimeException(e);
                }
            } else {
                userTransaction.enlistXaResource(xaResource);
            }
            UtxSession utxSession = new UtxSession(xaSession);
            utxSessions.add(utxSession);
            return utxSession;
        } else {
            return xaSession;
        }
    }

    @Override
    public synchronized void begin() throws NotSupportedException, SystemException {
        isActiveTransaction = true;
    }

    @Override
    public synchronized void commit() throws RollbackException, HeuristicMixedException, HeuristicRollbackException,
            SecurityException, IllegalStateException, SystemException {

        if (userTransaction == null) {
            throw new IllegalStateException("Transaction not active");
        }
        userTransaction.commit();
        cleanup();
    }

    @Override
    public synchronized void rollback() throws IllegalStateException, SecurityException, SystemException {
        if (userTransaction == null) {
            throw new IllegalStateException("Transaction not active");
        }
        userTransaction.rollback();
        cleanup();
    }

    @Override
    public synchronized void setRollbackOnly() throws IllegalStateException, SystemException {
        if (userTransaction == null) {
            throw new IllegalStateException("Transaction not active");
        }
        userTransaction.setRollbackOnly();
    }

    @Override
    public synchronized int getStatus() throws SystemException {
        if (userTransaction == null) {
            return Status.STATUS_NO_TRANSACTION;
        }
        return userTransaction.getStatus();
    }

    @Override
    public synchronized void setTransactionTimeout(int seconds) throws SystemException {
        if (userTransaction != null) {
            userTransaction.setTransactionTimeout(seconds);
        }
    }

    private void cleanup() {
        for (UtxSession utxSession : utxSessions) {
            utxSession.superLogout();
        }
        utxSessions.clear();
        userTransaction = null;
        isActiveTransaction = false;
    }
}
