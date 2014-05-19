/**
 * Copyright (c) MuleSoft, Inc. All rights reserved. http://www.mulesoft.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.md file.
 */

package org.mule.module.magento;

import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.argThat;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.magento.api.*;
import com.sun.mail.iap.Argument;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.issues.GenericArgumentMatcher;
import org.mule.common.query.Field;
import org.mule.common.query.Query;
import org.mule.common.query.expression.*;
import org.mule.module.magento.api.AxisPortProvider;
import org.mule.module.magento.api.catalog.AxisMagentoCatalogClient;
import org.mule.module.magento.api.customer.AxisMagentoInventoryClient;
import org.mule.module.magento.api.directory.AxisMagentoDirectoryClient;
import org.mule.module.magento.api.inventory.AxisMagentoCustomerClient;
import org.mule.module.magento.api.order.AxisMagentoOrderClient;
import org.mule.module.magento.api.order.model.Carrier;
import org.mule.module.magento.api.shoppingCart.AxisMagentoShoppingCartClient;

import edu.emory.mathcs.backport.java.util.Collections;

public class MagentoCloudConnectorUnitTest {
    private static final String SESSION_ID = "A_SESSION_ID";
    private static final String ORDER_ID = "10001";
    private MagentoCloudConnector connector;
    private Mage_Api_Model_Server_Wsi_HandlerPortType port;

    @Before
    public void setup() throws Exception {
        connector = new MagentoCloudConnector();
        AxisPortProvider portProvider = mock(AxisPortProvider.class);
        port = mock(Mage_Api_Model_Server_Wsi_HandlerPortType.class);
        connector.setOrderClient(new AxisMagentoOrderClient(portProvider));
        connector.setCatalogClient(new AxisMagentoCatalogClient(portProvider));
        connector.setCustomerClient(new AxisMagentoCustomerClient(portProvider));
        connector.setDirectoryClient(new AxisMagentoDirectoryClient(portProvider));
        connector.setInventoryClient(new AxisMagentoInventoryClient(portProvider));
        connector.setShoppingCartClient(new AxisMagentoShoppingCartClient(portProvider));
        when(portProvider.getPort()).thenReturn(port);
        when(portProvider.getSessionId()).thenReturn(SESSION_ID);
    }

    @Test
    public void testSalesOrdersListNoFilters() throws Exception {
        when(port.salesOrderList(eq(
                new SalesOrderListRequestParam(SESSION_ID, new Filters())
        ))).thenReturn(
                new SalesOrderListResponseParam(
                        new SalesOrderListEntityArray(new SalesOrderListEntity[]{new SalesOrderListEntity()}))
        );
        assertEquals(1, connector.listOrders(null).size());
    }

    @Test
    public void testSalesOrdersList() throws Exception {
        when(port.salesOrderList(
                eq(new SalesOrderListRequestParam(
                        SESSION_ID,
                        new Filters(
                                null,
                                new ComplexFilterArray(
                                        new ComplexFilter[]{
                                                new ComplexFilter("customer_id", new AssociativeEntity("eq", "500"))
                                        }
                                )
                        )
                ))
        ))//
                .thenReturn(
                        new SalesOrderListResponseParam(
                                new SalesOrderListEntityArray(new SalesOrderListEntity[]{new SalesOrderListEntity()}))
                );
        assertEquals(1, connector.listOrders("eq(customer_id, 500)").size());
    }

    @Test
    public void testSalesOrderInfo() throws Exception {
        when(port.salesOrderInfo(eq(new SalesOrderInfoRequestParam(SESSION_ID, ORDER_ID))))
                .thenReturn(new SalesOrderInfoResponseParam(new SalesOrderEntity()));
        connector.getOrder(ORDER_ID);
    }

    @Test
    public void testSalesOrderHold() throws Exception {
        when(port.salesOrderHold(eq(new SalesOrderHoldRequestParam(SESSION_ID, ORDER_ID))))
                .thenReturn(new SalesOrderHoldResponseParam(1));
        connector.holdOrder(ORDER_ID);
        verify(port).salesOrderHold(eq(new SalesOrderHoldRequestParam(SESSION_ID, ORDER_ID)));
    }

