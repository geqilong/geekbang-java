package org.geektimes.projects.user.web.controller;

import org.apache.commons.lang.StringUtils;
import org.geektimes.projects.context.ClassicComponentContext;
import org.geektimes.projects.user.service.UserService;
import org.geektimes.web.mvc.controller.PageController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import java.util.Map;

import static org.geektimes.projects.user.web.constant.ClientEnum.*;


/**
 * 输出 “Hello,World” Controller
 */
@Path("/hello")
public class HelloWorldController implements PageController {
    @GET
    @POST
    @Path("/world") // /hello/world -> HelloWorldController
    public String execute(HttpServletRequest request, HttpServletResponse response) throws Throwable {
        String code = request.getParameter("code");
        if (StringUtils.isNotEmpty(code)) {
            //请求Gitee获取access_token
            //根据access_token获取用户信息
            UserService userService = ClassicComponentContext.getInstance().getComponent("bean/UserService");
            Map giteeUserInfo = userService.queryGiteeUserInfo(code, CLIENT_ID.getValue(), CLIENT_SECRET.getValue(), REDIRECT_URL.getValue());
            request.setAttribute("userName", giteeUserInfo.get("name"));
        }
        return "index.jsp";
    }

}
