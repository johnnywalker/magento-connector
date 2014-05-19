/**
 * Copyright (c) MuleSoft, Inc. All rights reserved. http://www.mulesoft.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.md file.
 */

package org.mule.module.magento.api.shoppingCart;

import com.magento.api.*;

import java.util.List;
import java.util.Map;

public interface MagentoShoppingCartClient<ExceptionType extends Exception>
{
    int createShoppingCart(String storeId) throws ExceptionType;

    ShoppingCartInfoEntity getShoppingCartInfo(int quoteId, String storeId) throws ExceptionType;

    List<ShoppingCartTotalsEntity> listShoppingCartTotals(int quoteId, String storeId) throws ExceptionType;

    String createShoppingCartOrder(int quoteId, String storeId, List<String> licenses) throws ExceptionType;

    List<ShoppingCartLicenseEntity> listShoppingCartLicenses(int quoteId, String storeId) throws ExceptionType;

    boolean addShoppingCartProduct(int quoteId,
                                   List<ShoppingCartProductEntity> products,
                                   String storeId) throws ExceptionType;

    boolean updateShoppingCartProduct(int quoteId,
                                      List<ShoppingCartProductEntity> products,
                                      String storeId) throws ExceptionType;

    boolean removeShoppingCartProduct(int quoteId,
                                      List<ShoppingCartProductEntity> products,
                                      String storeId) throws ExceptionType;

    List<CatalogProductEntity> listShoppingCartProducts(int quoteId, String storeId) throws ExceptionType;

    boolean moveShoppingCartProductToCustomerQuote(int quoteId,
                                                   List<ShoppingCartProductEntity> products,
                                                   String storeId) throws ExceptionType;

    boolean setShoppingCartCustomer(int quoteId, ShoppingCartCustomerEntity customer, String storeId) throws ExceptionType;

    boolean setShoppingCartCustomerAddresses(int quoteId, List<ShoppingCartCustomerAddressEntity> addresses, String storeId) throws ExceptionType;

    boolean setShoppingCartShippingMethod(int quoteId, String method, String storeId) throws ExceptionType;

    List<ShoppingCartShippingMethodEntity> listShoppingCartShippingMethods(int quoteId, String storeId) throws ExceptionType;

    boolean setShoppingCartPaymentMethod(int quoteId, ShoppingCartPaymentMethodEntity method, String storeId) throws ExceptionType;

    ShoppingCartPaymentMethodResponseEntity[] listShoppingCartPaymentMethods(int quoteId, String storeId) throws ExceptionType;

    boolean addShoppingCartCoupon(int quoteId, String couponCode, String storeId) throws ExceptionType;

    boolean removeShoppingCartCoupon(int quoteId, String storeId) throws ExceptionType;
}
