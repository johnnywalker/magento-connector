/**
 * Copyright (c) MuleSoft, Inc. All rights reserved. http://www.mulesoft.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.md file.
 */

package org.mule.module.magento;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import com.magento.api.*;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.mule.module.magento.api.MagentoException;
import org.mule.module.magento.api.catalog.model.MediaMimeType;
import org.springframework.core.io.ClassPathResource;

/**
 * Integration test of the {@link MagentoCloudConnector}
 */
public class MagentoCloudConnectorTestDriver
{
    private static final int EXISTENT_PRODUCT_ID = 11;
    /**A category that is supposed to exist, as a workaround to the create category magento bug*/
    private static final int ROOT_CATEGORY_ID = 3;
    private static final int CATEGORY_ID_1 = 4;
    private static final int CATEGORY_ID_2 = 5;
    private static final int CATEGORY_ID_3 = 6;
    
    private static final String ORDER_ID = "100000001";
    private MagentoCloudConnector connector;

    @Before
    public void setup() throws Exception
    {
        connector = new MagentoCloudConnector();
        //it looks like http://<host>/index.php/api/v2_soap
        connector.initialiseConnector("magento", "magento",
                "http://172.16.20.35/magento/index.php/api/v2_soap");
    }

    @Test
    public void listOrders() throws Exception
    {
        assertNotNull(connector.listOrders(null));
    }

    /**
     * Tests listing order invoices
     */
    @Test
    public void listOrdersInvoices() throws Exception
    {
        assertNotNull(connector.listOrdersInvoices(null));
    }

    /**
     * Tests getting information of an existent order.
     * The order {@link #ORDER_ID} must exist 
     */
    @Test
    public void getOrder() throws Exception
    {
        SalesOrderEntity orderInfo = connector.getOrder(ORDER_ID);
        assertNotNull(orderInfo);
        System.out.println(ToStringBuilder.reflectionToString(orderInfo));
    }

    /**
     * Tests adding a comment to an existent order
     */
    @Test
    public void addOrderComment() throws Exception
    {
        connector.addOrderComment(ORDER_ID, "status", "A comment", false);
    }

    @Test
    public void getShipmentCarriers() throws Exception
    {
        assertFalse(connector.getOrderShipmentCarriers(ORDER_ID).isEmpty());
    }

    /**
     * Tests getting an order that does not exists
     */
    @Test(expected = MagentoException.class)
    public void getOrderInexistent()
    {
        connector.getOrder("899966");
    }

    @Test
    public void updateStockItem() throws Exception
    {

        CatalogInventoryStockItemUpdateEntity entity = new CatalogInventoryStockItemUpdateEntity();
        entity.setManage_stock(0);
        entity.setUse_config_manage_stock(0);

        connector.updateStockItem(String.valueOf(EXISTENT_PRODUCT_ID), entity);
    }

    /**
     * Tests that filtering expressions are interpreted properly the the webservice
     * when passing numeric arguments
     */
    @Test
    public void getWithNumericFilter() throws Exception
    {
        assertEquals(connector.listOrders("").size(), connector.listOrders("gt(subtotal, 800)").size()
                                                      + connector.listOrders("lteq(subtotal, 800)").size());
    }

    /**
     * Tests that filtering expressions are interpreted properly the the webservice
     * when passing string arguments
     */
    @Test
    public void getWithStringFilter() throws Exception
    {
        assertEquals(connector.listOrders("").size(), 
                     connector.listOrders("eq(customer_firstname, 'John')").size() 
                         + connector.listOrders("neq(customer_firstname, 'John')").size());
    }

    /**
     * Test that a user can be created an deleted, and that such operations
     * impact on the customer listing
     */
    @Test
    public void createCustomer() throws Exception
    {
        final String email = "johndoe@mycia.com";
        final String firstname = "John";
        final String lastname = "Doe";
        assertEquals(0, countCustomers(email, firstname, lastname));

        CustomerCustomerEntityToCreate customer = new CustomerCustomerEntityToCreate();
        customer.setEmail(email);
        customer.setFirstname(firstname);
        customer.setLastname(lastname);
        customer.setPassword("123456");
        customer.setGroup_id(1);
        
        int customerId = connector.createCustomer(customer);
        try
        {
            assertEquals(1, countCustomers(email, firstname, lastname));
            assertEquals(firstname, 
                connector.getCustomer(customerId, Arrays.asList("firstname")).getFirstname());
        }
        finally
        {
            connector.deleteCustomer(customerId);
            assertEquals(0, countCustomers(email, firstname, lastname));
        }
    }
    
