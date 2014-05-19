/**
 * Copyright (c) MuleSoft, Inc. All rights reserved. http://www.mulesoft.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.md file.
 */


package org.mule.module.magento;

import org.junit.Test;
import org.mule.construct.Flow;
import org.mule.tck.junit4.FunctionalTestCase;

/**
 * Test for the xml mapping of the {@link MagentoCloudConnector}
 */
public class MagentoNamespaceHandlerTestCase extends FunctionalTestCase {

    @Test
    public void testNamespace() {

    }

    @Override
    protected String getConfigFile() {
        return "magento-namespace-config.xml";
    }

    public Flow lookupFlowConstruct(String name) {
        return (Flow) muleContext.getRegistry().lookupFlowConstruct(name);
    }

}
