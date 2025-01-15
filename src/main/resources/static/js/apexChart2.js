function handleEnterKey(event) {
    if (event.key === 'Enter') {
        event.preventDefault();     // 줄바꿈 방지(기본 엔터 키 동작 방지)
        getCandleData();
    }
}

function getCandleData() {
    const tickerTag = document.getElementById('ticker');
    const ticker = tickerTag.value;
    const startDay = document.getElementById('startDay').value;
    const endDay = document.getElementById('endDay').value;
    tickerTag.value = '';

    fetch(`/stock/candleData2/${ticker}?s=${startDay}&e=${endDay}`)    // 캔들 데이터 가져오기
        .then(response => response.json())
        .then(result => {
            document.getElementById('startDay').value = result.startDay;
            document.getElementById('endDay').value = result.endDay;
            const tickerName = result.tickerName;
            // 데이터 변환
            const candlestickData = result.data.map(entry => ({
                x: entry.t, 
                y: [entry.o, entry.h, entry.l, entry.c] // [Open, High, Low, Close] 값
            }));

            const options = {
                chart: {
                    type: 'candlestick',
                    height: 600
                },
                title: {
                    text: `${tickerName} (${ticker})`, // 그래프 타이틀
                    align: 'center', // 타이틀 정렬: left, center, right
                    margin: 10, // 타이틀과 그래프 사이 간격
                    style: {
                        fontSize: '18px', // 폰트 크기
                        fontWeight: 'bold', // 폰트 굵기
                        color: '#333' // 폰트 색상
                    }
                },
                series: [{
                    name: ticker + ' Daily Prices',
                    data: candlestickData
                }],
                xaxis: {
                    type: 'datetime',
                    title: { text: 'Date' }
                },
                yaxis: {
                    title: { text: 'Price (USD)' }
                },
                tooltip: {
                    shared: false,
                    custom: function({ seriesIndex, dataPointIndex, w }) {
                        // 데이터 가져오기
                        const dataPoint = w.config.series[seriesIndex].data[dataPointIndex];
                        const date = new Date(dataPoint.x).toISOString().split('T')[0]; // 타임스탬프를 날짜로 변환
                        const [open, high, low, close] = dataPoint.y; // OHLC 값 추출

                        // 툴팁 HTML 생성
                        return `
                            <div style="padding: 10px; border: 1px solid #ccc; background: #fff;">
                                <div><strong>Date:</strong> ${date}</div>
                                <div><strong>Open:</strong> ${open}</div>
                                <div><strong>High:</strong> ${high}</div>
                                <div><strong>Low:</strong> ${low}</div>
                                <div><strong>Close:</strong> ${close}</div>
                            </div>
                        `;
                    }
                }
            };

            const chart = new ApexCharts(document.querySelector("#candleChart"), options);
            chart.render();
        })
        .catch(error => console.error("Error loading data:", error));

}
