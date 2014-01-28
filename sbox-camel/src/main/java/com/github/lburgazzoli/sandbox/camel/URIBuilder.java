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
package com.github.lburgazzoli.sandbox.camel;

import com.google.common.collect.Maps;
import org.apache.commons.lang3.ObjectUtils;

import java.util.Map;

/**
 *
 */
public final class URIBuilder {
    public static final String VAL_TRUE  = "true";
    public static final String VAL_FALSE = "false";

    private final String m_type;
    private final String m_scheme;
    private final Map<String,String> m_parameters;

    /**
     * c-tor
     *
     * @param type
     * @param schemeFmt
     * @param schemeArgs
     */
    public URIBuilder(String type, String schemeFmt, Object... schemeArgs) {
        m_type       = type;
        m_scheme     = String.format(schemeFmt,schemeArgs);
        m_parameters = Maps.newHashMap();
    }

    /**
     *
     * @param key
     * @param fmt
     * @param args
     * @return
     */
    public URIBuilder param(String key,String fmt,Object... args) {
        m_parameters.put(key,String.format(fmt,args));
        return this;
    }

    /**
     *
     * @param key
     * @param cfg
     * @param cfgKey
     * @return
     */
    public URIBuilder param(String key, Map<String,Object> cfg, String cfgKey) {
        m_parameters.put(key, ObjectUtils.toString(cfg.get(cfgKey)));
        return this;
    }

    /**
     *
     * @return
     */
    public String build() {
        StringBuilder sb = new StringBuilder()
            .append(m_type)
            .append(":")
            .append(m_scheme);

        if(!m_parameters.isEmpty()) {
            sb.append("?");

            int counter = 0;
            for(Map.Entry<String,String> entry : m_parameters.entrySet()) {
                sb.append(entry.getKey());
                sb.append("=");
                sb.append(entry.getValue());

                if(++counter < m_parameters.size()) {
                    sb.append("&");
                }
            }
        }

        return sb.toString();
    }

    /**
     *
     * @param type
     * @return
     */
    public static final String defaultLogUri(Class<?> type) {
        return new URIBuilder("log", type.getName())
            .param("level"      , "DEBUG")
            .param("showHeaders", URIBuilder.VAL_TRUE)
            .param("multiline"  , URIBuilder.VAL_TRUE)
            .build();
    }
}

