let stompClient = null, socket = null;
let userId = null, roomId = null;

function connect() {
    userId = $('#userId').val();
    roomId = $('#roomId').val();
    socket = new SockJS('/ws');
    stompClient = Stomp.over(socket);
    stompClient.connect({}, frame => {
        // console.log('Connected:', frame);
        $('#statusIcon').css({color: 'orange', fontWeight: 'bold'});
        stompClient.subscribe('/topic/' + roomId, message => {
            showMessage(message.body);
        });
    });
}

function sendMessage() {
    const message = $('#messageInput').val();
    const groupMessage = { sender: userId, roomId, content: message };
    // console.log(groupMessage);
    stompClient.send('/app/chatRoom/' + roomId, {}, JSON.stringify(groupMessage));
}

function showMessage(message) {
    const parsedMessage = JSON.parse(message);
    // console.log("Parsed message: ", parsedMessage);
    setTimeout(async () => {
        await fetchChatItems();
        if (parsedMessage.content !== 'OK_SIGNAL')
            sendSignal();
    }, 100);
}

function sendSignal() {
    const groupMessage = { sender: userId, roomId, content: 'OK_SIGNAL' };
    stompClient.send('/app/signal/' + roomId, {}, JSON.stringify(groupMessage));
}

async function fetchChatItems() {
    try {
        const response = await fetch(`/chat/getChatItems?userId=${userId}&roomId=${roomId}`);
        if (response.ok) {
            setTimeout(async () => {
                const chatItemsByDate = await response.json();
                updateChatContainer(chatItemsByDate);
            }, 100);
        } 
    } catch (error) {
        console.error("Failed to fetch messages:", error);
    }
}

function updateChatContainer(chatItemsByDate) {
    const chatContainer = document.getElementById("chatContainer");
    chatContainer.innerHTML = ""; // 기존 메시지 초기화

    for (const [date, chatItems] of Object.entries(chatItemsByDate)) {
        // 날짜 표시
        const dateDiv = document.createElement("div");
        dateDiv.className = "text-center mt-2 mb-3";
        dateDiv.style.fontSize = "0.7rem";
        dateDiv.style.backgroundColor = "lightgrey";
        dateDiv.textContent = date;
        chatContainer.appendChild(dateDiv);

        // 메시지 표시
        chatItems.forEach(chat => {
            const chatItemDiv = document.createElement("div");

            if (chat.isMine === 0) {
                // console.log(chat);
                // 받은 메시지
                chatItemDiv.innerHTML = `
                    <div>
                        <img src="${chat.senderProfile}" alt="${chat.senderName}" width="28" style="border-radius: 30%">
                        <span style="font-size: 0.6rem;">${chat.senderName}</span>
                    </div>
                    <div class="message received">
                        <p>${chat.message}</p>
                        <span style="font-size: 0.6rem;">
                            ${chat.timeStr}
                            ${chat.unreadCount !== 0 ? `<span class="read-status">${chat.unreadCount}</span>` : ''}
                        </span>
                    </div>
                `;
            } else {
                // 보낸 메시지
                chatItemDiv.innerHTML = `
                    <div class="message sent">
                        <span style="font-size: 0.6rem; margin-right: 3px;">
                            ${chat.unreadCount !== 0 ? `<span class="read-status">${chat.unreadCount}</span>` : ''}
                            ${chat.timeStr}
                        </span>
                        <p>${chat.message}</p>
                    </div>
                `;
            }
            chatContainer.appendChild(chatItemDiv);
        });
    }
    // input tag
    const inputTag = document.createElement("input");
    inputTag.className = "form-control mt-2";
    inputTag.type = "text";
    inputTag.id = "messageInput";
    inputTag.placeholder = "메시지 입력";
    inputTag.addEventListener("keypress", handleEnterKey);
    chatContainer.appendChild(inputTag);

    // 스크롤을 가장 아래로 내리기
    chatContainer.scrollTop = chatContainer.scrollHeight;
}

function handleEnterKey(event) {
    if (event.key === 'Enter') {
        event.preventDefault();     // 줄바꿈 방지(기본 엔터 키 동작 방지)
        sendMessage();
    }
}

