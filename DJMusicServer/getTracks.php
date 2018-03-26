<?php
	//// DEPRECATED ////
	include_once("config.php");
	include_once("serverFunctions.php");
	
	//checkTimeAndUpdate();

	$json_input = json_decode(file_get_contents('php://input'));
	
	$result_data = array();
	
	if(!empty($json_input)){
		if(!isset($json_input->room_id) || empty($json_input->room_id)){
			echo "Error : room_id is empty";
			exit;
		}
		if(!isset($json_input->user_id) || empty($json_input->user_id)){
			echo "Error : user_id is empty";
			exit;
		}
		
		
		#Determine if the room exists
		$db->request("SELECT COUNT(*) as nbRooms FROM rooms WHERE id='" . $json_input->room_id . "'");
		$result = $db->read();
		if($result['nbRooms'] < 1) {
			echo "Error : room with id " . $json_input->room_id . " doesn't exist";
		} else {
			$db->request("SELECT tracksId FROM playlists WHERE id IN 
			(SELECT playlistId 
				FROM usersinrooms 
				WHERE roomId='" . $json_input->room_id . "' AND userId='" . $json_input->user_id . "')
			");
			$result = $db->read();
			$tracksId = explode(';', $result['tracksId']);
			
			foreach($tracksId as $trackId) {
				$db->request("SELECT * FROM tracks WHERE id='" . $trackId . "'");
				$result = $db->read();
				
				$trackInfos = array(
					"track_url" => $result['url'],
					"track_title" => $result['title'],
					"track_artist" => $result['artist'],
					"track_album" => $result['album'],
					"track_genre" => $result['genre'],
					"track_length" => $result['length']
				);
				
				array_push($result_data, $trackInfos);
			}
			
			echo json_encode($result_data);
		}
	}
?>