    /**
     * Test that a customer can be updated 
     */
    @Test
    public void updateCustomer() throws Exception
    {
        final String email = "johndoe@mycia.com";
        final String firstname = "John";
        final String lastname = "Doe";
        assertEquals(0, countCustomers(email, firstname, lastname));

        CustomerCustomerEntityToCreate customer = new CustomerCustomerEntityToCreate();
        customer.setEmail(email);
        customer.setFirstname(firstname);
        customer.setLastname(lastname);
        customer.setPassword("123456");
        customer.setGroup_id(1);

        int customerId = connector.createCustomer(customer);
        try
        {
            customer.setFirstname("Tom");
            connector.updateCustomer(customer.getCustomer_id(), customer);
            assertEquals("Tom", 
                connector.getCustomer(customerId, Arrays.asList("firstname")).getFirstname());
        }
        finally
        {
            connector.deleteCustomer(customerId);
        }
    }

    /**Counts customers that have the given email, firstname and lastname*/
    private int countCustomers(String email, String firstname, String lastname)
    {
        return connector.listCustomers(
            "eq(firstname,'" + firstname + "'), eq(lastname, '" + lastname + "'), eq(email, '" + email + "')").size();
    }
    
    /**Tests that customer groups can be listed*/
    @Test
    public void listCustomerGroups() throws Exception
    {
        assertFalse(connector.listCustomerGroups().isEmpty());
    }

    /**
     * Tests that the XXXCurrentStore SOAP methods can be used like a getter - they
     * are not very well documented
     * 
     * @throws Exception
     */
    @Test
    public void storeView() throws Exception
    {
        assertEquals(connector.getCatalogCurrentStoreView(), connector.getCatalogCurrentStoreView());
    }
    
    /**
     * Tests that search by SKU works
     * This test assumes that there exists a product with id {@link #EXISTENT_PRODUCT_ID}
     */
    @Test @Ignore("Broken since Magento 1.5.1.0")
    public void getExistentProductBySku() throws Exception
    {
        CatalogProductReturnEntity product = getExistentProductWithDescriptions();
        CatalogProductReturnEntity product2 = connector.getProduct(null, (String) product.getSku(), null, null,
                Arrays.asList("description"), Collections.<String>emptyList());
        assertEquals(product.getProduct_id(), product2.getProduct_id());
    }
    
    /**
     * Tests that search by ID or SKU works
     * This test assumes that there exists a product with id {@link #EXISTENT_PRODUCT_ID}
     */
    @Test
    public void getExistentProductByIdOrSku() throws Exception
    {
        CatalogProductReturnEntity product = getExistentProductWithDescriptions();
        CatalogProductReturnEntity product2 = connector.getProduct(null, null, (String) product.getSku(), null,
                Arrays.asList("description"), Collections.<String>emptyList());
        assertEquals(product.getProduct_id(), product2.getProduct_id());
    }

    /**
     * This test assumes that there exists a product with id {@link #EXISTENT_PRODUCT_ID}
     */
    @Test
    public void getAndUpdateExistentProduct() throws Exception
    {
        final String description = "A great wood kitchen table";
        final String shortDescription = "Best Product ever!";
        updateDescriptions(description, shortDescription);
        CatalogProductReturnEntity product = getExistentProductWithDescriptions();
        assertEquals(description, product.getDescription());
        assertEquals(shortDescription, product.getDescription());
        
        final Object description2 = "An acceptable kitchen table";
        final Object shortDescription2 = "A good product";
        updateDescriptions(description2, shortDescription2);
        product = getExistentProductWithDescriptions();
        assertEquals(description2, product.getDescription());
        assertEquals(shortDescription2, product.getDescription());
    }
    
