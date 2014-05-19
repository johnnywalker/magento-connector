/**
 * Copyright (c) MuleSoft, Inc. All rights reserved. http://www.mulesoft.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.md file.
 */

package org.mule.module.magento.api.shoppingCart;

import com.magento.api.*;
import org.mule.module.magento.api.AbstractMagentoClient;
import org.mule.module.magento.api.AxisPortProvider;

import java.rmi.RemoteException;
import java.util.Arrays;
import java.util.List;

import static org.mule.module.magento.api.util.MagentoObject.fromMap;

public class AxisMagentoShoppingCartClient extends AbstractMagentoClient
        implements MagentoShoppingCartClient<RemoteException> {
    public AxisMagentoShoppingCartClient(AxisPortProvider provider) {
        super(provider);
    }

    @Override
    public int createShoppingCart(String storeId) throws RemoteException {
        return getPort().shoppingCartCreate(new ShoppingCartCreateRequestParam(getSessionId(), storeId)).getResult();
    }

    @Override
    public ShoppingCartInfoEntity getShoppingCartInfo(int quoteId, String storeId) throws RemoteException {
        return getPort().shoppingCartInfo(new ShoppingCartInfoRequestParam(getSessionId(), quoteId, storeId))
                        .getResult();
    }

    @Override
    public List<ShoppingCartTotalsEntity> listShoppingCartTotals(int quoteId, String storeId) throws RemoteException {
        return Arrays.asList(getPort().shoppingCartTotals(
                new ShoppingCartTotalsRequestParam(getSessionId(), quoteId, storeId)).getResult()
                                      .getComplexObjectArray());
    }

    @Override
    public String createShoppingCartOrder(int quoteId, String storeId, List<String> licenses) throws RemoteException {
        return getPort().shoppingCartOrder(new ShoppingCartOrderRequestParam(getSessionId(), quoteId, storeId,
                                                                             licenses == null ? null :
                                                                             toArrayOfString(licenses)
        )).getResult();
    }

    @Override
    public List<ShoppingCartLicenseEntity> listShoppingCartLicenses(int quoteId, String storeId) throws
                                                                                                 RemoteException {
        return Arrays.asList(getPort().shoppingCartLicense(
                new ShoppingCartLicenseRequestParam(getSessionId(), quoteId, storeId)).getResult()
                                      .getComplexObjectArray());
    }

    @Override
    public boolean addShoppingCartProduct(int quoteId,
                                          List<ShoppingCartProductEntity> products,
                                          String storeId) throws RemoteException {
        return getPort().shoppingCartProductAdd(
                new ShoppingCartProductAddRequestParam(getSessionId(), quoteId,
                                                       new ShoppingCartProductEntityArray(products.toArray(
                                                               new ShoppingCartProductEntity[products.size()])),
                                                       storeId
                )
        ).isResult();
    }

    @Override
    public boolean updateShoppingCartProduct(int quoteId,
                                             List<ShoppingCartProductEntity> products,
                                             String storeId) throws RemoteException {

        return getPort().shoppingCartProductUpdate(
                new ShoppingCartProductUpdateRequestParam(getSessionId(), quoteId,
                                                          new ShoppingCartProductEntityArray(products.toArray(
                                                                  new ShoppingCartProductEntity[products.size()])),
                                                          storeId
                )
        ).isResult();
    }

    @Override
    public boolean removeShoppingCartProduct(int quoteId,
                                             List<ShoppingCartProductEntity> products,
                                             String storeId) throws RemoteException {
        return getPort().shoppingCartProductRemove(
                new ShoppingCartProductRemoveRequestParam(getSessionId(), quoteId,
                                                          new ShoppingCartProductEntityArray(products.toArray(
                                                                  new ShoppingCartProductEntity[products.size()])),
                                                          storeId
                )
        ).isResult();
    }

    @Override
    public List<CatalogProductEntity> listShoppingCartProducts(int quoteId, String storeId) throws RemoteException {
        return Arrays.asList(getPort().shoppingCartProductList(
                new ShoppingCartProductListRequestParam(getSessionId(), quoteId, storeId)).getResult()
                                      .getComplexObjectArray());
    }

    @Override
    public boolean moveShoppingCartProductToCustomerQuote(int quoteId,
                                                          List<ShoppingCartProductEntity> products,
                                                          String storeId) throws RemoteException {
        return getPort().shoppingCartProductMoveToCustomerQuote(
                new ShoppingCartProductMoveToCustomerQuoteRequestParam(
                        getSessionId(), quoteId, new ShoppingCartProductEntityArray(
                        products.toArray(new ShoppingCartProductEntity[products.size()])), storeId
                )
        ).isResult();
    }

    @Override
    public boolean setShoppingCartCustomer(int quoteId, ShoppingCartCustomerEntity customer, String storeId) throws
                                                                                                             RemoteException {
        return getPort().shoppingCartCustomerSet(
                new ShoppingCartCustomerSetRequestParam(getSessionId(), quoteId, customer, storeId)).isResult();
    }

    @Override
    public boolean setShoppingCartCustomerAddresses(int quoteId, List<ShoppingCartCustomerAddressEntity> addresses,
                                                    String storeId) throws RemoteException {
        return getPort().shoppingCartCustomerAddresses(
                new ShoppingCartCustomerAddressesRequestParam(
                        getSessionId(), quoteId, new ShoppingCartCustomerAddressEntityArray(addresses.toArray(
                        new ShoppingCartCustomerAddressEntity[addresses.size()])), storeId
                )
        ).isResult();
    }

    @Override
    public boolean setShoppingCartShippingMethod(int quoteId, String method, String storeId) throws RemoteException {
        return getPort().shoppingCartShippingMethod(
                new ShoppingCartShippingMethodRequestParam(getSessionId(), quoteId, method, storeId)).isResult();
    }

    @Override
    public List<ShoppingCartShippingMethodEntity> listShoppingCartShippingMethods(int quoteId, String storeId) throws
                                                                                                               RemoteException {
        return Arrays.asList(getPort().shoppingCartShippingList(
                new ShoppingCartShippingListRequestParam(getSessionId(), quoteId, storeId)).getResult()
                                      .getComplexObjectArray());
    }

    @Override
    public boolean setShoppingCartPaymentMethod(int quoteId, ShoppingCartPaymentMethodEntity method,
                                                String storeId) throws RemoteException {
        return getPort().shoppingCartPaymentMethod(
                new ShoppingCartPaymentMethodRequestParam(getSessionId(), quoteId, method, storeId)).isResult();
    }

    @Override
    public ShoppingCartPaymentMethodResponseEntity[] listShoppingCartPaymentMethods(int quoteId, String storeId) throws
                                                                                                                 RemoteException {
        return getPort().shoppingCartPaymentList(
                new ShoppingCartPaymentListRequestParam(getSessionId(), quoteId, storeId)).getResult()
                        .getComplexObjectArray();
    }

    @Override
    public boolean addShoppingCartCoupon(int quoteId, String couponCode, String storeId) throws RemoteException {
        return getPort().shoppingCartCouponAdd(
                new ShoppingCartCouponAddRequestParam(getSessionId(), quoteId, couponCode, storeId)).isResult();
    }

    @Override
    public boolean removeShoppingCartCoupon(int quoteId, String storeId) throws RemoteException {
        return getPort().shoppingCartCouponRemove(
                new ShoppingCartCouponRemoveRequestParam(getSessionId(), quoteId, storeId)).isResult();
    }
}
