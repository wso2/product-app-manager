package org.wso2.jaggery.scxml;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.wso2.carbon.CarbonException;
import org.wso2.carbon.registry.core.Registry;
import org.wso2.carbon.registry.core.exceptions.RegistryException;
import org.wso2.carbon.registry.core.service.RegistryService;
import org.wso2.carbon.user.core.UserStoreException;
import org.wso2.carbon.user.core.service.RealmService;

/*
Description:The class contains references to the realm service
Filename:RealmContext.java
Created Date: 26/8/2013
 */
public class RealmContext {

    private static Log log = LogFactory.getLog(RealmContext.class);

    private static RealmService realmService;

    public static void setRealmService(RealmService rs) throws CarbonException {
        RealmContext.realmService = rs;
    }

    public static RealmService getRealmService() throws CarbonException {
        if (realmService == null) {
            String msg = "System has not been started properly. Realm Service is null.";
            log.error(msg);
            throw new CarbonException(msg);
        }
        return realmService;
    }
}
