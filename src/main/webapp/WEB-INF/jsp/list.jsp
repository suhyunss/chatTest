<%@ page contentType="text/html; charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<html lang="ko">
<head>
    <%@ include file="/WEB-INF/bootstrap.jsp" %>
    <title>List</title>
</head>
<body>

<nav class="navbar navbar-expand navbar-light bg-white topbar mb-4 static-top shadow">
    <!-- Topbar Navbar -->
    <ul class="navbar-nav ml-auto">
        <div class="topbar-divider d-none d-sm-block"></div>
        <!-- Nav Item - User Information -->
        <li class="nav-item dropdown no-arrow">
            <a class="nav-link dropdown-toggle" href="#" id="userDropdown" role="button" data-toggle="dropdown" aria-haspopup="true" aria-expanded="false">
                <span class="mr-2 d-none d-lg-inline text-gray-600 small">${user.id} (${user.userName})</span>
            </a>
        </li>
    </ul>
</nav>

<div class="row justify-content-center">
    <!-- 전체 상담 리스트 -->
    <div class="col-lg-4">
        <div class="card shadow mb-4">
            <div class="card-header py-3">
                <h6 class="m-0 font-weight-bold text-primary">전체 상담(채팅 미생성) 리스트</h6>
            </div>
            <div class="card-body">
                <table class="table table-bordered dataTable" role="grid">
                    <thead>
                        <tr>
                            <th>견적번호</th>
                            <c:if test="${user.authority eq 'ADMIN'}">
                                <th></th>
                            </c:if>
                        </tr>
                    </thead>
                    <tbody>
                        <c:forEach var="room" items="${allRooms}">
                            <tr>
                                <td>${room.estimateNum}</td>
                                <c:if test="${user.authority eq 'ADMIN' && not empty room.estimateNum}">
                                    <td>
                                        <a href="/createRoom.do?estimateNum=${fn:escapeXml(room.estimateNum)}" class="btn btn-info btn-icon-split">방 만들기</a>
                                    </td>
                                </c:if>
                            </tr>
                        </c:forEach>
                    </tbody>
                </table>
            </div>
        </div>
    </div>

    <!-- 로그인 권한별 채팅방 리스트 -->
    <div class="col-lg-4">
        <div class="card shadow mb-4">
            <div class="card-header py-3">
                <h6 class="m-0 font-weight-bold text-primary">로그인 권한별 채팅방 리스트</h6>
            </div>
            <div class="card-body">
                <table class="table table-bordered dataTable" role="grid">
                    <thead>
                        <tr>
                            <th>견적번호</th>
                            <th></th>
                        </tr>
                    </thead>
                    <tbody>
                        <c:choose>
                            <c:when test="${user.authority eq 'ADMIN'}">
                                <!-- ADMIN 권한에 따른 모든 방 목록 -->
                                <c:forEach var="room" items="${allRooms}">
                                    <tr>
                                        <td>${room.estimateNum}</td>
                                        <td><a href="/${room.estimateNum}/chat.do" class="btn btn-info btn-icon-split">입장</a></td>
                                    </tr>
                                </c:forEach>
                            </c:when>
                            <c:otherwise>
                                <!-- 일반 사용자에 따른 참여한 방 목록 -->
                                <c:forEach var="room" items="${userRooms}">
                                    <tr>
                                        <td>${room.estimateNum}</td>
                                        <td><a href="/${room.estimateNum}/chat.do" class="btn btn-info btn-icon-split">입장</a></td>
                                    </tr>
                                </c:forEach>
                            </c:otherwise>
                        </c:choose>
                    </tbody>
                </table>
            </div>
        </div>
    </div>
</div>

</body>
</html>
