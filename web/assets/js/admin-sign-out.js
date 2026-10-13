async function signOut() {
    
    const response = await fetch("AdminSignOut");
    
    if (response.ok) {
        const json = await response.json();
        if (json.status) {
            window.location = "admin-sign-in.html";
        } else {
            window.location.reload();
        }
    } else {
        console.log("Logout Failed!");
    }
}