    @Test
    public void testSalesOrderUnhold() throws Exception {
        when(port.salesOrderUnhold(eq(new SalesOrderUnholdRequestParam(SESSION_ID, ORDER_ID))))
                .thenReturn(new SalesOrderUnholdResponseParam(1));
        connector.unholdOrder(ORDER_ID);
        verify(port).salesOrderUnhold(eq(new SalesOrderUnholdRequestParam(SESSION_ID, ORDER_ID)));
    }

    @Test
    public void testSalesOrderCancel() throws Exception {
        when(port.salesOrderCancel(eq(new SalesOrderCancelRequestParam(SESSION_ID, ORDER_ID))))
                .thenReturn(new SalesOrderCancelResponseParam(1));
        connector.cancelOrder(ORDER_ID);
        verify(port).salesOrderCancel(new SalesOrderCancelRequestParam(SESSION_ID, ORDER_ID));
    }

    @Test
    public void testSalesOrderAddComment() throws RemoteException {
        when(port.salesOrderAddComment(eq(new SalesOrderAddCommentRequestParam(
                SESSION_ID, ORDER_ID, "status", "A comment", 0
        ))))
                .thenReturn(new SalesOrderAddCommentResponseParam(1));
        connector.addOrderComment(ORDER_ID, "status", "A comment", false);
        verify(port).salesOrderAddComment(
                eq(new SalesOrderAddCommentRequestParam(SESSION_ID, ORDER_ID, "status", "A comment", 0))
        );
    }

    @Test
    public void testSalesOrderShipmentsList() throws RemoteException {
        SalesOrderShipmentEntity shipment = new SalesOrderShipmentEntity();
        shipment.setIs_active("1");
        when(port.salesOrderShipmentList(eq(new SalesOrderShipmentListRequestParam(SESSION_ID, new Filters()))))
                .thenReturn(new SalesOrderShipmentListResponseParam(
                        new SalesOrderShipmentEntityArray(new SalesOrderShipmentEntity[]{shipment})));
        assertEquals(1, connector.listOrdersShipments("").size());
    }

    @Ignore
    @Test
    public void testSalesOrderShipmentInfo() {
        fail("Not yet implemented");
    }

    @Ignore
    @Test
    public void testSalesOrderShipmentComment() {
        fail("Not yet implemented");
    }

    @Test
    public void testSalesOrderShipmentGetCarriers() throws RemoteException {
        when(port.salesOrderShipmentGetCarriers(
                eq(new SalesOrderShipmentGetCarriersRequestParam(SESSION_ID, ORDER_ID)))) //
                .thenReturn(
                        new SalesOrderShipmentGetCarriersResponseParam(new AssociativeArray(
                                new AssociativeEntity[]{new AssociativeEntity("FDX", "Fedex Express")}
                        ))
                );
        assertEquals(Collections.singletonList(new Carrier("FDX", "Fedex Express")),
                     connector.getOrderShipmentCarriers(ORDER_ID));
    }


    @Test
    public void testSalesOrderShipmentAddTrack() throws RemoteException {
        when(port.salesOrderShipmentAddTrack(eq(new SalesOrderShipmentAddTrackRequestParam(
                SESSION_ID, "1", "carrier", "title", "track"
        ))))
                .thenReturn(new SalesOrderShipmentAddTrackResponseParam(1));
        connector.addOrderShipmentTrack("1", "carrier", "title", "track");
        verify(port).salesOrderShipmentAddTrack(eq(
                new SalesOrderShipmentAddTrackRequestParam(SESSION_ID, "1", "carrier", "title", "track")
        ));
    }


    @Test
    public void testSalesOrderShipmentRemoveTrack() throws RemoteException {
        when(port.salesOrderShipmentRemoveTrack(eq(new SalesOrderShipmentRemoveTrackRequestParam(
                SESSION_ID, "1", "id"
        ))))
                .thenReturn(new SalesOrderShipmentRemoveTrackResponseParam(1));
        connector.deleteOrderShipmentTrack("1", "id");
        verify(port).salesOrderShipmentRemoveTrack(
                eq(new SalesOrderShipmentRemoveTrackRequestParam(SESSION_ID, "1", "id")));
    }

