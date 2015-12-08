package org.wso2.carbon.appmanager.integration.ui.Util;

import org.wso2.carbon.appmanager.integration.ui.Util.Bean.Tenant;
import org.wso2.carbon.automation.core.utils.LoginLogoutUtil;


import java.util.*;

public class TenantPopulator {
    private List<Tenant> tenants = new ArrayList<Tenant>();
    private AppMTenantMgtAdminServiceClient tenantManagementServiceClient;

    public TenantPopulator(String backendUrl, String userName, String password) throws Exception {
        String adminServiceBackend = backendUrl + "/services/";
        LoginLogoutUtil loginLogoutUtil = new LoginLogoutUtil(adminServiceBackend);
        String sessionCookie = loginLogoutUtil.login(userName, password, adminServiceBackend);
        tenantManagementServiceClient =
                new AppMTenantMgtAdminServiceClient(adminServiceBackend, sessionCookie);
    }

    public void populateTenants(List<Tenant> tenantList) throws Exception {
        for (Tenant tenant : tenantList) {
            tenantManagementServiceClient
                    .addTenant(tenant.getTenantDomain(), tenant.getTenantAdminPassword(),
                            tenant.getTenantAdminUsername(),
                            "demo");
            tenants.add(tenant);
        }

    }

    public void deactivateAllTenants() throws Exception {
        if (tenants.size() > 0) {
            for (Tenant t : tenants) {
                this.deactivateTenant(t);
            }
        }

    }


    public void deactivateTenant(Tenant tenant) throws Exception {
        tenantManagementServiceClient.deactivateTenant(tenant.getTenantDomain());
    }

    public void activateTenant(Tenant tenant) throws Exception {
        tenantManagementServiceClient.activateTenant(tenant.getTenantDomain());
    }


}
