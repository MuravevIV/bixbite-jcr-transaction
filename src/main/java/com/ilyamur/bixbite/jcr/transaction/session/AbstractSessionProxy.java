package com.ilyamur.bixbite.jcr.transaction.session;

import org.xml.sax.ContentHandler;
import org.xml.sax.SAXException;

import javax.jcr.AccessDeniedException;
import javax.jcr.Credentials;
import javax.jcr.InvalidItemStateException;
import javax.jcr.InvalidSerializedDataException;
import javax.jcr.Item;
import javax.jcr.ItemExistsException;
import javax.jcr.ItemNotFoundException;
import javax.jcr.LoginException;
import javax.jcr.NamespaceException;
import javax.jcr.Node;
import javax.jcr.PathNotFoundException;
import javax.jcr.Property;
import javax.jcr.ReferentialIntegrityException;
import javax.jcr.Repository;
import javax.jcr.RepositoryException;
import javax.jcr.Session;
import javax.jcr.UnsupportedRepositoryOperationException;
import javax.jcr.ValueFactory;
import javax.jcr.Workspace;
import javax.jcr.lock.LockException;
import javax.jcr.nodetype.ConstraintViolationException;
import javax.jcr.nodetype.NoSuchNodeTypeException;
import javax.jcr.retention.RetentionManager;
import javax.jcr.security.AccessControlManager;
import javax.jcr.version.VersionException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.security.AccessControlException;

/**
 * AbstractSessionProxy.
 *
 * @author Ilya_Muravyev
 */
abstract class AbstractSessionProxy implements Session {

    private Session session;

    public AbstractSessionProxy(Session session) {
        this.session = session;
    }

    @Override
    public Repository getRepository() {
        return session.getRepository();
    }

    @Override
    public String getUserID() {
        return session.getUserID();
    }

    @Override
    public String[] getAttributeNames() {
        return session.getAttributeNames();
    }

    @Override
    public Object getAttribute(String name) {
        return session.getAttribute(name);
    }

    @Override
    public Workspace getWorkspace() {
        return session.getWorkspace();
    }

    @Override
    public Node getRootNode() throws RepositoryException {
        return session.getRootNode();
    }

    @Override
    public Session impersonate(Credentials credentials) throws LoginException, RepositoryException {
        return session.impersonate(credentials);
    }

    @Override
    public Node getNodeByUUID(String uuid) throws ItemNotFoundException, RepositoryException {
        return session.getNodeByUUID(uuid);
    }

    @Override
    public Node getNodeByIdentifier(String id) throws ItemNotFoundException, RepositoryException {
        return session.getNodeByIdentifier(id);
    }

    @Override
    public Item getItem(String absPath) throws PathNotFoundException, RepositoryException {
        return session.getItem(absPath);
    }

    @Override
    public Node getNode(String absPath) throws PathNotFoundException, RepositoryException {
        return session.getNode(absPath);
    }

    @Override
    public Property getProperty(String absPath) throws PathNotFoundException, RepositoryException {
        return session.getProperty(absPath);
    }

    @Override
    public boolean itemExists(String absPath) throws RepositoryException {
        return session.itemExists(absPath);
    }

    @Override
    public boolean nodeExists(String absPath) throws RepositoryException {
        return session.nodeExists(absPath);
    }

    @Override
    public boolean propertyExists(String absPath) throws RepositoryException {
        return session.propertyExists(absPath);
    }

    @Override
    public void move(String srcAbsPath, String destAbsPath) throws ItemExistsException, PathNotFoundException,
            VersionException, ConstraintViolationException, LockException, RepositoryException {

        session.move(srcAbsPath, destAbsPath);
    }

    @Override
    public void removeItem(String absPath) throws VersionException, LockException, ConstraintViolationException,
            AccessDeniedException, RepositoryException {

        session.removeItem(absPath);
    }

    @Override
    public void save() throws AccessDeniedException, ItemExistsException, ReferentialIntegrityException,
            ConstraintViolationException, InvalidItemStateException, VersionException, LockException,
            NoSuchNodeTypeException, RepositoryException {

        session.save();
    }

