<?php
	include_once("config.php");

	$json_manager = new JSONManager(file_get_contents('php://input'));
	
	$user_id = $json_manager->get("user_id");
	$room_id = $json_manager->get("room_id");
	
	checkIsLogged($user_id);
	checkPrivileges($user_id, $room_id, 1);
	checkExistingRoom($room_id);
	
	skipTrack($room_id);
	
	$json_manager->send();
	
	$db->writeLog("event", "user $user_id wants to skip on room $room_id");
?>