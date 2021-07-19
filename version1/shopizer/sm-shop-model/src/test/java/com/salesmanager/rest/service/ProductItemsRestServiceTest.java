package com.salesmanager.rest.service;

import com.salesmanager.shop.model.catalog.product.ReadableProductList;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.eclipse.microprofile.rest.client.RestClientBuilder;
import org.junit.Test;

import java.net.URI;
import java.net.URISyntaxException;

public class ProductItemsRestServiceTest {

    @Test
    public void testGetProductItemsByGroup() throws URISyntaxException {
        URI uri = new URI("http://127.0.0.1:8080");
        ProductItemsRestService productItemsRestService = RestClientBuilder.newBuilder()
                .baseUri(uri)
                .build(ProductItemsRestService.class);

        ReadableProductList readableProductList = productItemsRestService.getProductItemsByGroup("DEFAULT", "FEATURED_ITEM");
        System.out.println(ToStringBuilder.reflectionToString(readableProductList));
    }
}
