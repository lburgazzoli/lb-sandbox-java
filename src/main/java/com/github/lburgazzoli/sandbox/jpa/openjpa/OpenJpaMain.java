package com.github.lburgazzoli.sandbox.jpa.openjpa;

import com.google.common.collect.Maps;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import java.util.Map;

/**
 *
 */
public class OpenJpaMain {
    private static final Logger LOGGER = LoggerFactory.getLogger(OpenJpaMain.class);

    // *************************************************************************
    //
    // *************************************************************************

    public static void main(String[] args) {
        EntityManagerFactory emf = null;
        EntityManager em = null;
        try {
            Map<Object,Object> properties = Maps.newHashMap();
            if(args.length == 1) {
                properties.put("openjpa.Log",args[0]);
            }

            emf = Persistence.createEntityManagerFactory("DATA_OPENJPA",properties);
            em  = emf.createEntityManager();

            em.getTransaction().begin();
            em.persist(new OpenJpaItem("item_1","desr_1"));
            em.getTransaction().commit();
        } catch(Exception e) {
            if(em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }

            LOGGER.warn("Exception",e);
        } finally {
            if(em != null) {
                em.close();
            }
        }
    }
}
