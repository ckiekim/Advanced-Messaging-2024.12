let si;

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

async function insertInterestGroup() {
    const groupName = document.getElementById('groupName').value.trim();
    const checkResult = await checkGroupName(groupName);
    if (!checkResult) {
        alert('관심그룹 이름을 변경하세요.');
        return;
    }

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
                location.href = '/interest/multi?n=' + groupName;
            } else {
                alert('등록 실패!!!');
            }
        })
        .catch((error) => {
            console.error("에러 발생:", error);
        });
}

async function checkGroupName(groupName, groupId = 0) {
    try {
        const response = await fetch('/interest/getInterestGroupList');
        const groupList = await response.json();
        for (let group of groupList) {
            if (group.name === groupName && group.id !== groupId)
                return false;
        }
        return true;
    } catch (error) {
        console.error("Error in getGroupList:", error);
    }
}

function getMultiValueInterval(codes) {
    si = setInterval(() => {
        getMultiValue(codes);
    }, 3000);
}

function getMultiValue(codes) {
    const tbody = document.getElementById('tbody');
    fetch('/interest/getMultiValue', {
        method: 'POST',
        headers: { 'Content-type': 'application/json' },
        body: JSON.stringify(codes)
    })
        .then(response => response.json())
        .then(output => {       // output은 array 타입.
            console.log(output);
            tbody.innerHTML = '';
            output.forEach((row, index) => {
                const tr = document.createElement('tr');
                const previousPrice = Number(row.inter2_prdy_clpr);
                tr.innerHTML = `
                    <td style="text-align: center;">${index + 1}</td>
                    <td style="text-align: center;">${row.inter_shrn_iscd || '-'}</td>
                    <td style="text-align: center;">
                        <a href="/kis/realtime?itemCode=${row.inter_shrn_iscd}">${row.inter_kor_isnm || '-'}</a>
                    </td>
                    ${generateTd(previousPrice, Number(row.inter2_prpr), 'font-weight: bold;')}
                    ${generateTd2(Number(row.inter2_prdy_vrss), row.prdy_ctrt)}
                    ${generateTd(previousPrice, Number(row.inter2_oprc))}
                    ${generateTd(previousPrice, Number(row.inter2_hgpr))}
                    ${generateTd(previousPrice, Number(row.inter2_lwpr))}
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

function generateTd(prevPrice, dispPrice, style = '') {
    const color = prevPrice < dispPrice ? 'class="text-danger"' :
                    prevPrice > dispPrice ?  'class="text-primary"' : '';
    return `<td style="text-align: right; ${style}" ${color}>${dispPrice.toLocaleString()}</td>`;
}

function generateTd2(dispPrice, rate) {
    const color = dispPrice > 0 ? 'class="text-danger"' :
                    dispPrice < 0 ?  'class="text-primary"' : '';
    return `<td style="text-align: center;" ${color}>${dispPrice.toLocaleString()} (${dispPrice > 0 ? '+' : ''}${rate}%)</td>`;
}

async function changeGroup() {
    const selectedValue = this.value;
    const codes = await getGroupCode(selectedValue);
    if (si)
        clearInterval(si);
    getMultiValue(codes);
    getMultiValueInterval(codes);
}

async function getGroupCode(selectedValue) {
    try {
        const response = await fetch('/interest/getStockList?groupName=' + selectedValue);
        const output = await response.json();
        console.log(output);
        document.getElementById('groupId').value = output.id;
        return output.codes;
    } catch (error) {
        console.error("Error in getStockList:", error);
    }
}

function updateGroup() {
    const id = document.getElementById('groupId').value;
    location.href = '/interest/update/' + id;
}

async function updateInterestGroup() {
    const groupId = Number(document.getElementById('groupId').value);
    const groupName = document.getElementById('groupName').value.trim();
    const checkResult = await checkGroupName(groupName, groupId);
    if (!checkResult) {
        alert('관심그룹 이름을 변경하세요.');
        return;
    }

    const itemCodes = [];
    for (let i = 1; i <= 20; i++) {
        const itemCode = document.getElementById('itemCode' + i).value.trim();
        const checkbox = document.getElementById('checkbox' + i);
        if (checkbox.checked)
            continue;
        if (itemCode === '')
            break;
        itemCodes.push(itemCode);
    }

    const interestGroup = {
        id: groupId,
        name: groupName,
        codes: itemCodes
    }
    
    fetch('/interest/update', {
        method: 'POST',
        headers: { 'Content-type': 'application/json' },
        body: JSON.stringify(interestGroup)
    })
        .then(response => {
            if (response.ok) {
                alert('수정 성공!!!');
                location.href = '/interest/multi?n=' + groupName;
            } else {
                alert('수정 실패!!!');
            }
        })
        .catch((error) => {
            console.error("에러 발생:", error);
        });
}

function deleteGroup() {
    const id = document.getElementById('groupId').value;
    if (confirm('정말로 삭제하시겠습니까?'))
        location.href = '/interest/delete/' + id;
}