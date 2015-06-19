package org.wso2.carbon.appmanager.integration.ui.Util.Bean;

/*
*  Copyright (c) 2005-2014, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
*
*  WSO2 Inc. licenses this file to you under the Apache License,
*  Version 2.0 (the "License"); you may not use this file except
*  in compliance with the License.
*  You may obtain a copy of the License at
*
*    http://www.apache.org/licenses/LICENSE-2.0
*
* Unless required by applicable law or agreed to in writing,
* software distributed under the License is distributed on an
* "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
* KIND, either express or implied.  See the License for the
* specific language governing permissions and limitations
* under the License.
*/

import java.io.File;

public class MobileApplicationBean {

    private String appmeta;
    private String version = "1.0.0";
    private String provider;
    private String markettype;
    private String platform;
    private String name;
    private String description;
    private File bannerFilePath;
    private String sso_ssoProvider = "wso2is-5.0.0";
    private File iconFile;
    private File screenShot1File;
    private File screenShot2File;
    private File screenShot3File;
    private String mobileapp;

    private String apkFile;

    public MobileApplicationBean(){

    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public String getProvider() {
        return provider;
    }

    public void setProvider(String provider) {
        this.provider = provider;
    }

    public String getMarkettype() {
        return markettype;
    }

    public void setMarkettype(String markettype) {
        this.markettype = markettype;
    }

    public String getPlatform() {
        return platform;
    }

    public void setPlatform(String platform) {
        this.platform = platform;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {return description;}

    public void setDescription(String description) {
        this.description = description;
    }

    public String getMobileapp() {
        return mobileapp;
    }

    public void setMobileapp(String mobileapp) {
        this.mobileapp = mobileapp;
    }

    public String getAppmeta() {
        return appmeta;
    }

    public void setAppmeta(String appmeta) {
        this.appmeta = appmeta;
    }

    public String getApkFile() {
        return apkFile;
    }

    public void setApkFile(String apkFile) {
        this.apkFile = apkFile;
    }


    public String getSso_ssoProvider() {
        return sso_ssoProvider;
    }

    public void setSso_ssoProvider(String sso_ssoProvider) {
        this.sso_ssoProvider = sso_ssoProvider;
    }

    public File getBannerFilePath() {
        return bannerFilePath;
    }

    public void setBannerFilePath(File bannerFilePath) {
        this.bannerFilePath = bannerFilePath;
    }

    public File getIconFile() {
        return iconFile;
    }

    public void setIconFile(File iconFile) {
        this.iconFile = iconFile;
    }

    public File getScreenShot1File() {
        return screenShot1File;
    }

    public void setScreenShot1File(File screenShot1File) {
        this.screenShot1File = screenShot1File;
    }

    public File getScreenShot2File() {
        return screenShot2File;
    }

    public void setScreenShot2File(File screenShot2File) {
        this.screenShot2File = screenShot2File;
    }

    public File getScreenShot3File() {
        return screenShot3File;
    }

    public void setScreenShot3File(File screenShot3File) {
        this.screenShot3File = screenShot3File;
    }
}
