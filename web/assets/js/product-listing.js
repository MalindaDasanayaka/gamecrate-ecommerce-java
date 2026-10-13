const popup = Notification();

async function loadProductData() {
    const response = await fetch("LoadProductData");

    if (response.ok) {
        const json = await response.json();
        if (json.status) {

            loadSelect("category", json.categoryList);
            loadSelect("platform", json.platformList);
            loadSelect("genre", json.genreList);
            loadTable(0);

        } else {
            document.getElementById("message").innerHTML = "Unable to load product data! Please try again later.";
        }
    } else {
        document.getElementById("message").innerHTML = "Unable to load product data! Please try again later.";
    }
}

function loadSelect(selectId, list) {
    const select = document.getElementById(selectId);

    list.forEach(item => {
        const option = document.createElement("option");
        option.value = item.id;
        option.innerHTML = item.value;
        select.appendChild(option);
    });
}

async function saveProduct(event) {
    event.preventDefault();

    const title = document.getElementById("title").value;
    const categoryId = document.getElementById("category").value;
    const platformId = document.getElementById("platform").value;
    const genreId = document.getElementById("genre").value;
    const description = document.getElementById("description").value;
    const price = document.getElementById("price").value;
    const qty = document.getElementById("qty").value;

    const image1 = document.getElementById("img1").files[0];
    const image2 = document.getElementById("img2").files[0];
    const image3 = document.getElementById("img3").files[0];

    const form = new FormData();
    form.append("title", title);
    form.append("categoryId", categoryId);
    form.append("platformId", platformId);
    form.append("genreId", genreId);
    form.append("description", description);
    form.append("price", price);
    form.append("qty", qty);
    form.append("image1", image1);
    form.append("image2", image2);
    form.append("image3", image3);

    const response = await fetch(
            "SaveProduct",
            {
                method: "POST",
                body: form
            }
    );

    if (response.ok) {
        const json = await response.json();
        if (json.status) {
            popup.success({
                message: "New Product added successfully"
            });
            document.getElementById("title").value = "";
            document.getElementById("category").value = 0;
            document.getElementById("platform").value = 0;
            document.getElementById("genre").value = 0;
            document.getElementById("description").value = "";
            document.getElementById("price").value = "";
            document.getElementById("price").placeholder = "0.00";
            document.getElementById("qty").value = 1;
            document.getElementById("img1").value = "";
            document.getElementById("img2").value = "";
            document.getElementById("img3").value = "";
            
            loadTable(currentPage * product_per_page);
        } else {
            if (json.message === "Please Sign In!") {
                window.location = "admin-sign-in.html";
            } else {
                popup.error({
                    message: json.message
                });
            }
        }
    } else {
        popup.error({
            message: json.message
        });
    }
}

const product_table_tr = document.getElementById("product-table-tr");
let pagination_button = document.getElementById("pagination-button");
let currentPage = 0;
let product_per_page = 10;

