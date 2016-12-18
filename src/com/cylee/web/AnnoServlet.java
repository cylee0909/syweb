package com.cylee.web;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

/**
 * Created by cylee on 16/10/22.
 */
@WebServlet(name = "test", urlPatterns = {"/anno/*"})
public class AnnoServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        Log.d("context path = "+req.getContextPath()+" "+req.getRealPath("/"));
        Log.d("servlet path = "+req.getServletPath());
        Log.d("path info = "+req.getPathInfo());

        resp.setContentType("text/html");
        resp.addCookie(new Cookie("name", "myname"));
        resp.setStatus(HttpServletResponse.SC_OK);
        PrintWriter out = resp.getWriter();
        out.write("anno servlet");
        out.flush();
    }
}
