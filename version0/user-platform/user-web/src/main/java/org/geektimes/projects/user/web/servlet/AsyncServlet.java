package org.geektimes.projects.user.web.servlet;

import javax.servlet.AsyncContext;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

@WebServlet(asyncSupported = true, urlPatterns = "/async")
public class AsyncServlet extends HttpServlet {

    @Override
    public void service(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        PrintWriter writer = resp.getWriter();
        //开启异步上下文
        AsyncContext asyncServlet = req.startAsync();
        writer.printf("[线程:%s] AsyncContext 开始<p>", Thread.currentThread().getName());

        // HTTP 接受 Servlet 线程，将任务提交到异步线程
        // 当 Web Client 请求到当前 Servlet 时，开始通过当前 Servlet 执行线程操作，
        // 当 AsyncContext#start(Runnable) 方法执行后，将任务提交到运行 Runnable
        // 线程上，Servlet 执行线程快速释放。
        // Servlet 执行线程类似于 Netty 中的 Boss 线程，Runnable 执行线程相当于
        // Netty 中的 Worker 线程。
        asyncServlet.start(() -> {
            writer.printf("[线程:%s] AsyncContext Runnable 执行...", Thread.currentThread().getName());
        });

        //主动调用complete方法，指示异步上下文结束执行
        asyncServlet.complete();
    }
}
