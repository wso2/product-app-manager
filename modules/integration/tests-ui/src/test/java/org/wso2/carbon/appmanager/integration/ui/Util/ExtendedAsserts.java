/*
 * Copyright (c) 2015, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
 *
 * WSO2 Inc. licenses this file to you under the Apache License,
 * Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package org.wso2.carbon.appmanager.integration.ui.Util;

import static org.testng.Assert.fail;

/**
 * Some convenience asserts for unit testing
 */
public class ExtendedAsserts {

    /**
     * Checks whether the given string is a part of subject string.
     *
     * @param subject the string which we scan
     * @param expected the string part we search for in the subject
     * @param message message to be displayed upon failure
     */
    public static void assertContains(String subject, String expected, String message) {
        if (!subject.contains(expected)) {
            fail(message +" But was :["+subject+"]");
        }
    }
}
