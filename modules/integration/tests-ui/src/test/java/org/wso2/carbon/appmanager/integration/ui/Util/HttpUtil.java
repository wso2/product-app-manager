/*
 *Copyright (c) 2005-2010, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
 *
 *WSO2 Inc. licenses this file to you under the Apache License,
 *Version 2.0 (the "License"); you may not use this file except
 *in compliance with the License.
 *You may obtain a copy of the License at
 *
 *http://www.apache.org/licenses/LICENSE-2.0
 *
 *Unless required by applicable law or agreed to in writing,
 *software distributed under the License is distributed on an
 *"AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 *KIND, either express or implied.  See the License for the
 *specific language governing permissions and limitations
 *under the License.
 */

package org.wso2.carbon.appmanager.integration.ui.Util;

import org.apache.http.HttpEntity;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.DefaultHttpClient;
import org.wso2.carbon.appmanager.integration.ui.Util.Bean.MobileApplicationBean;
import org.wso2.carbon.automation.core.utils.HttpResponse;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLSession;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.ProtocolException;
import java.net.URL;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.logging.Logger;

public class HttpUtil {

    private static Logger log = Logger.getLogger("HttpUtil");

    static {
        HttpsURLConnection.setDefaultHostnameVerifier(new HostnameVerifier() {
            public boolean verify(String hostname, SSLSession session) {
                if (hostname.equals("localhost"))
                    return true;
                return false;
            }
        });
    }

    public static HttpResponse doPut(URL endpoint, String postBody,
                                     Map<String, String> headers) throws Exception {
        HttpURLConnection urlConnection = null;
        try {
            urlConnection = (HttpURLConnection) endpoint.openConnection();
            try {
                urlConnection.setRequestMethod("PUT");
            } catch (ProtocolException e) {
                throw new Exception(
                        "Shouldn't happen: HttpURLConnection doesn't support POST??",
                        e);
            }
            urlConnection.setDoOutput(true);

            if (headers != null && headers.size() > 0) {
                Iterator<String> itr = headers.keySet().iterator();
                while (itr.hasNext()) {
                    String key = itr.next();
                    urlConnection.setRequestProperty(key, headers.get(key));
                }
            }
            OutputStream out = urlConnection.getOutputStream();
            try {
                Writer writer = new OutputStreamWriter(out, "UTF-8");
                writer.write(postBody);
                writer.close();
            } catch (IOException e) {
                throw new Exception("IOException while puting data", e);
            } finally {
                if (out != null) {
                    out.close();
                }
            }

            // Get the response
            StringBuilder sb = new StringBuilder();
            BufferedReader rd = null;
            try {
                rd = new BufferedReader(new InputStreamReader(
                        urlConnection.getInputStream()));
                String line;
                while ((line = rd.readLine()) != null) {
                    sb.append(line);
                }
            } catch (FileNotFoundException ignored) {
            } finally {
                if (rd != null) {
                    rd.close();
                }
            }
            Iterator<String> itr = urlConnection.getHeaderFields().keySet()
                    .iterator();
            Map<String, String> responseHeaders = new HashMap();
            while (itr.hasNext()) {
                String key = itr.next();
                if (key != null) {
                    responseHeaders.put(key, urlConnection.getHeaderField(key));
                }
            }
            return new HttpResponse(sb.toString(),
                    urlConnection.getResponseCode(), responseHeaders);

        } catch (IOException e) {
            throw new Exception("Connection error (is server running at "
                    + endpoint + " ?): " + e);
        } finally {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
        }
    }

    public static HttpResponse doDelete(URL endpoint,
                                        Map<String, String> headers) throws Exception {
        HttpURLConnection urlConnection = null;
        try {
            urlConnection = (HttpURLConnection) endpoint.openConnection();
            try {
                urlConnection.setRequestMethod("DELETE");
            } catch (ProtocolException e) {
                throw new Exception(
                        "Shouldn't happen: HttpURLConnection doesn't support Delete??",
                        e);
            }
            urlConnection.setDoOutput(true);
            urlConnection.setDoInput(true);
            urlConnection.setUseCaches(false);
            urlConnection.setAllowUserInteraction(false);

            // setting headers
            if (headers != null && headers.size() > 0) {
                Iterator<String> itr = headers.keySet().iterator();
                while (itr.hasNext()) {
                    String key = itr.next();
                    urlConnection.setRequestProperty(key, headers.get(key));
                }
            }

            // Get the response
            StringBuilder sb = new StringBuilder();
            BufferedReader rd = null;
            try {
                rd = new BufferedReader(new InputStreamReader(
                        urlConnection.getInputStream()));
                String line;
                while ((line = rd.readLine()) != null) {
                    sb.append(line);
                }
            } catch (FileNotFoundException ignored) {
            } finally {
                if (rd != null) {
                    rd.close();
                }
            }
            Iterator<String> itr = urlConnection.getHeaderFields().keySet()
                    .iterator();
            Map<String, String> responseHeaders = new HashMap();
            while (itr.hasNext()) {
                String key = itr.next();
                if (key != null) {
                    responseHeaders.put(key, urlConnection.getHeaderField(key));
                }
            }
            return new HttpResponse(sb.toString(),
                    urlConnection.getResponseCode(), responseHeaders);

        } catch (IOException e) {
            throw new Exception("Connection error (is server running at "
                    + endpoint + " ?): " + e);
        } finally {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
        }
    }

