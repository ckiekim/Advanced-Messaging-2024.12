let socket;

function connect() {
    const websocketUrl = 'ws://ops.koreainvestment.com:21000';
    const approvalKey = document.getElementById('approvalKey').value;
    const itemCode = document.getElementById('itemCode').value.trim();
    
    if (!itemCode) {
        alert('종목 코드를 입력해주세요.');
        return;
    }

    if (socket && socket.readyState === WebSocket.OPEN) {
        socket.close();
        console.log('기존 WebSocket 연결 종료');
    }

    socket = new WebSocket(websocketUrl);
    document.getElementById("nominalTable").style.display = "table";
    document.getElementById("marketTable").style.display = "table";
    
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
        if (event.data[0] === '0') {
            const realData = event.data.split('^');
            if (event.data.includes('H0STASP0')) {              // 실시간 호가
                document.getElementById('BSOP_HOUR').innerText = realData[1];
                for (let i=1; i<=10; i++) {
                    document.getElementById('ASKP'+i).innerText = realData[i + 2];
                    document.getElementById('ASKP_RSQN'+i).innerText = realData[20 + i + 2];
                    document.getElementById('BIDP'+i).innerText = realData[10 + i + 2];
                    document.getElementById('BIDP_RSQN'+i).innerText = realData[30 + i + 2];
                }
            } else if (event.data.includes('H0STCNT0')) {       // 실시간 체결가
                document.getElementById('STCK_CNTG_HOUR').innerText = realData[1];
                document.getElementById('STCK_PRPR').innerText = realData[2];
                document.getElementById('PRDY_VRSS_SIGN').innerText = realData[3];
                document.getElementById('PRDY_VRSS').innerText = realData[4];
                document.getElementById('PRDY_CTRT').innerText = realData[5];
                document.getElementById('STCK_OPRC').innerText = realData[7];
                document.getElementById('STCK_HGPR').innerText = realData[8];
                document.getElementById('STCK_LWPR').innerText = realData[9];
                document.getElementById('CNTG_VOL').innerText = realData[12];
                document.getElementById('ACML_VOL').innerText = realData[13];
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

function handleEnterKey(event) {
    if (event.key === 'Enter') {
        event.preventDefault();     // 줄바꿈 방지(기본 엔터 키 동작 방지)
        connect();
    }
}