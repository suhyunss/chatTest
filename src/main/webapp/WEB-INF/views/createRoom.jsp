<%@ page contentType="text/html; charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<html>
<head>
    <%@include file="/WEB-INF/bootstrap.jsp" %>
    <title>Home</title>
</head>
<body>

<nav class="navbar navbar-expand navbar-light bg-white topbar mb-4 static-top shadow">
    <ul class="navbar-nav ml-auto">
        <div class="topbar-divider d-none d-sm-block"></div>
        <li class="nav-item dropdown no-arrow">
            <a class="nav-link dropdown-toggle" href="#" id="userDropdown" role="button" data-toggle="dropdown"
               aria-haspopup="true" aria-expanded="false">
                <span class="mr-2 d-none d-lg-inline text-gray-600 small">${user.id} (${user.userName})</span>
            </a>
        </li>
    </ul>
</nav>

<div class="row justify-content-center">
    <div class="col-lg-3">
        <div class="card shadow mb-4">
            <div class="card-header py-3"><h6 class="m-0 font-weight-bold text-primary">AG 리스트</h6></div>
            <div class="card-body">
                <c:forEach var="user" items="${agList}">
                    <div class="w-100 d-flex justify-content-between align-items-center px-3">
                        <span class="text">${user.id}</span>
                        <a href="#" class="btn btn-secondary btn-icon-split invite-btn" data-id="${user.id}"
                           data-type="AG">초대</a>
                    </div>
                    <div class="my-2"></div>
                </c:forEach>
            </div>
        </div>
    </div>

    <div class="col-lg-3">
        <div class="card shadow mb-4">
            <div class="card-header py-3"><h6 class="m-0 font-weight-bold text-primary">직원 리스트</h6></div>
            <div class="card-body">
                <c:forEach var="user" items="${capList}">
                    <div class="w-100 d-flex justify-content-between align-items-center px-3">
                        <span class="text">${user.id}</span>
                        <a href="#" class="btn btn-secondary btn-icon-split invite-btn" data-id="${user.id}"
                           data-type="CAP">초대</a>
                    </div>
                    <div class="my-2"></div>
                </c:forEach>
            </div>
        </div>
    </div>


    <div class="col-lg-5">
        <div class="card shadow mb-4">
            <div class="card-header py-3"><h6 class="m-0 font-weight-bold text-primary">초대 리스트</h6></div>
            <div class="card-body">
                <div id="invitedList" class="d-flex">
                    <div class="<c:if test="${agUsers.included.size() != 0}">col-6</c:if>" id="invitedAGList">
                        <c:forEach var="user" items="${agUsers.included}">
                            <div class="invited-user w-100 d-flex justify-content-between align-items-center px-3">
                                <span class="text">${user.id}</span>
                                <a href="#" class="remove-invite btn btn-sm btn-danger" data-id="${user.id}">X</a>
                            </div>
                            <div class="my-2"></div>
                        </c:forEach>
                    </div>
                    <div class="<c:if test="${capUsers.included.size() != 0}">col-6</c:if>" id="invitedCAPList">
                        <c:forEach var="user" items="${capUsers.included}">
                            <div class="invited-user w-100 d-flex justify-content-between align-items-center px-3">
                                <span class="text">${user.id}</span>
                                <a href="#" class="remove-invite btn btn-sm btn-danger"
                                   data-id="${user.id}">X</a>
                            </div>
                            <div class="my-2"></div>
                        </c:forEach>
                    </div>
                </div>
            </div>
        </div>
    </div>

    <div class="d-flex justify-content-center">
        <a href="#" id="createRoomBtn" class="btn btn-info btn-icon-split"><span class="text">방 만들기</span></a>
    </div>
</div>
    <script>

        let invitedAGUsers = [];
        let invitedCAPUsers = [];

        <c:forEach var="user" items="${room.participantList}">
        if ("AG" === "${user.authority}") invitedAGUsers.push("${user.id}");
        else if ("CAP" === "${user.authority}") invitedCAPUsers.push("${user.id}");
        </c:forEach>

        $(".invite-btn").click(function () {
            let userId = $(this).data("id"), userType = $(this).data("type");
            if (userType === "AG" && !invitedAGUsers.includes(userId)) invitedAGUsers.push(userId);
            else if (userType === "CAP" && !invitedCAPUsers.includes(userId)) invitedCAPUsers.push(userId);
            updateInvitedList();
        });

        $(document).on("click", ".remove-invite", function () {
            let userId = $(this).data("id");
            invitedAGUsers = invitedAGUsers.filter(id => id !== userId);
            invitedCAPUsers = invitedCAPUsers.filter(id => id !== userId);
            updateInvitedList();
        });

        function updateInvitedList() {
            updateInvitedListClass();
            $("#invitedAGList").html(invitedAGUsers.map(id =>
                `<div class="invited-user w-100 d-flex justify-content-between align-items-center px-3">
                <span class="text">` + id + `</span>
                <a href="#" class="remove-invite btn btn-sm btn-danger" data-id="` + id + `">X</a>
            </div><div class="my-2"></div>`).join(""));

            $("#invitedCAPList").html(invitedCAPUsers.map(id =>
                `<div class="invited-user w-100 d-flex justify-content-between align-items-center px-3">
                <span class="text">` + id + `</span>
                <a href="#" class="remove-invite btn btn-sm btn-danger" data-id="` + id + `">X</a>
            </div><div class="my-2"></div>`).join(""));
        }

        function updateInvitedListClass() {
            if (invitedAGUsers.length > 0)
                $("#invitedAGList").addClass("col-6");
            else {
                $("#invitedAGList").removeClass("col-6");
            }
        }

        $("#createRoomBtn").click(function () {
            let roomEstimateNum = "${room.estimateNum}";
            if (!invitedAGUsers.length && !invitedCAPUsers.length) return alert("최소 한 명 이상 초대해야 합니다.");

            $.ajax({
                url: "/addParticipantsToRoom.do",
                type: "POST",
                contentType: "application/json",
                data: JSON.stringify({estimateNum: roomEstimateNum, userIds: [...invitedAGUsers, ...invitedCAPUsers]}),
                success: function (response) {
                    window.location.href = "/" + response.estimateNum + "/chat.do";
                },
                error: function () {
                    alert("방 생성 실패!");
                }
            });
        });
    </script>

</body>
</html>
