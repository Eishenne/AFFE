<?php
$servername = "localhost";
$username = "root";
$password = "Sami2008!";
$dbname = "webcrawler";

header("Content-Type: application/json; charset=UTF-8");
$page = intval(json_decode($_GET["page"], false));
$searchTerm = $_GET["searchTerm"];
$searchSingleTerms = preg_split("/[\s]+/", $searchTerm);

$questionMarks = "";
$bindDataType = "";
$bindParams = array();
foreach ($searchSingleTerms as &$term) {
    if (strlen($questionMarks) > 0) {
        $questionMarks = $questionMarks . ", ";
    }
    $questionMarks = $questionMarks . "?";
    $bindDataType = $bindDataType . "s";
    array_push($bindParams, $term);
}
$bindDataType = $bindDataType . "ii";
array_push($bindParams, $page);
array_push($bindParams, $page);

/*
$sql = "SELECT * from (SELECT title, url, description, row_number() over (order by target.id asc) rowNr  " .
    "FROM webcrawler.target " .
    "join webcrawler.searchresult on target.id = searchresult.FK_targetId " .
    "where searchresult.keyword = ? " .
    ") A where rowNr between (2*? +1) AND (2*(?+1))";
*/

$conn = new mysqli($servername, $username, $password, $dbname);
$sql = "SELECT * from (SELECT title, url, description, row_number() over (order by searchTemp.relevanz desc) rowNr 
FROM webcrawler.target 
join ( select searchresult.FK_targetId, SUM(relevanz) relevanz from
    webcrawler.searchresult 
where searchresult.keyword in (" .

    $questionMarks .

    ")
group by searchresult.FK_targetId) searchTemp
on target.id = searchTemp.FK_targetId
) A where rowNr between (20*? +1) AND (20*(?+1))
order by rowNr;";

$stmt = $conn->prepare($sql);
$stmt->bind_param($bindDataType, ...$bindParams);
$stmt->execute();
$result = $stmt->get_result(); // get the mysqli result


$outp = array();
while ($row = $result->fetch_assoc()) { //process result
    array_push($outp, array("title" => $row['title'], "url" => $row['url'], "description" => $row['description']));
}

echo json_encode($outp);
