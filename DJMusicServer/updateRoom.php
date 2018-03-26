<?php
	include_once("config.php");

	$json_manager = new JSONManager(file_get_contents('php://input'));
	
	$user_id = $json_manager->get("user_id");
	$room_id = $json_manager->get("room_id");
	$room_name = $json_manager->get("room_name", false, false);
	$room_genre = $json_manager->get("room_genre", false, false);
	
	checkIsLogged($user_id);
	checkPrivileges($user_id, $room_id, 1);
	checkExistingRoom($room_id);
	
	$updateRoom = array();
	
	if(!empty($room_name)){
		$updateRoom["name"] = $room_name;
	}
	if(!empty($room_genre)){
		$updateRoom["genre"] = $room_genre;
	}
	
	$db->update(
		"rooms", 
		$updateRoom,
		array("id" => $room_id)
	);
	
	$json_manager->send();
?>