    public static HttpResponse doPost(URL endpoint, String postBody,
                                      Map<String, String> headers) throws Exception {
        HttpURLConnection urlConnection = null;
        try {
            urlConnection = (HttpURLConnection) endpoint.openConnection();
            try {
                urlConnection.setRequestMethod("POST");
            } catch (ProtocolException e) {
                throw new Exception(
                        "Shouldn't happen: HttpURLConnection doesn't support POST??",
                        e);
            }
            urlConnection.setDoOutput(true);
            urlConnection.setDoInput(true);
            urlConnection.setUseCaches(false);
            urlConnection.setAllowUserInteraction(false);

            // setting headers
            if (headers != null && headers.size() > 0) {
                Iterator<String> itr = headers.keySet().iterator();
                while (itr.hasNext()) {
                    String key = itr.next();
                    urlConnection.setRequestProperty(key, headers.get(key));
                }
            }

            OutputStream out = urlConnection.getOutputStream();
            try {
                Writer writer = new OutputStreamWriter(out, "UTF-8");
                writer.write(postBody);
                writer.close();
            } catch (IOException e) {
                throw new Exception("IOException while posting data", e);
            } finally {
                if (out != null) {
                    out.close();
                }
            }

            // Get the response
            StringBuilder sb = new StringBuilder();
            BufferedReader rd = null;
            try {
                rd = new BufferedReader(new InputStreamReader(
                        urlConnection.getInputStream()));
                String line;
                while ((line = rd.readLine()) != null) {
                    sb.append(line);
                }
            } catch (FileNotFoundException ignored) {
            } finally {
                if (rd != null) {
                    rd.close();
                }
            }
            Iterator<String> itr = urlConnection.getHeaderFields().keySet()
                    .iterator();
            Map<String, String> responseHeaders = new HashMap();
            while (itr.hasNext()) {
                String key = itr.next();
                if (key != null) {
                    responseHeaders.put(key, urlConnection.getHeaderField(key));
                }
            }
            return new HttpResponse(sb.toString(),
                    urlConnection.getResponseCode(), responseHeaders);

        } catch (IOException e) {
            throw new Exception("Connection error (is server running at "
                    + endpoint + " ?): " + e);
        } finally {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
        }
    }

    public static HttpResponse doGet(String endpoint,
                                     Map<String, String> headers) throws IOException {
        HttpResponse httpResponse;
        if (endpoint.startsWith("http://")) {
            URL url = new URL(endpoint);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            conn.setDoOutput(true);
            conn.setReadTimeout(30000);

            // setting headers
            if (headers != null && headers.size() > 0) {
                Iterator<String> itr = headers.keySet().iterator();
                while (itr.hasNext()) {
                    String key = itr.next();
                    conn.setRequestProperty(key, headers.get(key));
                }
            }

            conn.connect();

            // Get the response
            StringBuilder sb = new StringBuilder();
            BufferedReader rd = null;
            try {
                rd = new BufferedReader(new InputStreamReader(
                        conn.getInputStream()));
                String line;
                while ((line = rd.readLine()) != null) {
                    sb.append(line);
                }
                httpResponse = new HttpResponse(sb.toString(),
                        conn.getResponseCode());
                httpResponse.setResponseMessage(conn.getResponseMessage());

            } catch (IOException ignored) {
                rd = new BufferedReader(new InputStreamReader(
                        conn.getErrorStream()));
                String line;
                while ((line = rd.readLine()) != null) {
                    sb.append(line);
                }
                httpResponse = new HttpResponse(sb.toString(),
                        conn.getResponseCode());
                httpResponse.setResponseMessage(conn.getResponseMessage());
            } finally {
                if (rd != null) {
                    rd.close();
                }
            }

            return httpResponse;
        }
        return null;
    }

