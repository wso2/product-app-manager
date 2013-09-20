package org.wso2.carbon.social.internal;


import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.osgi.framework.BundleContext;
import org.osgi.service.component.ComponentContext;
import org.wso2.carbon.social.SocialActivityServiceImpl;
import org.wso2.carbon.social.service.SocialActivityService;

/**
 * Registering {@link SocialActivityService}
 * @scr.component name="org.wso2.carbon.bam.service.data.publisher " immediate="true"
 */


public class SocialComponent {

    private static Log log = LogFactory.getLog(SocialComponent.class);

    protected void activate(ComponentContext context) {
        BundleContext bundleContext = context.getBundleContext();
        bundleContext.registerService(SocialActivityService.class, new SocialActivityServiceImpl(), null);
        log.info("Social Activity bundle is activated");
    }

    protected void deactivate(ComponentContext context) {

    }
}