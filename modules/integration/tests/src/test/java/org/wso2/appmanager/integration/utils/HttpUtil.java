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
        OutputStream outputStream = null;
        Writer writer = null;
        try {
            urlConnection = (HttpURLConnection) endpoint.openConnection();
            urlConnection.setRequestMethod("PUT");
            urlConnection.setDoOutput(true);

            setHeadersToRequest(urlConnection, headers);

            outputStream = urlConnection.getOutputStream();
            writer = new OutputStreamWriter(outputStream, "UTF-8");
            writer.write(putBody);
            writer.close();
            String response = readResponse(urlConnection);
            Map<String, String> responseHeaders = readHeadersFromResponse(urlConnection);
            return new HttpResponse(response, urlConnection.getResponseCode(), responseHeaders);
        } finally {
            closeAll(urlConnection, writer, outputStream);
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
        OutputStream outputStream = null;
        try {
            urlConnection = (HttpURLConnection) endpoint.openConnection();
            urlConnection.setRequestMethod("POST");
            urlConnection.setDoOutput(true);
            urlConnection.setDoInput(true);
            urlConnection.setUseCaches(false);
            urlConnection.setAllowUserInteraction(false);

            // setting headers
            setHeadersToRequest(urlConnection, headers);

            outputStream = urlConnection.getOutputStream();
            writer = new OutputStreamWriter(outputStream, "UTF-8");
            writer.write(postBody);
            writer.close();

            String response = readResponse(urlConnection);
            Map<String, String> responseHeaders = readHeadersFromResponse(urlConnection);
            return new HttpResponse(response, urlConnection.getResponseCode(), responseHeaders);
        } finally {
            closeAll(urlConnection, writer, outputStream);
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
        HttpURLConnection httpURLConnection = null;
        HttpResponse httpResponse;
        try {
            URL url = new URL(endpoint);
            httpURLConnection = (HttpURLConnection) url.openConnection();
            httpURLConnection.setRequestMethod("GET");
            httpURLConnection.setDoOutput(true);
            httpURLConnection.setReadTimeout(30000);

            // setting headers
            setHeadersToRequest(httpURLConnection, headers);

            String response = readResponse(httpURLConnection);
            httpResponse = new HttpResponse(response, httpURLConnection.getResponseCode());
            httpResponse.setResponseMessage(httpURLConnection.getResponseMessage());
            return httpResponse;
        } finally {
            closeAll(httpURLConnection, null, null);
        }
    }

    private static void closeAll(HttpURLConnection httpURLConnection, Writer writer, OutputStream outputStream) throws IOException {
        if (httpURLConnection != null) {
            httpURLConnection.disconnect();
        }
        if (outputStream != null) {
            outputStream.close();
        }
        if (writer != null) {
            writer.close();
        }
    }

    private static void setHeadersToRequest(HttpURLConnection urlConnection, Map<String, String> headersMap) {
        if (headersMap != null && headersMap.size() > 0) {
            Iterator<String> iterator = headersMap.keySet().iterator();
            while (iterator.hasNext()) {
                String key = iterator.next();
                urlConnection.setRequestProperty(key, headersMap.get(key));
            }
        }
    }

    private static String readResponse(HttpURLConnection httpURLConnection) throws IOException {
        InputStream inputStream = null;
        BufferedReader bufferedReader = null;
        try {
            StringBuilder stringBuilder = new StringBuilder();
            int responseCode = httpURLConnection.getResponseCode();
            if (responseCode / 100 == 2) { //For 20X response codes.
                inputStream = httpURLConnection.getInputStream();
            } else {
                inputStream = httpURLConnection.getErrorStream();
            }
            bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
            String line;
            while ((line = bufferedReader.readLine()) != null) {
                stringBuilder.append(line);
            }
            return stringBuilder.toString();
        } finally {
            if (bufferedReader != null) {
                bufferedReader.close();
            }
            if (inputStream != null) {
                inputStream.close();
            }
        }
    }

    private static Map<String, String> readHeadersFromResponse(HttpURLConnection httpURLConnection) {
        Iterator<String> iterator = httpURLConnection.getHeaderFields().keySet().iterator();
        Map<String, String> responseHeaders = new HashMap();
        while (iterator.hasNext()) {
            String key = iterator.next();
            if (key != null) {
                responseHeaders.put(key, httpURLConnection.getHeaderField(key));
            }
        }
        return responseHeaders;
    }
}
