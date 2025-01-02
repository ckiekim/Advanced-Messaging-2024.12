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