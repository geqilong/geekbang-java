package org.geektimes.projects.user.web.controller;

import org.geektimes.web.mvc.controller.PageController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;

import static org.geektimes.projects.user.web.constant.ClientEnum.CLIENT_ID;
import static org.geektimes.projects.user.web.constant.ClientEnum.REDIRECT_URL;

@Path("/index")
public class HomeController implements PageController {

    @GET
    @POST
    @Override
    public String execute(HttpServletRequest request, HttpServletResponse response) throws Throwable {
        request.setAttribute("clientId", CLIENT_ID.getValue());
        request.setAttribute("redirectUrl", REDIRECT_URL.getValue());
        return "home.jsp";
    }
}
