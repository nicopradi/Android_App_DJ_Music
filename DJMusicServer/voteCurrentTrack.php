<?php
	include_once("config.php");
	$limitDownVotes = -2;

	$json_manager = new JSONManager(file_get_contents('php://input'));
	
	$user_id = $json_manager->get("user_id");
	$room_id = $json_manager->get("room_id");
	$note = intval($json_manager->get("note", true));
	
	checkIsLogged($user_id);
	if(!isSubscribed($user_id, $room_id)){
		$json_manager->error("user $user_id is not subscribed to room $room_id");
	}
	checkExistingRoom($room_id);
	if(!in_array($note, array(-2,-1, 1, 2))) {
		$json_manager->error("note must be -2 or -1 (downvote), or 1 or 2 (upvote), was $note");
	}
	
	$trackId = getTrackId($room_id);
	if($trackId <> $NOTAVAILABLE){
	
		$db->request("SELECT COUNT(*) as nbVotes FROM votes WHERE track_id=:trackId", array('trackId' => $trackId));
		$result = $db->read();
		if($result['nbVotes'] > 0){
			$db->add("votes", "currentVotes", $note, array("track_id" => $trackId));
			$db->add("votes", "totalVotes", $note, array("track_id" => $trackId));
		} else {
			$db->insert(
				"votes", 
				array(
					"track_id" => $trackId,
					"roomId" => $room_id,
					"currentVotes" => $note,
					"totalVotes" =>  $note
				)
			);
			
			$db->insert(
				"historyVotes",
				array(
					"userId" => $user_id,
					"roomId" => $room_id,
					"trackId" => $trackId,
					"note" => $note
				)
			);
		}
		
		#skipTrack if too many downvotes
		$db->request("SELECT currentVotes FROM votes WHERE track_id=:trackId", array('trackId' => $trackId));
		$result = $db->read();
		if($result['currentVotes'] <= $limitDownVotes){
			$json_manager->put("info", "skip track");
			skipTrack($room_id);
		}
		$json_manager->send();
	} else {
		$json_manager->error("track was null, cannot continue");
	}
?>