/**
 * Copyright (c) MuleSoft, Inc. All rights reserved. http://www.mulesoft.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.md file.
 */

package org.mule.module.magento.api.directory;

import com.magento.api.DirectoryCountryEntity;
import com.magento.api.DirectoryCountryListRequestParam;
import com.magento.api.DirectoryRegionEntity;
import com.magento.api.DirectoryRegionListRequestParam;
import org.mule.module.magento.api.AbstractMagentoClient;
import org.mule.module.magento.api.AxisPortProvider;

import java.rmi.RemoteException;
import java.util.Arrays;
import java.util.List;

import javax.validation.constraints.NotNull;

import org.apache.commons.lang.Validate;

public class AxisMagentoDirectoryClient extends AbstractMagentoClient
        implements MagentoDirectoryClient<RemoteException> {
    public AxisMagentoDirectoryClient(AxisPortProvider provider) {
        super(provider);
    }

    public List<DirectoryCountryEntity> listDirectoryCountries() throws RemoteException {
        return Arrays.asList(getPort().directoryCountryList(
                new DirectoryCountryListRequestParam(getSessionId())).getResult().getComplexObjectArray());
    }

    public List<DirectoryRegionEntity> listDirectoryRegions(@NotNull String countryId) throws RemoteException {
        Validate.notNull(countryId);
        return Arrays.asList(getPort().directoryRegionList(
                new DirectoryRegionListRequestParam(getSessionId(), countryId)).getResult().getComplexObjectArray());
    }

}
