<?php
	include_once("config.php");

	$json_manager = new JSONManager(file_get_contents('php://input'));
	
	$room_id = $json_manager->get("room_id");
	checkExistingRoom($room_id);
	
	$syncMode = $json_manager->get("sync_mode", false);
	if(empty($syncMode)){
		$syncMode = true;
	} else {
		$syncMode = ($syncMode === "false")? false: true;
	}
	
	if($syncMode){
		while(time() % 5 <> 0){
			//wait
		}
	}
	
	checkTimeAndUpdate($room_id);
	
	$currentTrack = getTrack($room_id);
	$nextTrack = getTrack($room_id, 1);
	$currentTrackPosition = getCurrentPosition($room_id);
	
	$json_manager->put("current_track_available", $currentTrack <> $NOTAVAILABLE);
	$json_manager->put("current_track", $currentTrack);
	$json_manager->put("current_track_pos", $currentTrackPosition);
	$json_manager->put("next_track_available", $nextTrack <> $NOTAVAILABLE);
	$json_manager->put("next_track", $nextTrack);
	
	$json_manager->send();
?>