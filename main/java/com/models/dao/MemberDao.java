package com.models.dao;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.servlet.ServletRequest;
import java.sql.*;

import com.core.*;
import com.exception.*;
import org.mindrot.jbcrypt.*;

import com.models.dto.Member;
import com.snslogin.NaverLogin;


public class MemberDao {
	

	public static void init(ServletRequest request) {
		
		Member member = null;
		boolean isLogin = false;
		if (request instanceof HttpServletRequest) {
			HttpServletRequest req = (HttpServletRequest)request;
			HttpSession session = req.getSession();
			
			int memNo = 0;
			if (session.getAttribute("memNo") != null) {
				memNo = (Integer)session.getAttribute("memNo");
			}
			
			if (memNo > 0) { 
				MemberDao dao = new MemberDao();
				member = dao.get(memNo);
				if (member != null) {
					isLogin = true;
				}
			} // endif 
		} // endif 
		
		request.setAttribute("member", member);
		request.setAttribute("isLogin", isLogin);
	}
	

	public boolean join(HttpServletRequest request) throws AlertException {
		
	
		checkJoinData(request);
		
		NaverLogin naver = new NaverLogin();
		Member socialMember = naver.getSocialUserInfo(request);
		
		String sql = "INSERT INTO member (memId, memPw, memNm, socialChannel, socialId) VALUES(?,?,?,?,?)";
		try (Connection conn = DB.getConnection();
			PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
			String memId = request.getParameter("memId");
			String memPw = request.getParameter("memPw");
			String memNm = request.getParameter("memNm");
			
			String hash = "";
			
			if (socialMember == null) {
				hash = BCrypt.hashpw(memPw, BCrypt.gensalt(10));
			}
			String socialChannel = "none";
			String socialId = null;
			
			if(socialMember != null) {
				socialChannel = socialMember.getSocialChannel();
				socialId = socialMember.getSocialId();
			}
			
			pstmt.setString(1, memId);
			pstmt.setString(2, hash);
			pstmt.setString(3, memNm);
			pstmt.setString(4, socialChannel);
			pstmt.setString(5, socialId);
			
			int result = pstmt.executeUpdate();
			if (result < 1) 
				return false;

			if (socialMember != null) {
				naver.login(request);
			}
			
			return true;
			
		} catch (SQLException | ClassNotFoundException e) {
			e.printStackTrace();
			return false;
		}
	}
	

	public void checkJoinData(HttpServletRequest request) throws AlertException {

		NaverLogin naver = new NaverLogin();
		Member socialMember = naver.getSocialUserInfo(request);
		
		String[] required = null;
		if (socialMember == null) {
			required = new String[] {
			"memId//???????????? ???????????????",
			"memPw//??????????????? ???????????????",
			"memPwRe//??????????????? ??????????????????",
			"memNm//???????????? ???????????????"
			};
		} else {
			required = new String[] {
			"memId//???????????? ???????????????",
			"memNm//???????????? ???????????????"
			};
		}
		
		for (String s : required) {
			String[] re = s.split("//");
			
			if (request.getParameter(re[0]) == null || request.getParameter(re[0]).trim().equals("")) {
				throw new AlertException(re[1]);
			}
		}

		String memId = request.getParameter("memId").trim();
		
		if (memId.length() < 6 || memId.length() > 20) {
			throw new AlertException("???????????? ?????????, ?????? 6?????? ?????? 20?????? ????????? ????????? ?????????.");
		}
		
		String memPw = request.getParameter("memPw").trim();
		if (memPw.length() < 8) {
			throw new AlertException("??????????????? 8?????? ?????? ????????? ?????????");
		}

		String sql = "SELECT COUNT(*) cnt FROM member WHERE memId = ?";
		try (Connection conn = DB.getConnection();
			PreparedStatement pstmt = conn.prepareStatement(sql)) {
			pstmt.setString(1, memId);
			ResultSet rs = pstmt.executeQuery();
			if (rs.next()) {
				int cnt = rs.getInt("cnt");
				if (cnt > 0) { 
					throw new AlertException("??????????????? ????????? ?????????. - " + memId);
				}
			}
			rs.close();
		} catch (SQLException | ClassNotFoundException e) {
			e.printStackTrace();
		}

		String memPwRe = request.getParameter("memPwRe");
		if (!memPw.equals(memPwRe)) {
			throw new AlertException("????????????????????? ??????????????????.");
		}

	}
	

	public void login(HttpServletRequest request, String memId, String memPw) throws AlertException {

		if (memId == null || memId.trim().equals("")) {
			throw new AlertException("???????????? ????????? ?????????.");
		}
		
		if (memPw == null || memPw.trim().equals("")) {
			throw new AlertException("??????????????? ????????? ?????????.");
		}
	
		memId = memId.trim();
		memPw = memPw.trim();
		
		Member member = get(memId);
		if (member == null) { 
			throw new AlertException("??????????????? ????????????.");
		}
		

		boolean match = BCrypt.checkpw(memPw, member.getMemPw());
		if (!match) { 
			throw new AlertException("??????????????? ???????????? ????????????.");
		}

		

		HttpSession session = request.getSession();
		session.setAttribute("memNo", member.getMemNo());

	}
	
	public void login(HttpServletRequest request) throws AlertException {
		String memId = request.getParameter("memId");
		String memPw = request.getParameter("memPw");
		
		login(request, memId, memPw);
	}
	

	public Member get(int memNo) {
		Member member = null;
		String sql = "SELECT * FROM member WHERE memNo = ?";
		try (Connection conn = DB.getConnection();
			PreparedStatement pstmt = conn.prepareStatement(sql)) {
			pstmt.setInt(1, memNo);
			ResultSet rs = pstmt.executeQuery();
			if (rs.next()) {
				member = new Member(rs);
			}
		} catch (SQLException | ClassNotFoundException e) {
			e.printStackTrace();
		}
 		
		return member;
	}
	
	public Member get(String memId) {
		int memNo = 0;
		String sql = "SELECT memNo FROM member WHERE memId = ?";
		try(Connection conn = DB.getConnection();
			PreparedStatement pstmt = conn.prepareStatement(sql)) {
			pstmt.setString(1, memId);
			ResultSet rs = pstmt.executeQuery();
			if (rs.next()) {
				memNo = rs.getInt("memNo");
			}
		} catch (SQLException | ClassNotFoundException e) {
			e.printStackTrace();
		}
		
		return get(memNo);
	}
}


