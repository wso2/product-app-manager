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

import org.wso2.carbon.automation.core.utils.HttpResponse;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.ProtocolException;
import java.net.URL;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class HttpUtil {

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

}
