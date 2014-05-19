/**
 * Copyright (c) MuleSoft, Inc. All rights reserved. http://www.mulesoft.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.md file.
 */

package org.mule.module.magento.api;

import com.magento.api.ArrayOfString;
import com.magento.api.Mage_Api_Model_Server_Wsi_HandlerPortType;
import org.apache.commons.lang.BooleanUtils;

import java.lang.reflect.Array;
import java.rmi.RemoteException;
import java.util.Collection;
import java.util.Collections;
import java.util.Map;

import static org.apache.commons.lang.BooleanUtils.toIntegerObject;

/**
 * Base class for all Magento clients. Magento clients follow the following
 * conventions:
 * <ul>
 * <li>They are parameterized by the exception type they throw. For example, axis
 * clients should be parameterized to throw {@link RemoteException}s, while
 * {@link MagentoClientAdaptor} will throw {@link MagentoException}s</li>
 * <li>They are parameterized by the way they model attributes and attributes
 * collections. For example, axis clients will answers arrays of magento objects for
 * representing collection of attributes, while {@link MagentoClientAdaptor} will
 * answer lists of maps, instead</li>
 * <li>Create and update operations take the target's object attributes in a Map with
 * String keys and Object values. </li>
 * <li>Retrieve operations return object's attributes in a Map similar to the
 * previously described</li>
 * </ul>
 */
public abstract class AbstractMagentoClient
{
    private final AxisPortProvider provider;

    public AbstractMagentoClient(AxisPortProvider provider)
    {
        this.provider = provider;
    }

    protected final String getSessionId() throws RemoteException
    {
        return provider.getSessionId();
    }

    protected final Mage_Api_Model_Server_Wsi_HandlerPortType getPort() throws RemoteException
    {
        return provider.getPort();
    }

    @SuppressWarnings("unchecked")
    protected static <T> T[] toArray(Collection<T> collection, Class<T> clazz)
    {
        return collection.toArray((T[]) Array.newInstance(clazz, collection.size()));
    }

    protected static ArrayOfString toArrayOfString(Collection<String> collection)
    {
        return new ArrayOfString(collection.toArray(new String[collection.size()]));
    }

    protected static String toIntegerString(boolean value)
    {
        return toIntegerObject(value).toString();
    }

    protected static boolean fromIntegerString(String value)
    {
        return BooleanUtils.toBoolean(Integer.parseInt(value));
    }
    
    protected <T> Collection<T> nullToEmpty(Collection<T> collection)
    {
        if (collection == null)
        {
            return Collections.emptyList();
        }
        return collection;
    }

    protected <K, V> Map<K, V> nullToEmpty(Map<K, V> attributes)
    {
        if (attributes == null)
        {
            return Collections.emptyMap();
        }
        return attributes;
    }

}
