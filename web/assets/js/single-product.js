async function loadData() {
    const searchParams = new URLSearchParams(window.location.search);
    if (searchParams.has("id")) {
        const productId = searchParams.get("id");
        const response = await fetch("LoadSingleProduct?id=" + productId);

        if (response.ok) {
            const json = await response.json();
            if (json.status) {
                //single-product-images - FIX: Changed backslash (\) to forward slash (/) for URLs
                document.getElementById("main-product-image").src = "product-images/" + json.product.id + "/image1.png";
                document.getElementById("thumb-image1").src = "product-images/" + json.product.id + "/image1.png";
                document.getElementById("thumb-image2").src = "product-images/" + json.product.id + "/image2.png";
                document.getElementById("thumb-image3").src = "product-images/" + json.product.id + "/image3.png";
                //single-product-images

                document.getElementById("title").innerHTML = json.product.title;
                const fullDate = json.product.created_at;
                const dateOnly = new Date(fullDate).toLocaleDateString('en-US', {
                    year: 'numeric',
                    month: 'short',
                    day: 'numeric'
                });
                document.getElementById("date").innerHTML = dateOnly;
                document.getElementById("price").innerHTML = new Intl.NumberFormat(
                        "en-US",
                        {minimumFractionDigits: 2})
                        .format(json.product.price);
                document.getElementById("category").innerHTML = json.product.category.value;
                document.getElementById("platform").innerHTML = json.product.platform.value;
                
                // Handle genre display - hide if "Not Applicable"
                const genreElement = document.getElementById("genre");
                const genreListItem = genreElement.closest('li');
                if (json.product.genre.value === "Not Applicable") {
                    genreListItem.style.display = "none";
                } else {
                    genreElement.innerHTML = json.product.genre.value;
                }
                
                document.getElementById("stock").innerHTML = json.product.qty;
                document.getElementById("description").innerHTML = json.product.description;

                //add-to-cart-main-button
                const addToCartMain = document.getElementById("cart-main");
                addToCartMain.addEventListener("click", function () {
                    const qty = document.getElementById("cart-qty").value;
                    addToCart(json.product.id, qty);
                });
                //add-to-cart-main-button

                // Initialize image slider functionality
                initializeImageSlider();

                //simillar-products
                let similer_product_main = document.getElementById("similer-product-main");
                let productHtml = document.getElementById("similer-product");
                similer_product_main.innerHTML = "";
                json.productList.forEach(item => {
                    let productCloneHtml = productHtml.cloneNode(true);
                    productCloneHtml.querySelector("#similer-product-a1").href = "single-product.html?id=" + item.id;
                    // FIX: Changed backslash (\) to forward slash (/) for URLs
                    productCloneHtml.querySelector("#similer-product-image").src = "product-images/" + item.id + "/image1.png";
                    productCloneHtml.querySelector("#similer-product-title").innerHTML = item.title;
                    productCloneHtml.querySelector("#similer-product-price").innerHTML = new Intl.NumberFormat(
                            "en-US",
                            {minimumFractionDigits: 2})
                            .format(item.price);
                    productCloneHtml.querySelector("#similer-product-a2").href = "single-product.html?id=" + item.id;
                    productCloneHtml.querySelector("#similer-product-add-to-cart").addEventListener(
                            "click", (e) => {
                        addToCart(item.id, 1);
                        e.preventDefault();
                    });

                    similer_product_main.appendChild(productCloneHtml);
                });

                //simillar-products

            } else {
                window.location = "index.html";
            }
        } else {
            window.location = "index.html";
        }
    }
}

function initializeImageSlider() {
    const thumbnails = document.querySelectorAll('.product-gallery .img-thumbnail');
    const mainImage = document.getElementById('main-product-image');

    thumbnails.forEach((thumbnail) => {
        
        thumbnail.addEventListener('click', function() {
            const newImageSrc = this.src;
            
            if (mainImage.src !== newImageSrc) {
                
                mainImage.style.opacity = '0';
                
                setTimeout(() => {
                    mainImage.src = newImageSrc;
                    mainImage.style.opacity = '1';
                }, 200);
            }

            thumbnails.forEach(thumb => thumb.classList.remove('active'));
            this.classList.add('active');
        });
    });
}

function initializeQuantityControls() {
    const qtyInput = document.getElementById("cart-qty");
    const minusBtn = document.querySelector(".btn-minus");
    const plusBtn = document.querySelector(".btn-plus");

    if (qtyInput && minusBtn && plusBtn) {

        plusBtn.addEventListener("click", () => {
            let currentQty = parseInt(qtyInput.value) || 1;
            qtyInput.value = currentQty + 1;
        });

        minusBtn.addEventListener("click", () => {
            let currentQty = parseInt(qtyInput.value) || 1;
            if (currentQty > 1) {
                qtyInput.value = currentQty - 1;
            }
        });

        qtyInput.addEventListener("input", () => {
            let value = qtyInput.value.replace(/\D/g, "");
            qtyInput.value = value ? Math.max(parseInt(value), 1) : 1;
        });

        qtyInput.addEventListener("keypress", (e) => {
            if (!/\d/.test(e.key)) {
                e.preventDefault();
            }
        });
        
    }
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

document.addEventListener("DOMContentLoaded", () => {
    initializeQuantityControls();
});