async function loadTable(firstResult) {

    const response = await fetch(`LoadProductTable?start=${firstResult}&count=${product_per_page}`);
    if (response.ok) {
        const json = await response.json();
        if (json.status) {
            const product_table_body = document.getElementById("product-table-body");
            const pagination_container = document.getElementById("pagination-container");
            product_table_body.innerHTML = "";
            pagination_container.innerHTML = "";

            json.productList.forEach(product => {
                let product_table_tr_clone = product_table_tr.cloneNode(true);

                product_table_tr_clone.querySelector(".product-table-id").innerHTML = product.id;
                product_table_tr_clone.querySelector(".product-table-title").innerHTML = product.title;
                product_table_tr_clone.querySelector(".product-table-category").innerHTML = product.category.value;
                product_table_tr_clone.querySelector(".product-table-category-id").innerHTML = product.category.id;
                product_table_tr_clone.querySelector(".product-table-platform").innerHTML = product.platform.value;
                product_table_tr_clone.querySelector(".product-table-platform-id").innerHTML = product.platform.id;
                product_table_tr_clone.querySelector(".product-table-description").innerHTML = product.description;
                product_table_tr_clone.querySelector(".product-table-genre").innerHTML = product.genre.value;
                product_table_tr_clone.querySelector(".product-table-genre-id").innerHTML = product.genre.id;
                product_table_tr_clone.querySelector(".product-table-price").innerHTML = new Intl.NumberFormat(
                        "en-US",
                        {minimumFractionDigits: 2})
                        .format(product.price);
                product_table_tr_clone.querySelector(".product-table-qty").innerHTML = product.qty;
                product_table_tr_clone.querySelector(".product-table-registered").innerHTML = formatDate(product.created_at);

                let updateButton = product_table_tr_clone.querySelector(".update-button");

                updateButton.onclick = function () {
                    fillProduct(this);
                };

                let statusSpan = product_table_tr_clone.querySelector(".status-color");
                let statusButton = product_table_tr_clone.querySelector(".status-button");

                if (product.productStatus.value === "Active") {
                    statusSpan.className = "status-color badge bg-success";
                    statusButton.className = "status-button btn btn-sm btn-outline-danger";
                    statusButton.innerText = "Discontinue";
                } else {
                    statusSpan.className = "status-color badge bg-danger";
                    statusButton.className = "status-button btn btn-sm btn-outline-success";
                    statusButton.innerText = "Re-Active";
                }
                statusSpan.innerText = product.productStatus.value;

                statusButton.onclick = function () {
                    changeStatus(this);
                };

                product_table_body.appendChild(product_table_tr_clone);
            });

            let all_product_count = json.allProductCount;
            let pages = Math.ceil(all_product_count / product_per_page);

            //previous-button
            if (currentPage !== 0) {
                let pagination_button_prev_clone = pagination_button.cloneNode(true);
                pagination_button_prev_clone.innerHTML = "Prev";
                pagination_button_prev_clone.addEventListener(
                        "click", (e) => {
                    currentPage--;
                    loadTable(currentPage * product_per_page);
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
                    loadTable(i * product_per_page);
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
                    loadTable(currentPage * product_per_page);
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

function fillProduct(buttonElement) {
    const row = buttonElement.closest("tr");
    const productId = row.querySelector(".product-table-id").innerText;
    const title = row.querySelector(".product-table-title").innerText;
    const categoryId = row.querySelector(".product-table-category-id").innerText;
    const platformId = row.querySelector(".product-table-platform-id").innerText;
    const genreId = row.querySelector(".product-table-genre-id").innerText;
    const description = row.querySelector(".product-table-description").innerText;
    const price = row.querySelector(".product-table-price").innerText.replace(/,|\.00/g, "");
    const qty = row.querySelector(".product-table-qty").innerText;

    document.getElementById("product-id").value = productId;
    document.getElementById("title").value = title;
    document.getElementById("category").value = categoryId;
    document.getElementById("platform").value = platformId;
    document.getElementById("genre").value = genreId;
    document.getElementById("description").value = description;
    document.getElementById("price").value = price;
    document.getElementById("qty").value = qty;

    document.getElementById("img1").disabled = true;
    document.getElementById("img2").disabled = true;
    document.getElementById("img3").disabled = true;

    const saveButton = document.getElementById("saveBtn");
    const updateButton = document.getElementById("updateBtn");

    if (saveButton) {
        saveButton.style.display = 'none';
    }

    if (updateButton) {
        updateButton.style.display = 'inline-block';
    }
}

async function updateProduct(event) {
    event.preventDefault();

    const productId = document.getElementById("product-id").value;
    const title = document.getElementById("title").value;
    const categoryId = document.getElementById("category").value;
    const platformId = document.getElementById("platform").value;
    const genreId = document.getElementById("genre").value;
    const description = document.getElementById("description").value;
    const price = document.getElementById("price").value;
    const qty = document.getElementById("qty").value;

    const productObject = {
        productId: productId,
        title: title,
        categoryId: categoryId,
        platformId: platformId,
        genreId: genreId,
        description: description,
        price: price,
        qty: qty
    };

    const productJson = JSON.stringify(productObject);

    const response = await fetch("SaveProduct", {
        method: "PUT",
        headers: {
            "Content-Type": "application/json"
        },
        body: productJson
    });

    if (response.ok) {
        const json = await response.json();
        if (json.status) {
            popup.success({
                message: "Product Updated Successfully"
            });
            document.getElementById("product-id").value = "";
            document.getElementById("title").value = "";
            document.getElementById("category").value = 0;
            document.getElementById("platform").value = 0;
            document.getElementById("genre").value = 0;
            document.getElementById("description").value = "";
            document.getElementById("price").value = "";
            document.getElementById("price").placeholder = "0.00";
            document.getElementById("qty").value = 1;
            document.getElementById("img1").disabled = false;
            document.getElementById("img2").disabled = false;
            document.getElementById("img3").disabled = false;

            const saveButton = document.getElementById("saveBtn");
            const updateButton = document.getElementById("updateBtn");

            if (saveButton) {
                saveButton.style.display = 'inline-block';
            }

            if (updateButton) {
                updateButton.style.display = 'none';
            }
            loadTable(currentPage * product_per_page);
        } else {
            if (json.message === "Please Sign In!") {
                window.location = "admin-sign-in.html";
            } else {
                popup.error({
                    message: json.message
                });
            }
        }
    } else {
        popup.error({
            message: json.message
        });
    }
}

async function changeStatus(buttonElement) {
    const row = buttonElement.closest("tr");
    const productId = row.querySelector(".product-table-id").innerText;
    const status = row.querySelector(".status-color").innerText;

    const data = {
        productId: productId,
        status: status
    };
    const dataJson = JSON.stringify(data);
    const response = await fetch("LoadProductTable", {
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
                message: "Product Status Changed!"
            });
            loadTable(currentPage * product_per_page);
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