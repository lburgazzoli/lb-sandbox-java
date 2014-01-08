package com.github.lburgazzoli.sandbox.jpa.batoo.connection;

import com.jolbox.bonecp.BoneCPConfig;
import com.jolbox.bonecp.BoneCPDataSource;
import org.slf4j.LoggerFactory;

import java.util.Map;
import java.util.Properties;

/**
 * @author lburgazzoli
 */
public class BoneConnectionProvider extends AbstractConnectionProvider {
    private static final org.slf4j.Logger LOGGER = LoggerFactory.getLogger(BoneConnectionProvider.class);

    public static final String CONFIG_PREFIX = "org.batoo.bonecp.";

    /**
     * c-tor
     */
    public BoneConnectionProvider() {
    }

    // *************************************************************************
    //
    // *************************************************************************

    @Override
    public void open(String persistanceUnitName, String hintName, Map<String, Object> props) {
        try {
            LOGGER.debug("Configuring BoneCP");
            setWrappedDataSource( new BoneCPDataSource(loadConfiguration(props)));

        } catch(Exception e) {
            LOGGER.warn("Exception",e);
        }

        LOGGER.debug("BoneCP Configured");
    }

    @Override
    public void close() {
        ((BoneCPDataSource)getWrappedDataSource()).close();
        setWrappedDataSource(null);
    }

    // *************************************************************************
    //
    // *************************************************************************

    /**
     *
     * @param props
     * @return
     */
    private BoneCPConfig loadConfiguration(Map<?,?> props) throws Exception {
        Properties cpProps = mapToProperties(props,CONFIG_PREFIX);

        if(props.containsKey("javax.persistence.jdbc.driver")) {
            cpProps.setProperty("driverClass",(String)props.get("javax.persistence.jdbc.driver"));
        }
        if(props.containsKey("javax.persistence.jdbc.url")) {
            cpProps.setProperty("jdbcUrl",(String)props.get("javax.persistence.jdbc.url"));
        }
        if(props.containsKey("javax.persistence.jdbc.user")) {
            cpProps.setProperty("username",(String)props.get("javax.persistence.jdbc.user"));
        }
        if(props.containsKey("javax.persistence.jdbc.password")) {
            cpProps.setProperty("password",(String)props.get("javax.persistence.jdbc.password"));
        }

        return new BoneCPConfig(cpProps);
    }
}
