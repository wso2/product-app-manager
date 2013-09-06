package org.wso2.jaggery.scxml.threading;


/**
 *  Copyright (c) WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */

import org.wso2.jaggery.scxml.threading.contexts.JaggeryThreadContext;

/*
Description: Encapsulates and provides access to a ThreadLocal variable holding a
            JaggeryThreadContext within a thread

    The following sources were used in the design and implementation of the ThreadLocal message sharing model.
    Source:http://veerasundar.com/blog/2010/11/java-thread-local-how-to-use-and-code-sample/
Filename: JaggeryThreadLocalMediator.java
Created Date: 3/9/2013
 */
public class JaggeryThreadLocalMediator {

    public static final ThreadLocal jaggeryThreadMessageThreadLocal=new ThreadLocal();

    public static void set(JaggeryThreadContext message){
         jaggeryThreadMessageThreadLocal.set(message);
    }

    public static void unset(){
        jaggeryThreadMessageThreadLocal.remove();
    }

    public static JaggeryThreadContext get(){
        return (JaggeryThreadContext) jaggeryThreadMessageThreadLocal.get();
    }

}
