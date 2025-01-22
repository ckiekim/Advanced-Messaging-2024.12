let socket, beforeClosingPrice, previousPrice;

async function explore() {
    previousPrice = new Map();
    try {
        await fetchStockInfo();
        await fetchCurrentPrice();
        connect();
    } catch (error) {
        console.error("Error in fetchCurrentPrice:", error);
    }
}

function connect() {
    const websocketUrl = 'ws://ops.koreainvestment.com:21000';
    const approvalKey = document.getElementById('approvalKey').value;
    const itemCode = document.getElementById('itemCode').value.trim();
    const todayStr = new Date().toISOString().substring(0,10);
    
    if (!itemCode) {
        alert('종목 코드를 입력해주세요.');
        return;
    }

    if (socket && socket.readyState === WebSocket.OPEN) {
        socket.close();
        console.log('기존 WebSocket 연결 종료');
    }

    socket = new WebSocket(websocketUrl);

    socket.onopen = () => {
        console.log('Websocket 연결 성공');
        const nominalPriceRequest = {
            header: {
                'approval_key': approvalKey,
                'custtype': 'P',
                'tr_type': '1',
                'content-type': 'utf-8'
            },
            body: {
                input: {
                    'tr_id': 'H0STASP0',
                    'tr_key': itemCode       
                }
            }
        };
        const marketPriceRequest = {
            header: {
                'approval_key': approvalKey,
                'custtype': 'P',
                'tr_type': '1',
                'content-type': 'utf-8'
            },
            body: {
                input: {
                    'tr_id': 'H0STCNT0',
                    'tr_key': itemCode       
                }
            }
        };
        socket.send(JSON.stringify(nominalPriceRequest));
        socket.send(JSON.stringify(marketPriceRequest));
        console.log('요청 데이터 전송:', nominalPriceRequest, marketPriceRequest);
    }

    socket.onmessage = event => {
        console.log('서버 응답:', event.data);
        document.getElementById('stck_shrn_iscd').innerText = itemCode;
        document.getElementById('today').innerText = todayStr;

        if (event.data[0] === '0') {
            const realData = event.data.split('^');
            if (event.data.includes('H0STASP0')) {              // 실시간 호가
                for (let i=1; i<=5; i++) {
                    const askPrice = Number(realData[i + 2]);
                    const bidPrice = Number(realData[10 + i + 2]);
                    const askQuantity = Number(realData[20 + i + 2]);
                    const bidQuantity = Number(realData[30 + i + 2]);
                    setNominalElementAndColor(document.getElementById('ASKP'+i), askPrice);
                    setNominalElementAndColor(document.getElementById('BIDP'+i), bidPrice);
                    document.getElementById('ASKP_RSQN'+i).innerText = askQuantity.toLocaleString();
                    document.getElementById('BIDP_RSQN'+i).innerText = bidQuantity.toLocaleString();
                    
                    const previousAskQuantity = previousPrice.get(askPrice) || 0;
                    const previousBidQuantity = previousPrice.get(bidPrice) || 0;
                    setCompareElementAndColor(document.getElementById('ASKP_RSQN'+i+'_comp'), askQuantity - previousAskQuantity);
                    setCompareElementAndColor(document.getElementById('BIDP_RSQN'+i+'_comp'), bidQuantity - previousBidQuantity);
                    // document.getElementById('ASKP_RSQN'+i+'_comp').innerText = (askQuantity - previousAskQuantity).toLocaleString();
                    // document.getElementById('BIDP_RSQN'+i+'_comp').innerText = (bidQuantity - previousBidQuantity).toLocaleString();
                    previousPrice.set(askPrice, askQuantity);
                    previousPrice.set(bidPrice, bidQuantity);
                }
            } else if (event.data.includes('H0STCNT0')) {       // 실시간 체결가
                document.getElementById('STCK_CNTG_HOUR').innerText = realData[1].substring(0,2) + ':' + realData[1].substring(2,4) + ':' + realData[1].substring(4);
                setElementAndColorClass(document.getElementById('STCK_PRPR'), Number(realData[2]));
                document.getElementById('PRDY_VRSS_SIGN').innerHTML = 
                    realData[3] === '2' ? '<i class="fa-solid fa-caret-up"></i>' : 
                        realData[3] === '5' ? '<i class="fa-solid fa-caret-down"></i>' : '';
                document.getElementById('PRDY_VRSS').innerText = Math.abs(Number(realData[4])).toLocaleString();
                document.getElementById('PRDY_CTRT').innerText = Math.abs(Number(realData[5]));
                setElementAndColorClass(document.getElementById('STCK_OPRC'), Number(realData[7]));
                setElementAndColorClass(document.getElementById('STCK_HGPR'), Number(realData[8]));
                setElementAndColorClass(document.getElementById('STCK_LWPR'), Number(realData[9]));
                document.getElementById('CNTG_VOL').innerText = Number(realData[12]).toLocaleString();
                document.getElementById('ACML_VOL').innerText = Number(realData[13]).toLocaleString();
                compareElementSetColor(document.getElementById('compareBeforeDay'), realData[3]);

                // const compareElement = document.getElementById('compareBeforeDay');
                // const classValue = compareElement.classList;
                // compareElement.classList.remove(classValue[0]);
                // if (realData[3] === '1' || realData[3] === '2') {
                //     compareElement.classList.add('text-danger');
                //     document.getElementById('compareSign').innerText = '+';
                // } else if (realData[3] === '4' || realData[3] === '5') {
                //     compareElement.classList.add('text-primary');
                //     document.getElementById('compareSign').innerText = '-';
                // } else {
                //     compareElement.classList.add('text-body');
                //     document.getElementById('compareSign').innerText = '';
                // }
            }
        } 
    }

    socket.onerror = (error) => {
        console.error('WebSocket 오류:', error);
    };

    socket.onclose = () => {
        console.log('WebSocket 연결 종료');
    };
}

