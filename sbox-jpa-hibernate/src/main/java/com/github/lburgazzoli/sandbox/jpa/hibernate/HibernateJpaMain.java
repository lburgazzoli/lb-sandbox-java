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

import com.google.common.collect.Maps;
import org.hibernate.c3p0.internal.C3P0ConnectionProvider;
import org.hibernate.hikaricp.internal.HikariCPConnectionProvider;
import org.hsqldb.jdbc.JDBCDataSource;
import org.hsqldb.jdbc.JDBCDriver;
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
        } finally {
            if(em != null) {
                em.close();
            }
        }
    }

    // *************************************************************************
    //
    // *************************************************************************

    /**
     *
     * @return
     */
    private static Map<String,String> props(Class<?> providerClass) throws Exception {
        Map<String,String> props = Maps.newHashMap();
        props.put("hibernate.hbm2ddl.auto"              , "create-drop");
        props.put("hibernate.show_sql"                  , "false");
        props.put("hibernate.format_sql"                , "true");
        props.put("hibernate.connection.provider_class" , providerClass.getName());
        props.put("hibernate.connection.autocommit"     , "false");

        if(providerClass == HikariCPConnectionProvider.class) {
            props.put("hibernate.hikari.dataSourceClassName" , JDBCDataSource.class.getName());
            props.put("hibernate.hikari.dataSource.url"      , "jdbc:hsqldb:mem:hbdb");
            props.put("hibernate.hikari.dataSource.user"     , "sa");
            props.put("hibernate.hikari.dataSource.password" , "");
        } else if(providerClass == C3P0ConnectionProvider.class) {
            props.put("hibernate.connection.url"             , "jdbc:hsqldb:mem:hbdb");
            props.put("hibernate.connection.driver_class"    , JDBCDriver.class.getName());
            props.put("hibernate.connection.username"        , "sa");
            props.put("hibernate.connection.password"        , "");
            props.put("hibernate.c3p0.min_size"              , "5");
            props.put("hibernate.c3p0.max_size"              , "10");
        } else {
            throw new Exception("Unknown providerClass " + providerClass.getName());
        }

        return props;
    }

    // *************************************************************************
    //
    // *************************************************************************

    public static void main(String[] args) {
        EntityManagerFactory emf = null;
        Map<String,String> emfProps = null;

        try {
            if(args.length == 1) {
                if("hikari".equalsIgnoreCase(args[0])) {
                    emfProps = props(HikariCPConnectionProvider.class);
                } else if("c3p0".equalsIgnoreCase(args[0])) {
                    emfProps = props(C3P0ConnectionProvider.class);
                }

                if(emfProps != null) {
                    emf = Persistence.createEntityManagerFactory("HBPU",emfProps);
                    if(emf != null) {
                        insert(emf);
                        list(emf);
                    }
                }
            }

        } catch(Exception e) {
            LOGGER.warn("Exception",e);
        } finally {
            if(emf != null) {
                emf.close();
            }
        }
    }
}
