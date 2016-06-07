package org.wso2.carbon.appmgt.migration.util;

import org.wso2.carbon.appmgt.api.model.APIIdentifier;
import org.wso2.carbon.appmgt.api.model.WebApp;

import java.util.Date;

public class MigratingWebApp {
    private Date createdTime;
    private WebApp webApp;

    public WebApp getWebApp() {
        return webApp;
    }

    public MigratingWebApp(WebApp webApp) {
        this.webApp = webApp;
    }

    public boolean isPublished() {
        boolean isPublished = false;
        if ("PUBLISHED".equals(webApp.getStatus().getStatus())) {
           isPublished = true;
        }
        return isPublished;
    }

    public String getAppName(){
        return getId().getApiName();
    }

    public String getVersion(){
        return getId().getVersion();
    }

    public APIIdentifier getId(){
        return webApp.getId();
    }

    public Date getCreatedTime() {
        return createdTime;
    }

    public void setCreatedTime(Date createdTime) {
        this.createdTime = createdTime;
    }
}
