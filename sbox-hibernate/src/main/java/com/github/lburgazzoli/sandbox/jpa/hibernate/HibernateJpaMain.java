/*
 * Copyright 2014 lb
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.github.lburgazzoli.sandbox.jpa.hibernate;

import com.github.lburgazzoli.sandbox.jpa.hibernate.connection.HikariConnectionProvider;
import com.google.common.collect.Maps;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.persistence.TypedQuery;
import java.util.Map;

/**
 *
 */
public class HibernateJpaMain {
    private static final Logger LOGGER = LoggerFactory.getLogger(HibernateJpaMain.class);

    // *************************************************************************
    //
    // *************************************************************************

    /**
     *
     * @param emf
     */
    private static void insert(EntityManagerFactory emf) {
        EntityManager em = null;

        try {
            em = emf.createEntityManager();
            em.getTransaction().begin();
            em.persist(new HibernateJpaItem("item_1","descr_1"));
            em.getTransaction().commit();
        } catch(Exception e) {
            if(em != null) {
                if(em.getTransaction().isActive()) {
                    em.getTransaction().rollback();
                }
            }

            LOGGER.warn("Exception",e);
        } finally {
            if(em != null) {
                em.close();
            }
        }
    }

    /**
     *
     * @param emf
     */
    private static void list(EntityManagerFactory emf) {
        EntityManager em = null;
        TypedQuery<HibernateJpaItem> query = null;

        try {
            em = emf.createEntityManager();
            query = em.createQuery("select i from HibernateJpaItem i", HibernateJpaItem.class);

            for(HibernateJpaItem item: query.getResultList()) {
                LOGGER.debug("Item: name={}, description={}",item.getName(),item.getDescription());
            }

        } catch(Exception e) {
            if(em != null) {
                if(em.getTransaction().isActive()) {
                    em.getTransaction().rollback();
                }
            }

            LOGGER.warn("Exception",e);
        }
    }

    // *************************************************************************
    //
    // *************************************************************************

    public static void main(String[] args) {
        EntityManagerFactory emf = null;

        try {
            Map<String,String> props = Maps.newHashMap();
            props.put("hibernate.hbm2ddl.auto"               , "create-drop");
            props.put("hibernate.dialect"                    , org.hibernate.dialect.HSQLDialect.class.getName());
            props.put("hibernate.show_sql"                   , "false");
            props.put("hibernate.format_sql"                 , "true");
            props.put("hibernate.connection.provider_class"  , HikariConnectionProvider.class.getName());
            props.put("hibernate.connection.autocommit"      , "false");
            props.put("hibernate.hikari.dataSourceClassName" , org.hsqldb.jdbc.JDBCDataSource.class.getName());
            props.put("hibernate.hikari.dataSource.url"      , "jdbc:hsqldb:mem:hbdb");
            props.put("hibernate.hikari.dataSource.user"     , "sa");
            props.put("hibernate.hikari.dataSource.password" , "");

            emf = Persistence.createEntityManagerFactory("HBPU",props);

            insert(emf);
            list(emf);

        } catch(Exception e) {
            LOGGER.warn("Exception",e);
        } finally {
            if(emf != null) {
                emf.close();
            }
        }
    }
}
