const popup = new Notification();

function loadData() {
    loadUserData();
    loadCityData();
    loadAddress();
    loadOrderHistory();
}

async function loadUserData() {
    const response = await fetch("Profile");

    if (response.ok) {
        const json = await response.json();
        document.getElementById("username").innerHTML = `Hello, ${json.firstName} ${json.lastName}`;
        document.getElementById("since").innerHTML = `Member Since ${json.since}`;
        document.getElementById("firstName").value = json.firstName;
        document.getElementById("lastName").value = json.lastName;
        document.getElementById("mobile").value = json.mobile;
        document.getElementById("email").value = json.email;

        document.getElementById("avatar").src = `https://ui-avatars.com/api/?background=1ecb5c&color=000&name=${json.firstName}+${json.lastName}`;
    } else {
        if (response.status === 401) {
            window.location = "sign-in.html";
        }
    }
}

async function saveProfileChanges(event) {
    event.preventDefault();

    const firstName = document.getElementById("firstName").value;
    const lastName = document.getElementById("lastName").value;
    const mobile = document.getElementById("mobile").value;

    const userDataObject = {
        firstName: firstName,
        lastName: lastName,
        mobile: mobile
    };

    const userDataJson = JSON.stringify(userDataObject);

    const response = await fetch("Profile", {
        method: "PUT",
        headers: {
            "Content-Type": "application/json"
        },
        body: userDataJson
    });
    if (response.ok) {
        const json = await response.json();
        if (json.status) {
            popup.success({
                message: "Profile Details Updated!"
            });
            loadUserData();
        } else {
            document.getElementById("message").innerHTML = json.message;
        }
    } else {
        document.getElementById("message").innerHTML = "Profile details update failed!";
    }
}

async function loadCityData() {
    const response = await fetch("CityData");
    if (response.ok) {
        const json = await response.json();
        const citySelect = document.getElementById("citySelect");
        json.forEach(city => {
            let option = document.createElement("option");
            option.innerHTML = city.name;
            option.value = city.id;
            citySelect.appendChild(option);
        });

    }
}

async function saveAddress(event) {
    event.preventDefault();

    const lineOne = document.getElementById("lineOne").value;
    const lineTwo = document.getElementById("lineTwo").value;
    const postalCode = document.getElementById("postalCode").value;
    const cityId = document.getElementById("citySelect").value;

    const addressObject = {
        lineOne: lineOne,
        lineTwo: lineTwo,
        postalCode: postalCode,
        cityId: cityId
    };

    const addressJson = JSON.stringify(addressObject);

    const response = await fetch("SaveAddress", {
        method: "POST",
        headers: {
            "Content-Type": "application/json"
        },
        body: addressJson
    });
    if (response.ok) {
        const json = await response.json();
        if (json.status) {
            popup.success({
                message: "Address Saved!"
            });
            resetAddressForm();
            loadAddress();
        } else {
            document.getElementById("message").innerHTML = json.message;
        }
    } else {
        document.getElementById("message").innerHTML = "Address Saving failed!";
    }
}

async function loadAddress() {
    const response = await fetch("SaveAddress");
    const container = document.getElementById("addresses-accordion");
    const msgBox = document.getElementById("no-address-message");

    container.innerHTML = "";
    msgBox.innerHTML = "";

    if (response.ok) {
        const json = await response.json();

        if (!json.addressList || json.addressList.length === 0) {
            msgBox.innerHTML = `<p>No addresses have been added yet.</p>`;
            return;
        }

        json.addressList.forEach((address, index) => {
            const {
                id, line_1, line_2, city, postal_code
            } = address;

            const collapseId = `collapseAddr${index}`;
            const accordionItem = `
                <div class="accordion-item mb-3">
                    <h2 class="accordion-header">
                        <button class="accordion-button ${index !== 0 ? 'collapsed' : ''}" 
                                type="button" 
                                data-bs-toggle="collapse" 
                                data-bs-target="#${collapseId}">
                            <strong>Address ${index + 1}</strong>
                        </button>
                    </h2>
                    <div id="${collapseId}" 
                         class="accordion-collapse collapse ${index === 0 ? 'show' : ''}" 
                         data-bs-parent="#addresses-accordion">
                        <div class="accordion-body">
                            <ul class="address-details list-unstyled">
                                <li>${line_1},</li>
                                <li>${line_2},</li>
                                <li>${city.name}.</li> 
                                <li>${postal_code}</li>
                            </ul>
                            <button class="btn btn-sm btn-outline-secondary" onclick="updateAddress(${id}, '${line_1}', '${line_2}', ${city.id}, '${postal_code}')">Update</button>
                        </div>
                    </div>
                </div>
            `;

            container.insertAdjacentHTML("beforeend", accordionItem);
        });
    } else {
        msgBox.innerHTML = `<p class="text-danger">Failed to load addresses. Please try again later.</p>`;
    }
}

function updateAddress(addressId, lineOne, lineTwo, cityId, postalCode) {
    document.getElementById("lineOne").value = lineOne;
    document.getElementById("lineTwo").value = lineTwo;
    document.getElementById("citySelect").value = cityId;
    document.getElementById("postalCode").value = postalCode;

    document.getElementById("lineOne").setAttribute("data-address-id", addressId);

    const saveButton = document.getElementById("saveAddressBtn");
    const updateButton = document.getElementById("updateAddressBtn");

    if (saveButton) {
        saveButton.style.display = 'none';
    }

    if (updateButton) {
        updateButton.style.display = 'inline-block';
    }

    document.getElementById("v-pills-addresses-tab").click();
    document.getElementById("lineOne").scrollIntoView({behavior: 'smooth', block: 'center'});
}

