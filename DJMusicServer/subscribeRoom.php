<?php
	include_once("config.php");

	$json_manager = new JSONManager(file_get_contents('php://input'));
	
	$user_id = $json_manager->get("user_id");
	$room_id = $json_manager->get("room_id");
		
	checkIsLogged($user_id);
	checkExistingRoom($room_id);
	unSubscribeAllRooms($user_id, $room_id);
	
	#Check if the user is the creator of the room
	$db->request("SELECT userId FROM rooms WHERE id=:roomId", array('roomId' => $room_id));
	$response = $db->read();
	$creatorUser = $response['userId'];
	if($user_id == $creatorUser){
		$privilege = 1;
	} else {
		$privilege = 0;
	}
	$json_manager->put("is_admin", $privilege);
	
	if(!isSubscribed($user_id, $room_id)){
		#If the room is private and the user is not the creator, the we ask for the password
		if(isRoomPrivate($room_id) ){ //&& $privilege < 1
			$client_password = $json_manager->get("password");
			$client_password = sha1($client_password);
			
			$db->request("SELECT password FROM rooms WHERE id=:roomId", array('roomId' => $room_id));
			$response = $db->read();
			$server_password = $response['password'];
			
			if($client_password <> $server_password){
				$json_manager->error("the password is incorrect");
			}
		}

		$db->insert(
			"usersinrooms", 
			array(
				"userId" => $user_id, 
				"roomId" => $room_id,
				"turnInRoom" => -1,
				"privilege" => $privilege
			)
		);
		
		$db->increase("rooms", "listeningUsers", array("id" => $room_id));
	}

	$json_manager->send();
	
	$db->writeLog("event", "user $user_id has subscribed to room $room_id");
?>