    private void updateDescriptions(final Object description2, final Object shortDescription2)
    {

        CatalogProductCreateEntity entity = new CatalogProductCreateEntity();
        entity.setDescription(description2.toString());
        entity.setShort_description(shortDescription2.toString());
        connector.updateProduct(null, null, String.valueOf(EXISTENT_PRODUCT_ID), entity, null, null);
    }

    private CatalogProductReturnEntity getExistentProductWithDescriptions()
    {
        return connector.getProduct(EXISTENT_PRODUCT_ID, null, null, null, Arrays.asList("sku",
            "description", "short_description"), null);
    }
    
    
    /**
     * Test that products can be created, linked and deleted
     */
    @Test
    public void createAndLinkProduct() throws Exception
    {
        Integer productId = null;
        Integer productId2 = null;
        try
        {
            productId = connector.createProduct("simple", 4, "FOOO457", null, null, null);
            productId2 = connector.createProduct("simple", 4, "AOOO986", null, null, null);
            connector.addProductLink("related", productId, null, null, productId2.toString(), null);
        }
        finally
        {
            if (productId != null)
            {
                connector.deleteProduct(productId, null, null);
            }
            if (productId2 != null)
            {
                connector.deleteProduct(productId2, null, null);
            }
        }
    }
    
    /**
     * Test that product inventory can be set retrieved
     */
    @Test
    public void productInventory() throws Exception
    {
        Integer productId = null;
        CatalogProductCreateEntity entity = new CatalogProductCreateEntity();
        CatalogInventoryStockItemUpdateEntity stock = new CatalogInventoryStockItemUpdateEntity();
        stock.setQty("10");
        stock.setIs_in_stock(1);
        entity.setStock_data(stock);
        productId = connector.createProduct("simple", 4, "X8960", entity, null, null);
        try
        {
            List<CatalogInventoryStockItemEntity> stockItems = connector.listStockItems(Arrays.asList("X8960"));
            assertEquals(1, stockItems.size());
            assertEquals("10.0000", stockItems.get(0).getQty());
            assertEquals("1", stockItems.get(0).getIs_in_stock());
        }
        finally
        {
            if (productId != null)
            {
                connector.deleteProduct(productId, null, null);
            }
        }
    }

    /**
     * Test that product can be listed, and their special prices retrieved and
     * updated. It assumes that a product with sku 986320 exists
     */
    @Test
    public void specialPrices() throws Exception
    {
        Integer productId = null;
        try
        {
            int originalProductsCount = connector.listProducts(null, null).size();
            productId = connector.createProduct("simple", 4, "AK4596", null, null, null);
            assertEquals(originalProductsCount + 1, connector.listProducts(null, null).size());
            connector.updateProductSpecialPrice(null, null, productId.toString(), "6953.6", "2011-30-01", null, null);
            CatalogProductSpecialPriceReturnEntity
                    productSpecialPrice = connector.getProductSpecialPrice(productId, null, null,
                                                                           null);
            assertNotNull(productSpecialPrice);
            System.out.printf("Special price:%s%n", productSpecialPrice);
        }
        finally
        {
            if (productId != null)
            {
                connector.deleteProduct(productId, null, null);
            }
        }
    }

    /** Test that images can be uploaded, fetched and deleted */
    @Test
    public void createMedia() throws Exception
    {
        Integer productId = null;
        String fileName = null;
        try
        {
            productId = connector.createProduct("simple", 4, "W875651", null, null, null);
            int originalMediaCount = connector.listProductAttributeMedia(productId, null, null, null).size();

            fileName = connector.createProductAttributeMedia(productId, null, null, null, null,
                new ClassPathResource("img.gif").getInputStream(), MediaMimeType.GIF, "img.gif");
            assertNotNull(connector.getProductAttributeMedia(productId, null, null, fileName, null));

            assertEquals(originalMediaCount + 1, connector.listProductAttributeMedia(productId, null, null,
                null).size());
        }
        finally
        {
            if (productId != null)
            {
                if (fileName != null)
                {
                    connector.deleteProductAttributeMedia(productId, null, null, fileName);
                }
                connector.deleteProduct(productId, null, null);
            }
        }
    }

