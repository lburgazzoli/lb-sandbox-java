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
package com.github.lburgazzoli.sandbox.jpa.hibernate.connection;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.hibernate.HibernateException;
import org.hibernate.engine.jdbc.connections.spi.ConnectionProvider;
import org.hibernate.service.UnknownUnwrapTypeException;
import org.hibernate.service.spi.Configurable;
import org.hibernate.service.spi.Stoppable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Map;

/**
 *
 */
public class HSQLConnectionProvider implements ConnectionProvider,Configurable,Stoppable {

    private static final Logger LOGGER = LoggerFactory.getLogger(HikariConnectionProvider.class);

    /**
     * HikariCP data source.
     */
    private org.hsqldb.jdbc.JDBCDataSource hds;

    // *************************************************************************
    //
    // *************************************************************************

    /**
     * c-tor
     */
    public HSQLConnectionProvider() {
        this.hds = null;
    }

    // *************************************************************************
    // Configurable
    // *************************************************************************

    @Override
    public void configure(Map props) throws HibernateException {
        try {
            LOGGER.debug("Configuring HSQL DataSource");

            this.hds = new org.hsqldb.jdbc.JDBCDataSource();
            this.hds.setUrl((String)props.get("hibernate.connection.url"));
            this.hds.setUser((String)props.get("hibernate.connection.username"));
            this.hds.setPassword((String)props.get("hibernate.connection.password"));

        } catch(Exception e) {
            throw new HibernateException(e);
        }

        LOGGER.debug("HSQL DataSource Configured");
    }

    // *************************************************************************
    // ConnectionProvider
    // *************************************************************************

    @Override
    public Connection getConnection() throws SQLException {
        Connection conn = null;
        if(this.hds != null) {
            conn = this.hds.getConnection();
        }

        return conn;
    }

    @Override
    public void closeConnection(Connection conn) throws SQLException {
        conn.close();
    }

    @Override
    public boolean supportsAggressiveRelease() {
        return false;
    }

    @Override
    public boolean isUnwrappableAs(Class unwrapType) {
        return ConnectionProvider.class.equals(unwrapType)
            || HSQLConnectionProvider.class.isAssignableFrom(unwrapType);
    }

    @Override
    public <T> T unwrap(Class<T> unwrapType) {
        if (isUnwrappableAs(unwrapType)) {
            return (T) this;
        } else {
            throw new UnknownUnwrapTypeException(unwrapType);
        }
    }

    // *************************************************************************
    // Stoppable
    // *************************************************************************

    @Override
    public void stop() {
    }
}
