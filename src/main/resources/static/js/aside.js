function showMsgInput() {
    $('#stateMsgInput').attr({'class': 'mt-2'});    // 입력창이 보이게
    $('#stateInput').val($('#stateMsg').text());    // 입력창에 stateMsg 내용이 보이게
}

function handleStateMsg(event) {
    if (event.key === 'Enter') {
        event.preventDefault();     // 줄바꿈 방지(기본 엔터 키 동작 방지)
        sendStateMsg();
    }
}

function sendStateMsg() {
    const stateInputVal = $('#stateInput').val().trim();
    $('#stateMsgInput').attr({'class': 'mt-2 d-none'});
    $.ajax({
        type: 'GET',
        url: '/aside/stateMsg',
        data: {stateMsg: stateInputVal},
        success: result => {
            console.log(result, stateInputVal);
            $('#stateMsg').html(stateInputVal);
        }
    });
}

function startSSE() {
    const eventSource = new EventSource('http://localhost:8080/notice/clock');
    
    eventSource.onmessage = event => {
        const noticeElement = document.getElementById('clock');
        noticeElement.innerText = event.data;
    };
    
    eventSource.onerror = event => {
        console.error("EventSource failed.");
    }
}
