const popup = new Notification();

let pagination_button = document.getElementById("pagination-button");
let currentPage = 0;
let order_per_page = 10;

async function loadTable(firstResult) {
    const response = await fetch(`LoadOrderTable?start=${firstResult}&count=${order_per_page}`);
    if (response.ok) {
        const json = await response.json();
        if (json.status) {
            const order_table_body = document.getElementById("order-table-body");
            const pagination_container = document.getElementById("pagination-container");
            order_table_body.innerHTML = "";
            pagination_container.innerHTML = "";

            json.ordersList.forEach((order, index) => {
                const collapseId = `collapse-${index + 1}`;
                let total = 0;

                // Main row
                const row = document.createElement("tr");

                order.items.forEach(item => {
                    const quantity = item.qty;
                    const price = item.product.price;
                    total += quantity * price;
                });

                const delivery = order.deliveryType.price;
                total += delivery;

                row.innerHTML = `
                    <td>${index + 1 + firstResult}</td>
                    <td>#<span class="order-table-id">${order.id}</span></td>
                    <td>${order.user.first_name} ${order.user.last_name}</td>
                    <td>${order.address.line_1}, <br/>
                        ${order.address.line_2}, <br/>
                        ${order.address.city.name}, ${order.address.postal_code}
                    </td>
                    <td>${order.user.mobile}</td>
                    <td>${formatDate(order.created_at)}</td>
                    <td>Rs.${new Intl.NumberFormat("en-US", {minimumFractionDigits: 2}).format(total)}</td>
                    <td>
                        <span class="status-badge badge ${getStatusBadge(order.orderStatus.value)}">${order.orderStatus.value}</span>
                    </td>
                    <td>
                        ${order.orderStatus.value !== "Delivered"
                        ? `<button class="btn btn-sm btn-primary status-button">${getStatusButtonLabel(order.orderStatus.value)}</button>`
                        : ""}
                    </td>
                    <td class="justify-content-end">
                        <button class="btn btn-sm btn-outline-success"
                                data-bs-toggle="collapse"
                                data-bs-target="#${collapseId}"
                                aria-expanded="false"
                                aria-controls="${collapseId}">
                            <i class="fa fa-chevron-down"></i>
                        </button>
                    </td>
                `;

                const statusBtn = row.querySelector(".status-button");
                if (statusBtn) {
                    statusBtn.addEventListener("click", function () {
                        changeStatus(this);
                    });
                }

                order_table_body.appendChild(row);

                // Collapsible detail row
                const collapseRow = document.createElement("tr");
                collapseRow.classList.add("collapse");
                collapseRow.id = collapseId;

                let itemsHTML = order.items.map(item => {
                    const subtotal = item.qty * item.product.price;
                    return `
                        <tr>
                            <td>${item.product.title}</td>
                            <td>${item.qty}</td>
                            <td>Rs.${new Intl.NumberFormat("en-US", {minimumFractionDigits: 2}).format(subtotal)}</td>
                        </tr>
                    `;
                }).join("");

                collapseRow.innerHTML = `
                    <td colspan="10">
                        <div class="order-items-placeholder collapse" id="${collapseId}">
                            <table class="table table-striped table-hover">
                                <thead>
                                    <tr>
                                        <th>Title</th>
                                        <th>Qty</th>
                                        <th>Subtotal</th>
                                    </tr>
                                </thead>
                                <tbody>
                                    ${itemsHTML}
                                </tbody>
                            </table>
                        </div>
                    </td>
                `;

                order_table_body.appendChild(collapseRow);
            });

            let all_order_count = json.allOrderCount;
            let pages = Math.ceil(all_order_count / order_per_page);

            //previous-button
            if (currentPage !== 0) {
                let pagination_button_prev_clone = pagination_button.cloneNode(true);
                pagination_button_prev_clone.innerHTML = "Prev";
                pagination_button_prev_clone.addEventListener(
                        "click", (e) => {
                    currentPage--;
                    loadTable(currentPage * order_per_page);
                    e.preventDefault();
                });

                pagination_container.appendChild(pagination_button_prev_clone);
            }

            // pagination-buttons
            for (let i = 0; i < pages; i++) {
                let pagination_button_clone = pagination_button.cloneNode(true);
                pagination_button_clone.innerHTML = i + 1;
                pagination_button_clone.addEventListener(
                        "click", (e) => {
                    currentPage = i;
                    loadTable(i * order_per_page);
                    e.preventDefault();
                });

                if (i === Number(currentPage)) {
                    pagination_button_clone.className = "pagination-btn active";
                } else {
                    pagination_button_clone.className = "pagination-btn";
                }

                pagination_container.appendChild(pagination_button_clone);
            }

            //next-button
            if (currentPage !== (pages - 1)) {
                let pagination_button_next_clone = pagination_button.cloneNode(true);
                pagination_button_next_clone.innerHTML = "Next";
                pagination_button_next_clone.addEventListener(
                        "click", (e) => {
                    currentPage++;
                    loadTable(currentPage * order_per_page);
                    e.preventDefault();
                });

                pagination_container.appendChild(pagination_button_next_clone);
            }
        } else {
            popup.error({message: "Something went wrong"});
        }
    } else {
        popup.error({message: "Something went wrong"});
    }
}

async function changeStatus(buttonElement) {
    const row = buttonElement.closest("tr");
    const orderId = row.querySelector(".order-table-id").innerText;
    const status = row.querySelector(".status-badge").innerText;

    const data = {
        orderId: orderId,
        status: status
    };
    const dataJson = JSON.stringify(data);
    const response = await fetch("LoadOrderTable", {
        method: "PUT",
        headers: {
            "Content-Type": "application/json"
        },
        body: dataJson
    });

    if (response.ok) {
        const json = await response.json();
        if (json.status) {
            popup.success({
                message: "Order Status Changed!"
            });
            loadTable(currentPage * order_per_page);
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

// Helper Functions
function getStatusBadge(status) {
    switch (status) {
        case "Paid":
            return "bg-dark";
        case "Processing":
            return "bg-warning text-dark";
        case "Out for Delivery":
            return "bg-info text-dark";
        case "Delivered":
            return "bg-success";
        default:
            return "bg-secondary";
    }
}

function getStatusButtonLabel(status) {
    switch (status) {
        case "Paid":
            return "Process";
        case "Processing":
            return "Deliver";
        case "Out for Delivery":
            return "Confirm Delivery";
        default:
            return "";
    }
}

function formatDate(date) {
    const d = new Date(date);
    const year = d.getFullYear();
    const month = String(d.getMonth() + 1).padStart(2, '0');
    const day = String(d.getDate()).padStart(2, '0');
    return `${year}-${month}-${day}`;
}