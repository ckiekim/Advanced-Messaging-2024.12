function handleEnterKey(event, num) {
    if (event.key === 'Enter') {
        event.preventDefault();     // 줄바꿈 방지(기본 엔터 키 동작 방지)
        search(num);
    }
}

function search(num) {
    const itemName = document.getElementById('itemName' + num).value.trim().toUpperCase();

    // 모달 열기
    const searchModal = new bootstrap.Modal(document.getElementById('searchModal'));
    searchModal.show();

    fetch('/kis/getCodeList?stockName=' + encodeURI(itemName))
        .then(response => response.json())
        .then(output => {
            let html = '';
            for (let item of output) {
                html += `
                    <div>
                        <span style="cursor: pointer;" onclick="setItemCode('${item.code}', '${item.name}', ${num})">
                            ${item.code}
                        </span>
                        &nbsp;&nbsp;
                        <span style="cursor: pointer; color: blue;" onclick="setItemCode('${item.code}','${item.name}', ${num})">
                            ${item.name}
                        </span>
                    </div>`;
            }
            document.getElementById('stockCodeList').innerHTML = html;
        })
        .catch(error => console.error("Error loading data:", error));
}

function setItemCode(code, name, num) {
    document.getElementById('itemName' + num).value = name;
    document.getElementById('itemCode' + num).value = code;

    // 모달 닫기
    const searchModal = bootstrap.Modal.getInstance(document.getElementById('searchModal'));
    searchModal.hide();
}

function submitInterestGroup() {
    const groupName = document.getElementById('groupName').value.trim();
    const itemCodes = [];
    for (let i = 1; i <= 20; i++) {
        const itemCode = document.getElementById('itemCode' + i).value.trim();
        if (itemCode === '')
            break;
        itemCodes.push(itemCode);
    }

    const interestGroup = {
        name: groupName,
        codes: itemCodes
    }
    
    fetch('/interest/insert', {
        method: 'POST',
        headers: { 'Content-type': 'application/json' },
        body: JSON.stringify(interestGroup)
    })
        .then(response => {
            if (response.ok) {
                alert('등록 성공!!!');
                location.href = '/interest/multi';
            } else {
                alert('등록 실패!!!');
            }
        })
        .catch((error) => {
            console.error("에러 발생:", error);
        });
}

function getMultiValue(codes) {
    const tbody = document.querySelector('tbody');
    fetch('/interest/getMultiValue', {
        method: 'POST',
        headers: { 'Content-type': 'application/json' },
        body: JSON.stringify(codes)
    })
        .then(response => response.json())
        .then(output => {       // output은 array 타입.
            tbody.innerHTML = '';
            output.forEach((row, index) => {
                const tr = document.createElement('tr');
                tr.innerHTML = `
                    <td style="text-align: center;">${index + 1}</td>
                    <td style="text-align: center;">${row.inter_shrn_iscd || '-'}</td>
                    <td style="text-align: center;">${row.inter_kor_isnm || '-'}</td>
                    <td style="text-align: right;">${Number(row.inter2_prpr).toLocaleString()}</td>
                    <td style="text-align: center;">${Number(row.inter2_prdy_vrss).toLocaleString()}</td>
                    <td style="text-align: right;">${Number(row.inter2_oprc).toLocaleString()}</td>
                    <td style="text-align: right;">${Number(row.inter2_hgpr).toLocaleString()}</td>
                    <td style="text-align: right;">${Number(row.inter2_lwpr).toLocaleString()}</td>
                    <td style="text-align: right;">${Number(row.acml_vol).toLocaleString()}</td>
                `;
                tbody.appendChild(tr);
            })
        })
        .catch((error) => {
            console.error("에러 발생:", error);
            tbody.innerHTML = '<tr><td colspan="9" style="text-align: center; color: red;">데이터를 불러오는 데 실패했습니다.</td></tr>';
        });
}
