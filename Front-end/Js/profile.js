function editInfo() {
    document.getElementById("firstNameText").style.display = "none";
    document.getElementById("lastNameText").style.display = "none";
    document.getElementById("firstNameInput").style.display = "inline-block";
    document.getElementById("lastNameInput").style.display = "inline-block";

    document.getElementById("addressText").style.display = "none";
    document.getElementById("addressInput").style.display = "inline-block";

    document.getElementById("postalCodeText").style.display = "none";
    document.getElementById("postalCodeInput").style.display = "inline-block";

    document.getElementById("phoneNumber").style.display = "none";
    document.getElementById("phoneInput").style.display = "inline-block";

    document.querySelector('button[type="button"]').style.display = "none";
    document.querySelector('input[type="submit"]').style.display = "inline-block";
}

function cancelOrder() {
    if (confirm("Are you sure you want to cancel your order?")) {
        // Make an AJAX call to clear the cart
        var xhr = new XMLHttpRequest();
        xhr.open("POST", "OrderCancellationServlet", true);
        xhr.onreadystatechange = function () {
            if (xhr.readyState === 4 && xhr.status === 200) {

                // Redirect to Welcome.jsp after canceling the order
                window.location.href = 'Welcome.jsp';
            }
        };
        xhr.send();
    }
}