async function updateAddressInDatabase(event) {
    event.preventDefault();

    const addressId = document.getElementById("lineOne").getAttribute("data-address-id");
    const lineOne = document.getElementById("lineOne").value;
    const lineTwo = document.getElementById("lineTwo").value;
    const postalCode = document.getElementById("postalCode").value;
    const cityId = document.getElementById("citySelect").value;

    const addressObject = {
        addressId: addressId,
        lineOne: lineOne,
        lineTwo: lineTwo,
        postalCode: postalCode,
        cityId: cityId
    };

    const addressJson = JSON.stringify(addressObject);

    const response = await fetch("SaveAddress", {
        method: "PUT",
        headers: {
            "Content-Type": "application/json"
        },
        body: addressJson
    });

    if (response.ok) {
        const json = await response.json();
        if (json.status) {
            popup.success({
                message: "Address Updated!"
            });
            resetAddressForm();
            loadAddress();
        } else {
            document.getElementById("message").innerHTML = json.message;
        }
    } else {
        document.getElementById("message").innerHTML = "Address update failed!";
    }
}

function resetAddressForm() {
    document.getElementById("lineOne").value = "";
    document.getElementById("lineTwo").value = "";
    document.getElementById("citySelect").value = "0";
    document.getElementById("postalCode").value = "";

    document.getElementById("lineOne").removeAttribute("data-address-id");

    const saveButton = document.getElementById("saveAddressBtn");
    const updateButton = document.getElementById("updateAddressBtn");

    if (saveButton) {
        saveButton.style.display = 'inline-block';
    }

    if (updateButton) {
        updateButton.style.display = 'none';
    }
}

async function resetPassword(event) {
    event.preventDefault();

    const currentPassword = document.getElementById("currentPassword").value;
    const newPassword = document.getElementById("newPassword").value;
    const confirmPassword = document.getElementById("confirmPassword").value;

    const passwordObject = {
        currentPassword: currentPassword,
        newPassword: newPassword,
        confirmPassword: confirmPassword
    };

    const passwordJson = JSON.stringify(passwordObject);

    const response = await fetch("ResetPassword", {
        method: "PUT",
        headers: {
            "Content-Type": "application/json"
        },
        body: passwordJson
    });
    if (response.ok) {
        const json = await response.json();
        if (json.status) {
            popup.success({
                message: "Password Reset Successful!"
            });
            document.getElementById("currentPassword").value = "";
            document.getElementById("newPassword").value = "";
            document.getElementById("confirmPassword").value = "";
        } else {
            document.getElementById("message").innerHTML = json.message;
        }
    } else {
        document.getElementById("message").innerHTML = "Password Reset failed!";
    }
}

async function loadOrderHistory() {
    const response = await fetch("LoadOrderHistory");
    const container = document.getElementById("purchaseHistoryAccordion");
    const msgBox = document.getElementById("no-orders-message");

    container.innerHTML = "";
    msgBox.innerHTML = "";

    if (response.ok) {
        const json = await response.json();

        if (!json.ordersList || json.ordersList.length === 0) {
            msgBox.innerHTML = `<p>You haven't purchased any product yet.</p>`;
            return;
        }

        json.ordersList.forEach((order, index) => {
            const collapseId = `collapseOrder${index}`;

            let total = 0;

            // Generate order items list
            let orderItemsHtml = "";
            if (order.items && order.items.length > 0) {
                order.items.forEach(item => {
                    const productName = item.product.title;
                    const quantity = item.qty;
                    const price = item.product.price;

                    total += quantity * price;

                    orderItemsHtml += `
                        <li class="list-group-item d-flex justify-content-between align-items-center"
                            style="background: transparent; color: var(--text-color);">
                            ${productName} x ${quantity}
                            <span>Rs.${new Intl.NumberFormat(
                            "en-US",
                            {minimumFractionDigits: 2})
                            .format(quantity * price)}</span>
                        </li>`;
                });
            }

            const delivery = order.deliveryType.price;
            total += delivery;

            const accordionItem = `
                <div class="accordion-item mb-3">
                    <h2 class="accordion-header">
                        <button class="accordion-button ${index !== 0 ? 'collapsed' : ''}" 
                                type="button" 
                                data-bs-toggle="collapse"
                                data-bs-target="#${collapseId}" 
                                aria-expanded="${index === 0 ? 'true' : 'false'}"
                                aria-controls="${collapseId}">
                            <div class="d-flex justify-content-between w-100 pe-3">
                                <span><strong>Order ID:</strong> #${order.id}</span>
                                <span><strong>Date:</strong> ${formatDate(order.created_at)}</span>
                                <span><strong>Total:</strong> Rs.${new Intl.NumberFormat(
                    "en-US",
                    {minimumFractionDigits: 2})
                    .format(total)}</span>
                                <span><strong>Status:</strong> ${order.orderStatus.value}</span>
                            </div>
                        </button>
                    </h2>
                    <div id="${collapseId}" 
                         class="accordion-collapse collapse ${index === 0 ? 'show' : ''}" 
                         aria-labelledby="heading${index}"
                         data-bs-parent="#purchaseHistoryAccordion">
                        <div class="accordion-body">
                            <ul class="list-group list-group-flush">
                                ${orderItemsHtml}
                            </ul>
                        </div>
                    </div>
                </div>
            `;

            container.insertAdjacentHTML("beforeend", accordionItem);
        });
    } else {
        msgBox.innerHTML = `<p class="text-danger">Failed to load order history. Please try again later.</p>`;
    }
}

function formatDate(date) {
    const d = new Date(date);
    const year = d.getFullYear();
    const month = String(d.getMonth() + 1).padStart(2, '0');
    const day = String(d.getDate()).padStart(2, '0');
    return `${year}-${month}-${day}`;
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