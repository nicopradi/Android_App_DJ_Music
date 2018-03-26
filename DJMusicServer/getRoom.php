<?php
	include_once("config.php");
	
	$json_manager = new JSONManager(file_get_contents('php://input'));
	
	$room_id = $json_manager->get("room_id");
	checkExistingRoom($room_id);
	
	$db->request("SELECT * FROM rooms WHERE id=:roomId", array('roomId' => $room_id));
	$room = $db->read();
	
	#Query users
	$users = getUsers($room_id);
	
	#Update Tracks
	checkTimeAndUpdate($room_id);
	
	#Query current track
	$room_current_track = getTrack($room_id);
	$room_current_track_available = ($room_current_track <> $NOTAVAILABLE);
	
	#Get current track position
	if($room_current_track_available){
		$currentTrackPosition = getCurrentPosition($room_id);
	} else {
		$currentTrackPosition = $NOTAVAILABLE;
	}
	
	#Query next track
	$room_next_track = getTrack($room_id, 1);
	$room_next_track_available = ($room_next_track <> $NOTAVAILABLE);
	
	$room_data = array(
			'room_id' => $room['id'],
			'room_name' => 	$room['name'], 
			'room_genre' => $room['genre'],
			'room_status' => $room['status'],
			'room_users' =>	$users,
			'room_current_track_available' => $room_current_track_available,
			'room_current_track' => $room_current_track,
			'room_current_track_pos' => $currentTrackPosition,
			'room_next_track_available' => $room_next_track_available,
			'room_next_track' => $room_next_track
	);
	
	$json_manager->put('room', $room_data);
	$json_manager->send();
?>