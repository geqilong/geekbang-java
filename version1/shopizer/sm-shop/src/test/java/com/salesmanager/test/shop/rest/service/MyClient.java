package com.salesmanager.test.shop.rest.service;

import javax.ws.rs.POST;
import javax.ws.rs.Path;

@Path("/actuator")
public interface MyClient {

    @POST
    @Path("/shutdown")
    String shutdown();
}
