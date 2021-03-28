package org.geektimes.projects.user.web.controller;

import org.geektimes.projects.context.ClassicComponentContext;
import org.geektimes.projects.user.domain.MessageResult;
import org.geektimes.projects.user.domain.User;
import org.geektimes.projects.user.service.UserService;
import org.geektimes.web.mvc.controller.PageController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;

@Path("/register")
public class RegController implements PageController {

    @POST
    @GET
    public String execute(HttpServletRequest request, HttpServletResponse response) throws Throwable {
        String name = request.getParameter("name");
        String password = request.getParameter("password");
        String email = request.getParameter("email");
        String phoneNumber = request.getParameter("phoneNumber");
        User user = new User();
        user.setName(name);
        user.setPassword(password);
        user.setEmail(email);
        user.setPhoneNumber(phoneNumber);
        UserService userService = ClassicComponentContext.getInstance().getComponent("bean/UserService");
        MessageResult msgRes = userService.register(user);
        if (msgRes.getStatus()) {
            return "success.jsp";
        } else {
            request.setAttribute("message", msgRes.getMessage());
            return "failure.jsp";
        }
    }

}
