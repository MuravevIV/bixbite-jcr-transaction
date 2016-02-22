package com.ilyamur.bixbite.jcr.transaction.example;

import org.apache.jackrabbit.core.TransientRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.jcr.Node;
import javax.jcr.Repository;
import javax.jcr.Session;
import javax.jcr.SimpleCredentials;

/**
 * Second hop example. Stores, retrieves, and removes example content.
 *
 * @author Ilya_Muravyev
 */
public class SecondHopExample {

    private static final Logger LOG = LoggerFactory.getLogger(SecondHopExample.class);

    /**
     * The main entry point of the example application.
     *
     * @param args command line arguments (ignored)
     * @throws Exception if an error occurs
     */
    public static void main(String[] args) throws Exception {
        Repository repository = new TransientRepository();
        Session session = repository.login(new SimpleCredentials("admin", "admin".toCharArray()));
        try {
            Node root = session.getRootNode();

            // Store content
            Node hello = root.addNode("hello");
            Node world = hello.addNode("world");
            world.setProperty("message", "Hello, World!");
            session.save();

            // Retrieve content
            Node node = root.getNode("hello/world");
            LOG.info(node.getPath());
            LOG.info(node.getProperty("message").getString());

            // Remove content
            root.getNode("hello").remove();
            session.save();
        } finally {
            session.logout();
        }
    }
}
