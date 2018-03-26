<?php
	include_once("config.php");
	
	//Check if we must pass to the next song
	function checkTimeAndUpdate($room_id){
		global $NOTAVAILABLE;
		global $db;
		if(isRoomActive($room_id)){
			$now = new DateTime("now");
			$endTime = new DateTime(getTrackEndTime($room_id));
			
			#If the song has finished
			if($now > $endTime) {
				$trackId = getTrackId($room_id);
				if($trackId <> $NOTAVAILABLE){
					resetVotes($trackId);
				}
				
				incrementTurn($room_id);
				passToNextTrack($room_id, true, false);
				
				#If the currentPosition is too big, then we skip again
				$pos = getCurrentPosition($room_id);
				$db->request("SELECT length FROM tracks WHERE id=:id", array('id' => getTrackId($room_id)));
				$track = $db->read();
				$length = $track['length'];
				
				if($pos > $length){
					incrementTurn($room_id);
					passToNextTrack($room_id, true, true);
				}
			}
		}
	}
	
	function skipTrack($room_id){
		global $NOTAVAILABLE;
		$trackId = getTrackId($room_id);
		if($trackId <> $NOTAVAILABLE){
			resetVotes($trackId);
		}
		
		incrementTurn($room_id);
		passToNextTrack($room_id, true, true);
	}
	
	function getTrackId($room_id, $shift = 0){
		global $db;
		global $NOTAVAILABLE;
		$db->request("
			SELECT playlistId 
			FROM usersinrooms 
			WHERE roomId=:roomId AND 
			turnInRoom IN (SELECT MOD(currentTurn + :shift, totalUsers) FROM rooms WHERE id=:roomId)", array('roomId' => $room_id, 'shift' => $shift));
		$result = $db->read();
		$playlistId = $result['playlistId'];
		
		if($playlistId == 0){
			return $NOTAVAILABLE;
		} else {
			$db->request("SELECT * FROM playlists WHERE id=:playlistId", array('playlistId' => $playlistId));
			$result = $db->read();
			$tracksId = explode(';', $result['tracksId']);
			
			#If shift > totalUsers, then we select a higher track
			$db->request("SELECT totalUsers FROM rooms WHERE id=:roomId", array('roomId' => $room_id));
			$result = $db->read();
			$totalUsers = $result['totalUsers'];
			if($totalUsers <> 0)
				$indexTrack = $shift / $totalUsers;
			else
				$indexTrack = 0;
			
			$track = $tracksId[$indexTrack % count($tracksId)];
			
			if(empty($track)){
				return $NOTAVAILABLE;
			} else {
				return $track;
			}
		}
	}
	
	function getTrack($room_id, $shift = 0){
		global $db;
		global $NOTAVAILABLE;
		$trackId = getTrackId($room_id, $shift);
			
		if($trackId == $NOTAVAILABLE){
			return $NOTAVAILABLE;
		} else {
			$db->request("SELECT * FROM tracks WHERE id=:trackId", array('trackId' => $trackId));
			$result = $db->read();
			
			$trackInfos = array(
				"track_url" => $result['url'],
				"track_title" => $result['title'],
				"track_artist" => $result['artist'],
				"track_album" => $result['album'],
				"track_genre" => $result['genre'],
				"track_length" => $result['length']
			);
			
			return $trackInfos;
		}
	}
	
	//ok
	function getCurrentPosition($room_id){
		global $db;
		$db->request("SELECT startCurrentTrack FROM rooms WHERE id=:roomId", array('roomId' => $room_id));
		$room = $db->read();
		$now = (new DateTime("now"))->getTimestamp();
		$start = (new DateTime($room['startCurrentTrack']))->getTimestamp();
		return $now - $start;
	}
	
	//ok
	function isRoomPrivate($room_id) {
		global $db;
		$db->request("SELECT status FROM rooms WHERE id=:roomId", array('roomId' => $room_id));
		$response = $db->read();
		$status = $response['status'];
		return $status == 1;
	}
	
	//ok
	function isLogged($user_id){
		global $db;
		$db->request("SELECT COUNT(*) as users FROM users WHERE user_id=:userId", array('userId' => $user_id));
		$result = $db->read();
		return $result['users'] >= 1;
	}
	
	//ok
	function checkIsLogged($user_id){
		if(!isLogged($user_id)){
			sendErrorConfirmation("user $user_id is not logged");
		}
	}
	
	//ok
	function checkPrivileges($user_id, $room_id, $privilege){
		global $db;
		$db->request("SELECT userId FROM rooms WHERE id=:roomId", array('roomId' => $room_id));
		$response = $db->read();
		$administrator = ($user_id == $response['userId']);
		
		$db->request("SELECT privilege FROM usersinrooms WHERE userId=:userId AND roomId=:roomId", array('userId' => $user_id, 'roomId' => $room_id));
		$response = $db->read();
		$user_privileges = $response['privilege'];
		if(empty($user_privileges)){
			$user_privileges = 0;
		}
		
		if(!$administrator && $response['privilege'] < $privilege){
			sendErrorConfirmation("user $user_id has not the required privileges (user : $user_privileges, needed : $privilege)");
		}
	}
	
	//ok
	function isSubscribed($user_id, $room_id){
		global $db;
		$db->request("SELECT COUNT(*) as nb_users FROM usersinrooms WHERE userId=:userId AND roomId=:roomId", array('userId' => $user_id, 'roomId' => $room_id));
		$result = $db->read();
		return $result['nb_users'] > 0;
	}
	
	//ok
	function existingRoom($room_id){
		global $db;
		$db->request("SELECT COUNT(*) as nb_rooms FROM rooms WHERE id=:roomId", array('roomId' => $room_id));
		$result = $db->read();
		return $result['nb_rooms'] == 1;
	}
	
	//ok
	function existingRoomByName($room_name){
		global $db;
		$db->request("SELECT COUNT(*) as nb_rooms FROM rooms WHERE name=:roomName", array('roomName' => $room_name));
		$result = $db->read();
		return $result['nb_rooms'] == 1;
	}
	
	//ok
	function checkExistingRoom($room_id) {
		if(!existingRoom($room_id)){
			sendErrorConfirmation("room $room_id does not exist");
		}
	}
	
	//ok
	function sendErrorConfirmation($msg){
		echo json_encode(array("confirmation" => "ERROR", "error_msg" => $msg));
		exit;
	}
	
	//ok
	function putWarningConfirmation(&$json_response, $msg){
		$json_response["confirmation"] = "WARNING"; 
		$json_response["warning_msg"] = $msg;
	}
	
	//ok
	function getUsers($room_id){
		global $db;
		$db->request("
			SELECT usersinrooms.*, users.*
			FROM usersinrooms usersinrooms
			INNER JOIN users users ON usersinrooms.userId = users.user_id
			WHERE usersinrooms.roomId=:roomId", array('roomId' => $room_id));
		$usersInRooms = $db->readAll();
		$users = array();
		foreach($usersInRooms as $user){
			array_push(
				$users, 
				array(
					"user_id" => $user['user_id'],
					"user_name" => $user['user_name']
				)
			);
		}
		return $users;
	}
	
	//ok
	function reassignTurns($deletedTurn, $room_id){
		global $db;
		$db->request("SELECT totalUsers as maxTurn FROM rooms WHERE id=:roomId", array('roomId' => $room_id));
		$response = $db->read();
		$maxTurn = $response['maxTurn'];
		
		//if deletedTurn wasn't the last one
		if($deletedTurn != $maxTurn){
			$db->request("SELECT * FROM usersinrooms WHERE roomId=:roomId AND turnInRoom > :deletedTurn", array('roomId' => $room_id, 'deletedTurn' => $deletedTurn));
			$users = $db->readAll();
			
			foreach($users as $user){
				$id = $user['id'];
				$db->decrease("usersinrooms", "turnInRoom", array('id' => $id));
			}
		}
	}
	
	//////////////////////////////////////////
	
	function unSubscribeAllRooms($user_id, $exception_room = -1){
		global $db;
		//$db->showDebug(true);
		checkIsLogged($user_id);
	
		$db->requestII("SELECT id FROM rooms", array());
		while($room = $db->readII()){
			$room_id = $room['id'];
			
			if(isSubscribed($user_id, $room_id) AND $room_id != $exception_room){
				$db->request("SELECT turnInRoom FROM usersinrooms WHERE userId=:userId AND roomId=:roomId", array('userId' => $user_id, 'roomId' => $room_id));
				$response = $db->read();
				$currentTurn = $response['turnInRoom'];
				
				$db->delete(
					"usersinrooms", 
					array("userId" => $user_id, 'roomId' => $room_id)
				);
				
				$db->decrease("rooms", "listeningUsers", array('id' => $room_id));
				if($currentTurn >= 0){
					$db->decrease("rooms", "totalUsers", array('id' => $room_id));
					reassignTurns($currentTurn, $room_id);
				}
			} 
		}
	}
	
	//ok
	function isRoomActive($room_id){
		global $db;
		$db->request("SELECT totalUsers FROM rooms WHERE id=:roomId", array('roomId' => $room_id));
		$result = $db->read();
		return $result['totalUsers'] <> 0;
	}
	
	//ok
	function getTrackEndTime($room_id) {
		global $db;
		$db->request("SELECT startNextTrack FROM rooms WHERE id=:roomId", array('roomId' => $room_id));
		$result = $db->read();
		return $result['startNextTrack'];
	}
	
	//ok
	function incrementTurn($room_id) {
		global $db;
		$db->request("SELECT currentTurn, totalUsers FROM rooms WHERE id=:roomId", array('roomId' => $room_id));
		$result = $db->read();
		if($result['totalUsers'] > 0) {
			$nextTurn = ($result['currentTurn'] + 1) % $result['totalUsers'];
		} else {
			$nextTurn = 0;
		}
		
		$db->update("rooms", array("currentTurn" => $nextTurn), array("id" => $room_id));
	}
	
	//ok
	function passToNextTrack($room_id, $repushTrack = true, $reset = false) {
		$user = getCurrentUser($room_id);
		
		$tracks = getTracks($room_id, $user);
		$last_track = array_shift($tracks);
		if($repushTrack){
			array_push($tracks, $last_track);
		}
		
		updateTracks($room_id, $user, $tracks, $reset);
	}
	
	//ok
	function resetVotes($track_id){
		global $db;
		if(is_numeric($track_id)){
			$db->update("votes", array("currentVotes" => 0), array("track_id" => $track_id));
		}
	}
	
	//ok
	function getCurrentUser($room_id){
		global $db;
		$db->request("
			SELECT userId 
			FROM usersinrooms 
			WHERE roomId=:roomId AND 
			turnInRoom IN (SELECT currentTurn FROM rooms WHERE id=:roomId)", array('roomId' => $room_id)
		);
		$result = $db->read();
		return $result['userId'];
	}
	
	//ok
	function getTracks($room_id, $user_id) {
		global $db;
		$db->request("SELECT tracksId FROM playlists WHERE userId=:userId AND roomId=:roomId", array('userId' => $user_id, 'roomId' => $room_id));
		$result = $db->read();
		$tracks = explode(';', $result['tracksId']);
		return $tracks;
	}
	
	//ok
	function updateTracks($room_id, $user_id, $tracks, $reset = false) {
		global $db;
		
		if(!empty($tracks)){
			$tracksFormat = implode(';', $tracks);
			
			if(!empty($tracksFormat)){
				$db->update("playlists", array("tracksId" => $tracksFormat), array("roomID" => $room_id,  "userId" => $user_id));
				
				#Update start and end positions
				$db->request("SELECT length FROM tracks WHERE id=:trackId", array('trackId' => $tracks[0]));
				$track = $db->read();
				$length = $track['length'];
				if($length > 9999){
					$length = 9999;
				}
				
				if(!empty($length)){
					$db->request("SELECT startNextTrack FROM rooms WHERE id=:roomId", array('roomId' => $room_id));
					$result = $db->read();
					
					$diffPos = (new DateTime("now"))->getTimestamp() - (new DateTime($result['startNextTrack']))->getTimestamp();
					if($diffPos > 60 * 30 || $reset){
						$start = new DateTime("now");
						$end = new DateTime("now");
					} else {
						$start = new DateTime($result['startNextTrack']);
						$end = new DateTime($result['startNextTrack']);
					}
					try{
						$end->add(new DateInterval('PT' . $length . 'S'));
					} catch(Exception $e){
						die('error : ' . $e->getMessage() . ". Details : " . $length);
					}
					
					$db->update(
						"rooms", 
						array(
							"startCurrentTrack" => $start->format("Y-m-d H:i:s"), 
							"startNextTrack" => $end->format("Y-m-d H:i:s"),
							"currentTrackId" => $tracks[0]
						), 
						array("id" => $room_id)
					);
				}
			}
		}
	}
	
	#Copyrights : https://stackoverflow.com/questions/9167442/get-duration-from-a-youtube-url/9167754#9167754?newreg=0cc7e59d43ff4ba38f0a85691ef94aa5
	function getDuration($video_id){
		$url = "http://gdata.youtube.com/feeds/api/videos/" . $video_id . "?v=2&alt=jsonc";
        $data = @file_get_contents($url);
        if ($data === false) { 
			return false;
		}

        $obj=json_decode($data);
        return $obj->data->duration;
    }
?>