package com.controller;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.snslogin.NaverLogin;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import java.io.IOException;

public class MainController extends HttpServlet {
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		response.setContentType("text/html; charset=utf-8");
		
		NaverLogin naver = new NaverLogin();
		String naverCodeURL = naver.getCodeURL(request);
		request.setAttribute("naverCodeURL", naverCodeURL);
		
		RequestDispatcher rd = request.getRequestDispatcher("/main.jsp");
		rd.include(request, response);
	}
}