    @Override
    public void refresh(boolean keepChanges) throws RepositoryException {
        session.refresh(keepChanges);
    }

    @Override
    public boolean hasPendingChanges() throws RepositoryException {
        return session.hasPendingChanges();
    }

    @Override
    public ValueFactory getValueFactory() throws UnsupportedRepositoryOperationException, RepositoryException {
        return session.getValueFactory();
    }

    @Override
    public boolean hasPermission(String absPath, String actions) throws RepositoryException {
        return session.hasPermission(absPath, actions);
    }

    @Override
    public void checkPermission(String absPath, String actions) throws AccessControlException, RepositoryException {
        session.checkPermission(absPath, actions);
    }

    @Override
    public boolean hasCapability(String methodName, Object target, Object[] arguments) throws RepositoryException {
        return session.hasCapability(methodName, target, arguments);
    }

    @Override
    public ContentHandler getImportContentHandler(String parentAbsPath, int uuidBehavior) throws PathNotFoundException,
            ConstraintViolationException, VersionException, LockException, RepositoryException {

        return session.getImportContentHandler(parentAbsPath, uuidBehavior);
    }

    @Override
    public void importXML(String parentAbsPath, InputStream in, int uuidBehavior) throws IOException,
            PathNotFoundException, ItemExistsException, ConstraintViolationException, VersionException,
            InvalidSerializedDataException, LockException, RepositoryException {

        session.importXML(parentAbsPath, in, uuidBehavior);
    }

    @Override
    public void exportSystemView(String absPath, ContentHandler contentHandler, boolean skipBinary,
                                 boolean noRecurse) throws PathNotFoundException, SAXException, RepositoryException {

        session.exportSystemView(absPath, contentHandler, skipBinary, noRecurse);
    }

    @Override
    public void exportSystemView(String absPath, OutputStream out, boolean skipBinary,
                                 boolean noRecurse) throws IOException, PathNotFoundException, RepositoryException {

        session.exportSystemView(absPath, out, skipBinary, noRecurse);
    }

    @Override
    public void exportDocumentView(String absPath, ContentHandler contentHandler, boolean skipBinary,
                                   boolean noRecurse) throws PathNotFoundException, SAXException, RepositoryException {

        session.exportSystemView(absPath, contentHandler, skipBinary, noRecurse);
    }

    @Override
    public void exportDocumentView(String absPath, OutputStream out, boolean skipBinary,
                                   boolean noRecurse) throws IOException, PathNotFoundException, RepositoryException {

        session.exportSystemView(absPath, out, skipBinary, noRecurse);
    }

    @Override
    public void setNamespacePrefix(String prefix, String uri) throws NamespaceException, RepositoryException {
        session.setNamespacePrefix(prefix, uri);
    }

    @Override
    public String[] getNamespacePrefixes() throws RepositoryException {
        return session.getNamespacePrefixes();
    }

    @Override
    public String getNamespaceURI(String prefix) throws NamespaceException, RepositoryException {
        return session.getNamespaceURI(prefix);
    }

    @Override
    public String getNamespacePrefix(String uri) throws NamespaceException, RepositoryException {
        return session.getNamespacePrefix(uri);
    }

    @Override
    public void logout() {
        session.logout();
    }

    @Override
    public boolean isLive() {
        return session.isLive();
    }

    @Override
    public void addLockToken(String lt) {
        session.logout();
    }

    @Override
    public String[] getLockTokens() {
        return session.getLockTokens();
    }

    @Override
    public void removeLockToken(String lt) {
        session.removeLockToken(lt);
    }

    @Override
    public AccessControlManager getAccessControlManager() throws UnsupportedRepositoryOperationException,
            RepositoryException {

        return session.getAccessControlManager();
    }

    @Override
    public RetentionManager getRetentionManager() throws UnsupportedRepositoryOperationException, RepositoryException {
        return session.getRetentionManager();
    }
}
