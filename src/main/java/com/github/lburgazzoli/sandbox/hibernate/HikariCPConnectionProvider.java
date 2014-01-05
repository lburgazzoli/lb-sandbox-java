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
import org.hibernate.cfg.AvailableSettings;
import org.hibernate.engine.jdbc.connections.spi.ConnectionProvider;
import org.hibernate.exception.spi.Configurable;
import org.hibernate.internal.util.config.ConfigurationHelper;
import org.hibernate.service.UnknownUnwrapTypeException;
import org.hibernate.service.spi.Stoppable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Properties;

/**
 * https://github.com/hibernate/hibernate-orm/blob/master/hibernate-c3p0/src/main/java/org/hibernate/c3p0/internal/C3P0ConnectionProvider.java
 * https://github.com/hibernate/hibernate-orm/blob/master/hibernate-proxool/src/main/java/org/hibernate/proxool/internal/ProxoolConnectionProvider.java
 * https://github.com/wwadge/bonecp/blob/master/bonecp-hbnprovider/src/main/java/com/jolbox/bonecp/provider/BoneCPConnectionProvider.java
 */
public class HikariCPConnectionProvider implements ConnectionProvider,Configurable,Stoppable {

    private static final Logger LOGGER = LoggerFactory.getLogger(HikariCPConnectionProvider.class);

    private static final String CONFIG_PREFIX                  = "hibernate.hikaricp";
    private static final String CONFIG_CONNECTION_DRIVER_CLASS = "hibernate.connection.driver_class";
    private static final String CONFIG_CONNECTION_PASSWORD     = "hibernate.connection.password";
    private static final String CONFIG_CONNECTION_USERNAME     = "hibernate.connection.username";
    private static final String CONFIG_CONNECTION_URL          = "hibernate.connection.url";

    /**
     * Isolation level.
     */
    private Integer isolation;

    /**
     * Autocommit option.
     */
    private Boolean autocommit;

    /**
     * HikariCP config.
     */
    private HikariConfig config;

    /**
     * HikariCP data source.
     */
    private HikariDataSource ds;

    // *************************************************************************
    //
    // *************************************************************************

    /**
     * c-tor
     */
    public HikariCPConnectionProvider() {
        this.ds = null;
        this.config = null;
        this.autocommit = null;
        this.isolation = null;
    }

    // *************************************************************************
    // Configurable
    // *************************************************************************

    @Override
    public void configure(Properties properties) throws HibernateException {
        try {

            // Remember Isolation level
            this.isolation = ConfigurationHelper.getInteger(AvailableSettings.ISOLATION, properties);
            this.autocommit = ConfigurationHelper.getBoolean(AvailableSettings.AUTOCOMMIT, properties);

        } catch(Exception e) {
            throw new HibernateException(e);
        }
    }

    // *************************************************************************
    // ConnectionProvider
    // *************************************************************************

    @Override
    public Connection getConnection() throws SQLException {
        Connection connection = this.ds.getConnection();

        try {
            // set the Transaction Isolation if defined
            if ((this.isolation != null) && (connection.getTransactionIsolation() != this.isolation.intValue())) {
                connection.setTransactionIsolation (this.isolation.intValue());
            }

            // toggle autoCommit to false if set
            if ( connection.getAutoCommit() != this.autocommit ){
                connection.setAutoCommit(this.autocommit);
            }
        } catch (SQLException e) {
            try {
                connection.close();
            } catch (Exception e2) {
                LOGGER.warn("Setting connection properties failed and closing this connection failed again", e);
            }

            throw e;
        }

        return connection;
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
            || HikariCPConnectionProvider.class.isAssignableFrom(unwrapType);
    }

    @Override
    public <T> T unwrap(Class<T> unwrapType) {
        if ( ConnectionProvider.class.equals(unwrapType) ||
             HikariCPConnectionProvider.class.isAssignableFrom(unwrapType) ) {
            return (T) this;
        }

        throw new UnknownUnwrapTypeException( unwrapType );
    }

    // *************************************************************************
    // Stoppable
    // *************************************************************************

    @Override
    public void stop() {
        this.ds.shutdown();
    }
}
