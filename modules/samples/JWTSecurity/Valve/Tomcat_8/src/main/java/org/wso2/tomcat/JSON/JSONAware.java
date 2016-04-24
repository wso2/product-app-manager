package org.wso2.tomcat.JSON;

/**
 * Beans that support customized output of JSON text shall implement this interface.  
 * @author FangYidong<fangyidong@yahoo.com.cn>
 */
public interface JSONAware {
	/**
	 * @return JSON text
	 */
	String toJSONString();
}
