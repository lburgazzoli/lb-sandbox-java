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
package com.github.lburgazzoli.sandbox.jpa.batoo;

import com.github.lburgazzoli.sandbox.jpa.batoo.connection.BoneConnectionProvider;
import com.github.lburgazzoli.sandbox.jpa.batoo.connection.HikariConnectionProvider;
import com.google.common.collect.Maps;
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
public class BatooJpaMain {
    private static final Logger LOGGER = LoggerFactory.getLogger(BatooJpaMain.class);

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
            em.persist(new BatooJpaItem("item_1","descr_1"));
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
        TypedQuery<BatooJpaItem> query = null;

        try {
            em = emf.createEntityManager();
            query = em.createQuery("select i from BatooJpaItem i", BatooJpaItem.class);

            for(BatooJpaItem item: query.getResultList()) {
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
        props.put("org.batoo.jpa.ddl","DROP");

        if(providerClass == HikariConnectionProvider.class) {
            props.put("org.batoo.jdbc.datasource.pool"         , providerClass.getName());
            props.put("org.batoo.jdbc.datasource.name"         , "hikaricp");
            props.put("org.batoo.hikaricp.autoCommit"          , "false");
            props.put("org.batoo.hikaricp.dataSourceClassName" , JDBCDataSource.class.getName());
            props.put("org.batoo.hikaricp.dataSource.url"      , "jdbc:hsqldb:mem:btdb");
            props.put("org.batoo.hikaricp.dataSource.user"     , "sa");
            props.put("org.batoo.hikaricp.dataSource.password" , "");
        } else if(providerClass == BoneConnectionProvider.class) {
            props.put("org.batoo.jdbc.datasource.pool"         , providerClass.getName());
            props.put("org.batoo.jdbc.datasource.name"         , "bonecp");
            props.put("javax.persistence.jdbc.driver"          , JDBCDriver.class.getName());
            props.put("javax.persistence.jdbc.url"             , "jdbc:hsqldb:mem:btdb");
            props.put("javax.persistence.jdbc.user"            , "sa");
            props.put("javax.persistence.jdbc.password"        , "");
            props.put("org.batoo.bonecp.autoCommit"            , "false");
            props.put("org.batoo.bonecp.partitionCount"        , "1");

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
                    emfProps = props(HikariConnectionProvider.class);
                } else if("bone".equalsIgnoreCase(args[0])) {
                    emfProps = props(BoneConnectionProvider.class);
                }

                if(emfProps != null) {
                    emf = Persistence.createEntityManagerFactory("BTPU",emfProps);
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
