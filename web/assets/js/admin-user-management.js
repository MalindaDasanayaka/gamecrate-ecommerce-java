const popup = new Notification();

const user_table_tr = document.getElementById("user-table-tr");
let pagination_button = document.getElementById("pagination-button");
let currentPage = 0;
let user_per_page = 10;

async function loadTable(firstResult) {

    const response = await fetch(`LoadUserTable?start=${firstResult}&count=${user_per_page}`);
    if (response.ok) {
        const json = await response.json();
        if (json.status) {
            const user_table_body = document.getElementById("user-table-body");
            const pagination_container = document.getElementById("pagination-container");
            user_table_body.innerHTML = "";
            pagination_container.innerHTML = "";

            json.userList.forEach(user => {
                let user_table_tr_clone = user_table_tr.cloneNode(true);

                user_table_tr_clone.querySelector(".user-table-id").innerHTML = user.id;
                user_table_tr_clone.querySelector(".user-table-name").innerHTML = user.first_name + " " + user.last_name;
                user_table_tr_clone.querySelector(".user-table-email").innerHTML = user.email;
                user_table_tr_clone.querySelector(".user-table-mobile").innerHTML = user.mobile;
                user_table_tr_clone.querySelector(".user-table-registered").innerHTML = formatDate(user.created_at);

                let statusSpan = user_table_tr_clone.querySelector(".status-color");
                let statusButton = user_table_tr_clone.querySelector(".status-button");

                if (user.userStatus.value === "Active") {
                    statusSpan.className = "status-color badge bg-success";
                    statusButton.className = "status-button btn btn-sm btn-outline-danger";
                    statusButton.innerText = "Block";
                } else {
                    statusSpan.className = "status-color badge bg-danger";
                    statusButton.className = "status-button btn btn-sm btn-outline-success";
                    statusButton.innerText = "Unblock";
                }
                statusSpan.innerText = user.userStatus.value;

                statusButton.onclick = function () {
                    changeStatus(this);
                };

                user_table_body.appendChild(user_table_tr_clone);
            });

            let all_user_count = json.allUserCount;
            let pages = Math.ceil(all_user_count / user_per_page);

            //previous-button
            if (currentPage !== 0) {
                let pagination_button_prev_clone = pagination_button.cloneNode(true);
                pagination_button_prev_clone.innerHTML = "Prev";
                pagination_button_prev_clone.addEventListener(
                        "click", (e) => {
                    currentPage--;
                    loadTable(currentPage * user_per_page);
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
                    loadTable(i * user_per_page);
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
                    loadTable(currentPage * user_per_page);
                    e.preventDefault();
                });

                pagination_container.appendChild(pagination_button_next_clone);
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

async function changeStatus(buttonElement) {
    const row = buttonElement.closest("tr");
    const userId = row.querySelector(".user-table-id").innerText;
    const status = row.querySelector(".status-color").innerText;

    const data = {
        userId: userId,
        status: status
    };
    const dataJson = JSON.stringify(data);
    const response = await fetch("LoadUserTable", {
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
                message: "User Status Changed!"
            });
            loadTable(currentPage * user_per_page);
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