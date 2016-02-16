/*
 *​Copyright (c) 2015, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
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

package org.wso2.appmanager.integration.utils;

import org.wso2.carbon.automation.test.utils.http.client.HttpResponse;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class HttpUtil {

    /**
     * Send a PUT request.
     * @param endpoint Url.
     * @param putBody String.
     * @param headers Map<String, String>.
     * @return httpResponse HttpResponse.
     * @throws IOException on errors.
     */
    public static HttpResponse doPut(URL endpoint, String putBody, Map<String, String> headers) throws IOException {
        HttpURLConnection urlConnection = null;
        OutputStream out = null;
        Writer writer = null;
        try {
            urlConnection = (HttpURLConnection) endpoint.openConnection();
            urlConnection.setRequestMethod("PUT");
            urlConnection.setDoOutput(true);

            setHeadersToRequest(urlConnection, headers);

            out = urlConnection.getOutputStream();
            writer = new OutputStreamWriter(out, "UTF-8");
            writer.write(putBody);
            writer.close();
            String response = readResponse(urlConnection);
            Map<String, String> responseHeaders = readHeadersFromResponse(urlConnection);
            return new HttpResponse(response, urlConnection.getResponseCode(), responseHeaders);
        } finally {
            closeAll(urlConnection, writer, out);
        }
    }

    /**
     * Send a DELETE request.
     * @param endpoint URL.
     * @param headers  Map<String, String>.
     * @return httpResponse HttpResponse.
     * @throws Exception on errors.
     */
    public static HttpResponse doDelete(URL endpoint, Map<String, String> headers) throws IOException {
        HttpURLConnection urlConnection = null;
        try {
            urlConnection = (HttpURLConnection) endpoint.openConnection();
            urlConnection.setRequestMethod("DELETE");
            urlConnection.setDoOutput(true);
            urlConnection.setDoInput(true);
            urlConnection.setUseCaches(false);
            urlConnection.setAllowUserInteraction(false);

            // setting headers
            setHeadersToRequest(urlConnection, headers);
            String response = readResponse(urlConnection);
            Map<String, String> responseHeaders = readHeadersFromResponse(urlConnection);
            return new HttpResponse(response, urlConnection.getResponseCode(), responseHeaders);
        } finally {
            closeAll(urlConnection, null, null);
        }
    }

    /**
     * Send a POST request.
     * @param endpoint URL.
     * @param postBody String.
     * @param headers Map<String, String>.
     * @return httpResponse HttpResponse.
     * @throws Exception on errors.
     */
    public static HttpResponse doPost(URL endpoint, String postBody, Map<String, String> headers) throws IOException {
        HttpURLConnection urlConnection = null;
        Writer writer = null;
        OutputStream out = null;
        try {
            urlConnection = (HttpURLConnection) endpoint.openConnection();
            urlConnection.setRequestMethod("POST");
            urlConnection.setDoOutput(true);
            urlConnection.setDoInput(true);
            urlConnection.setUseCaches(false);
            urlConnection.setAllowUserInteraction(false);

            // setting headers
            setHeadersToRequest(urlConnection, headers);

            out = urlConnection.getOutputStream();
            writer = new OutputStreamWriter(out, "UTF-8");
            writer.write(postBody);
            writer.close();

            String response = readResponse(urlConnection);
            Map<String, String> responseHeaders = readHeadersFromResponse(urlConnection);
            return new HttpResponse(response, urlConnection.getResponseCode(), responseHeaders);
        } finally {
            closeAll(urlConnection, writer, out);
        }
    }

    /**
     * send a GET request.
     * @param endpoint String.
     * @param headers  Map<String, String>.
     * @return httpResponse HttpResponse.
     * @throws IOException on errors.
     */
    public static HttpResponse doGet(String endpoint, Map<String, String> headers) throws IOException {
        HttpURLConnection conn = null;
        HttpResponse httpResponse;
        try {
            URL url = new URL(endpoint);
            conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            conn.setDoOutput(true);
            conn.setReadTimeout(30000);

            // setting headers
            setHeadersToRequest(conn, headers);

            String response = readResponse(conn);
            httpResponse = new HttpResponse(response, conn.getResponseCode());
            httpResponse.setResponseMessage(conn.getResponseMessage());
            return httpResponse;
        } finally {
            closeAll(conn, null, null);
        }
    }

    private static void closeAll(HttpURLConnection urlConnection, Writer writer, OutputStream out) throws IOException {
        if (urlConnection != null) {
            urlConnection.disconnect();
        }
        if (out != null) {
            out.close();
        }
        if (writer != null) {
            writer.close();
        }
    }

    private static void setHeadersToRequest(HttpURLConnection urlConnection, Map<String, String> headers) {
        if (headers != null && headers.size() > 0) {
            Iterator<String> itr = headers.keySet().iterator();
            while (itr.hasNext()) {
                String key = itr.next();
                urlConnection.setRequestProperty(key, headers.get(key));
            }
        }
    }

    private static String readResponse(HttpURLConnection urlConnection) throws IOException {
        InputStream in = null;
        BufferedReader rd = null;
        try {
            StringBuilder sb = new StringBuilder();
            int responseCode = urlConnection.getResponseCode();
            if (responseCode / 100 == 2) { //For 20X response codes.
                in = urlConnection.getInputStream();
            } else {
                in = urlConnection.getErrorStream();
            }
            rd = new BufferedReader(new InputStreamReader(in));
            String line;
            while ((line = rd.readLine()) != null) {
                sb.append(line);
            }
            return sb.toString();
        } finally {
            if (rd != null) {
                rd.close();
            }
            if (in != null) {
                in.close();
            }
        }
    }

    private static Map<String, String> readHeadersFromResponse(HttpURLConnection urlConnection) {
        Iterator<String> itr = urlConnection.getHeaderFields().keySet().iterator();
        Map<String, String> responseHeaders = new HashMap();
        while (itr.hasNext()) {
            String key = itr.next();
            if (key != null) {
                responseHeaders.put(key, urlConnection.getHeaderField(key));
            }
        }
        return responseHeaders;
    }
}
