/**
 * Copyright (c) MuleSoft, Inc. All rights reserved. http://www.mulesoft.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.md file.
 */

package org.mule.module.magento.api.util;

import com.magento.api.SalesOrderEntity;
import com.magento.api.SalesOrderStatusHistoryEntity;
import com.magento.api.SalesOrderStatusHistoryEntityArray;
import org.junit.Test;

import java.util.List;
import java.util.Map;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.instanceOf;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertThat;

public class MagentoMapUnitTest
{

    @Test
    public void testToMapSimpleAttributes() throws Exception
    {
        SalesOrderEntity magentoObject = new SalesOrderEntity();
        magentoObject.setBase_tax_amount("1563.0");
        magentoObject.setBase_grand_total("10.5");
        Map<String, Object> map = MagentoMap.toMap(magentoObject);
        assertEquals(map.get("base_grand_total"), "10.5");
        assertEquals(map.get("base_tax_amount"), "1563.0");
    }

    @Test
    public void testNullAtributes() throws Exception
    {
        Map<String, Object> map = MagentoMap.toMap(new SalesOrderEntity());
        assertNull(map.get("bar"));
        assertNull(map.get("subtotal"));
        assertNull(map.get("status_history"));
    }

    @Test
    public void testMagentoArrayAttributes() throws Exception
    {
        SalesOrderEntity magentoObject = new SalesOrderEntity();
        magentoObject.setStatus_history(new SalesOrderStatusHistoryEntityArray(
                new SalesOrderStatusHistoryEntity[]{new SalesOrderStatusHistoryEntity()}));
        Map<String, Object> map = MagentoMap.toMap(magentoObject);
        assertThat(map.get("status_history"), instanceOf(MagentoMap.class));
        assertThat(((MagentoMap)map.get("status_history")).get("class").toString(),
                   equalTo(SalesOrderStatusHistoryEntityArray.class.toString()));
    }

    @Test(expected = IllegalArgumentException.class)
    public void testNonMagentoObject() throws Exception
    {
        MagentoMap.toMap("hello");
    }
}
