function loadSidebar() {
    $("#sidebar").load("admin-sidebar.html", function () {
        // After loading, highlight current page
        const currentPage = location.pathname.split('/').pop();
        document.querySelectorAll('.sidebar-nav .nav-link').forEach(link => {
            if (link.getAttribute('href') === currentPage) {
                link.classList.add('active');
            }
        });
    });
    loadAdmin();
}

async function loadAdmin() {
    const response = await fetch("AdminData");
    
    if (response.ok) {
        const json = await response.json();
        document.getElementById("adminEmail").innerHTML = ` ${json.email}`;
    }
}

$(document).ready(function() {

     // Sidebar Toggle
     $('#sidebar-toggle').on('click', function() {
        $('.sidebar, .main-content').toggleClass('toggled');
    });

    // Set Active Nav Link
    const currentPath = window.location.pathname.split("/").pop();
    if (currentPath === '') {
        $('.sidebar-nav a[href="index.html"]').addClass('active');
    } else {
        $('.sidebar-nav a[href="' + currentPath + '"]').addClass('active');
    }
    
    // Scroll to Top Button
    const scrollToTopBtn = document.getElementById('scrollToTop');
    
    if (scrollToTopBtn) {
        // Show/hide button based on scroll position
        window.addEventListener('scroll', function() {
            if (window.pageYOffset > 300) {
                scrollToTopBtn.classList.add('show');
            } else {
                scrollToTopBtn.classList.remove('show');
            }
        });
        
        // Smooth scroll to top when button is clicked
        scrollToTopBtn.addEventListener('click', function() {
            window.scrollTo({
                top: 0,
                behavior: 'smooth'
            });
        });
    }
});