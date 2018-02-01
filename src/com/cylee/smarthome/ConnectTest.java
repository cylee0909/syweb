package com.cylee.smarthome;

import com.cylee.socket.tcp.ConnectManager;
import com.cylee.socket.tcp.DataChannel;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

/**
 * Created by cylee on 2018/1/21.
 */
@WebServlet(urlPatterns = {"/connect"}, asyncSupported = true)
public class ConnectTest extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        doPost(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        DataChannel channel = ConnectManager.getInstance().getChannel("INIT");
        PrintWriter writer = resp.getWriter();
        writer.write("current connect is "+(channel == null ? "empty!" : "connected!"));
        writer.close();
    }
}
