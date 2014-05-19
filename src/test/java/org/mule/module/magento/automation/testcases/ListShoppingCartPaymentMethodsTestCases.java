/**
 * Copyright (c) MuleSoft, Inc. All rights reserved. http://www.mulesoft.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.md file.
 */

package org.mule.module.magento.automation.testcases;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;

import java.util.List;
import java.util.Map;

import com.magento.api.ShoppingCartPaymentMethodResponseEntity;
import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.mule.api.MuleEvent;
import org.mule.api.processor.MessageProcessor;

import com.magento.api.AssociativeEntity;
import com.magento.api.ShoppingCartCustomerAddressEntity;
import com.magento.api.ShoppingCartPaymentMethodEntity;

public class ListShoppingCartPaymentMethodsTestCases extends MagentoTestParent {

	@SuppressWarnings("unchecked")
	@Before
	public void setUp() {
		try {
			testObjects = (Map<String, Object>) context.getBean("listShoppingCartPaymentMethods");

			String storeId = testObjects.get("storeId").toString();
			
			int quoteId = createShoppingCart(storeId);
			testObjects.put("quoteId", quoteId);
			
			ShoppingCartPaymentMethodEntity paymentMethod = (ShoppingCartPaymentMethodEntity) testObjects.get("paymentMethod");
			List<ShoppingCartCustomerAddressEntity> customerAddresses = (List<ShoppingCartCustomerAddressEntity>) testObjects.get("customerAddresses");
			setCustomerAddressesToShoppingCart(quoteId, customerAddresses);
			setShoppingCartPaymentMethod(quoteId, paymentMethod);
		}
		catch (Exception e) {
			e.printStackTrace();
			fail();
		}
	}
	
	@Category({RegressionTests.class})
	@Test
	@Ignore
	public void testListShoppingCartPaymentMethodsTestCases() {
		try {
			MessageProcessor flow = lookupFlowConstruct("list-shopping-cart-payment-methods");
			MuleEvent response = flow.process(getTestEvent(testObjects));
			
			ShoppingCartPaymentMethodResponseEntity cartPayments = (ShoppingCartPaymentMethodResponseEntity) response.getMessage().getPayload();
			assertNotNull(cartPayments);
			System.out.println(cartPayments.getCode());
			System.out.println(cartPayments.getTitle());
			for (AssociativeEntity entity : cartPayments.getCc_types().getComplexObjectArray()) {
				System.out.println(entity.getKey() + " : " + entity.getValue());
			}
		}
		catch (Exception e) {
			e.printStackTrace();
			fail();
		}
	}
	
	@After
	public void tearDown() {
		try {
			
		}
		catch (Exception e) {
			e.printStackTrace();
			fail();
		}
	}
}
