<?php
	include_once("config.php");
	
	$json_manager = new JSONManager(file_get_contents('php://input'));

	$room_status = $json_manager->get("room_status");
	if($room_status < 0 || $room_status > 2){
		$json_manager->error("wrong status $room_status, expected 0 (public), 1 (private) or 2 (both)");
	}
	switch($room_status){
		case 0 : $status_request = "WHERE status = 0"; break;
		case 1 : $status_request = "WHERE status = 1"; break;
		case 2 : $status_request = "WHERE status in (0, 1)"; break;
		default : $status_request = "WHERE status in (0, 1)"; break;
	}
	
	$room_current_track_available = true;
	$json_rooms = array();
	
	$db->request("SELECT COUNT(*) as nb_rooms FROM rooms $status_request");
	$result = $db->read();
	if($result['nb_rooms'] > 0){
		$db->request("SELECT * FROM rooms $status_request");
		$rooms = $db->readAll();
		
		foreach($rooms as $room){
			#Query users
			$users = getUsers($room['id']);
			
			#Update Tracks
			checkTimeAndUpdate($room['id']);
			
			#Query current track
			$room_current_track = getTrack($room['id']);
			$room_current_track_available = ($room_current_track <> $NOTAVAILABLE);
			
			#Get current track position
			if($room_current_track_available){
				$currentTrackPosition = getCurrentPosition($room['id']);
			} else {
				$currentTrackPosition = $NOTAVAILABLE;
			}
			
			#Query next track
			$room_next_track = getTrack($room['id'], 1);
			$room_next_track_available = ($room_next_track <> $NOTAVAILABLE);
			
			#Merge all together
			array_push(
				$json_rooms, 
				array(
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
				)
			);
		}
		
		$json_manager->put('rooms', $json_rooms);
	} else {
		$json_manager->warning("No room available");
	}
	
	$json_manager->send();
?>