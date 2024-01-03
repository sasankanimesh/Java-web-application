function displayModal(message) {
    document.getElementById('customModal').style.display = 'block';
    document.getElementById('overlay').style.display = 'block';
    document.getElementById('modalMessage').innerHTML = message;
}

function hideModal() {
    document.getElementById('customModal').style.display = 'none';
    document.getElementById('overlay').style.display = 'none';
}

function validateForm() {
    var password = document.getElementById("password").value;
    var confirmPassword = document.getElementById("confirmPassword").value;

    if (password !== confirmPassword) {
        displayModal("Passwords do not match!");
        return false;
    }
    return true;
}
