<?php
	include_once("config.php");

	$json_manager = new JSONManager(file_get_contents('php://input'));
	
	$user_id = $json_manager->get("user_id");
		
	checkIsLogged($user_id);
	unSubscribeAllRooms($user_id);
	
	$db->delete("users", array("user_id" => $user_id));
	$db->delete("usersinrooms", array("userId" => $user_id));
	//$db->delete("playlists", "userId='$user_id'");
	//$db->delete("tracks", "userId='$user_id'");
	
	$json_manager->send();
?>