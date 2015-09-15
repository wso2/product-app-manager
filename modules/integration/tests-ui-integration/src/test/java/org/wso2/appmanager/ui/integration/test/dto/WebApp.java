package org.wso2.appmanager.ui.integration.test.dto;

/**
 * Created by dilan on 9/15/15.
 */
public class WebApp {

    private String name;
    private String displayName;
    private String context;
    private String version;
    private String webAppUrl;
    private String transport;

    public WebApp(String name, String displayName, String context, String version, String webAppUrl, String transport) {
        this.name = name;
        this.displayName = displayName;
        this.context = context;
        this.version = version;
        this.webAppUrl = webAppUrl;
        this.transport = transport;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public String getContext() {
        return context;
    }

    public void setContext(String context) {
        this.context = context;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public String getWebAppUrl() {
        return webAppUrl;
    }

    public void setWebAppUrl(String webAppUrl) {
        this.webAppUrl = webAppUrl;
    }

    public String getTransport() {
        return transport;
    }

    public void setTransport(String transport) {
        this.transport = transport;
    }
}
