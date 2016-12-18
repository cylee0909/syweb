package com.cylee.smarthome;

import com.cylee.smarthome.model.BaseModel;
import com.cylee.smarthome.model.Verify;
import com.cylee.smarthome.util.EncryptUtil;
import com.cylee.web.Log;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

/**
 * Created by cylee on 16/12/18.
 */
@WebServlet("/verify")
public class VerifyServlet extends HttpServlet{
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        doPost(req,resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String id = req.getParameter("id");
        BaseModel result = null;
        if (id != null) {
            Verify verify = new Verify();
            verify.setResult(EncryptUtil.getVerify(id));
            verify.setResult("invalid");
            result = BaseModel.buildSuccess(verify);
        } else {
            result = BaseModel.buildInvalidInput();
        }
        Log.d("id = "+id);
        PrintWriter out = resp.getWriter();
        out.write(result.toJson());
        out.flush();
    }
}
