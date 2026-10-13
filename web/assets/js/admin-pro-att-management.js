const popup = Notification();

async function loadProductData() {
    const response = await fetch("LoadProductData");

    if (response.ok) {
        const json = await response.json();
        if (json.status) {

            loadTable("category", json.categoryList);
            loadTable("platform", json.platformList);
            loadTable("genre", json.genreList);
            loadDeliveryTable(json.deliveryTypeList);

        } else {
            document.getElementById("message").innerHTML = "Unable to load product data! Please try again later.";
        }
    } else {
        document.getElementById("message").innerHTML = "Unable to load product data! Please try again later.";
    }
}

function loadTable(tableName, row) {
    const table = document.getElementById(tableName + "-table");
    table.innerHTML = "";

    row.forEach(item => {
        const tr = document.createElement("tr");
        tr.innerHTML = `
            <td>${item.id}</td>
            <td>${item.value}</td>
        `;

        tr.addEventListener("click", () => {
            document.getElementById(tableName).value = item.value;
            document.getElementById(tableName + "-id").value = item.id;
        });

        table.appendChild(tr);
    });
}

function loadDeliveryTable(row) {
    const table = document.getElementById("delivery-table");
    table.innerHTML = "";

    row.forEach(item => {
        const tr = document.createElement("tr");
        tr.innerHTML = `
            <td>${item.name}</td>
            <td>Rs.${new Intl.NumberFormat(
                        "en-US",
                        {minimumFractionDigits: 2})
                        .format(item.price)}</td>
        `;

        tr.addEventListener("click", () => {
            document.getElementById("delivery").value = item.price;
            document.getElementById("delivery-id").value = item.id;
        });

        table.appendChild(tr);
    });
}

async function submitAttribute(type, method) {
    const value = document.getElementById(type).value;
    const id = document.getElementById(type + "-id")?.value;
    
    const data = {};
    data[type] = value;
    
    if (method === "PUT" && id) {
        data["id"] = id;
    }

    const jsonData = JSON.stringify(data);

    const servletURL = type.charAt(0).toUpperCase() + type.slice(1) + "Data";

    const response = await fetch(servletURL, {
        method: method,
        body: jsonData,
        headers: {
            "Content-Type": "application/json"
        }
    });

    if (response.ok) {
        const json = await response.json();
        if (json.status) {
            popup.success({
                message: method === "POST" ? `New ${type} added successfully` : `${type.charAt(0).toUpperCase() + type.slice(1)} updated successfully`
            });

            document.getElementById(type).value = "";
            document.getElementById(type).placeholder = `New ${type.charAt(0).toUpperCase() + type.slice(1)}`;

            loadProductData();

        } else {
            popup.error({message: json.message});
        }
    } else {
        popup.error({message: "Server error. Please try again."});
    }
}

async function updatePrice() {
    const price = document.getElementById("delivery").value;
    const id = document.getElementById("delivery-id")?.value;
    
    const data = {
        id:id,
        price:price
    };
    const jsonData = JSON.stringify(data);
    const response = await fetch("UpdateDeliveryPrice", {
        method: "PUT",
        headers: {
            "Content-Type": "application/json"
        },
        body: jsonData
    });

    if (response.ok) {
        const json = await response.json();
        if (json.status) {
            popup.success({
                message: "Delivery Price Updated!"
            });

            document.getElementById("delivery").value = "";
            loadProductData();

        } else {
            popup.error({message: json.message});
        }
    } else {
        popup.error({message: "Server error. Please try again."});
    }
}