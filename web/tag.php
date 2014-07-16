<?php

require_once("common.php");


if (!isset($_GET["user"]) || !isset($_GET["tag"])) {
	die_with_message(false, "One or more parameters are missing from the request.");
}


$user = mysql_real_escape_string($_GET["user"]);
$tag = mysql_real_escape_string($_GET["tag"]);


$ready = is_tag_ready($tag);
if ($ready === true) {
	// Insert the tag to the database
	$query = "INSERT INTO tag(user, tag) VALUES('$user', '$tag')";
	mysql_query($query) or die_with_message(false, "Databasefeil. Prøv igjen senere.");
}

// Get the name of the owner if applicable
$ownerName = get_user_name($tag);
if (!$ownerName) {
	$ownerName = "uregistrert";
}

// Count the points of the user
$query = "SELECT COUNT(*) as count FROM tag WHERE user='$user'";
$result = mysql_query($query);
if (!$result)
	die_with_message(false, "Databasefeil. Prøv igjen senere.");

$row = mysql_fetch_array($result, MYSQL_ASSOC);
if (!$row)
	die_with_message(false, "Databasefeil. Prøv igjen senere.");
$score = $row["count"];

$json = array("status" => true);
$json ["score"] = $score;

if ($ready === true) {
	$json["tag_registered"] = true;
} else {
	$json["tag_registered"] = false;
	$json["cooldown"] = $ready;
}

$json["owner_name"] = $ownerName;

echo json_encode($json);

?>