    @Test 
    public void createCategory() throws Exception
    {
        Integer categoryId = null;

        CatalogCategoryEntityCreate entity = new CatalogCategoryEntityCreate();
        entity.setName("Hardware");
        entity.setIs_active(1);
        try
        {
            categoryId = connector.createCategory(1,  entity, null);
        }
        finally
        {
            if (categoryId != null)
            {
                connector.deleteCategory(categoryId);
            }
        }
    }
    
    /**
     * Tests that can list countries and regions
     */
    @Test
    public void directory() throws Exception
    {
        List<DirectoryCountryEntity> countries = connector.listDirectoryCountries();
        assertFalse(countries.isEmpty());
        assertEquals("Andorra", countries.get(0).getName());
        assertEquals("United Arab Emirates", countries.get(1).getName());
        assertFalse(connector.listDirectoryRegions("US").isEmpty());
    }
    
    /**
     * Test that category attributes can be fetched 
     * 
     * This test assumes there exist a category with id {@link #CATEGORY_ID_1},
     * name subCategory1, active
     * and description "This a subcategory!"
     * 
     * @throws Exception
     */
    @Test
    public void getCategory() throws Exception
    {
        CatalogCategoryInfo attributes = connector.getCategory(CATEGORY_ID_1, null, Arrays.asList("name", "is_active",
                "description"));
        assertEquals(attributes.getName(), "SubCategory1");
        assertEquals(1, attributes.getIs_active());
        assertEquals(attributes.getDescription(), "This a subcategory!");
    }
    
    /**
     * Test that the category tree can be fetched. This tests assumes the existence
     * of a root category with id {@link #ROOT_CATEGORY_ID}, and that the category structure is the following:
     * <pre>
     * Root:{@value #ROOT_CATEGORY_ID} +- SubCategory1:{@value #CATEGORY_ID_1}
     *                                 |
     *                                 +-SubCategory3:{@value #CATEGORY_ID_3}
     *                                 |
     *                                 +-SubCategory2:{@value #CATEGORY_ID_2}   
     * </pre>
     */
    @Test
    public void getCategoryTree() throws Exception
    {
        CatalogCategoryTree categoryTree = connector.getCategoryTree(String.valueOf(ROOT_CATEGORY_ID), null);
        assertEquals(ROOT_CATEGORY_ID, categoryTree.getCategory_id());
        assertEquals(CATEGORY_ID_1, categoryTree.getChildren().getComplexObjectArray(0).getCategory_id());
        assertEquals(CATEGORY_ID_3, categoryTree.getChildren().getComplexObjectArray(1).getCategory_id());
        assertEquals(CATEGORY_ID_2, categoryTree.getChildren().getComplexObjectArray(2).getCategory_id());
    }
    
    /**
     * Gets the nth children of a category
     * @param categoryTree
     * @param pos
     * @return
     */
    @SuppressWarnings("unchecked")
    private Map<String, Object> getChildren(Map<String, Object> categoryTree, int pos)
    {
        return ((List<Map<String, Object>>) categoryTree.get("children")).get(pos);
    }
    
    @Test
    public void move() throws Exception
    {
        assertEquals(ROOT_CATEGORY_ID, connector.getCategory(CATEGORY_ID_2, null, Arrays.asList("parent_id")).getParent_id());
        
        connector.moveCategory(CATEGORY_ID_2, CATEGORY_ID_3, null);
        assertEquals(CATEGORY_ID_3, connector.getCategory(CATEGORY_ID_2, null, Arrays.asList("parent_id")).getParent_id());
        
        connector.moveCategory(CATEGORY_ID_2, ROOT_CATEGORY_ID, null);
        assertEquals(ROOT_CATEGORY_ID, connector.getCategory(CATEGORY_ID_2, null, Arrays.asList("parent_id")).getParent_id());
    }

    @Test
    public void listShoppingCartPaymentMethods() {
        connector.listShoppingCartPaymentMethods(34, "1");
    }
}
