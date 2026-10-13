function loadHeaderFooter() {
    fetch("header.html")
            .then(res => res.text())
            .then(data => {
                document.getElementById("header").innerHTML = data;
            });

    fetch("footer.html")
            .then(res => res.text())
            .then(data => {
                document.getElementById("footer").innerHTML = data;
            });
}

async function searchProducts() {
    const value = document.getElementById("searchInput").value;
    const response = await fetch("SearchProduct?value=" + encodeURIComponent(value));
    const popup = new Notification();
    if (response.ok) {
        const json = await response.json();
        if (json.status) {
            const search_container = document.getElementById("search-container");
            search_container.innerHTML = "";

            json.productList.forEach(item => {
                let product_card = `<div class="col-lg-6">
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
                search_container.innerHTML += product_card;
            });
            const modal = new bootstrap.Modal(document.getElementById('searchModal'));
            modal.show();
        } else {
            popup.error({
                message: json.message
            });
        }
    } else {
        popup.error({
            message: "Something went wrong. Please try again later."
        });
    }
}

const minSlider = document.getElementById("rangeMin");
const maxSlider = document.getElementById("rangeMax");
const priceLabel = document.getElementById("priceRangeLabel");

let minGap = 100;

if (minSlider && maxSlider && priceLabel) {
    minSlider.addEventListener("input", updateSlider);
    maxSlider.addEventListener("input", updateSlider);
}

function updateSlider() {
    let minVal = parseInt(minSlider.value);
    let maxVal = parseInt(maxSlider.value);

    // Ensure minimum gap between the sliders
    if (maxVal - minVal < minGap) {
        if (this.id === "rangeMin") {
            minSlider.value = maxVal - minGap;
        } else {
            maxSlider.value = parseInt(minSlider.value) + minGap;
        }
    }

    priceLabel.textContent = `Rs.${minSlider.value} - Rs.${maxSlider.value}`;
}

// js/script.js
$(document).ready(function () {

    // Active Navbar Link
    const currentPath = window.location.pathname.split("/").pop();
    if (currentPath === '') {
        $('.navbar-nav a[href="index.html"]').addClass('active');
    } else {
        $('.navbar-nav a[href="' + currentPath + '"]').addClass('active');
    }

    // Scroll to Top Button
    const scrollToTopBtn = document.getElementById('scrollToTop');

    if (scrollToTopBtn) {
        // Show/hide button based on scroll position
        window.addEventListener('scroll', function () {
            if (window.pageYOffset > 300) {
                scrollToTopBtn.classList.add('show');
            } else {
                scrollToTopBtn.classList.remove('show');
            }
        });

        // Smooth scroll to top when button is clicked
        scrollToTopBtn.addEventListener('click', function () {
            window.scrollTo({
                top: 0,
                behavior: 'smooth'
            });
        });
    }

});