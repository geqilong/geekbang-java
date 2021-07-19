package com.salesmanager.rest.service;

import com.salesmanager.shop.model.catalog.product.ReadableProductList;

import javax.ws.rs.*;

/**
 * Product Items REST Service
 */
@Path("/services")
public interface ProductItemsRestService {

    @GET
    @Path("/public/{store}/products/group/{code}")
    ReadableProductList getProductItemsByGroup(@PathParam("store") String store, @PathParam("code") String code);

    @POST
    @Path("/public/product")
    @Consumes("application/json") // Accept:application/json
    String addProducts(@BeanParam ReadableProductList productList);
}
