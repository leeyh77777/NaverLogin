<%@ page contentType="text/html; charset=utf-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ page import="com.models.dto.Member" %>
<%
	Member member = (Member)request.getAttribute("member");
%>
<c:set var="member" value="<%=member%>" />
<c:choose>
	<c:when test="${member == null}">
		<a href='member/join'>회원가입</a>
		<a href='member/login'>로그인</a>
	</c:when>
	<c:otherwise>
	  <c:out value="${member.memNm}" />
	  (<c:out value="${member.memId}" />)님 로그인....
	  <a href='member/logout'>로그아웃</a>
	</c:otherwise>
</c:choose>