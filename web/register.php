<?php

require_once("common.php");



if (!isset($_GET["username"]) || !isset($_GET["userid"]) || !isset($_GET["usertag"])) {
	die_with_message(false, "Missing one or more parameters");
}

$username = mysql_real_escape_string($_GET["username"]);
$usertag = mysql_real_escape_string($_GET["usertag"]);
$userid = mysql_real_escape_string($_GET["userid"]);


if (user_exists($usertag)) {
	// Check if it is the same device as last time
	if (is_users_device($usertag, $userid)) {
		// Update the name
		$query = "UPDATE user SET name='$username' WHERE tag='$usertag' AND userid='$userid'";
		mysql_query($query);
	} else {
		die_with_message(false, "Chipen er allerede registrert.");
	}
} else {
	// Create the new user
	$query = "INSERT INTO user(name, tag, userid) " .
			 "VALUES('$username', '$usertag', '$userid')";
	mysql_query($query) or die_with_message(false, "Databasefeil. Wut?");

}

$response = array(
	"status" => true, 
	"message" => "OK", 
	"username" => $username, 
	"usertag" => $usertag
);
echo json_encode($response);

?>
