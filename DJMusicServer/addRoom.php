<?php
	include_once("config.php");

	$json_manager = new JSONManager(file_get_contents('php://input'));
	
	$user_id = $json_manager->get("user_id");
	$room_name = $json_manager->get("room_name");
	$room_genre = $json_manager->get("room_genre");
	$room_status = $json_manager->get("room_status", true);
	$password = "";
	
	checkIsLogged($user_id);
	if(existingRoomByName($room_name)) {
		$json_manager->error("room with name $room_name already exists");
	}
	if($room_status < 0 || $room_status > 1){
		$json_manager->error("wrong status $room_status, expected 0 (public) or 1 (private)");	
	}
	
	#If the room is private, we expect a password
	if($room_status == 1){
		$password = $json_manager->get("password");
		$password = sha1($password);
	}
		
	$db->insert(
		"rooms",
		array(
			"name" => $room_name, 
			"genre" => $room_genre,
			"userId" => $user_id,
			"status" => $room_status,
			"password" => $password
		)
	);
	
	$json_manager->put("room_id", $db->lastIdInsert());
	$json_manager->send();
	
	$db->writeLog("event", "room $room_name ($room_id) was added by user $user_id");
?>