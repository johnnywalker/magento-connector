/**
 * Copyright (c) MuleSoft, Inc. All rights reserved. http://www.mulesoft.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.md file.
 */

package org.mule.module.magento.automation.testcases;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.HashMap;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.mule.api.MuleEvent;
import org.mule.api.processor.MessageProcessor;

import com.magento.api.CatalogCategoryEntity;
import com.magento.api.CatalogCategoryEntityCreate;
import com.magento.api.CatalogCategoryTree;

public class GetCategoryTreeTestCases extends MagentoTestParent {
	
	@SuppressWarnings("unchecked")
	@Before
	public void setUp() {
		try {
			testObjects = (HashMap<String, Object>) context.getBean("getCategoryTree");
			
			CatalogCategoryEntityCreate parentCategory = (CatalogCategoryEntityCreate) testObjects.get("parentCategory");
			CatalogCategoryEntityCreate childCategory = (CatalogCategoryEntityCreate) testObjects.get("childCategory");
			
			int parentId = (Integer) testObjects.get("parentId");
			int parentCategoryId = createCategory(parentId, parentCategory);
			int childCategoryId = createCategory(parentCategoryId, childCategory);
			
			testObjects.put("parentCategoryId", parentCategoryId);
			testObjects.put("childCategoryId", childCategoryId);
		}
		catch (Exception e) {
			e.printStackTrace();
			fail();
		}
	}
	
	@Category({RegressionTests.class})
	@Test
	public void testGetCategoryTree() {
		try {
			int parentCategoryId = (Integer) testObjects.get("parentCategoryId");
			int childCategoryId = (Integer) testObjects.get("childCategoryId");
			
			MessageProcessor flow = lookupFlowConstruct("get-category-tree");
			MuleEvent response = flow.process(getTestEvent(testObjects));
			
			CatalogCategoryTree tree = (CatalogCategoryTree) response.getMessage().getPayload();
			assertTrue(tree.getCategory_id() == parentCategoryId);
			
			CatalogCategoryEntity[] children = tree.getChildren().getComplexObjectArray();
			CatalogCategoryEntity child = children[0]; // We only created 1 child.
			
			assertTrue(child.getCategory_id() == childCategoryId);
			assertTrue(child.getParent_id() == parentCategoryId);
		}
		catch (Exception e) {
			e.printStackTrace();
			fail();
		}
	}
	
	@After
	public void tearDown() {
		try {
			// Deleting the parent also deletes the child			
			int parentCategoryId = (Integer) testObjects.get("parentCategoryId");
			deleteCategory(parentCategoryId);
		}
		catch (Exception e) {
			e.printStackTrace();
			fail();
		}
	}
}
