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
package com.github.lburgazzoli.sandbox.hibernate;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.hibernate.HibernateException;
import org.hibernate.engine.jdbc.connections.spi.ConnectionProvider;
import org.hibernate.exception.spi.Configurable;
import org.hibernate.service.UnknownUnwrapTypeException;
import org.hibernate.service.spi.Stoppable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Properties;

/**
 *
 */
public class HikariConnectionProvider implements ConnectionProvider,Configurable,Stoppable {

    private static final Logger LOGGER = LoggerFactory.getLogger(HikariConnectionProvider.class);

    /**
     * HikariCP configuration.
     */
    private HikariConfig hcfg;

    /**
     * HikariCP data source.
     */
    private HikariDataSource hds;

    // *************************************************************************
    //
    // *************************************************************************

    /**
     * c-tor
     */
    public HikariConnectionProvider() {
        this.hcfg = null;
        this.hds = null;
    }

    // *************************************************************************
    // Configurable
    // *************************************************************************

    @Override
    public void configure(Properties properties) throws HibernateException {
        try {
            this.hcfg = HikariConfigurationUtil.loadConfiguration(properties);
            this.hds = new HikariDataSource(this.hcfg);

        } catch(Exception e) {
            throw new HibernateException(e);
        }
    }

    // *************************************************************************
    // ConnectionProvider
    // *************************************************************************

    @Override
    public Connection getConnection() throws SQLException {
        return this.hds.getConnection();
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
            || HikariConnectionProvider.class.isAssignableFrom(unwrapType);
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
        this.hds.shutdown();
    }
}
