package org.wso2.carbon.appmanager.integration.ui.Util.Bean;


public class Tenant {
    String tenantDomain;
    String tenantAdminUsername;
    String tenantAdminPassword;

    public Tenant(String domain, String username, String password) {
      this.tenantDomain = domain;
      this.tenantAdminUsername = username;
      this.tenantAdminPassword = password;
    }

    public void setTenantAdminPassword(String tenantAdminPassword) {
        this.tenantAdminPassword = tenantAdminPassword;
    }

    public String getTenantDomain() {
        return tenantDomain;
    }

    public void setTenantDomain(String tenantDomain) {
        this.tenantDomain = tenantDomain;
    }

    public String getTenantAdminUsername() {
        return tenantAdminUsername;
    }

    public void setTenantAdminUsername(String tenantAdminUsername) {
        this.tenantAdminUsername = tenantAdminUsername;
    }

    public String getTenantAdminPassword() {
        return tenantAdminPassword;
    }


}
