<?php
	#DEPRECATED
	include_once("config.php");

	$json_manager = new JSONManager(file_get_contents('php://input'));
	
	$json_manager->expect("room_id");
	$room_id = $json_manager->get("room_id");
		
	#Determine if the room exists
	$db->request("SELECT COUNT(*) as nbRooms FROM djmusic_rooms WHERE id=$room_id");
	$result = $db->read();
	if($result['nbRooms'] < 1) {
		$json_manager->error("Room with id $room_id doesn't exist");
	} else {
		checkTimeAndUpdate($room_id);
		
		$db->request("
			SELECT userId, playlistId 
			FROM djmusic_usersinrooms 
			WHERE roomId=$room_id AND 
			turnInRoom IN (SELECT currentTurn FROM djmusic_rooms WHERE id=$room_id)");
		$result = $db->read();
		$userId = $result['userId'];
		$playlistId = $result['playlistId'];
		
		$db->request("SELECT * FROM djmusic_playlists WHERE id='" . $playlistId . "'");
		$result = $db->read();
		$tracksId = explode(';', $result['tracksId']);
		$currentTrack = $tracksId[0];
		
		$db->request("SELECT * FROM djmusic_tracks WHERE id='" . $currentTrack . "'");
		$result = $db->read();
		
		$trackInfos = array(
			"track_url" => $result['url'],
			"track_title" => $result['title'],
			"track_artist" => $result['artist'],
			"track_album" => $result['album'],
			"track_genre" => $result['genre'],
			"track_length" => $result['length']
		);
		
		#Get track position
		$db->request("SELECT startCurrentTrack FROM djmusic_rooms WHERE id=$room_id");
		$room = $db->read();
		$now = (new DateTime("now"))->getTimestamp();
		$start = (new DateTime($room['startCurrentTrack']))->getTimestamp();
		$currentTrackPosition = $now - $start;
		
		$json_manager->put("track", $trackInfos);
		$json_manager->put("track_pos", $currentTrackPosition);
	}
	
	$json_manager->send();
?>