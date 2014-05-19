/**
 * Copyright (c) MuleSoft, Inc. All rights reserved. http://www.mulesoft.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.md file.
 */

package org.mule.module.magento.api;

import com.magento.api.Mage_Api_Model_Server_Wsi_HandlerPortType;

import java.rmi.RemoteException;

public interface AxisPortProvider
{

    Mage_Api_Model_Server_Wsi_HandlerPortType getPort() throws RemoteException;

    /**
     * Need to authenticate before every call
     * 
     * @return Magento session ID
     * @throws Exception
     */
    String getSessionId() throws RemoteException;

}
