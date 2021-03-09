package org.geektimes.projects.user.web.controller;

import org.geektimes.projects.user.context.ComponentContext;
import org.geektimes.projects.user.domain.User;
import org.geektimes.projects.user.service.UserService;
import org.geektimes.web.mvc.controller.PageController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import java.util.List;

@Path("/users")
public class UserController implements PageController {

    @GET
    @POST
    public String execute(HttpServletRequest request, HttpServletResponse response) throws Throwable {
        UserService userService = ComponentContext.getInstance().getComponent("bean/UserService");
        List<User> userList = userService.queryAllUsers();
        request.setAttribute("users", userList);
        return "user/allUsers.jsp";
    }

}
