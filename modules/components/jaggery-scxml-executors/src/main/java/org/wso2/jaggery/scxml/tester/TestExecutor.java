
package org.wso2.jaggery.scxml.tester;


import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class TestExecutor {

    private static final Log log= LogFactory.getLog(TestExecutor.class);

    public TestExecutor(){
        log.info("Test executor has been called");
    }

    public String getName(){
        return "test name";
    }
}
