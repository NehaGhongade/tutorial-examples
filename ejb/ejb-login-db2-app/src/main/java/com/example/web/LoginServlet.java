package com.example.web;

import com.example.ejb.LoginService;

import javax.ejb.EJB;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import java.io.IOException;

@WebServlet("/login")
public class LoginServlet extends HttpServlet {

  @EJB
  private LoginService loginService;

  @Override
  protected void doPost(HttpServletRequest request, HttpServletResponse response)
      throws ServletException, IOException {

    String username = request.getParameter("username");
    String password = request.getParameter("password");

   /* if (loginService.authenticate(username, password)) {
      response.getWriter().println("Login Successful For" + username );
    } else {
      response.getWriter().println("Login Failed For " + username );
    }*/
    boolean success = loginService.authenticate(username, password);

    response.setContentType("application/json");
    response.setCharacterEncoding("UTF-8");

    String jsonResponse = "{\"status\":\"" + (success ? "success" : "failure") + "\","
        + "\"username\":\"" + username + "\"}";

    response.getWriter().write(jsonResponse);
  }
}
