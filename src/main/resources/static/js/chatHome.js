let stompClient = null, socket = null;
let userId = null;

function connect() {
    userId = $('#userId').val();
    const roomListStr = $('#roomListStr').val();
    const roomIds = roomListStr.split(',');
    const serverIp = $('#serverIp').val();
    const serverPort = $('#serverPort').val();
    socket = new SockJS(`http://${serverIp}:${serverPort}/ws`);
    stompClient = Stomp.over(socket);
    stompClient.connect({}, frame => {
        // console.log('Connected:', frame);
        $('#statusIcon').css({color: 'green', fontWeight: 'bold'});
        for (let roomId of roomIds) {
            stompClient.subscribe('/topic/' + roomId, message => {
                showMessage(message.body);
            });
        }
    });
}

function showMessage(message) {
    // console.log("Parsed message: ", JSON.parse(message));
    setTimeout(async () => {
        await fetchChatterList();
    }, 100);
}

async function fetchChatterList() {
    const userId = $('#userId').val();
    try {
        const response = await fetch(`/chat/getChatterList?userId=${userId}`);
        if (response.ok) {
            const chatterList = await response.json();
            updateChatterList(chatterList);
        }
    } catch (error) {
        console.error('Failed to fetch chatter list:', error);
    }
}

function updateChatterList(chatterList) {
    const tableBody = document.querySelector('.table > tbody');
    tableBody.innerHTML = ''; // 기존 내용을 초기화

    chatterList.forEach(chatter => {
        const row = document.createElement('tr');

        row.innerHTML = `
            <td style="text-align: center;">
                <img src="${chatter.roomProfile}" alt="${chatter.roomName}" width="40" style="border-radius: 50%; text-align: center;">
            </td>
            <td>
                <a href="/chat/each/${chatter.roomId}">
                    <span style="font-weight: bold; font-size: 0.8rem">${chatter.roomName}</span>
                </a><br>
                <span style="font-size: 0.8rem;">${chatter.message}</span>
            </td>
            <td style="text-align: center;">
                <span style="font-size: 0.8rem;">
                    ${chatter.timeStr}
                    ${chatter.newCount !== 0 ? `<br><span class="new-count">${chatter.newCount}</span>` : ''}
                </span>
            </td>
        `;
        tableBody.appendChild(row);
    });
}

function handlePopover() {
    // Popover 초기화
    const popoverTrigger = document.getElementById('addFriendPopover');
    const popover = new bootstrap.Popover(popoverTrigger, {
        trigger: 'manual', html: true, placement: 'bottom', title: 'ChatRoom 개설',
        content: document.getElementById('mypopover-content')
    });

    // 팝오버 열기
    popoverTrigger.addEventListener('click', function(event) {
        event.stopPropagation(); // 이벤트 버블링 방지
        if (popoverTrigger.getAttribute('aria-expanded') === 'true') {
            popover.hide();
        } else {
            popover.show();
        }
    });

    // Popover 열렸을 때 버튼 클릭 이벤트 바인딩
    document.addEventListener('shown.bs.popover', function () {
        const addButton = document.getElementById('addRoomButton');
        const closeButton = document.getElementById('closePopoverButton');
        if (addButton) {
            addButton.addEventListener('click', function () {
                const memberId = document.getElementById('memberId').value.trim();
                if (memberId !== '') {
                    if (confirm(`멤버 ${memberId}를 추가합니다.`)) {
                        // 서버에 AJAX 요청 예시
                        $.post('/chat/createChatRoom', { memberId: memberId }, function (response) {
                            console.log('ChatRoom 개설 성공:', response);
                            fetchChatterList();
                        });
                    }
                    popover.hide();
                } else {
                    alert('멤버 ID를 입력하세요.');
                }
            });
        }
        if (closeButton) {
            closeButton.onclick = function () {
                popover.hide();
            };
        }
    });

    // 팝오버 외부를 클릭하면 닫기
    document.addEventListener('click', function (event) {
        const popoverElement = document.querySelector('.popover');
        if (popoverElement && !popoverElement.contains(event.target) && event.target !== popoverTrigger) {
            popover.hide();
        }
    });
}