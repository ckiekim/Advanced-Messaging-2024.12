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