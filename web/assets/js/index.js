function indexOnloadFunctions() {
    checkSessionCart();
    loadProductData();
}

async function checkSessionCart() {
    const popup = new Notification();
    const response = await fetch("CheckSessionCart");
    if (!response.ok) {
        popup.error({
            message: "Something went wrong! Try again later"
        });
    }
}

async function loadProductData() {

    const popup = new Notification();
    const response = await fetch("LoadHomeData");
    if (response.ok) {
        const json = await response.json();
        if (json.status) {
            loadBrands(json);
            loadConsoles(json);
            loadFeaturedGames(json);
            loadNewReleases(json);
        } else {
            popup.error({
                message: "Something went wrong! Try again later"
            });
        }
    } else {
        popup.error({
            message: "Something went wrong! Try again later"
        });
    }
}

function loadBrands(json) {
    const product_platform_container = document.getElementById("product-platform-container");
    let product_platform_card = document.getElementById("product-platform-card");
    product_platform_container.innerHTML = "";
    json.platformList.forEach(item => {
        let product_platform_card_clone = product_platform_card.cloneNode(true);
        product_platform_card_clone.querySelector("#product-platform-img")
                .src = "assets/images/platforms/platform" + item.id + ".png";

        product_platform_container.appendChild(product_platform_card_clone);
    });
}

function loadConsoles(json) {
    const consoles_container = document.getElementById("consoles-container");
    consoles_container.innerHTML = "";

    json.consolesList.forEach(item => {
        let product_card = `<div class="col-md-6 col-lg-3">
                            <div class="product-card">
                                <a href="single-product.html?id=${item.id}">
                                   <img src="product-images\\${item.id}\\image1.png"
                                     class="card-img-top" alt="PS5 Console"> 
                                </a>
                                <div class="card-body">
                                    <h5 class="card-title">${item.title}</h5>
                                    <p class="card-text">Rs.<span>${new Intl.NumberFormat(
                        "en-US",
                        {minimumFractionDigits: 2})
                        .format(item.price)}</span></p>
                                    <a href="single-product.html?id=${item.id}" class="btn btn-outline-dark w-100 mb-2" id="st-product-a-2"><i class="fas fa-eye me-2"></i> View Details</a>
                                    <a class="btn btn-primary w-100" id="st-product-add-to-cart" onclick="addToCart(${item.id}, 1);"><i class="fas fa-cart-plus me-2"></i> Add to Cart</a>
                                </div>
                            </div>
                        </div>`;
        consoles_container.innerHTML += product_card;
    });
}

function loadFeaturedGames(json) {
    const featured_games_container = document.getElementById("featured-games-container");
    featured_games_container.innerHTML = "";

    json.featuredGamesList.forEach(item => {
        let product_card = `<div class="col-md-6 col-lg-3">
                            <div class="product-card">
                                <a href="single-product.html?id=${item.id}">
                                   <img src="product-images\\${item.id}\\image1.png"
                                     class="card-img-top" alt="PS5 Console"> 
                                </a>
                                <div class="card-body">
                                    <h5 class="card-title">${item.title}</h5>
                                    <p class="card-text">Rs.<span>${new Intl.NumberFormat(
                        "en-US",
                        {minimumFractionDigits: 2})
                        .format(item.price)}</span></p>
                                    <a href="single-product.html?id=${item.id}" class="btn btn-outline-dark w-100 mb-2" id="st-product-a-2"><i class="fas fa-eye me-2"></i> View Details</a>
                                    <a class="btn btn-primary w-100" id="st-product-add-to-cart" onclick="addToCart(${item.id}, 1);"><i class="fas fa-cart-plus me-2"></i> Add to Cart</a>
                                </div>
                            </div>
                        </div>`;
        featured_games_container.innerHTML += product_card;
    });
}

function loadNewReleases(json) {
    const new_releases_container = document.getElementById("new-releases-container");
    new_releases_container.innerHTML = "";

    json.newReleasesList.forEach(item => {
        let product_card = `<div class="col-md-6 col-lg-3">
                            <div class="product-card">
                                <a href="single-product.html?id=${item.id}">
                                   <img src="product-images\\${item.id}\\image1.png"
                                     class="card-img-top" alt="PS5 Console"> 
                                </a>
                                <div class="card-body">
                                    <h5 class="card-title">${item.title}</h5>
                                    <p class="card-text">Rs.<span>${new Intl.NumberFormat(
                        "en-US",
                        {minimumFractionDigits: 2})
                        .format(item.price)}</span></p>
                                    <a href="single-product.html?id=${item.id}" class="btn btn-outline-dark w-100 mb-2" id="st-product-a-2"><i class="fas fa-eye me-2"></i> View Details</a>
                                    <a class="btn btn-primary w-100" id="st-product-add-to-cart" onclick="addToCart(${item.id}, 1);"><i class="fas fa-cart-plus me-2"></i> Add to Cart</a>
                                </div>
                            </div>
                        </div>`;
        new_releases_container.innerHTML += product_card;
    });
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