function handleCodeEnterKey(event) {
    if (event.key === 'Enter') {
        event.preventDefault();     // 줄바꿈 방지(기본 엔터 키 동작 방지)
        explore();
    }
}

function handleNameEnterKey(event) {
    if (event.key === 'Enter') {
        event.preventDefault();     // 줄바꿈 방지(기본 엔터 키 동작 방지)
        search();
    }
}

async function fetchStockInfo() {
    const itemCode = document.getElementById('itemCode').value.trim();
    try {
        const response = await fetch('/kis/getStockInfo?itemCode=' + itemCode);
        const output = await response.json();
        document.getElementById('prdt_abrv_name').innerText = output.prdt_abrv_name;
        document.getElementById('excg_dvsn_cd').innerText = output.excg_dvsn_cd === '02' ? '코스피' : '코스닥';
        beforeClosingPrice = Number(output.bfdy_clpr);
        document.getElementById('bfdy_clpr').innerText = beforeClosingPrice.toLocaleString();
    } catch (error) {
        console.error("Error in fetchStockInfo:", error);
    }
}
    
async function fetchCurrentPrice() {
    const itemCode = document.getElementById('itemCode').value.trim();
    try {
        const response = await fetch('/kis/getCurrentPrice?itemCode=' + itemCode);
        const output = await response.json();
        document.getElementById('stck_mxpr').innerText = Number(output.stck_mxpr).toLocaleString();
        document.getElementById('stck_llam').innerText = Number(output.stck_llam).toLocaleString();
        document.getElementById('w52_hgpr').innerText = Number(output.w52_hgpr).toLocaleString();
        document.getElementById('w52_lwpr').innerText = Number(output.w52_lwpr).toLocaleString();
        document.getElementById('crdt_able_yn').innerText = output.crdt_able_yn;
    
        setElementAndColorClass(document.getElementById('STCK_PRPR'), Number(output.stck_prpr));
        document.getElementById('PRDY_VRSS_SIGN').innerHTML = 
            output.prdy_vrss_sign === '2' ? '<i class="fa-solid fa-caret-up"></i>' : 
                output.prdy_vrss_sign === '5' ? '<i class="fa-solid fa-caret-down"></i>' : '';
        document.getElementById('PRDY_VRSS').innerText = Math.abs(Number(output.prdy_vrss)).toLocaleString();
        document.getElementById('PRDY_CTRT').innerText = Math.abs(Number(output.prdy_ctrt));
        setElementAndColorClass(document.getElementById('STCK_OPRC'), Number(output.stck_oprc));
        setElementAndColorClass(document.getElementById('STCK_HGPR'), Number(output.stck_hgpr));
        setElementAndColorClass(document.getElementById('STCK_LWPR'), Number(output.stck_lwpr));
        document.getElementById('ACML_VOL').innerText = Number(output.acml_vol).toLocaleString();
        compareElementSetColor(document.getElementById('compareBeforeDay'), output.prdy_vrss_sign);
    } catch (error) {
        console.error("Error in fetchCurrentPrice:", error);
    }
}

function search() {
    const itemName = document.getElementById('itemName').value.trim();
    document.getElementById('itemName').value = '';

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
                        <span style="cursor: pointer; " onclick="setItemCode('${item.code}')">
                            ${item.code}
                        </span>
                        &nbsp;&nbsp;
                        <span style="cursor: pointer; color: blue;" onclick="setItemCode('${item.code}')">
                            ${item.name}
                        </span>
                    </div>`;
            }
            document.getElementById('stockCodeList').innerHTML = html;
        })
        .catch(error => console.error("Error loading data:", error));
}

function setItemCode(code) {
    document.getElementById('itemCode').value = code;

    // 모달 닫기
    const searchModal = bootstrap.Modal.getInstance(document.getElementById('searchModal'));
    searchModal.hide();
}

function compareElementSetColor(element, value) {
    const classValue = element.classList;
    element.classList.remove(classValue[0]);
    if (value === '1' || value === '2') {
        element.classList.add('text-danger');
        document.getElementById('compareSign').innerText = '+';
    } else if (value === '4' || value === '5') {
        element.classList.add('text-primary');
        document.getElementById('compareSign').innerText = '-';
    } else {
        element.classList.add('text-body');
        document.getElementById('compareSign').innerText = '';
    }
}

function setElementAndColorClass(element, price) {
    element.innerText = price.toLocaleString();
    console.log('price=' + price + ', beforeClosingPrice=' + beforeClosingPrice);
    const classValue = element.classList;
    element.classList.remove(classValue[0]);
    if (price > beforeClosingPrice)
        element.classList.add('text-danger');
    else if (price < beforeClosingPrice)
        element.classList.add('text-primary');
    else
        element.classList.add('text-body');
}

function setNominalElementAndColor(element, price) {
    const pct = (((price - beforeClosingPrice) / beforeClosingPrice) * 100).toFixed(2);
    element.innerText = price.toLocaleString() + ' (' + pct + '%)';
    const classValue = element.classList;
    element.classList.remove(classValue[0]);
    if (price > beforeClosingPrice)
        element.classList.add('text-danger');
    else if (price < beforeClosingPrice)
        element.classList.add('text-primary');
    else
        element.classList.add('text-body');
}

function setCompareElementAndColor(element, price) {
    element.innerText = price !== 0 ? price.toLocaleString() : ' ';
    const classValue = element.classList;
    element.classList.remove(classValue[0]);
    if (price > 0)
        element.classList.add('text-danger');
    else if (price < 0)
        element.classList.add('text-primary');
}
