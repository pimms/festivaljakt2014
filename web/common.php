<?php

define("EXPIRY_TIME", 30);


db_connect() or die_with_message(false, "Unable to connect to databse");


function db_connect() 
{
	if (!mysql_connect("localhost", "root"))
		return false;
	mysql_select_db("slottsfjell");
	return true;
}



function die_with_message($status, $msg) 
{
	$arr = array("status" => $status, "message" => $msg);
	$json = json_encode($arr);
	echo $json;
	die;
}


function user_exists($usertag) 
{
	$query = "SELECT COUNT(*) as count FROM user ".
			 "WHERE tag='$usertag'";
	$result = mysql_query($query);
	if (!$result)
		return false;

	while ($row = mysql_fetch_array($result, MYSQL_ASSOC)) {
		return $row["count"] != 0;
	}

	return false;
}


/* Checks of the tag $usertag has been previously associated with the
 * device ID $userid.
 */
function is_users_device($usertag, $userid) 
{
	$query = "SELECT * FROM user WHERE tag='$usertag' AND userid='$userid'";
	$result = mysql_query($query);
	if (!$result)
		return false;

	while ($row = mysql_fetch_array($result, MYSQL_ASSOC)) {
		if ($row["tag"] == $usertag && $row["userid"] == $userid) {
			return true;
		}
	}

	return false;
}

function get_user_name($tag) 
{
	$query = "SELECT name FROM user WHERE tag='$tag'";
	$result = mysql_querY($query);
	if (!$result)
		return NULL;

	$row = mysql_fetch_array($result, MYSQL_ASSOC);
	if (!$row)
		return NULL;

	return $row["name"];
}

function is_tag_ready($tag)
{
	$query = "SELECT time FROM tag WHERE tag='$tag' ORDER BY time DESC LIMIT 1";
	$result = mysql_query($query);
	if (!$result)
		return true;

	$row = mysql_fetch_array($result, MYSQL_ASSOC);
	if (!$row)
		return true;

	$time = strtotime($row["time"]);
	$now = time();
	
	if ($now - $time > EXPIRY_TIME) 
		return true;

	return EXPIRY_TIME - ($now-$time);
}

?>
