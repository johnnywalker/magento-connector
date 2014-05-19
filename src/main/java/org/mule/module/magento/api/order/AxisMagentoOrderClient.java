/**
 * Copyright (c) MuleSoft, Inc. All rights reserved. http://www.mulesoft.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.md file.
 */

package org.mule.module.magento.api.order;

import com.magento.api.*;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.Transformer;
import org.apache.commons.lang.Validate;
import org.mule.module.magento.api.AbstractMagentoClient;
import org.mule.module.magento.api.AxisPortProvider;
import org.mule.module.magento.api.order.model.Carrier;
import org.mule.module.magento.filters.FiltersParser;

import javax.validation.constraints.NotNull;
import java.rmi.RemoteException;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import static org.apache.commons.lang.BooleanUtils.toBoolean;
import static org.apache.commons.lang.BooleanUtils.toInteger;

public class AxisMagentoOrderClient extends AbstractMagentoClient
        implements MagentoOrderClient<RemoteException> {

    public AxisMagentoOrderClient(AxisPortProvider provider) {
        super(provider);
    }

    public List<SalesOrderListEntity> listOrders(String filter) throws RemoteException {
        return Arrays.asList(getPort().salesOrderList(
                new SalesOrderListRequestParam(getSessionId(), FiltersParser.parse(filter))).getResult()
                                      .getComplexObjectArray());
    }

    public com.magento.api.SalesOrderEntity getOrder(String orderId) throws RemoteException {
        return getPort().salesOrderInfo(new SalesOrderInfoRequestParam(getSessionId(), orderId)).getResult();
    }

    public boolean holdOrder(String orderId) throws RemoteException {
        return toBoolean(getPort().salesOrderHold(new SalesOrderHoldRequestParam(getSessionId(), orderId)).getResult());
    }

    public boolean unholdOrder(@NotNull String orderId) throws RemoteException {
        Validate.notNull(orderId);
        return toBoolean(
                getPort().salesOrderUnhold(new SalesOrderUnholdRequestParam(getSessionId(), orderId)).getResult());
    }

    public boolean cancelOrder(@NotNull String orderId) throws RemoteException {
        Validate.notNull(orderId);
        return toBoolean(
                getPort().salesOrderCancel(new SalesOrderCancelRequestParam(getSessionId(), orderId)).getResult());
    }

    public boolean addOrderComment(@NotNull String orderId,
                                   @NotNull String status,
                                   @NotNull String comment,
                                   boolean sendEmail) throws RemoteException {
        Validate.notNull(orderId);
        Validate.notNull(status);
        Validate.notNull(comment);
        return toBoolean(getPort().salesOrderAddComment(
                new SalesOrderAddCommentRequestParam(getSessionId(), orderId, status, comment, toInteger(sendEmail)))
                                  .getResult());
    }

    @NotNull
    public List<SalesOrderShipmentEntity> listOrdersShipments(String filter) throws RemoteException {
        return Arrays.asList(getPort().salesOrderShipmentList(
                new SalesOrderShipmentListRequestParam(getSessionId(), FiltersParser.parse(filter))).getResult()
                                      .getComplexObjectArray());
    }

    public SalesOrderShipmentEntity getOrderShipment(String shipmentId) throws RemoteException {
        return getPort().salesOrderShipmentInfo(new SalesOrderShipmentInfoRequestParam(getSessionId(), shipmentId))
                        .getResult();
    }

    public boolean addOrderShipmentComment(@NotNull String shipmentId,
                                           String comment,
                                           boolean sendEmail,
                                           boolean includeCommentInEmail) throws RemoteException {
        Validate.notNull(shipmentId);
        Validate.notNull(comment);
        return toBoolean(getPort().salesOrderShipmentAddComment(
                new SalesOrderShipmentAddCommentRequestParam(getSessionId(), shipmentId, comment,
                                                             toIntegerString(sendEmail),
                                                             toIntegerString(includeCommentInEmail))
        ).getResult());
    }

    @SuppressWarnings("unchecked")
    @NotNull
    public List<Carrier> getOrderShipmentCarriers(@NotNull String orderId) throws RemoteException {
        Validate.notNull(orderId);
        return (List<Carrier>) CollectionUtils.collect(
                Arrays.asList(getPort().salesOrderShipmentGetCarriers(
                                      new SalesOrderShipmentGetCarriersRequestParam(getSessionId(), orderId))
                                       .getResult()
                                       .getComplexObjectArray()
                ), new Transformer() {
                    public Object transform(Object input) {
                        AssociativeEntity entity = (AssociativeEntity) input;
                        return new Carrier(entity.getKey(), entity.getValue());
                    }
                }
        );
    }

    public int addOrderShipmentTrack(@NotNull String shipmentId,
                                     @NotNull String carrier,
                                     @NotNull String title,
                                     @NotNull String trackNumber) throws RemoteException {
        Validate.notNull(shipmentId);
        Validate.notNull(carrier);
        Validate.notNull(title);
        Validate.notNull(trackNumber);
        return getPort().salesOrderShipmentAddTrack(
                new SalesOrderShipmentAddTrackRequestParam(getSessionId(), shipmentId, carrier, title, trackNumber))
                        .getResult();
    }

    public boolean deleteOrderShipmentTrack(@NotNull String shipmentId, @NotNull String trackId)
            throws RemoteException {
        Validate.notNull(shipmentId);
        Validate.notNull(trackId);
        return toBoolean(getPort().salesOrderShipmentRemoveTrack(
                new SalesOrderShipmentRemoveTrackRequestParam(getSessionId(), shipmentId, trackId)).getResult());
    }

    public String createOrderShipment(@NotNull String orderId,
                                      List<OrderItemIdQty> itemsQuantities,
                                      String comment,
                                      boolean sendEmail,
                                      boolean includeCommentInEmail) throws RemoteException {
        return getPort().salesOrderShipmentCreate(
                new SalesOrderShipmentCreateRequestParam(
                        getSessionId(), orderId, itemsQuantities == null ? null :
                                                 new OrderItemIdQtyArray(
                                                         itemsQuantities.toArray(
                                                                 new OrderItemIdQty[itemsQuantities
                                                                         .size()]
                                                         )
                                                 ), comment,
                        toInteger(sendEmail), toInteger(includeCommentInEmail)
                )
        ).getResult();
    }

    public List<SalesOrderInvoiceEntity> listOrdersInvoices(String filter) throws RemoteException {
        return Arrays.asList(getPort().salesOrderInvoiceList(
                new SalesOrderInvoiceListRequestParam(getSessionId(), FiltersParser.parse(filter))).getResult()
                                      .getComplexObjectArray());
    }

    public SalesOrderInvoiceEntity getOrderInvoice(@NotNull String invoiceId) throws RemoteException {
        Validate.notNull(invoiceId);
        return getPort().salesOrderInvoiceInfo(new SalesOrderInvoiceInfoRequestParam(getSessionId(), invoiceId))
                        .getResult();
    }

    public String createOrderInvoice(@NotNull String orderId,
                                     @NotNull List<OrderItemIdQty> itemsQuantities,
                                     String comment,
                                     boolean sendEmail,
                                     boolean includeCommentInEmail) throws RemoteException {
        Validate.notNull(orderId);
        return getPort().salesOrderInvoiceCreate(
                new SalesOrderInvoiceCreateRequestParam(
                        getSessionId(), orderId,
                        new OrderItemIdQtyArray(itemsQuantities.toArray(
                                new OrderItemIdQty[itemsQuantities.size()])),
                        comment,
                        toIntegerString(sendEmail),
                        toIntegerString(includeCommentInEmail)
                )
        ).getResult();
    }

    public boolean addOrderInvoiceComment(@NotNull String invoiceId,
                                          @NotNull String comment,
                                          boolean sendEmail,
                                          boolean includeCommentInEmail) throws RemoteException {
        Validate.notNull(invoiceId);
        Validate.notNull(comment);
        return toBoolean(getPort().salesOrderInvoiceAddComment(
                new SalesOrderInvoiceAddCommentRequestParam(getSessionId(), invoiceId, comment,
                                                            toIntegerString(sendEmail),
                                                            toIntegerString(includeCommentInEmail))
        ).getResult());
    }

    public void captureOrderInvoice(@NotNull String invoiceId) throws RemoteException {
        Validate.notNull(invoiceId);
        getPort().salesOrderInvoiceCapture(new SalesOrderInvoiceCaptureRequestParam(getSessionId(), invoiceId))
                 .getResult();
    }

    public boolean voidOrderInvoice(@NotNull String invoiceId) throws RemoteException {
        Validate.notNull(invoiceId);
        return toBoolean(getPort().salesOrderInvoiceVoid(
                new SalesOrderInvoiceVoidRequestParam(getSessionId(), invoiceId)).getResult());
    }

    public void cancelOrderInvoice(@NotNull String invoiceId) throws RemoteException {
        Validate.notNull(invoiceId);
        getPort().salesOrderInvoiceCancel(new SalesOrderInvoiceCancelRequestParam(getSessionId(), invoiceId))
                 .getResult();
    }

    private OrderItemIdQty[] fromMap(Map<Integer, Double> map) {
        OrderItemIdQty[] quantities = new OrderItemIdQty[map.size()];
        int i = 0;
        for (Entry<Integer, Double> entry : map.entrySet()) {
            quantities[i] = new OrderItemIdQty(entry.getKey(), entry.getValue());
            i++;
        }
        return quantities;
    }

}
