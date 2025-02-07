function getStockIndex() {
    fetch('/kis/getStockIndex')
        .then(response => response.json())
        .then(output => {
            for (let i = 0; i <= 2; i++) {
                console.log(output[i]);
                if (output[i].prdy_vrss_sign === '1' || output[i].prdy_vrss_sign === '2') {
                    document.getElementById('v' + i).classList.add('text-danger');
                    document.getElementById('c' + i).classList.add('text-danger');
                } 
                if (output[i].prdy_vrss_sign === '4' || output[i].prdy_vrss_sign === '5') {
                    document.getElementById('v' + i).classList.add('text-primary');
                    document.getElementById('c' + i).classList.add('text-primary');
                }
                document.getElementById('value' + i).innerText = Number(output[i].bstp_nmix_prpr).toFixed(2).toLocaleString();
                document.getElementById('change' + i).innerText = 
                    output[i].bstp_nmix_prdy_vrss + ' (' + output[i].bstp_nmix_prdy_ctrt + '%)';
            }
        })
        .catch(error => console.error("Error loading data:", error));
}

function getFinanceInfo() {
    fetch('/kis/getFinanceInfo')
    .then(response => response.json())
    .then(output => {
        const overseasTbody = document.getElementById('overseas');
        overseasTbody.innerHTML = '';
        output.overseas.forEach((row, index) => {
            const tr = document.createElement('tr');
            tr.innerHTML = `
                <td>${row.marketName}</td>
                <td style="text-align: right" class="${row.sign === 'up' ? 'text-danger' : row.sign === 'down' ? 'text-primary' : ''}">
                    ${row.indexValue}
                </td>
                <td style="text-align: right" class="${row.sign === 'up' ? 'text-danger' : row.sign === 'down' ? 'text-primary' : ''}">
                    ${row.sign === 'down' ? '-' : ''}${row.changeValue}
                </td>
            `;
            overseasTbody.appendChild(tr);
        });

        const popularTbody = document.getElementById('popular');
        output.popular.forEach((row, index) => {
            const tr = document.createElement('tr');
            tr.innerHTML = `
                <td>${row.itemName}</td>
                <td style="text-align: right" class="${row.sign === 'up' ? 'text-danger' : row.sign === 'down' ? 'text-primary' : ''}">
                    ${row.indexValue}
                </td>
                <td style="text-align: right" class="${row.sign === 'up' ? 'text-danger' : row.sign === 'down' ? 'text-primary' : ''}">
                    ${row.sign === 'down' ? '-' : ''}${row.changeValue}
                </td>
            `;
            popularTbody.appendChild(tr);
        });
    })
    .catch(error => console.error("Error loading data:", error));
}