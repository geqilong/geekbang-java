package com.salesmanager.shop.rest;

import com.salesmanager.shop.model.catalog.product.ReadableProductList;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;

@Path("/services")
public interface ProductItemsService {

    @GET
    @Path("/public/{store}/products/group/{code}")
    ReadableProductList getProductItemsByGroup(@PathParam("store") String store, @PathParam("code") String code);
}