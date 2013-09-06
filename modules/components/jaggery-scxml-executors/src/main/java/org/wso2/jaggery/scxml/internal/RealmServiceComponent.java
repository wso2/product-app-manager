package org.wso2.jaggery.scxml.internal;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.wso2.carbon.CarbonException;
import org.wso2.carbon.registry.core.service.RegistryService;
import org.wso2.carbon.user.core.service.RealmService;
import org.wso2.jaggery.scxml.RealmContext;
import org.wso2.jaggery.scxml.RealmContext;

/**
 * @scr.component name="jaggery.scxml.realmservicecomponent"
 * immediate="true"
 * @scr.reference name="user.realmservice.default"
 * interface="org.wso2.carbon.user.core.service.RealmService"
 * cardinality="1..1" policy="dynamic" bind="setRealmService"
 * unbind="unsetRealmService"
 */

public class RealmServiceComponent {
    private static final Log log = LogFactory.getLog(RealmServiceComponent.class);

   /* protected void setRegistryService(RegistryService registryService) {
        if (log.isDebugEnabled()) {
            log.info("Setting the Registry Service to RegistryHostObjectContext");
        }
        RealmContext.setRegistryService(registryService);
    }

    protected void unsetRegistryService(RegistryService registryService) {
        if (log.isDebugEnabled()) {
            log.info("Unsetting the Registry Service");
        }
        RealmContext.setRegistryService(null);
    }  */

    protected void setRealmService(RealmService realmService) throws CarbonException {
        if (log.isDebugEnabled()) {
            log.debug("Obtained realm service.");
        }
        RealmContext.setRealmService(realmService);
    }

    protected void unsetRealmService(RealmService realmService) throws CarbonException {
        RealmContext.setRealmService(null);
    }
}
