package org.wso2.appmanager.ui.integration.test.utils;

import org.apache.log4j.Logger;
import org.wso2.carbon.utils.CarbonUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class DeploySampleWebApp {

    final static Logger log = Logger.getLogger(DeploySampleWebApp.class.getName());
    private static String homePath = CarbonUtils.getCarbonHome();

    /**
     * This method is use deploy war file according to given name
     *
     * @param warFileName Name of the war file that need to be deploy
     * @throws Exception Throws this when failed to deploy web application
     */
    public void copyFileUsingFileStreams(String warFileName) throws Exception {
        File warFile = new File(homePath + "/samples/" + warFileName + ".war");
        log.info(warFile.getAbsolutePath());
        File outputFolder = new File(
                homePath + "/repository/deployment/server/webapps/" + warFile.getName());
        InputStream is = null;
        OutputStream os = null;

        try {
            is = new FileInputStream(warFile);
            os = new FileOutputStream(outputFolder);
            byte[] buffer = new byte[1024];
            int length;
            while ((length = is.read(buffer)) > 0) {
                os.write(buffer, 0, length);
            }
        } catch (IOException e) {
            log.error("Error while deploying a " + warFileName + ".war", e);
            throw new Exception("Error while deploying a " + warFileName + ".war", e);
        } finally {
            try {
                if (is != null) {
                    is.close();
                }
                if (os != null) {
                    os.close();
                }
            } catch (IOException e) {
                String erroMessge = "Error while deploying a " + warFileName + ".war";
                log.error(erroMessge, e);
                throw new Exception(erroMessge, e);
            }

        }
        File webAppFolder = new File(
                homePath + "/repository/deployment/server/webapps/" + warFileName);
        while (!webAppFolder.exists()) {
        }
    }
}
