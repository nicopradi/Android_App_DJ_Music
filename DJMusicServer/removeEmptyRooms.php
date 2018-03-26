<?php
	include_once("config.php");
	
	#We delete all rooms which are in deleteRooms table
	$db->delete("rooms", "id IN (SELECT roomId FROM deletedRooms)");
	$db->request("TRUNCATE TABLE deletedRooms");
	
	#We add to deleteRooms table all rooms which are empty
	$db->requestII("SELECT id FROM rooms WHERE listeningUsers=0");
	while($room = $db->readII()) {
		$db->insert("deletedRooms", array("roomId" => $room['id']));
	}
?>