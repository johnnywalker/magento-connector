/**
 * Copyright (c) MuleSoft, Inc. All rights reserved. http://www.mulesoft.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.md file.
 */

package org.mule.module.magento.api.catalog;

import com.magento.api.*;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.UnhandledException;
import org.apache.commons.lang.Validate;
import org.mule.module.magento.api.AbstractMagentoClient;
import org.mule.module.magento.api.AxisPortProvider;
import org.mule.module.magento.api.catalog.model.MediaMimeType;
import org.mule.module.magento.api.catalog.model.ProductIdentifier;
import org.mule.module.magento.filters.FiltersParser;
import org.mule.util.Base64;

import javax.validation.constraints.NotNull;
import java.io.IOException;
import java.io.InputStream;
import java.rmi.RemoteException;
import java.util.Arrays;
import java.util.List;

import static org.apache.commons.lang.BooleanUtils.toBoolean;
import static org.mule.module.magento.api.util.MagentoObject.fromMap;


public class AxisMagentoCatalogClient extends AbstractMagentoClient
        implements MagentoCatalogClient<RemoteException> {

    public AxisMagentoCatalogClient(AxisPortProvider provider) {
        super(provider);
    }

    /*Category*/

    /**
     * Lists product of a given category. See  catalog-category-assignedProducts SOAP method.
     *
     * @param categoryId
     * @return the listing of category products
     */
    public List<CatalogAssignedProduct> listCategoryProducts(int categoryId) throws RemoteException {
        return Arrays.asList(getPort().catalogCategoryAssignedProducts(
                new CatalogCategoryAssignedProductsRequestParam(getSessionId(), categoryId, null)).getResult()
                                      .getComplexObjectArray());
    }

    /**
     * Assign product to category. See catalog-category-assignProduct SOAP method
     *
     * @param categoryId
     * @param productId
     * @param position
     */
    public boolean addCategoryProduct(int categoryId,
                                      @NotNull ProductIdentifier productId,
                                      String position) throws RemoteException {
        return getPort()
                .catalogCategoryAssignProduct(
                        new CatalogCategoryAssignProductRequestParam(
                                getSessionId(), categoryId, productId.getIdentifierAsString(), position,
                                productId.getIdentifierType())
                ).isResult();
    }

    /**
     * Creates a new category. See catalog-category-create SOAP method.
     *
     * @param parentId
     * @param attributes
     * @param storeView
     * @return the new category id
     */
    public int createCategory(int parentId, @NotNull CatalogCategoryEntityCreate attributes, String storeView)
            throws RemoteException {
        Validate.notNull(attributes);
        return getPort().catalogCategoryCreate(
                new CatalogCategoryCreateRequestParam(getSessionId(), parentId, attributes, storeView)).getResult();
    }

    public int getCatalogCurrentStoreView() throws RemoteException {
        return getPort().catalogCategoryCurrentStore(new CatalogCategoryCurrentStoreRequestParam(getSessionId(), null))
                        .getResult();
    }

    public int updateCatalogCurrentStoreView(String storeViewIdOrCode) throws RemoteException {
        Validate.notNull(storeViewIdOrCode);
        return getPort().catalogCategoryCurrentStore(
                new CatalogCategoryCurrentStoreRequestParam(getSessionId(), storeViewIdOrCode)).getResult();
    }

    /**
     * Deletes a category. See  catalog-category-delete SOAP method
     *
     * @param categoryId
     * @return if the category is deleted
     */
    public boolean deleteCategory(int categoryId) throws RemoteException {
        return getPort().catalogCategoryDelete(new CatalogCategoryDeleteRequestParam(getSessionId(), categoryId))
                        .isResult();
    }

    /**
     * Answers category attributes. See catalog-category-info  SOAP method.
     *
     * @param categoryId
     * @param storeView
     * @param attributeNames
     * @return the category attributes
     */
    public CatalogCategoryInfo getCategory(int categoryId, String storeView, List<String> attributeNames) throws
                                                                                                          RemoteException {
        return getPort()
                .catalogCategoryInfo(new CatalogCategoryInfoRequestParam(
                        getSessionId(), categoryId, storeView,
                        toArrayOfString(attributeNames))).getResult();
    }

    public List<CatalogCategoryEntityNoChildren> listCategoryLevels(String website, String storeView,
                                                                    String parentCategory)
            throws RemoteException {
        return Arrays.asList(getPort().catalogCategoryLevel(
                new CatalogCategoryLevelRequestParam(getSessionId(), website, storeView, parentCategory)).getResult()
                                      .getComplexObjectArray());
    }

    /**
     * Move category in tree. See  catalog-category-move SOAP method.
     *
     * @param categoryId
     * @param parentId
     * @param afterId
     */
    public void moveCategory(int categoryId, int parentId, String afterId) throws RemoteException {
        getPort()
                .catalogCategoryMove(new CatalogCategoryMoveRequestParam(getSessionId(), categoryId, parentId, afterId))
                .isResult();
    }

    /**
     * Remove a product assignment. See catalog-category-removeProduct SOAP method.
     *
     * @param categoryId
     * @param productId
     * @return if the product is removed from the category
     */
    public boolean deleteCategoryProduct(int categoryId, @NotNull ProductIdentifier productId)
            throws RemoteException {
        return getPort().catalogCategoryRemoveProduct(
                new CatalogCategoryRemoveProductRequestParam(getSessionId(), categoryId,
                                                             productId.getIdentifierAsString(),
                                                             productId.getIdentifierType())
        ).isResult();
    }

    /**
     * Retrieve hierarchical tree. See  catalog-category-tree SOAP method.
     *
     * @param parentId
     * @param storeView
     * @return
     */
    public CatalogCategoryTree getCategoryTree(String parentId, String storeView) throws RemoteException {
        return getPort().catalogCategoryTree(new CatalogCategoryTreeRequestParam(getSessionId(), parentId, storeView))
                        .getResult();
    }

    /**
     * Updates a category. See catalog-category-update SOAP method
     *
     * @param categoryId
     * @param attributes
     * @param storeView
     * @return
     */
    public boolean updateCategory(int categoryId, @NotNull CatalogCategoryEntityCreate attributes, String storeView)
            throws RemoteException {
        Validate.notNull(attributes);
        return getPort().catalogCategoryUpdate(
                new CatalogCategoryUpdateRequestParam(getSessionId(), categoryId, attributes, storeView)).isResult();
    }

    /**
     * @param categoryId
     * @param productId
     * @param position
     * @return
     */
    public boolean updateCategoryProduct(int categoryId,
                                         @NotNull ProductIdentifier productId,
                                         String position) throws RemoteException {
        return getPort()
                .catalogCategoryUpdateProduct(
                        new CatalogCategoryUpdateProductRequestParam(getSessionId(), categoryId,
                                                                     productId.getIdentifierAsString(), position,
                                                                     productId.getIdentifierType())
                ).isResult();
    }

    public List<CatalogInventoryStockItemEntity> listInventoryStockItems(List<String> products) throws RemoteException {
        return Arrays.asList(getPort().catalogInventoryStockItemList(
                new CatalogInventoryStockItemListRequestParam(getSessionId(),
                                                              toArrayOfString(products))
        )
                                      .getResult().getComplexObjectArray());
    }

    /**
     * @param productId
     * @param attributes
     * @return
     */
    public int updateInventoryStockItem(@NotNull ProductIdentifier productId,
                                        @NotNull CatalogInventoryStockItemUpdateEntity attributes)
            throws RemoteException {
        Validate.notNull(attributes);
        return getPort().catalogInventoryStockItemUpdate(
                new CatalogInventoryStockItemUpdateRequestParam(getSessionId(), productId.getIdentifierAsString(),
                                                                attributes)
        ).getResult();
    }
 
    
    /*Product*/

    /**
     * Creates a new product
     *
     * @param type                 the new product's type
     * @param set                  the new product's set
     * @param sku                  the new product's sku
     * @param attributes           the standard attributes of the new product
     * @param additionalAttributes the non standard attributes of the new product
     * @return the new product's id
     */
    public int createProduct(@NotNull String type,
                             @NotNull int set,
                             @NotNull String sku,
                             CatalogProductCreateEntity attributes,
                             List<AssociativeEntity> additionalAttributes,
                             String storeView)
            throws RemoteException {
        Validate.notNull(type);
        Validate.notNull(set);
        Validate.notNull(sku);

        if (additionalAttributes != null) {
            attributes.setAdditional_attributes(
                    new AssociativeArray(additionalAttributes.toArray(
                            new AssociativeEntity[additionalAttributes.size()]))
            );
        }

        return getPort().catalogProductCreate(
                new CatalogProductCreateRequestParam(getSessionId(), type, String.valueOf(set), sku,
                                                     attributes, storeView)
        ).getResult();
    }


    /**
     * Deletes a product.
     * See catalog-product-delete SOAP method.
     *
     * @param productId the product identifier
     * @return if the product is deleted
     */
    public int deleteProduct(ProductIdentifier productId) throws RemoteException {
        return getPort().catalogProductDelete(new CatalogProductDeleteRequestParam(getSessionId(),
                                                                                   productId.getIdentifierAsString(),
                                                                                   productId.getIdentifierType()))
                        .getResult();
    }

    /**
     * Answers a product special price.
     * <p/>
     * See catalog-product-getSpecialPrice SOAP method.
     *
     * @param productId
     * @param storeView
     * @return
     */
    public CatalogProductSpecialPriceReturnEntity getProductSpecialPrice(@NotNull ProductIdentifier productId,
                                                                         String storeView)
            throws RemoteException {
        return getPort().catalogProductGetSpecialPrice(
                new CatalogProductGetSpecialPriceRequestParam(getSessionId(), productId.getIdentifierAsString(),
                                                              storeView)
        ).getResult();
    }

    /**
     * Answers a product's attributes. See catalog-product-info SOAP method.
     *
     * @param productId
     * @param storeView
     * @param attributeNames
     * @param additionalAttributeNames
     * @return the product attributes
     */
    public CatalogProductReturnEntity getProduct(@NotNull ProductIdentifier productId,
                                                 String storeView,
                                                 List<String> attributeNames,
                                                 List<String> additionalAttributeNames) throws RemoteException {
        Validate.notNull(productId);
        Validate.isTrue(CollectionUtils.isNotEmpty(attributeNames)
                        || CollectionUtils.isNotEmpty(additionalAttributeNames));

        CatalogProductRequestAttributes request =
                new CatalogProductRequestAttributes(
                        toArrayOfString(nullToEmpty(attributeNames)),
                        toArrayOfString(nullToEmpty(additionalAttributeNames)));

        return getPort().catalogProductInfo(
                new CatalogProductInfoRequestParam(getSessionId(), productId.getIdentifierAsString(), storeView,
                                                   request, productId.getIdentifierType())
        ).getResult();
    }


    /**
     * Retrieve products list by filters
     * See catalog-product-list SOAP method.
     *
     * @param filters   an optional filtering expression
     * @param storeView an optional storeView
     * @return the list of product attributes that match the given optional filtering expression
     */
    public List<CatalogProductEntity> listProducts(String filters, String storeView) throws RemoteException {
        return Arrays.asList(getPort().catalogProductList(
                new CatalogProductListRequestParam(getSessionId(), FiltersParser.parse(filters), storeView)).getResult()
                                      .getComplexObjectArray());
    }

    /**
     * Sets a product special price. See catalog-product-setSpecialPrice  SOAP method
     *
     * @param productId
     * @param specialPrice
     * @param fromDate
     * @param toDate
     * @param storeView
     * @return
     */
    public int updateProductSpecialPrice(@NotNull ProductIdentifier productId,
                                         @NotNull String specialPrice,
                                         String fromDate,
                                         String toDate,
                                         String storeView) throws RemoteException {
        Validate.notNull(specialPrice);
        Validate.notNull(productId);
        return getPort().catalogProductSetSpecialPrice(
                new CatalogProductSetSpecialPriceRequestParam(getSessionId(), productId.getIdentifierAsString(),
                                                              specialPrice,
                                                              fromDate,
                                                              toDate, storeView)
        ).getResult();
    }

    /**
     * Updates a product. At least one of attributes or additionalAttributes
     * must be non null and non empty. See catalog-category-updateProduct SOAP method
     *
     * @param attributes           attributes to update
     * @param additionalAttributes non standard product attributes to update
     */
    public boolean updateProduct(@NotNull ProductIdentifier productId,
                                 CatalogProductCreateEntity attributes,
                                 List<AssociativeEntity> additionalAttributes,
                                 String storeView) throws RemoteException {
        Validate.notNull(productId);
        Validate.isTrue((attributes != null) ||
                        (additionalAttributes != null));

        if (additionalAttributes != null) {
            //noinspection ConstantConditions
            attributes.setAdditional_attributes(
                    new AssociativeArray(additionalAttributes.toArray(
                            new AssociativeEntity[additionalAttributes.size()]))
            );
        }

        return getPort().catalogProductUpdate(
                new CatalogProductUpdateRequestParam(getSessionId(), productId.getIdentifierAsString(),
                                                     attributes, storeView, productId.getIdentifierType())
        ).isResult();
    }


    /*
     * Product Images
     */
    public String createProductAttributeMedia(@NotNull ProductIdentifier productId,
                                              CatalogProductAttributeMediaCreateEntity attributes,
                                              @NotNull InputStream content,
                                              @NotNull MediaMimeType mimeType,
                                              @NotNull String baseFileName,
                                              String storeView) throws RemoteException {
        Validate.notNull(productId);
        Validate.notNull(mimeType);

        CatalogProductImageFileEntity file =
                new CatalogProductImageFileEntity(encodeStream(content), mimeType.toString(), baseFileName);
        attributes.setFile(file);

        return getPort().catalogProductAttributeMediaCreate(
                new CatalogProductAttributeMediaCreateRequestParam(getSessionId(), productId.getIdentifierAsString(),
                                                                   attributes, storeView,
                                                                   productId.getIdentifierType())
        ).getResult();
    }


    private String encodeStream(InputStream content) {
        try {
            return Base64.encodeBytes(IOUtils.toByteArray(content));
        } catch (IOException e) {
            throw new UnhandledException("Could not encode the stream", e);
        } finally {
            IOUtils.closeQuietly(content);
        }
    }

    public CatalogProductImageEntity getProductAttributeMedia(@NotNull ProductIdentifier productId,
                                                              @NotNull String file,
                                                              String storeView
    ) throws RemoteException {
        return getPort()
                .catalogProductAttributeMediaInfo(
                        new CatalogProductAttributeMediaInfoRequestParam(getSessionId(),
                                                                         productId.getIdentifierAsString(), file,
                                                                         storeView,
                                                                         productId.getIdentifierType())
                ).getResult();
    }

    public List<CatalogProductImageEntity> listProductAttributeMedia(@NotNull ProductIdentifier productId,
                                                                     String storeViewIdOrCode)
            throws RemoteException {
        return Arrays.asList(getPort().catalogProductAttributeMediaList(
                new CatalogProductAttributeMediaListRequestParam(getSessionId(),
                                                                 productId.getIdentifierAsString(),
                                                                 storeViewIdOrCode,
                                                                 productId.getIdentifierType())
        ).getResult()
                                      .getComplexObjectArray());
    }

    public boolean deleteProductAttributeMedia(@NotNull ProductIdentifier productId, @NotNull String file)
            throws RemoteException {
        Validate.notNull(productId);
        Validate.notNull(file);
        return toBoolean(
                getPort().catalogProductAttributeMediaRemove(
                        new CatalogProductAttributeMediaRemoveRequestParam(getSessionId(),
                                                                           productId.getIdentifierAsString(),
                                                                           file,
                                                                           productId.getIdentifierType())
                )
                         .getResult()
        );
    }

    public List<CatalogProductAttributeMediaTypeEntity> listProductAttributeMediaTypes(int setId) throws
                                                                                                  RemoteException {
        return Arrays.asList(getPort().catalogProductAttributeMediaTypes(
                new CatalogProductAttributeMediaTypesRequestParam(getSessionId(), String.valueOf(setId))).getResult()
                                      .getComplexObjectArray());
    }

    public boolean updateProductAttributeMedia(@NotNull ProductIdentifier productId,
                                               String fileName,
                                               @NotNull CatalogProductAttributeMediaCreateEntity attributes,
                                               String storeView) throws RemoteException {
        Validate.notNull(attributes);
        return toBoolean(
                getPort().catalogProductAttributeMediaUpdate(
                        new CatalogProductAttributeMediaUpdateRequestParam(getSessionId(),
                                                                           productId.getIdentifierAsString(),
                                                                           fileName,
                                                                           attributes, storeView,
                                                                           productId.getIdentifierType())
                )
                         .getResult()
        );
    }

    /*
     * Category Attributes
     */

    public List<CatalogAttributeEntity> listCategoryAttributes() throws RemoteException {
        return Arrays.asList(getPort().catalogCategoryAttributeList(
                new CatalogCategoryAttributeListRequestParam(getSessionId())).getResult().getComplexObjectArray());
    }

    public List<CatalogAttributeOptionEntity> listCategoryAttributeOptions(@NotNull String attributeId,
                                                                           String storeView)
            throws RemoteException {
        Validate.notNull(attributeId);
        return Arrays.asList(getPort().catalogCategoryAttributeOptions(
                new CatalogCategoryAttributeOptionsRequestParam(getSessionId(), attributeId, storeView)).getResult()
                                      .getComplexObjectArray());
    }

    /* Product Attribute */

    public List<CatalogAttributeEntity> listProductAttributes(int setId) throws RemoteException {
        return Arrays.asList(getPort().catalogProductAttributeList(
                new CatalogProductAttributeListRequestParam(getSessionId(), setId)).getResult()
                                      .getComplexObjectArray());
    }

    @NotNull
    public List<CatalogAttributeOptionEntity> listProductAttributeOptions(String attributeId, String storeView)
            throws RemoteException {
        Validate.notNull(attributeId);
        return Arrays.asList(getPort().catalogProductAttributeOptions(
                new CatalogProductAttributeOptionsRequestParam(getSessionId(), Integer.parseInt(attributeId),
                                                               storeView)
        ).getResult()
                                      .getComplexObjectArray());
    }

    @NotNull
    public List<CatalogProductAttributeSetEntity> listProductAttributeSets() throws RemoteException {
        return Arrays.asList(getPort().catalogProductAttributeSetList(
                new CatalogProductAttributeSetListRequestParam(getSessionId())).getResult().getComplexObjectArray());
    }

    /* Product Type */

    @NotNull
    public List<CatalogProductTypeEntity> listProductTypes() throws RemoteException {
        return Arrays.asList(getPort().catalogProductTypeList(new CatalogProductTypeListRequestParam(getSessionId()))
                                      .getResult().getComplexObjectArray());
    }

    /* Product Tier Price */

    @NotNull
    public List<CatalogProductTierPriceEntity> listProductAttributeTierPrices(@NotNull ProductIdentifier productId
    )
            throws RemoteException {
        Validate.notNull(productId);

        return Arrays.asList(getPort().catalogProductAttributeTierPriceInfo(
                new CatalogProductAttributeTierPriceInfoRequestParam(getSessionId(),
                                                                     productId.getIdentifierAsString(),
                                                                     productId.getIdentifierType())
        ).getResult()
                                      .getComplexObjectArray());
    }

    public int updateProductAttributeTierPrice(@NotNull ProductIdentifier productId,
                                               @NotNull CatalogProductTierPriceEntity attributes)
            throws RemoteException {
        Validate.notNull(productId);
        Validate.notNull(attributes);

        return getPort().catalogProductAttributeTierPriceUpdate(
                new CatalogProductAttributeTierPriceUpdateRequestParam(getSessionId(),
                                                                       productId.getIdentifierAsString(),
                                                                       new CatalogProductTierPriceEntityArray(
                                                                               new CatalogProductTierPriceEntity[]{
                                                                                       attributes
                                                                               }
                                                                       ),
                                                                       productId.getIdentifierType()
                )
        ).getResult();
    }

    /* h. Product Link (related, cross sells, up sells, grouped) */

    public boolean addProductLink(@NotNull String type,
                                  @NotNull ProductIdentifier productId,
                                  @NotNull String linkedProductIdOrSku,
                                  CatalogProductLinkEntity attributes) throws RemoteException {
        Validate.notNull(type);
        Validate.notNull(productId);
        Validate.notNull(linkedProductIdOrSku);
        return fromIntegerString(
                getPort().catalogProductLinkAssign(
                        new CatalogProductLinkAssignRequestParam(getSessionId(), type,
                                                                 productId.getIdentifierAsString(),
                                                                 linkedProductIdOrSku,
                                                                 attributes, productId.getIdentifierType())
                ).getResult()
        );
    }

    public List<CatalogProductLinkAttributeEntity> listProductLinkAttributes(String type) throws RemoteException {
        return Arrays.asList(getPort().catalogProductLinkAttributes(
                new CatalogProductLinkAttributesRequestParam(getSessionId(), type)).getResult()
                                      .getComplexObjectArray());
    }

    public List<CatalogProductLinkEntity> listProductLink(@NotNull String type,
                                                          @NotNull ProductIdentifier productId) throws RemoteException {
        return Arrays.asList(getPort().catalogProductLinkList(
                new CatalogProductLinkListRequestParam(getSessionId(), type, productId.getIdentifierAsString(),
                                                       productId.getIdentifierType())
        ).getResult()
                                      .getComplexObjectArray());
    }

    public boolean deleteProductLink(@NotNull String type,
                                     @NotNull ProductIdentifier productId,
                                     @NotNull String linkedProductIdOrSku) throws RemoteException {
        return fromIntegerString(
                getPort().catalogProductLinkRemove(
                        new CatalogProductLinkRemoveRequestParam(getSessionId(), type,
                                                                 productId.getIdentifierAsString(),
                                                                 linkedProductIdOrSku,
                                                                 productId.getIdentifierType())
                ).getResult()
        );
    }

    public List<String> listProductLinkTypes() throws RemoteException {
        return Arrays.asList(getPort().catalogProductLinkTypes(
                new CatalogProductLinkTypesRequestParam(getSessionId())).getResult().getComplexObjectArray());
    }

    public boolean updateProductLink(@NotNull String type,
                                     @NotNull ProductIdentifier productId,
                                     @NotNull String linkedProduct,
                                     @NotNull CatalogProductLinkEntity attributes) throws RemoteException {
        Validate.notNull(attributes);
        Validate.notNull(type);
        Validate.notNull(linkedProduct);

        return fromIntegerString(
                getPort().catalogProductLinkUpdate(
                        new CatalogProductLinkUpdateRequestParam(getSessionId(), type,
                                                                 productId.getIdentifierAsString(), linkedProduct,
                                                                 attributes, productId.getIdentifierType())
                ).getResult()
        );
    }
}
