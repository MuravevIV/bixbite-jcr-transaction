package com.ilyamur.bixbite.jcr.transaction;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.jcr.Node;
import javax.jcr.RepositoryException;
import javax.jcr.Session;

/**
 * JcrValueStoreImpl.
 *
 * @author Ilya_Muravyev
 */
public class JcrValueStoreImpl implements JcrValueStore {

    private static final String NODE_NAME = "tmp";
    private static final String PROPERTY_NAME = "message";

    private static final Logger LOG = LoggerFactory.getLogger(JcrValueStoreImpl.class);

    @Override
    public void save(Session session, String value) {
        try {
            Node root = session.getRootNode();
            if (root.hasNode(NODE_NAME)) {
                root.getNode(NODE_NAME).remove();
            }
            Node tmp = root.addNode(NODE_NAME);
            tmp.setProperty(PROPERTY_NAME, value);
            session.save();
        } catch (RepositoryException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public String load(Session session) {
        try {
            Node root = session.getRootNode();
            if (root.hasNode(NODE_NAME)) {
                return getPropertyValue(root.getNode(NODE_NAME));
            } else {
                return null;
            }
        } catch (RepositoryException e) {
            throw new RuntimeException(e);
        }
    }

    private String getPropertyValue(Node tmp) throws RepositoryException {
        return tmp.getProperty(PROPERTY_NAME).getString();
    }
}
