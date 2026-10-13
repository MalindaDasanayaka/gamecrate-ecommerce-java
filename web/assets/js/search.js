async function loadData() {
    const popup = new Notification();
    const response = await fetch("LoadData");
    if (response.ok) {
        const json = await response.json();
        if (json.status) {
            document.getElementById("all-item-count").innerHTML = json.allProductCount;
            loadOptions("category", json.categoryList);
            loadOptions("platform", json.platformList);
            loadOptions("genre", json.genreList);

            updateProductView(json);
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

function loadOptions(prefix, dataList) {
    const container = document.getElementById(prefix + "-options");
    container.innerHTML = "";

    // Filter out "Not Applicable" from genre list
    const filteredDataList = prefix === "genre"
            ? dataList.filter(item => item.value !== "Not Applicable")
            : dataList;

    filteredDataList.forEach((item, index) => {
        const inputId = `${prefix}-input-${index}`;
        const inputName = `${prefix}-input`;

        const input = document.createElement("input");
        input.className = "form-check-input";
        input.type = "radio";
        input.name = inputName;
        input.id = inputId;
        input.value = item.value;

        const label = document.createElement("label");
        label.className = "form-check-label";
        label.setAttribute("for", inputId);
        label.textContent = item.value;

        const wrapper = document.createElement("div");
        wrapper.className = "form-check";
        wrapper.appendChild(input);
        wrapper.appendChild(label);

        container.appendChild(wrapper);
    });

    const inputs = container.querySelectorAll("input");
    inputs.forEach(input => {
        input.addEventListener("change", () => {
            inputs.forEach(i => i.classList.remove("chosen"));
            input.classList.add("chosen");
        });
    });
}

async function searchProduct(firstResult) {
    const popup = new Notification();

    const categoryRadio = document.querySelector('#category-options input[name="category-input"]:checked');
    const category_name = categoryRadio ? categoryRadio.nextElementSibling.innerText : null;

    const platformRadio = document.querySelector('#platform-options input[name="platform-input"]:checked');
    const platform_name = platformRadio ? platformRadio.nextElementSibling.innerText : null;

    const genreRadio = document.querySelector('#genre-options input[name="genre-input"]:checked');
    const genre_name = genreRadio ? genreRadio.nextElementSibling.innerText : null;


    const price_range_start = document.getElementById("rangeMin").value;
    const price_range_end = document.getElementById("rangeMax").value;

    const sort_value = document.getElementById("st-sort").value;

    const data = {
        firstResult: firstResult,
        categoryName: category_name,
        platformName: platform_name,
        genreName: genre_name,
        priceStart: price_range_start,
        priceEnd: price_range_end,
        sortValue: sort_value
    };

    const dataJSON = JSON.stringify(data);

    const response = await fetch("SearchProducts", {
        method: "POST",
        headers: {
            "Content-Type": "application/json"
        },
        body: dataJSON
    });

    if (response.ok) {
        const json = await response.json();
        if (json.status) {
            updateProductView(json);
        } else {
            popup.error({
                message: "Something went wrong. Please try again later."
            });
        }
    } else {
        popup.error({
            message: "Something went wrong. Please try again later."
        });
    }
}

const st_product = document.getElementById("st-product");
let st_pagination_button = document.getElementById("st-pagination-button");
let currentPage = 0;

function updateProductView(json) {
    const product_container = document.getElementById("st-product-container");
    const st_pagination_container = document.getElementById("st-pagination-container");
    product_container.innerHTML = "";
    st_pagination_container.innerHTML = "";

    if (!json.productList || json.productList.length === 0) {
        document.getElementById("no-address-message").innerHTML = `<p>No Such Products with The Given Search Terms.</p>`;
        return;
    }

    json.productList.forEach(product => {
        let st_product_clone = st_product.cloneNode(true);
        st_product_clone.querySelector("#st-product-a-1").href = "single-product.html?id=" + product.id;
        st_product_clone.querySelector("#st-product-img-1").src = "product-images//" + product.id + "//image1.png";
        st_product_clone.querySelector("#st-product-title-1").innerHTML = product.title;
        st_product_clone.querySelector("#st-product-price-1").innerHTML = new Intl.NumberFormat(
                "en-US",
                {minimumFractionDigits: 2})
                .format(product.price);
        st_product_clone.querySelector("#st-product-a-2").href = "single-product.html?id=" + product.id;
        st_product_clone.querySelector("#st-product-add-to-cart").addEventListener(
                "click", (e) => {
            addToCart(product.id, 1);
            e.preventDefault();
        });

        product_container.appendChild(st_product_clone);
    });

    let all_product_count = json.allProductCount;
    document.getElementById("all-item-count").innerHTML = json.allProductCount;
    let product_per_page = 6;
    const firstResult = currentPage * product_per_page + 1;
    let lastResult = (currentPage + 1) * product_per_page;
    if (lastResult > all_product_count) {
        lastResult = all_product_count;
    }
    document.getElementById("first-result").innerHTML = firstResult;
    document.getElementById("last-result").innerHTML = lastResult;
    let pages = Math.ceil(all_product_count / product_per_page); // round upper integer

    //previous-button
    if (currentPage !== 0) {
        let st_pagination_button_prev_clone = st_pagination_button.cloneNode(true);
        st_pagination_button_prev_clone.innerHTML = "Prev";
        st_pagination_button_prev_clone.addEventListener(
                "click", (e) => {
            currentPage--;
            searchProduct(currentPage * product_per_page);
            e.preventDefault();
        });

        st_pagination_container.appendChild(st_pagination_button_prev_clone);
    }

    // pagination-buttons
    for (let i = 0; i < pages; i++) {
        let st_pagination_button_clone = st_pagination_button.cloneNode(true);
        st_pagination_button_clone.innerHTML = i + 1;
        st_pagination_button_clone.addEventListener(
                "click", (e) => {
            currentPage = i;
            searchProduct(i * product_per_page);
            e.preventDefault();
        });

        if (i === Number(currentPage)) {
            st_pagination_button_clone.className = "pagination-btn active";
        } else {
            st_pagination_button_clone.className = "pagination-btn";
        }

        st_pagination_container.appendChild(st_pagination_button_clone);
    }

    //next-button
    if (currentPage !== (pages - 1)) {
        let st_pagination_button_next_clone = st_pagination_button.cloneNode(true);
        st_pagination_button_next_clone.innerHTML = "Next";
        st_pagination_button_next_clone.addEventListener(
                "click", (e) => {
            currentPage++;
            searchProduct(currentPage * product_per_page);
            e.preventDefault();
        });

        st_pagination_container.appendChild(st_pagination_button_next_clone);
    }

}

function reset() {
    window.location.reload();
}

async function addToCart(productId, qty) {
    const popup = new Notification();
    const response = await fetch("AddToCart?prId=" + productId + "&qty=" + qty);
    if (response.ok) {
        const json = await response.json();
        if (json.status) {
            popup.success({
                message: json.message
            });
        } else {
            popup.error({
                message: json.message
            });
        }
    } else {
        popup.error({
            message: "Something went wrong. Try again"
        });
    }
}
