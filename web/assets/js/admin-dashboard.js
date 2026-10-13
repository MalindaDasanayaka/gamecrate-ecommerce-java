function  loadData() {
    loadDashboard();
    loadOrders();
}

const popup = new Notification();

async function loadDashboard() {

    const response = await fetch("LoadDashboardData");
    if (response.ok) {
        const json = await response.json();
        if (json.status) {
            document.getElementById("userCount").innerHTML = json.userCount;
            document.getElementById("productCount").innerHTML = json.productCount;
            document.getElementById("itemCount").innerHTML = json.itemCount;
            document.getElementById("totalSales").innerHTML = new Intl.NumberFormat(
                    "en-US",
                    {minimumFractionDigits: 2})
                    .format(json.totalSales);

            // Chart.js - Dashboard Charts
            // Make sure you are on the dashboard page before running this
            if (document.getElementById('salesChart')) {
                const ctxSales = document.getElementById('salesChart').getContext('2d');
                const salesChart = new Chart(ctxSales, {
                    type: 'line',
                    data: {
                        labels: json.salesLabels,
                        datasets: [{
                                label: 'Sales (Rs.)',
                                data: json.salesData,
                                backgroundColor: 'rgba(0, 209, 255, 0.2)',
                                borderColor: 'rgba(0, 209, 255, 1)',
                                borderWidth: 2,
                                tension: 0.4
                            }]
                    },
                    options: {
                        responsive: true,
                        plugins: {legend: {labels: {color: '#eef4ed'}}},
                        scales: {
                            y: {ticks: {color: '#eef4ed'}},
                            x: {ticks: {color: '#eef4ed'}}
                        }
                    }
                });
            }

            if (document.getElementById('platformChart')) {
                const ctxCategory = document.getElementById('platformChart').getContext('2d');
                const categoryChart = new Chart(ctxCategory, {
                    type: 'doughnut',
                    data: {
                        labels: json.platformLabels,
                        datasets: [{
                                label: 'Sales by Platform',
                                data: json.platformData,
                                backgroundColor: json.platformColors,
                                borderColor: '#232946',
                                borderWidth: 3
                            }]
                    },
                    options: {
                        responsive: true,
                        plugins: {legend: {position: 'top', labels: {color: '#eef4ed'}}}
                    }
                });
            }
        } else {
            popup.error({
                message: "Something went wrong"
            });
        }
    } else {
        popup.error({
            message: "Something went wrong"
        });
    }
}

async function loadOrders() {
    const response = await fetch("LoadRecentOrders");
    if (response.ok) {
        const json = await response.json();
        if (json.status) {
            const order_table = document.getElementById("order-table");
            const order_table_tr = document.getElementById("order-table-tr");
            order_table.innerHTML = "";


            json.ordersList.forEach(order => {
                let total = 0;
                if (order.items && order.items.length > 0) {
                    order.items.forEach(item => {
                        total += (item.qty * item.product.price);
                    });
                }

                total += order.deliveryType.price;

                let order_table_tr_clone = order_table_tr.cloneNode(true);

                order_table_tr_clone.querySelector("#order-table-id").innerHTML = order.id;
                order_table_tr_clone.querySelector("#order-table-user").innerHTML = order.user.first_name + " " + order.user.last_name;
                order_table_tr_clone.querySelector("#order-table-date").innerHTML = formatDate(order.created_at);
                order_table_tr_clone.querySelector("#order-table-total").innerHTML = new Intl.NumberFormat(
                        "en-US",
                        {minimumFractionDigits: 2})
                        .format(total);
                order_table_tr_clone.querySelector("#order-table-status").innerHTML = order.orderStatus.value;

                order_table.appendChild(order_table_tr_clone);
            });

        } else {
            popup.error({
                message: "Something went wrong"
            });
        }
    } else {
        popup.error({
            message: "Something went wrong"
        });
    }
}

function formatDate(date) {
    const d = new Date(date);
    const year = d.getFullYear();
    const month = String(d.getMonth() + 1).padStart(2, '0');
    const day = String(d.getDate()).padStart(2, '0');
    return `${year}-${month}-${day}`;
}