    @Test
    public void testSalesOrderShipmentCreate() throws RemoteException {
        List<OrderItemIdQty> itemIdQtyList = new ArrayList<OrderItemIdQty>();
        OrderItemIdQty itemIdQty = new OrderItemIdQty();
        itemIdQty.setQty(10.0);
        itemIdQty.setOrder_item_id(100);
        itemIdQtyList.add(itemIdQty);

        when(port.salesOrderShipmentCreate(new SalesOrderShipmentCreateRequestParam(
                SESSION_ID, "foo", new OrderItemIdQtyArray(new OrderItemIdQty[]{new OrderItemIdQty(100, 10)}),
                "comment", 1, 0
        )))
                .thenReturn(new SalesOrderShipmentCreateResponseParam("1"));
        connector.createOrderShipment("foo", itemIdQtyList, "comment", true, false);
        verify(port).salesOrderShipmentCreate(eq(
                new SalesOrderShipmentCreateRequestParam(
                        SESSION_ID, "foo", new OrderItemIdQtyArray(new OrderItemIdQty[]{new OrderItemIdQty(100, 10)}),
                        "comment", 1, 0)
        ));
    }

    @Test
    public void testSalesOrderInvoicesList() throws RemoteException {
        when(port.salesOrderInvoiceList(eq(new SalesOrderInvoiceListRequestParam(SESSION_ID, new Filters()))))
                .thenReturn(
                        new SalesOrderInvoiceListResponseParam(
                                new SalesOrderInvoiceEntityArray(new SalesOrderInvoiceEntity[]{})
                        )
                );

        connector.listOrdersInvoices("");
        verify(port).salesOrderInvoiceList(eq(new SalesOrderInvoiceListRequestParam(SESSION_ID, new Filters())));
    }

    @Test
    public void testSalesOrderInvoiceInfo() throws RemoteException {
        when(port.salesOrderInvoiceInfo(eq(new SalesOrderInvoiceInfoRequestParam(
                SESSION_ID, "invoiceId"
        ))))
                .thenReturn(new SalesOrderInvoiceInfoResponseParam(new SalesOrderInvoiceEntity()));
        connector.getOrderInvoice("invoiceId");
        verify(port).salesOrderInvoiceInfo(new SalesOrderInvoiceInfoRequestParam(SESSION_ID, "invoiceId"));
    }

    @Test
    public void testSalesOrderInvoiceComment() throws RemoteException {
        when(port.salesOrderInvoiceAddComment(eq(
                new SalesOrderInvoiceAddCommentRequestParam(SESSION_ID, "invoiceId", "comment", "0", "1")
        )))
                .thenReturn(new SalesOrderInvoiceAddCommentResponseParam("1"));
        connector.addOrderInvoiceComment("invoiceId", "comment", false, true);
        verify(port).salesOrderInvoiceAddComment(eq(
                new SalesOrderInvoiceAddCommentRequestParam(SESSION_ID, "invoiceId", "comment", "0", "1")));
    }

    @Test
    public void testListInventoryStockItems() throws Exception {
        final String[] idsOrSkus = new String[]{"SK100", "155600", "7896"};
        when(port.catalogInventoryStockItemList(
                eq(new CatalogInventoryStockItemListRequestParam(
                        SESSION_ID, new ArrayOfString(idsOrSkus)
                ))
        ))
                .thenReturn(
                        new CatalogInventoryStockItemListResponseParam(new CatalogInventoryStockItemEntityArray(
                                new CatalogInventoryStockItemEntity[]{}
                        ))
                );
        connector.listInventoryStockItems(Arrays.asList(idsOrSkus));
        verify(port).catalogInventoryStockItemList(eq(
                new CatalogInventoryStockItemListRequestParam(SESSION_ID, new ArrayOfString(idsOrSkus))));
    }

    @Test
    public void queryTranslator() throws Exception {
        FieldComparation name = new FieldComparation(new EqualsOperator(), new Field("name", "java.lang.String"),
                                                     new org.mule.common.query.expression.StringValue("mariano"));
        FieldComparation age = new FieldComparation(new LessOperator(), new Field("age", "int"), new IntegerValue(30));
        And and = new And(name, age);

        Query query = mock(Query.class);
        when(query.getFilterExpression()).thenReturn(and);

        String nativeQuery = this.connector.toNativeQuery(query);
        assertEquals(nativeQuery, "eq(name,'mariano'), lt(age,30)");
    }

    @Test
    public void metadataKeys() throws Exception {
        this.connector.getMetadataKeys();
    }
}
