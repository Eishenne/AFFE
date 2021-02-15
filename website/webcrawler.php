<!DOCTYPE html>
<html lang="en">

<head>


    <meta charset="utf-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <link rel="stylesheet" href="probe.css">


</head>

<body>




    <div class="header">


        <img class="logo" src="Affe.png" alt="HTML5 Icon;" />



        <form class="form-inline" action="webcrawler.php" method="POST">
            <div class="input-group">
                <input type="text" name="search" placeholder="Search..">
                <div class="input-group-btn">

                </div>
            </div>
        </form>


    </div>




    <?php
    $servername = "localhost";
    $username = "root";
    $password = "Sami2008!";
    $dbname = "webcrawler";

    // Create connection
    $conn = new mysqli($servername, $username, $password, $dbname);
    // Check connection
    if ($conn->connect_error) {
        die("Connection failed: " . $conn->connect_error);
    }

    /*$sql = "SELECT * from newschema.target";
        $result = $conn->query($sql);

        if ($result->num_rows > 0) {
        // output data of each row
         while($row = $result->fetch_assoc()) {
        echo "<br> id: ". $row["id"]. " ". $row["url"]. " " . $row["lastupdate"] . " " . $row["nextvisit"] .  "<br>";
        }
        } else {
            echo "0 results";
        }

        $sql = "SELECT * from newschema.searchresult";
        $result = $conn->query($sql);

        if ($result->num_rows > 0) {
            // output data of each row
            while($row = $result->fetch_assoc()) {
                echo "<br> id: ". $row["id"]. " ". $row["keyword"]. " " . $row["relevanz"] . " " . $row["FK_targetId"] .  "<br>";
            }
        } else {
            echo "0 results";
        }*/

    $sql = "SELECT target.url, target.title, target.description FROM target JOIN searchresult ON target.id = searchresult.fk_targetid order by searchresult.relevanz DESC limit 50";



    $result = $conn->query($sql);

    if ($result->num_rows > 0) {
        // output data of each row
        while ($row = $result->fetch_assoc()) {
            echo "<article>";
            echo "<div class = flex-container> ";
            //<a href="https://adslkfj">https://adslfk</a>

            echo "<fieldset>";
            echo "<h3><p>";
            echo   $row["title"];
            echo "</p></h3>";
            echo "<p> <a href= \"";
            echo   $row["url"];
            echo "\">";
            echo   $row["url"];
            echo "</a></p>";
            echo "<p>";
            echo   $row["description"];
            echo "</p>";
            echo "</fieldset>";
            echo "</div>";


            echo "</article>";
        }
    } else {
        echo "0 results";
    }

    $conn->close();
    ?>
    <fieldset>
        <div id="demo2">
            <button type="button" onclick="loadDoc()">Weitere Ergebnisse</button>
        </div>
        <div id="demo">
        </div>
        <script>
            function loadDoc() {
                var xhttp = new XMLHttpRequest();
                xhttp.onreadystatechange = function() {
                    if (this.readyState == 4 && this.status == 200) {
                        document.getElementById("demo").innerHTML =
                            this.responseText;
                    }
                };
                xhttp.open("GET", "webcrawler1.php", true);
                xhttp.send();
            }
        </script>
    </fieldset>
</body>

</html>