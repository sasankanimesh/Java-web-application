function changeContent(page, clickedTab) {
    var contentDiv = document.getElementById('content');
    var xhttp = new XMLHttpRequest();
    xhttp.onreadystatechange = function() {
        if (this.readyState === 4 && this.status === 200) {
            contentDiv.innerHTML = this.responseText;

            var activeTab = document.querySelector('nav a.active');
            if (activeTab) {
                activeTab.classList.remove('active');
            }

            clickedTab.classList.add('active');

            if (page.includes('Home.jsp')) {
                showAllProducts();
            }
        }
    };
    xhttp.open("GET", page, true);
    xhttp.send();
}
