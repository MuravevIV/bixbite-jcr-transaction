package com.ilyamur.bixbite.jcr.transaction;

import javax.jcr.Session;

/**
 * JcrValueStore.
 *
 * @author Ilya_Muravyev
 */
public interface JcrValueStore {

    void save(Session session, String value);

    String load(Session session);
}
