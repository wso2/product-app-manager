/*
*  Copyright (c) 2005-2011, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
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

import org.junit.Test;
import org.wso2.tomcat.authenticator.JWTValidator;

import javax.xml.bind.DatatypeConverter;

import static org.junit.Assert.*;

public class JWTValidatorTest {

    @Test
    public void testIsValid() throws Exception {

        final DatatypeConverter datatypeConverter = null;
        final String TRUST_STORE_PATH = "/home/visitha/WAT/AppManager/wso2appm-1.2.0-SNAPSHOT/repository/resources/security/wso2carbon.jks";
        final String TRUST_STORE_PASSWORD = "wso2carbon";
        final String ALIAS = "wso2carbon";


        String jwtToken = "eyJ0eXAiOiJKV1QiLCJhbGciOiJSUzI1NiIsIng1dCI6Ik5rSkdPRVV4TXpaRlFqTTJSRFJCTlRaRlFUQTFRemRCUlRSQ09VRTBOVUkyTTBKR09UYzFSQT09In0=.eyJpc3MiOiJ3c28yLm9yZy9wcm9kdWN0cy9hcHBtIiwiZXhwIjoxNDU1Njg4OTg1ODAwLCJzdWIiOiJhZG1pbkBjYXJib24uc3VwZXIiLCJodHRwOi8vd3NvMi5vcmcvY2xhaW1zL3JvbGUiOiJJbnRlcm5hbC9UZXN0ZXItMS4xLjEsSW50ZXJuYWwvcHVibGlzaGVyLEludGVybmFsL1RvbWNhdCBUZXN0LXYxLjAuMCxJbnRlcm5hbC9jcmVhdG9yLGFkbWluLEludGVybmFsL3N1YnNjcmliZXIsSW50ZXJuYWwvc3RvcmUtYWRtaW4sSW50ZXJuYWwvZXZlcnlvbmUsSW50ZXJuYWwvSldUU2VjdXJpdHlGaWx0ZXJUZXNzdC12MS4wLjAsSW50ZXJuYWwveHh4LTEuMS4xLEludGVybmFsL0ZpcnN0QXBwLXYxLjAuMCJ9.kA26M9M4XTz9rvArLQcctpIbs/505tiA90gZPShozGHeDkM/yAzxllYwolv2L/gVUeC4kdmnsqUIG0Axyw6BnC+ANESS14kN06ulC2n1923pKfhhr8L8vJpLgyzVrYFb6bCxTk7/4m5Ox9rQVeicRifQyFzqGHy+TZe3bfWTBbA=";
        JWTValidator processor = new JWTValidator();
        assertTrue(processor.isValid(jwtToken, TRUST_STORE_PATH, TRUST_STORE_PASSWORD, ALIAS));
    }
}