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


package org.wso2.carbon.appmgt.sample.jwt;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonPrimitive;
import net.oauth.jsontoken.JsonToken;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.codec.binary.StringUtils;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.regex.Pattern;

/**
 * Created by ace on 11/24/14.
 */
public class JWTProcessor {
    private HashMap<String, String> jwtDetails;

    public JsonToken deserialize(String tokenString) {
        String[] pieces = splitTokenString(tokenString);
        String jwtHeaderSegment = pieces[0];
        String jwtPayloadSegment = pieces[1];
        JsonParser parser = new JsonParser();
        JsonObject header = parser.parse(fromBase64ToJsonString(jwtHeaderSegment)).getAsJsonObject();
        JsonObject payload = parser.parse(fromBase64ToJsonString(jwtPayloadSegment)).getAsJsonObject();
        JsonToken jsonToken = new JsonToken(header, payload, null, tokenString);
        return jsonToken;
    }

    private String[] splitTokenString(String tokenString) {
        String[] pieces = tokenString.split(Pattern.quote("."));
        if (pieces.length != 3) {
            throw new IllegalStateException("Expected JWT to have 3 segments separated by '.', but it has " + pieces.length + " segments");
        }
        return pieces;
    }


    public Map process(String jwtToken){
        jwtDetails = new HashMap<String, String>();
        JsonToken token = deserialize(jwtToken);
//        JsonObject header = token.getHeader();
        JsonObject claims = token.getPayloadAsJsonObject();
        Set<Map.Entry<String, JsonElement>> claimSet = claims.entrySet();

        for(Map.Entry e : claimSet){
            JsonPrimitive primitive = (JsonPrimitive) e.getValue();
            jwtDetails.put(e.getKey().toString(), primitive.getAsString());
        }
        System.out.println("Claims : "+jwtDetails);
        return jwtDetails;
    }

    private static String fromBase64ToJsonString(String source) {
        return StringUtils.newStringUtf8(Base64.decodeBase64(source));
    }
}
