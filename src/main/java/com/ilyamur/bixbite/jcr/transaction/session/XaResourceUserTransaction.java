package com.ilyamur.bixbite.jcr.transaction.session;

import java.util.HashMap;
import java.util.Map;
import javax.transaction.HeuristicMixedException;
import javax.transaction.HeuristicRollbackException;
import javax.transaction.NotSupportedException;
import javax.transaction.RollbackException;
import javax.transaction.Status;
import javax.transaction.SystemException;
import javax.transaction.UserTransaction;
import javax.transaction.xa.XAException;
import javax.transaction.xa.XAResource;
import javax.transaction.xa.Xid;

/**
 * Internal {@link javax.transaction.UserTransaction} implementation.
 */
@SuppressWarnings("deprecation")
public class XaResourceUserTransaction implements UserTransaction {

    /**
     * Global transaction id counter.
     */
    private static byte counter = 0;

    /**
     * The XAResources map.
     */
    private Map<XAResource, Xid> xaResources = new HashMap<>();

    /**
     * Status.
     */
    private int status = Status.STATUS_NO_TRANSACTION;

    private boolean distributedThreadAccess = false;

    /**
     * Create a new instance of this class. Takes a XAResource as parameter.
     */
    public XaResourceUserTransaction(XAResource xaResource) {
        this(xaResource, false);
    }

    /**
     * Create a new instance of this class.
     */
    public XaResourceUserTransaction(boolean distributedThreadAccess) {
        counter++;
        this.distributedThreadAccess = distributedThreadAccess;
    }

    /**
     * Create a new instance of this class. Takes a XAResource as parameter.
     */
    public XaResourceUserTransaction(XAResource xaResource, boolean distributedThreadAccess) {
        this(distributedThreadAccess);
        enlistXaResource(xaResource);
    }

    /**
     * Enlists the given XAResource to this UserTransaction.
     */
    public void enlistXaResource(XAResource xaResource) {
        xaResources.put(xaResource, new XidImpl(counter));
    }

    /**
     * @see javax.transaction.UserTransaction#begin()
     */
    public void begin() throws NotSupportedException, SystemException {
        if (status != Status.STATUS_NO_TRANSACTION) {
            throw new IllegalStateException("Transaction already active");
        }

        try {
            for (XAResource resource : xaResources.keySet()) {
                XidImpl xid = (XidImpl) xaResources.get(resource);
                resource.start(xid, XAResource.TMNOFLAGS);
            }
            status = Status.STATUS_ACTIVE;

        } catch (XAException e) {

            throw new SystemException("Unable to begin transaction: "
                    + "XA_ERR=" + e.errorCode);
        }
    }

    /**
     * @see javax.transaction.UserTransaction#commit()
     */
    public void commit() throws HeuristicMixedException,
            HeuristicRollbackException, IllegalStateException,
            RollbackException, SecurityException, SystemException {

        if (status != Status.STATUS_ACTIVE) {
            throw new IllegalStateException("Transaction not active");
        }

        try {
            for (XAResource resource : xaResources.keySet()) {
                XidImpl xid = (XidImpl) xaResources.get(resource);
                resource.end(xid, XAResource.TMSUCCESS);
            }

            status = Status.STATUS_PREPARING;
            for (XAResource resource : xaResources.keySet()) {
                XidImpl xid = (XidImpl) xaResources.get(resource);
                resource.prepare(xid);
            }
            status = Status.STATUS_PREPARED;

            status = Status.STATUS_COMMITTING;
            if (distributedThreadAccess) {
                Thread distributedThread = new Thread() {
                    public void run() {
                        try {
                            for (XAResource resource : xaResources.keySet()) {
                                XidImpl xid = (XidImpl) xaResources.get(resource);
                                resource.commit(xid, false);
                            }
                        } catch (Exception e) {
                            throw new RuntimeException(e.getMessage());
                        }
                    }
                };
                distributedThread.start();
                distributedThread.join(1000);
                if (distributedThread.isAlive()) {
                    throw new SystemException(
                            "Commit from different thread but same XID must not block");
                }
            } else {
                for (XAResource resource : xaResources.keySet()) {
                    XidImpl xid = (XidImpl) xaResources.get(resource);
                    resource.commit(xid, false);
                }
            }

            status = Status.STATUS_COMMITTED;

        } catch (XAException e) {

            if ((e.errorCode >= XAException.XA_RBBASE) && (e.errorCode <= XAException.XA_RBEND)) {
                RollbackException re = new RollbackException("Transaction rolled back: XA_ERR=" + e.errorCode);
                re.initCause(e.getCause());
                throw re;
            } else {
                SystemException se = new SystemException("Unable to commit transaction: XA_ERR=" + e.errorCode);
                se.initCause(e.getCause());
                throw se;
            }
        } catch (InterruptedException e) {
            throw new SystemException("Thread.join() interrupted");
        }
    }

    /**
     * @see javax.transaction.UserTransaction#getStatus()
     */
    public int getStatus() throws SystemException {
        return status;
    }

    /**
     * @see javax.transaction.UserTransaction#rollback()
     */
    public void rollback() throws IllegalStateException, SecurityException,
            SystemException {

        if ((status != Status.STATUS_ACTIVE) && (status != Status.STATUS_MARKED_ROLLBACK)) {

            throw new IllegalStateException("Transaction not active");
        }

        try {
            for (XAResource resource : xaResources.keySet()) {
                XidImpl xid = (XidImpl) xaResources.get(resource);
                resource.end(xid, XAResource.TMFAIL);
            }

            status = Status.STATUS_ROLLING_BACK;
            for (XAResource resource : xaResources.keySet()) {
                XidImpl xid = (XidImpl) xaResources.get(resource);
                resource.rollback(xid);
            }
            status = Status.STATUS_ROLLEDBACK;

        } catch (XAException e) {
            SystemException se = new SystemException(
                    "Unable to rollback transaction: XA_ERR=" + e.errorCode);
            se.initCause(e.getCause());
            throw se;
        }
    }

    /**
     * @see javax.transaction.UserTransaction#setRollbackOnly()
     */
    public void setRollbackOnly() throws IllegalStateException, SystemException {
        if (status != Status.STATUS_ACTIVE) {
            throw new IllegalStateException("Transaction not active");
        }
        status = Status.STATUS_MARKED_ROLLBACK;
    }

    /**
     * @see javax.transaction.UserTransaction#setTransactionTimeout
     */
    public void setTransactionTimeout(int seconds) throws SystemException {
        try {
            for (XAResource xaResource : xaResources.keySet()) {
                xaResource.setTransactionTimeout(seconds);
            }
        } catch (XAException e) {
            SystemException se = new SystemException(
                    "Unable to set the TransactionTiomeout: XA_ERR=" + e.errorCode);
            se.initCause(e.getCause());
            throw se;
        }
    }


    /**
     * Internal {@link Xid} implementation.
     */
    class XidImpl implements Xid {

        /**
         * Global transaction id.
         */
        private final byte[] globalTxId;

        /**
         * Create a new instance of this class. Takes a global
         * transaction number as parameter.
         *
         * @param globalTxNumber global transaction number
         */
        public XidImpl(byte globalTxNumber) {
            this.globalTxId = new byte[]{globalTxNumber};
        }

        public int getFormatId() {
            return 0;
        }

        public byte[] getBranchQualifier() {
            return new byte[0];
        }

        public byte[] getGlobalTransactionId() {
            return globalTxId;
        }
    }
}
