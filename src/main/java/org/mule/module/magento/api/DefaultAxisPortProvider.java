/**
 * Copyright (c) MuleSoft, Inc. All rights reserved. http://www.mulesoft.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.md file.
 */

package org.mule.module.magento.api;

import com.magento.api.LoginParam;
import com.magento.api.Mage_Api_Model_Server_Wsi_HandlerPortType;
import com.magento.api.MagentoServiceLocator;
import org.apache.commons.lang.Validate;

import javax.validation.constraints.NotNull;
import javax.xml.rpc.ServiceException;
import java.rmi.RemoteException;

public class DefaultAxisPortProvider implements AxisPortProvider
{
    private final String username;
    private final String password;
    private final String address;

    /**
     * Creates the port provider
     * 
     * @param username
     * @param password
     * @param address
     */
    public DefaultAxisPortProvider(@NotNull String username, @NotNull String password, @NotNull String address)
    {
        Validate.notNull(username);
        Validate.notNull(password);
        Validate.notNull(address);
        this.username = username;
        this.password = password;
        this.address = address;
    }

    public Mage_Api_Model_Server_Wsi_HandlerPortType getPort() throws RemoteException
    {
        MagentoServiceLocator serviceLocator = new MagentoServiceLocator();
        serviceLocator.setMage_Api_Model_Server_Wsi_HandlerPortEndpointAddress(address);
        try
        {
            return serviceLocator.getMage_Api_Model_Server_Wsi_HandlerPort();
        }
        catch (ServiceException e)
        {
            throw new RemoteException("A Service exception occured while trying to create the port", e);
        }
    }

    /**
     * Need to authenticate before every call
     * 
     * @return Magento session ID
     * @throws Exception
     */
    public String getSessionId() throws RemoteException
    {
        return getPort().login(new LoginParam(username, password)).getResult();
    }
}
