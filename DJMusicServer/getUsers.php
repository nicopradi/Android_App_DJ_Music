<?php
	include_once("config.php");
	
	$json_manager = new JSONManager(file_get_contents('php://input'));
	
	$room_id = $json_manager->get("room_id");
	checkExistingRoom($room_id);
	
	$users = getUsers($room_id);
	$json_manager->put("users", $users);
	
	$json_manager->send();
?>