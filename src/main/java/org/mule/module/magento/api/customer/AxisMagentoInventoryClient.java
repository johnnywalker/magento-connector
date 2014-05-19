/**
 * Copyright (c) MuleSoft, Inc. All rights reserved. http://www.mulesoft.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.md file.
 */

package org.mule.module.magento.api.customer;

import com.magento.api.*;
import org.apache.commons.lang.Validate;
import org.mule.module.magento.api.AbstractMagentoClient;
import org.mule.module.magento.api.AxisPortProvider;

import javax.validation.constraints.NotNull;
import java.rmi.RemoteException;
import java.util.Arrays;
import java.util.List;

public class AxisMagentoInventoryClient extends AbstractMagentoClient
        implements MagentoInventoryClient<RemoteException> {
    public AxisMagentoInventoryClient(AxisPortProvider provider) {
        super(provider);
    }

    public List<CatalogInventoryStockItemEntity> listStockItems(@NotNull List<String> productIdsOrSkus) throws
                                                                                                        RemoteException {
        Validate.notNull(productIdsOrSkus);
        Validate.notEmpty(productIdsOrSkus);
        return Arrays.asList(getPort().catalogInventoryStockItemList(
                new CatalogInventoryStockItemListRequestParam(getSessionId(),
                                                              toArrayOfString(productIdsOrSkus)
                )
        ).getResult().getComplexObjectArray());
    }

    public int updateStockItem(@NotNull String productIdOrSkus,
                               @NotNull CatalogInventoryStockItemUpdateEntity attributes)
            throws RemoteException {
        Validate.notNull(productIdOrSkus);
        Validate.notNull(attributes);
        return getPort().catalogInventoryStockItemUpdate(
                new CatalogInventoryStockItemUpdateRequestParam(getSessionId(), productIdOrSkus, attributes))
                        .getResult();
    }

}
