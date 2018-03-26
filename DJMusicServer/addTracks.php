<?php
	include_once("config.php");

	$json_manager = new JSONManager(file_get_contents('php://input'));
	
	$user_id = $json_manager->get("user_id");
	$room_id = $json_manager->get("room_id", true);
	$tracks = $json_manager->get("tracks");
	$json_manager->expectIn("tracks", "track_url");
	$json_manager->expectIn("tracks", "track_title");
	$json_manager->expectIn("tracks", "track_artist");
	$json_manager->expectIn("tracks", "track_album");
	$json_manager->expectIn("tracks", "track_genre");
	//$json_manager->expectIn("tracks", "track_length", true, false);
	
	checkIsLogged($user_id);
	checkExistingRoom($room_id);
	if(!isSubscribed($user_id, $room_id)){
		$json_manager->error("user $user_id is not subscribed to room $room_id");
	}
	
	foreach($tracks as $track){
		#Determine if this track already exists and is associated with the user
		//$db->request("SELECT COUNT(*) as nbTracks FROM tracks WHERE userId=:userId AND url=:url", array('userId' => $user_id, 'url' => $track->track_url));
		//$result = $db->read();
		//if($result['nbTracks'] > 0) {
		//	$json_manager->warning("track (" . $track->track_title . " - by " . $track->track_artist . ") already exists"); 
		//} else {
			$length = getDuration($track->track_url);
			if($length === false){
				$json_manager->error("can't access length of video ! Invalid youtube id url (" . $track->track_url . ")");
			}
			
			$db->insert(
				"tracks",
				array(
					"userId" => $user_id,
					"url" => $track->track_url,
					"title" => $track->track_title,
					"artist" => $track->track_artist,
					"album" => $track->track_album,
					"genre" => $track->track_genre,
					"length" => $length
				)
			);

			$trackId = $db->lastIdInsert();
			
			#Check if a playlist exists, if not we create one
			$db->request("SELECT COUNT(*) as nbPlaylists FROM playlists WHERE roomId=:roomId AND userId=:userId", array('roomId' => $room_id, 'userId' => $user_id));
			$result = $db->read();
			if($result['nbPlaylists'] == 0){
				$db->insert(
					"playlists",
					array(
						"userId" => $user_id,
						"roomId" => $room_id
					)
				);
				
				$db->update("usersinrooms", array("playlistId" => $db->lastIdInsert()), array('roomId' => $room_id, 'userId' => $user_id));
			} else {
				$db->requestII("SELECT id FROM playlists WHERE roomId=:roomId AND userId=:userId", array('roomId' => $room_id, 'userId' => $user_id));
				$result = $db->readII();
				
				$db->update("usersinrooms", array("playlistId" => $result['id']), array('roomId' => $room_id, 'userId' => $user_id));
			}
			
			$db->request("SELECT tracksId FROM playlists WHERE roomId=:roomId AND userId=:userId", array('roomId' => $room_id, 'userId' => $user_id));
			$result = $db->read();
			$tracksId = $result['tracksId'];
				
			$db->requestII("SELECT turnInRoom FROM usersinrooms WHERE roomId=:roomId AND userId=:userId", array('roomId' => $room_id, 'userId' => $user_id));
			$result = $db->readII();
			$currentTurn = $result['turnInRoom'];
			
			#If there is no tracksId yet, it means that this is the first time a user adds music to the room
			if(!empty($tracksId)){
				$tracksIdUp = $tracksId . ';' . $trackId;
				$db->writeLog("info","current turn : " . $currentTurn);
				if($currentTurn == -1){
					$db->writeLog("info","test");
					$db->request("SELECT (MAX(turnInRoom) + 1) as nextTurn FROM usersinrooms WHERE roomId=:roomId", array('roomId' => $room_id));
					$result = $db->read();
					$nextTurn = $result['nextTurn'];
					
					$db->update("usersinrooms", array("turnInRoom" => $nextTurn), array('roomId' => $room_id, 'userId' => $user_id));
					$db->increase("rooms", "totalUsers", array("id" => $room_id));
				}
				
			} else {
				$tracksIdUp = $trackId;
				
				$db->request("SELECT (MAX(turnInRoom) + 1) as nextTurn FROM usersinrooms WHERE roomId=:roomId", array('roomId' => $room_id));
				$result = $db->read();
				$nextTurn = $result['nextTurn'];
				
				$db->update("usersinrooms", array("turnInRoom" => $nextTurn), array('roomId' => $room_id, 'userId' => $user_id));
				$db->increase("rooms", "totalUsers", array("id" => $room_id));
			}
			
			$db->update("playlists", array("tracksId" => $tracksIdUp), array('roomId' => $room_id, 'userId' => $user_id));
		//}
	}
	
	$json_manager->send();
	$db->writeLog("event", "user $user_id added tracks to room $room_id");
	
	checkTimeAndUpdate($room_id);
?>