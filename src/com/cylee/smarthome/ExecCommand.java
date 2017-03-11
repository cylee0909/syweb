package com.cylee.smarthome;

import com.cylee.smarthome.model.BaseModel;
import com.cylee.smarthome.model.CommandModel;
import com.cylee.smarthome.model.ExecMode;
import com.cylee.smarthome.util.Rc4Util;
import com.cylee.socket.tcp.ConnectManager;
import com.cylee.socket.tcp.DataChannel;
import com.cylee.socket.tcp.SendDataListener;
import com.cylee.web.Log;

import javax.servlet.AsyncContext;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

/**
 * Created by cylee on 16/12/21.
 */
@WebServlet(urlPatterns = {"/exec"}, asyncSupported = true)
public class ExecCommand extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        doPost(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.setCharacterEncoding("utf-8");
        String time = req.getParameter("t");
        String sign = req.getParameter("s");
        BaseModel result = null;
        if (sign != null && sign.equals(Rc4Util.quickEncry(time))) {
            DataChannel channel = ConnectManager.getInstance().getChannel("INIT");
            if (channel != null) {
                String command = req.getParameter("command");
                String params = req.getParameter("params");
                final AsyncContext context = req.startAsync();
                Log.d("exec command = " + command);
                channel.sendString(CommandModel.create(command, params), new SendDataListener() {
                    @Override
                    public void onError(int errorCode) {
                        Log.d("exec command = " + command + " error = " + errorCode);
                        try {
                            PrintWriter wirter = context.getResponse().getWriter();
                            ExecMode mode = new ExecMode();
                            mode.setErrorCode(errorCode);
                            wirter.write(BaseModel.buildSuccess(mode).toJson());
                            wirter.flush();
                            context.complete();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onSuccess(String data) {
                        Log.d("exec command = " + command + " response = " + data);
                        try {
                            PrintWriter wirter = context.getResponse().getWriter();
                            ExecMode mode = new ExecMode();
                            mode.setErrorCode(0);
                            mode.setResult(data);
                            wirter.write(BaseModel.buildSuccess(mode).toJson());
                            wirter.flush();
                            context.complete();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                });
                return;
            }
        }
        if (result == null) {
            result = BaseModel.buildInvalidInput();
        }
        PrintWriter out = resp.getWriter();
        out.write(result.toJson());
        out.flush();
    }
}