    /**
     * convert org.apache.http.HttpResponse  to org.wso2.carbon.automation.core.utils.HttpResponse
     *
     * @param res org.apache.http.HttpResponse
     * @return org.wso2.carbon.automation.core.utils.HttpResponse
     * @throws Exception
     */
    public static HttpResponse convertResponse(org.apache.http.HttpResponse res) throws IOException {
        int responseCode = res.getStatusLine().getStatusCode();
        String data = "";
        InputStreamReader in = null;
        BufferedReader br = null;
        try {
            in = new InputStreamReader((res.getEntity().getContent()));
            br = new BufferedReader(in);
            String line = "";
            while ((line = br.readLine()) != null) {
                data = data.concat(line);
            }
        } finally {
            in.close();
            br.close();
        }
        HttpResponse response = new HttpResponse(data, responseCode);
        return response;
    }

    /**
     * This method is use to post data in multidata format
     * @param url
     *          - backend url
     * @param mobileApplicationBean
     *          - Bean class of the mobile application
     * @param headers
     *          - header files
     */
    public static String doPostMultiData(String url, MobileApplicationBean mobileApplicationBean
            , Map<String, String> headers) throws IOException {
        DefaultHttpClient httpClient = new DefaultHttpClient();
        HttpPost httpPost = new HttpPost(url);
        // initializing headers
        if (headers != null && headers.size() > 0) {
            Iterator<String> itr = headers.keySet().iterator();
            while (itr.hasNext()) {
                String key = itr.next();
                httpPost.setHeader(key, headers.get(key));
            }
        }
        MultipartEntityBuilder reqEntity;
        reqEntity = MultipartEntityBuilder.create();
        reqEntity.addPart("version", new StringBody(mobileApplicationBean.getVersion(),
                ContentType.MULTIPART_FORM_DATA));
        reqEntity.addPart("provider", new StringBody(mobileApplicationBean.getProvider(),
                ContentType.MULTIPART_FORM_DATA));
        reqEntity.addPart("markettype", new StringBody(mobileApplicationBean.getMarkettype(),
                ContentType.MULTIPART_FORM_DATA));
        reqEntity.addPart("platform", new StringBody(mobileApplicationBean.getPlatform(),
                ContentType.MULTIPART_FORM_DATA));
        reqEntity.addPart("name", new StringBody(mobileApplicationBean.getName(), ContentType.MULTIPART_FORM_DATA));
        reqEntity.addPart("description", new StringBody(mobileApplicationBean.getDescription(),
                ContentType.MULTIPART_FORM_DATA));
        FileBody bannerImageFile = new FileBody(mobileApplicationBean.getBannerFilePath());
        reqEntity.addPart("bannerFile", bannerImageFile);
        FileBody iconImageFile = new FileBody(mobileApplicationBean.getIconFile());
        reqEntity.addPart("iconFile", iconImageFile);
        FileBody screenShot1 = new FileBody(mobileApplicationBean.getScreenShot1File());
        reqEntity.addPart("screenshot1File", screenShot1);
        FileBody screenShot2 = new FileBody(mobileApplicationBean.getScreenShot2File());
        reqEntity.addPart("screenshot2File", screenShot2);
        FileBody screenShot3 = new FileBody(mobileApplicationBean.getScreenShot3File());
        reqEntity.addPart("screenshot3File", screenShot3);
        reqEntity.addPart("addNewAssetButton", new StringBody("Submit", ContentType.MULTIPART_FORM_DATA));
        reqEntity.addPart("mobileapp", new StringBody(mobileApplicationBean.getMobileapp(),
                ContentType.MULTIPART_FORM_DATA));
        reqEntity.addPart("sso_ssoProvider", new StringBody(mobileApplicationBean.getSso_ssoProvider(),
                ContentType.MULTIPART_FORM_DATA));
        reqEntity.addPart("appmeta", new StringBody(mobileApplicationBean.getAppmeta(),
                ContentType.MULTIPART_FORM_DATA));

        final HttpEntity entity = reqEntity.build();
        httpPost.setEntity(entity);
        ResponseHandler<String> responseHandler = new BasicResponseHandler();
        String responseBody ="";
        try{
            responseBody = httpClient.execute(httpPost, responseHandler);
        }catch (Exception e){
            e.printStackTrace();
        }
        return responseBody;
    }

}
