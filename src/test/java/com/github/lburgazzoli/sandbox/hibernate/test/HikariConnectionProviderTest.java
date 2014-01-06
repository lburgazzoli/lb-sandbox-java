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
package com.github.lburgazzoli.sandbox.hibernate.test;

import com.github.lburgazzoli.sandbox.hibernate.HikariConfigurationUtil;
import com.github.lburgazzoli.sandbox.hibernate.HikariConnectionProvider;
import com.zaxxer.hikari.HikariConfig;
import org.hibernate.dialect.HSQLDialect;
import org.junit.Assert;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Properties;

/**
 *
 */
public class HikariConnectionProviderTest {

    private static final Logger LOGGER = LoggerFactory.getLogger(HikariConnectionProviderTest.class);

    // *************************************************************************
    // helpers
    // *************************************************************************

    /**
     *
     * @return
     */
    private Properties getHibernateProperties() {
        Properties props = new Properties();
        props.setProperty("hibernate.connection.driver_class"    ,org.hsqldb.jdbc.JDBCDriver.class.getName());
        props.setProperty("hibernate.connection.provider_class"  ,HikariConnectionProvider.class.getName());
        props.setProperty("hibernate.connection.autocommit"      ,"false");
        props.setProperty("hibernate.dialect"                    ,HSQLDialect.class.getName());
        props.setProperty("hibernate.hbm2ddl.auto"               ,"create-drop");
        props.setProperty("hibernate.show_sql"                   ,"false");
        props.setProperty("hibernate.format_sql"                 ,"true");
        props.setProperty("hibernate.hikari.dataSource.url"      ,"jdbc:hsqldb:mem:hikericptest");
        props.setProperty("hibernate.hikari.dataSource.user"     ,"sa");
        props.setProperty("hibernate.hikari.dataSource.password" ,"");
        props.setProperty("hibernate.hikari.connectionInitSql"   ,"SELECT 1");
        props.setProperty("hibernate.hikari.connectionTestQuery" ,"SELECT 1");

        return props;
    }

    // *************************************************************************
    // test cases
    // *************************************************************************

    @Test
    public void testHikariConnectionProvider() {
        Properties   hbp = getHibernateProperties();
        HikariConfig hc  = HikariConfigurationUtil.loadConfiguration(hbp);
        hc.validate();

        LOGGER.debug("{}",hc.getDataSourceProperties());

        Assert.assertFalse(hc.isAutoCommit());
        Assert.assertEquals(hc.getDataSourceClassName(), hbp.getProperty("hibernate.connection.driver_class"));
        Assert.assertEquals(hc.getConnectionInitSql(), hbp.getProperty("hibernate.hikari.connectionInitSql"));
        Assert.assertEquals(hc.getConnectionTestQuery(), hbp.getProperty("hibernate.hikari.connectionTestQuery"));

        for(String key : hbp.stringPropertyNames()) {
            if(key.startsWith(HikariConfigurationUtil.CONFIG_PREFIX_DATASOURCE)) {
                Assert.assertEquals(
                    hbp.getProperty(key),
                    hc.getDataSourceProperties().getProperty(key.substring(HikariConfigurationUtil.CONFIG_PREFIX_DATASOURCE.length() + 1))
                );
            }
        }
    }
}
