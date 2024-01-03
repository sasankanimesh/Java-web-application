function redirectToCheckout() {
    // Navigate to the Checkout.jsp page
    window.location.href = 'Checkout.jsp';
}

function removeItem(productId) {
    if (confirm("Are you sure you want to remove this item from your cart?")) {
        // AJAX call to remove the item
        var xhr = new XMLHttpRequest();
        xhr.open("POST", "RemoveItemServlet", true);
        xhr.setRequestHeader("Content-type", "application/x-www-form-urlencoded");

        // Send the productId as a parameter
        var params = "productId=" + productId;

        xhr.onreadystatechange = function () {
            if (xhr.readyState === 4) {
                if (xhr.status === 200) {
                    location.reload();
                } else {
                    console.error("Error removing item: " + xhr.status);
                }
            }
        };
        xhr.send(params);
    }
}

function clearCart() {
    if (confirm("Are you sure you want to clear your cart?")) {
        // AJAX call to clear the cart
        var xhr = new XMLHttpRequest();
        xhr.open("POST", "ClearCartServlet", true);
        xhr.onreadystatechange = function () {
            if (xhr.readyState === 4 && xhr.status === 200) {
                location.reload();
            }
        };
        xhr.send();
    }
}
