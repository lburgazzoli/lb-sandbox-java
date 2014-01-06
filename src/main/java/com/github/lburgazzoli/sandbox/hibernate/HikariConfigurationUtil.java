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
import org.hibernate.cfg.AvailableSettings;

import java.util.Properties;

/**
 *
 */
public class HikariConfigurationUtil {
    public static final String CONFIG_PREFIX                  = "hibernate.hikari";
    public static final String CONFIG_PREFIX_DATASOURCE       = "hibernate.hikari.dataSource";
    public static final String CONFIG_CONNECTION_DRIVER_CLASS = "hibernate.connection.driver_class";

    /**
     *
     * @param properties
     * @return
     */
    public static HikariConfig loadConfiguration(Properties properties) {
        Properties hicaryProps = new Properties();
        copyProperty(AvailableSettings.ISOLATION,properties,"transactionIsolation",hicaryProps);
        copyProperty(AvailableSettings.AUTOCOMMIT,properties,"autoCommit",hicaryProps);
        copyProperty(CONFIG_CONNECTION_DRIVER_CLASS,properties,"DataSourceClassName",hicaryProps);

        for(String key : properties.stringPropertyNames()) {
            if(key.startsWith(CONFIG_PREFIX)) {
                hicaryProps.setProperty(
                    key.substring(CONFIG_PREFIX.length()+1),
                    properties.getProperty(key));
            }
        }

        return new HikariConfig(hicaryProps);
    }

    /**
     *
     * @param srcKey
     * @param src
     * @param dstKey
     * @param dst
     */
    public static void copyProperty(String srcKey,Properties src,String dstKey,Properties dst) {
        if(src.containsKey(srcKey)) {
            dst.setProperty(dstKey,src.getProperty(srcKey));
        }
    }
}
