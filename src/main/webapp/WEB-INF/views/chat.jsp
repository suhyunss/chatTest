<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<html>
<head>
    <%@ include file="/WEB-INF/bootstrap.jsp" %>
    <title>채팅방</title>
</head>
<body>

<div class="container d-flex flex-column" style="height: 100vh;">
    <!-- 채팅방 헤더 -->
    <div class="chat-header bg-primary text-white py-3 d-flex justify-content-between align-items-center">
        <h5 class="mb-0 mx-auto">채팅방</h5>
        <a href="/list.do" class="btn btn-light align-self-center">리스트 보기</a>
    </div>

    <!-- 채팅 메시지 출력 부분 -->
    <div id="chatBox" class="container-fluid flex-grow-1" style="overflow-y: scroll; padding-bottom: 10px;"></div>

    <!-- 메시지 입력창 -->
    <div class="chat-footer bg-light p-3 d-flex">
        <input type="text" class="form-control mr-2" placeholder="텍스트 입력..." id="chat-input">
    </div>
</div>

<script>
    $(document).ready(function () {
        const user = JSON.parse('${user}');
        const estimateNum = '${estimateNum}';
        const socket = new WebSocket('ws://localhost:8080/chat/' + estimateNum);
        const $chatBox = $("#chatBox");
        const $chatInput = $("#chat-input");

        socket.onopen = function () {
            console.log("WebSocket 연결 성공");
            loadChatHistory();
        };

        socket.onmessage = function (event) {
            let message = parseMessage(event.data);
            appendMessage(message.user.id, message.content);
        };

        socket.onclose = function () {
            console.log("WebSocket 연결 종료");
        };

        socket.onerror = function (error) {
            console.log("WebSocket 오류:", error);
        };

        function parseMessage(data) {
            try {
                return JSON.parse(data);
            } catch (e) {
                console.log("수신된 메시지:", data);
                return { message: data };
            }
        }

        function appendMessage(userId, content) {
            const isCurrentUser = user.id === userId;
            const rowClass = isCurrentUser ? 'row justify-content-end' : 'row';

            const messageHtml = `
                <div class="container-fluid">
                    <div class="` + rowClass + `">
                        <div class="col-xl-6 col-md-6 mb-4">
                            <div class="card border-bottom-primary shadow h-10 py-2">
                                <div class="card-body">
                                    <div class="row no-gutters align-items-center">
                                        <div class="col mr-2">
                                            <div class="text-xs font-weight-bold text-primary text-uppercase mb-1">
                                                ` + userId + `
                                            </div>
                                            <div class="h5 mb-0 font-weight-bold text-gray-800">
                                                ` + content + `
                                            </div>
                                        </div>
                                        <div class="col-auto">
                                            <i class="fas fa-calendar fa-2x text-gray-300"></i>
                                        </div>
                                    </div>
                                </div>
                            </div>
                        </div>
                    </div>
                </div>
            `;
            $chatBox.append(messageHtml);
            $chatBox.scrollTop($chatBox[0].scrollHeight);
        }


        $chatInput.on("keypress", function (event) {
            if (event.key === "Enter") {
                event.preventDefault();
                sendMessage();
            }
        });

        function sendMessage() {
            const messageContent = $chatInput.val().trim();
            if (messageContent) {
                const message = { user, content: messageContent };
                socket.send(JSON.stringify(message));
                $chatInput.val("");
            }
        }

        function loadChatHistory() {
            $.ajax({
                url: `/${estimateNum}/history`,
                method: "GET",
                dataType: "json",
                success: function (data) {
                    $.each(data, function (index, chatMessage) {
                        appendMessage(chatMessage.user.id, chatMessage.content);
                    });
                },
                error: function (error) {
                    console.error("채팅 내역 로드 오류:", error);
                }
            });
        }
    });
</script>

</body>
</html>
