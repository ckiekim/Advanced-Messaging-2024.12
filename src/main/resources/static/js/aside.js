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

function openFlatpickr() {
    flatpickr("#date-picker", {
        dateFormat: "Y년 m월 d일",
        defaultDate: "today",
        locale: "ko",
        disableMobile: true,
        showMonths: 1,
        onReady: function (selectedDates, dateStr, instance) {
            instance.open();  // 페이지 로드 시 자동으로 달력 열기
        },
        onDayCreate: function (dObj, dStr, fp, dayElem) {
            const date = new Date(dayElem.dateObj);
            const dayOfWeek = date.getDay();
            if (dayOfWeek === 0) {
                dayElem.classList.add("sunday");
            }
        }
    });
}