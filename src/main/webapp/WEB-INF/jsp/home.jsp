<%@ page contentType="text/html; charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<html lang="ko">
<head>
    <%@ include file="/WEB-INF/bootstrap.jsp" %>
    <title>Home</title>
</head>
<body>


<div class="container d-flex justify-content-center align-items-center vh-100">
    <div class="row w-100 mt-5">
        <c:forEach var="groupEntry" items="${userGroups}">
            <div class="col-md-4 d-flex justify-content-center">
                <div class="dropdown">
                    <button class="btn btn-primary dropdown-toggle" type="button" id="dropdown${groupEntry.key}"
                            data-toggle="dropdown" data-group="${groupEntry.key}">${groupEntry.key}
                    </button>
                    <div class="dropdown-menu">
                        <c:forEach var="user" items="${groupEntry.value}">
                            <a class="dropdown-item" href="#" data-value="${user.id}" data-group="${groupEntry.key}">
                                ${user.id}
                            </a>
                        </c:forEach>
                    </div>
                </div>
            </div>
        </c:forEach>

        <!-- 로그인 버튼 중앙 정렬 -->
        <div class="col-12 d-flex justify-content-center mt-5">
            <button class="btn btn-success" id="loginButton">로그인</button>
        </div>
    </div>
</div>

</body>

<script>
    $(function () {
        let selectedValue = null;
        let selectedGroup = null;

        // 드롭다운 아이템 클릭 시 선택 처리
        $(".dropdown-item").click(function () {
            selectedValue = $(this).data("value");
            selectedGroup = $(this).data("group");

            // 다른 드롭다운 버튼 텍스트 초기화
            $(".dropdown-toggle").each(function () {
                let groupId = $(this).attr("id").replace('dropdown', '');
                if (groupId !== selectedGroup) {
                    $(this).text($(this).data("group") + " ");
                }
            });

            updateDropdownText(selectedGroup, selectedValue);
        });

        // 로그인 버튼 클릭 시
        $("#loginButton").click(function () {
            if (!selectedValue) {
                return alert("먼저 사용자를 선택해주세요!");
            }
            login(selectedGroup, selectedValue);
        });

        // 드롭다운 텍스트 업데이트 함수
        function updateDropdownText(group, value) {
            $("#dropdown" + group).text(value);
        }

        // 로그인 함수
        function login(group, user) {
            $.ajax({
                url: '/login.do',
                type: 'POST',
                data: { "userId": user },
            }).done(function () {
                window.location.href = "/list.do";
            }).fail(function () {
                alert("로그인 오류 발생!");
            });
        }
    });
</script>

</html>
