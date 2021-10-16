package com.filter;

import javax.servlet.Filter;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.FilterConfig;
import javax.servlet.FilterChain;
import javax.servlet.RequestDispatcher;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.ServletException;
import java.io.IOException;
import java.io.UnsupportedEncodingException;

import com.core.DB;
import com.models.dao.MemberDao;
import com.snslogin.NaverLogin;

/**
 * 사이트 공통 필터
 *
 */
public class CommonFilter implements Filter {
	
	// 정적 경로 -> 헤더, 푸터 출력 X 
	private String[] staticDirs = {"public", "logout"};
	
	
	@Override 
	public void init(FilterConfig filterConfig) {
		DB.init(filterConfig);
		
		try {
			NaverLogin.init(filterConfig);
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		
	}
	
	@Override
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws ServletException, IOException {
		/** 로그인 처리 */
		MemberDao.init(request);
		
		/** 기준 URI */
		String siteURL = request.getServletContext().getContextPath();
		request.setAttribute("siteURL", siteURL);
		
		
		printHeader(request, response);
		
		chain.doFilter(request, response);
	
		
		printFooter(request, response);
	}
	
	/** 헤더 HTML 출력 */
	public void printHeader(ServletRequest request, ServletResponse response) throws ServletException, IOException {
		if (isPrintOk(request)) { // 출력 가능 조건일때만 출력 
			response.setContentType("text/html; charset=utf-8");
			RequestDispatcher rd = request.getRequestDispatcher("/outline/header.jsp");
			rd.include(request, response);
		}
	}
	
	/** 푸터 HTML 출력 */
	public void printFooter(ServletRequest request, ServletResponse response) throws ServletException, IOException {
		if (isPrintOk(request)) {
			RequestDispatcher rd = request.getRequestDispatcher("/outline/footer.jsp");
			rd.include(request, response);
		}
	}
	
	/** 헤더, 푸터를 출력해도 되는지 체크하는 메서드 */
	public boolean isPrintOk(ServletRequest request) {
		
		/**
		 * 1. 요청 method이 GET이 아닌 경우 출력 제외(return false) (O)
		 * 			HttpServletRequest  -  getMethod()
		 * 2.정적 경로인 경우 헤더 푸터 출력 제외(return false)
		 *  	   URI에 정적 경로가 포함되어 있으면 false
		 *  		HttpServletRequest - getRequestURI();
		 */
		if (request instanceof HttpServletRequest) {
			HttpServletRequest req = (HttpServletRequest)request;
			if (!req.getMethod().toUpperCase().equals("GET")) {
				return false;
			}
			
			String URI = req.getRequestURI();
			for (String dir : staticDirs) {
				if (URI.indexOf("/" + dir) != -1) { 
					return false;
				}
			}
		}
		
		return true;
	}
}



