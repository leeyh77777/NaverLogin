package com.controller;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.snslogin.NaverLogin;

public class NaverLoginController extends HttpServlet {

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		resp.setContentType("text/html; charset=utf-8");
		PrintWriter out = resp.getWriter();
		NaverLogin naver = new NaverLogin();
		try {
			String accessToken = naver.getAccessToken(req);
			HashMap<String, String> userInfo = naver.getUserProfile(accessToken);
			if (userInfo == null) {
				throw new Exception("로그인 실패!");
			} else {
				if(naver.isJoin(userInfo, req)) {
					boolean result = naver.login(req);
					if(!result) {
						throw new Exception ("로그인 실패!");
					}
					
					out.print("<script>location.replace('main');</script>");
				} else {
					out.print("<script>location.replace('member/join');</script>");
				}
			}
			
			
		} catch(Exception e) {
			out.printf("<script>alert('%s');location.href='member/login';<script>", e.getMessage());
		}
		
	}
	
}
