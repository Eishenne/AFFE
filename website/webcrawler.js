var pageNr = 0;
var searchTerm = "";

function firstSearchResult(term) {
    pageNr = 0;
    document.getElementById("demo").innerHTML = "";
    searchTerm = term;
    nextSearchResult();
}

function nextSearchResult() {
    xmlhttp = new XMLHttpRequest();
    xmlhttp.onreadystatechange = function () {
        if (this.readyState == 4 && this.status == 200) {
            myObj = JSON.parse(this.responseText);
            myObj.forEach((value, index, array) => {
                var article = document.createElement("article");
                document.getElementById("demo").appendChild(article);
                /* Add H3 Header */
                var h3 = document.createElement("h3");
                article.appendChild(h3);
                h3.innerHTML = value.title;
                /* Add P mit dem Link */
                var url = new URL(value.url);
                var favicon = url.protocol + "//" + url.hostname + "/favicon.ico";
                var img = document.createElement('img');
                img.src = favicon;
                img.style.height = "16px";
                img.style.width = "16px";
                console.log("URL: " + favicon);

                var p = document.createElement("div");
                p.appendChild(img);
                p.classList.add("margin-top-bottom");
                article.appendChild(p);
                var link = document.createElement("a");
                p.appendChild(link);
                link.innerHTML = "  " + value.url;
                link.setAttribute("href", value.url);
                /* Add P mit dem Description */
                var p = document.createElement("div");
                p.classList.add("margin-top-bottom");
                article.appendChild(p);
                var disc = document.createElement("div");
                p.appendChild(disc);
                disc.innerHTML = value.description;

            });
            document.getElementById("demo").style.visibility = "visible";
            document.getElementById("demo2").style.visibility = "visible";
        }
    };
    xmlhttp.open("GET", "searchResult.php?searchTerm=" + searchTerm + "&page=" + pageNr, true);
    xmlhttp.setRequestHeader("Content-type", "application/x-www-form-urlencoded");
    xmlhttp.send();
    ++pageNr;
}

// When the user scrolls down 20px from the top of the document, show the button
window.onscroll = function () { scrollFunction() };

function scrollFunction() {
    //Get the button
    var mybutton = document.getElementById("myBtn");
    if (document.body.scrollTop > 20 || document.documentElement.scrollTop > 20) {
        mybutton.style.display = "block";
    } else {
        mybutton.style.display = "none";
    }
}

// When the user clicks on the button, scroll to the top of the document
function topFunction() {
    document.body.scrollTop = 0;
    document.documentElement.scrollTop = 0;
}