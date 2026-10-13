async function loadCartItems() {
    const popup = new Notification();
    const response = await fetch("LoadCartItems");
    const clearCartBtn = document.getElementById("clearCartBtn");
    const cartSummary = document.getElementById("cart-summary");
    if (response.ok) {
        const json = await response.json();
        const cart_item_container = document.getElementById("cart-item-container");
        cart_item_container.innerHTML = "";

        if (json.status && json.cartItems && json.cartItems.length > 0) {
            if (clearCartBtn)
                clearCartBtn.style.display = "inline-block";
            if (cartSummary)
                cartSummary.style.display = "block";

            json.cartItems.forEach((cart, index) => {
                const productPrice = cart.product.price;
                const productSubTotal = productPrice * cart.qty;

                let tableData = `<tr data-index="${index}">
                    <td><a href="single-product.html?id=${cart.product.id}">
                            <img src="product-images\\${cart.product.id}\\image1.png" class="img-fluid rounded" style="max-width: 100px;">
                        </a>
                    </td>
                    <td class="align-middle"><strong>${cart.product.title}</strong></td>
                    <td class="align-middle text-center">Rs.<span class="product-price">${productPrice.toFixed(2)}</span></td>
                    <td class="align-middle">
                        <div class="d-flex justify-content-center">
                            <div class="input-group qty-group">
                                <button class="btn btn-outline-secondary btn-minus" type="button">-</button>
                                <input type="text" class="form-control quantity-input" value="${cart.qty}" min="1">
                                <button class="btn btn-outline-secondary btn-plus" type="button">+</button>
                            </div>
                        </div>
                    </td>
                    <td class="align-middle text-center">Rs.<span class="product-subtotal">${productSubTotal.toFixed(2)}</span></td>
                    <td class="align-middle text-center"><button class="btn btn-danger btn-sm" onclick="deleteCartItem(${cart.id});"><i class="fas fa-trash"></i></button></td>
                </tr>`;
                cart_item_container.innerHTML += tableData;
            });

            function updateTotals() {
                let totalQty = 0;
                let subtotal = 0;

                document.querySelectorAll("#cart-item-container tr").forEach(row => {
                    const qty = parseInt(row.querySelector(".quantity-input").value);
                    const price = parseFloat(row.querySelector(".product-price").textContent);
                    const subTotal = price * qty;

                    row.querySelector(".product-subtotal").textContent = subTotal.toFixed(2);

                    totalQty += qty;
                    subtotal += subTotal;
                });

                document.getElementById("order-total-quantity").textContent = totalQty;
                document.getElementById("order-total-amount").textContent = new Intl.NumberFormat(
                        "en-US",
                        {minimumFractionDigits: 2})
                        .format(subtotal);
            }

            async function updateCartQuantity(productId, newQty) {
                const response = await fetch("AddToCart", {
                    method: "PUT",
                    headers: {"Content-Type": "application/json"},
                    body: JSON.stringify({productId, qty: newQty})
                });

                if (response.ok) {
                    return await response.json();
                } else {
                    return {status: false, message: "Something went wrong. Try again"};
                }
            }

            document.querySelectorAll(".btn-minus").forEach(button => {
                button.addEventListener("click", async function () {
                    const input = this.nextElementSibling;
                    let currentValue = parseInt(input.value);
                    const row = this.closest("tr");
                    const productId = row.querySelector("a").href.split("id=")[1];
                    if (!isNaN(currentValue) && currentValue > 1) {
                        const newQty = currentValue - 1;
                        const json = await updateCartQuantity(productId, newQty);

                        if (json.status) {
                            input.value = newQty;
                            updateTotals();
                            popup.success({message: json.message});
                        } else {
                            popup.error({message: json.message});
                        }
                    }
                });
            });

            document.querySelectorAll(".btn-plus").forEach(button => {
                button.addEventListener("click", async function () {
                    const input = this.previousElementSibling;
                    let currentValue = parseInt(input.value);
                    const row = this.closest("tr");
                    const productId = row.querySelector("a").href.split("id=")[1];
                    if (!isNaN(currentValue)) {
                        const newQty = currentValue + 1;
                        const json = await updateCartQuantity(productId, newQty);

                        if (json.status) {
                            input.value = newQty;
                            updateTotals();
                            popup.success({message: json.message});
                        } else {
                            popup.error({message: json.message});
                        }
                    }
                });
            });

            document.querySelectorAll(".quantity-input").forEach(input => {
                input.addEventListener("input", async function () {
                    this.value = this.value.replace(/[^0-9]/g, '').replace(/^0+/, '') || 1;
                    const row = this.closest("tr");
                    const productId = row.querySelector("a").href.split("id=")[1];
                    const newQty = parseInt(this.value);

                    if (!isNaN(newQty) && newQty > 0) {
                        const json = await updateCartQuantity(productId, newQty);

                        if (json.status) {
                            updateTotals();
                            popup.success({message: json.message});
                        } else {
                            popup.error({message: json.message});
                        }
                    }
                });
            });

            updateTotals();
        } else {
            cart_item_container.innerHTML = `<tr>
                <td colspan="6" class="text-center text-white-50">Your Cart is Empty.</td>
            </tr>`;
            if (cartSummary)
                cartSummary.style.display = "none";
            if (clearCartBtn)
                clearCartBtn.style.display = "none";
        }
    } else {
        popup.error({message: "Cart Items loading failed"});
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
            loadCartItems();
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

async function clearCart() {
    const popup = new Notification();
    const response = await fetch("ClearCart");
    if (response.ok) {
        const json = await response.json();
        if (json.status) {
            popup.success({
                message: json.message
            });
            loadCartItems();
        }
    } else {
        popup.error({message: "Cart Items deleting failed"});
    }
}

async function deleteCartItem(id) {
    const popup = new Notification();
    const response = await fetch("DeleteCartItem?id=" + id);
    if (response.ok) {
        const json = await response.json();
        if (json.status) {
            popup.success({
                message: json.message
            });
            loadCartItems();
        }
    } else {
        popup.error({message: "Cart Items deleting failed"});
    }
}