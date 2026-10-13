payhere.onCompleted = function onCompleted(orderId) {
    const popup = new Notification();
    popup.success({
        message: "Payment completed. Please check your emails for the Invoice. OrderID:" + orderId
    });
    setTimeout(() => {
        window.location = "index.html";
    }, 5000);
};

// Payment window closed
payhere.onDismissed = function onDismissed() {
    console.log("Payment dismissed");
};

// Error occurred
payhere.onError = function onError(error) {
    console.log("Error:" + error);
};

async function loadCheckoutData() {
    const popup = new Notification();
    const response = await fetch("LoadCheckOutData");
    if (response.ok) {
        const json = await response.json();
        if (json.status) {
            const addressList = json.addressList;
            const cityList = json.cityList;
            const cartItems = json.cartList;
            const deliveryTypes = json.deliveryTypes;

            document.getElementById("firstName").value = json.firstName;
            document.getElementById("lastName").value = json.lastName;
            document.getElementById("mobile").value = json.mobile;
            document.getElementById("email").value = json.email;

            let city_select = document.getElementById("city-select");

            cityList.forEach(city => {
                let option = document.createElement("option");
                option.value = city.id;
                option.innerHTML = city.name;
                city_select.appendChild(option);
            });

            const address_list = document.getElementById("address-list");
            address_list.innerHTML = "";

            if (json.addressList) {
                json.addressList.forEach((address, index) => {
                    let addressData = `<div class="address-selection-box">
                        <h5 class="address-header">Address ${index + 1}
                            <input class="form-check-input border-success" type="checkbox" id="checkbox${index}" name="shipping_address" value="checkbox${index}">
                        </h5>
                        <div class="address-body">
                            <ul class="address-details list-unstyled">
                                <li class="line-1">${address.line_1},</li>
                                <li class="line-2">${address.line_2},</li>
                                <li class="city" data-city-id="${address.city.id}">${address.city.name}.</li> 
                                <li class="postal-code">${address.postal_code}</li>
                                <input type="hidden" class="address-id" value="${address.id}">
                            </ul>
                        </div>
                    </div>`;
                    address_list.innerHTML += addressData;
                });
            }

            document.querySelectorAll('input[name="shipping_address"]').forEach(checkbox => {
                checkbox.addEventListener("change", function () {
                    let line_one = document.getElementById("lineOne");
                    let line_two = document.getElementById("lineTwo");
                    let postal_code = document.getElementById("postalCode");
                    let address_id = document.getElementById("addressId");

                    if (this.checked) {
                        let addressBox = this.closest('.address-selection-box');

                        let u_line_one = addressBox.querySelector('.line-1');
                        let u_line_two = addressBox.querySelector('.line-2');
                        let u_city = addressBox.querySelector('.city');
                        let user_postal_code = addressBox.querySelector('.postal-code');
                        let user_address_id = addressBox.querySelector('.address-id');

                        let user_line_one = u_line_one.innerHTML.replace(/,$/, '');
                        let user_line_two = u_line_two.innerHTML.replace(/,$/, '');
                        let user_city_id = u_city.getAttribute('data-city-id');

                        city_select.value = user_city_id;
                        city_select.disabled = true;
                        city_select.dispatchEvent(new Event("change"));

                        line_one.value = user_line_one;
                        line_one.disabled = true;
                        line_two.value = user_line_two;
                        line_two.disabled = true;
                        postal_code.value = user_postal_code.innerHTML;
                        postal_code.disabled = true;
                        address_id.value = user_address_id.value;
                    } else {
                        city_select.value = 0;
                        city_select.disabled = false;
                        city_select.dispatchEvent(new Event("change"));
                        line_one.value = "";
                        line_one.disabled = false;
                        line_two.value = "";
                        line_two.disabled = false;
                        postal_code.value = "";
                        postal_code.disabled = false;
                        address_id.value = 0;
                    }
                });
            });

            $('input[name="shipping_address"]').on('change', function () {
                // First, uncheck all other checkboxes and remove 'selected' class from all boxes
                $('input[name="shipping_address"]').not(this).prop('checked', false);
                $('.address-selection-box').removeClass('selected');
                $('.address-header').removeClass('selected');

                // If this checkbox is checked, add 'selected' class to its parent
                if ($(this).is(':checked')) {
                    $(this).closest('.address-selection-box').addClass('selected');
                    $(this).closest('.address-header').addClass('selected');
                }
            });

            // Trigger change on load to set the initial state correctly
            $('input[name="shipping_address"]:checked').trigger('change');

            let st_tbody = document.getElementById("st-tbody");
            let st_item_tr = document.getElementById("st-item-tr");
            let st_subtotal_tr = document.getElementById("st-subtotal-tr");
            let st_order_shipping_tr = document.getElementById("st-order-shipping-tr");
            let st_order_total_tr = document.getElementById("st-order-total-tr");

            st_tbody.innerHTML = "";

            let total = 0;
            let item_count = 0;
            cartItems.forEach(cart => {
                let st_item_tr_clone = st_item_tr.cloneNode(true);
                st_item_tr_clone.querySelector("#st-product-title")
                        .innerHTML = cart.product.title;
                st_item_tr_clone.querySelector("#st-product-qty")
                        .innerHTML = cart.qty;

                item_count += cart.qty;
                let item_sub_total = Number(cart.qty) * Number(cart.product.price);

                st_item_tr_clone.querySelector("#st-product-price")
                        .innerHTML = new Intl.NumberFormat(
                                "en-US",
                                {minimumFractionDigits: 2})
                        .format(item_sub_total);
                st_tbody.appendChild(st_item_tr_clone);
                total += item_sub_total;
            });

            st_subtotal_tr.querySelector("#st-product-total-amount")
                    .innerHTML = new Intl.NumberFormat(
                            "en-US",
                            {minimumFractionDigits: 2})
                    .format(total);
            st_tbody.appendChild(st_subtotal_tr);

            let shipping_charges = 0;
            city_select.addEventListener("change", (e) => {
                let cityName = city_select.options[city_select.selectedIndex].innerHTML;
                if (cityName === "Kurunegala") {
                    shipping_charges = deliveryTypes[0].price;
                } else {
                    shipping_charges = deliveryTypes[1].price;
                }
                st_order_shipping_tr.querySelector("#st-product-shipping-charges")
                        .innerHTML = new Intl.NumberFormat(
                                "en-US",
                                {minimumFractionDigits: 2})
                        .format(shipping_charges);
                st_tbody.appendChild(st_order_shipping_tr);

                st_order_total_tr.querySelector("#st-order-total-amount")
                        .innerHTML = new Intl.NumberFormat(
                                "en-US",
                                {minimumFractionDigits: 2})
                        .format(shipping_charges + total);
                st_tbody.appendChild(st_order_total_tr);

                if (city_select.value === "0") {
                    if (st_order_shipping_tr.parentNode) {
                        st_order_shipping_tr.remove();
                    }
                    if (st_order_total_tr.parentNode) {
                        st_order_total_tr.remove();
                    }
                    return;
                }
            });
        } else {
            if (json.message === "empty-cart") {
                popup.error({
                    message: "Empty cart. Please add some products"
                });
                window.location = "index.html";
            } else {
                popup.error({
                    message: json.message
                });
            }
        }
    } else {
        if (response.status === 401) {
            window.location = "sign-in.html";
        }
    }
}

$(document).ready(function () {
    loadCheckoutData();
});

async function checkout() {
    let address_id = document.getElementById("addressId");
    let city_select = document.getElementById("city-select");
    let line_one = document.getElementById("lineOne");
    let line_two = document.getElementById("lineTwo");
    let postal_code = document.getElementById("postalCode");

    let data = {
        addressId: address_id.value,
        citySelect: city_select.value,
        lineOne: line_one.value,
        lineTwo: line_two.value,
        postalCode: postal_code.value
    };
    let dataJSON = JSON.stringify(data);

    const response = await fetch("CheckOut", {
        method: "POST",
        header: {
            "Content-Type": "application/json"
        },
        body: dataJSON
    });

    const popup = new Notification();
    if (response.ok) {
        const json = await response.json();
        if (json.status) {
            console.log(json);
            // PayHere Process
            payhere.startPayment(json.payHereJson);
        } else {
            popup.error({
                message: json.message
            });
        }
    } else {
        popup.error({
            message: "Something went wrong.Please try again!"
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
            loadCheckoutData();
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