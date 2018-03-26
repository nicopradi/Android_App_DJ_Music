<?php
	include_once("config.php");
	
	$host = "localhost/djmusicserver";
	$user_name = "toto";
	$user_id = "487956542";
	
	
	#Tests 1
	/*
	*	-login
	*	-getRooms
	*	-subscribeRoom
	*	-getCurrentTrack (initial, every 10 seconds until totalLength - 10 sec[we don't need to ask at the 10
	*	last seconds because we call getNextTrack at that time])
	*	-getNextTrack (when a song finishes)
	*	-unsubscribeRoom
	*	-logout
	*/
	echo "START TESTS SUITE 1<br><br>";
	//login
	prettyPrint(
		"login - user_name:" . $user_name . ", user_id:" . $user_id, 
		sendJSONToURL(
			array(
				"user_name" => $user_name,
				"user_id"	=> $user_id
			),
			$host . "/login.php"
		)
	);
	printTable($db, "djmusic_users");
	
	//getRooms
	prettyPrint(
		"getRooms", 
		sendJSONToURL(
			array(
			),
			$host . "/getRooms.php"
		)
	);
	
	//subscribeRoom
	$room_id = 2;
	prettyPrint(
		"subscribeRoom - room_id:" . $room_id . ", user_id:" . $user_id, 
		sendJSONToURL(
			array(
				"room_id" => $room_id,
				"user_id"	=> $user_id
			),
			$host . "/subscribeRoom.php"
		)
	);
	printTable($db, "djmusic_usersinrooms");
	
	//getCurrentTrack
	$room_id = 2;
	prettyPrint(
		"getCurrentTrack - room_id:" . $room_id, 
		sendJSONToURL(
			array(
				"room_id" => $room_id
			),
			$host . "/getCurrentTrack.php"
		)
	);
	
	//getNextTrack
	$room_id = 2;
	prettyPrint(
		"getNextTrack - room_id:" . $room_id, 
		sendJSONToURL(
			array(
				"room_id" => $room_id
			),
			$host . "/getNextTrack.php"
		)
	);
	
	//unsubscribeRoom
	$room_id = 2;
	prettyPrint(
		"unsubscribeRoom - room_id:" . $room_id, 
		sendJSONToURL(
			array(
				"room_id" => $room_id,
				"user_id" => $user_id
			),
			$host . "/unSubscribeRoom.php"
		)
	);
	printTable($db, "djmusic_usersinrooms");
	
	//logout
	prettyPrint(
		"logout - user_id:" . $user_id, 
		sendJSONToURL(
			array(
				"user_id" => $user_id
			),
			$host . "/logout.php"
		)
	);
	printTable($db, "djmusic_users");
	
	echo "//**********END TESTS SUITE 1**********//<br><br>";
	
	#Tests 2 
	/**
	*	#user1
	*	-login
	*	-getRooms
	*	-addRoom
	*	-addTracks
	*	-subscribeRoom
	*	-getCurrentTrack
	*
	*	#user2
	*	-login
	*	-getRooms
	*	-subscribeRoom
	*	-getCurrentTrack
	*	-addTracks
	*	-getCurrenTrack
	*	-unsubscribeRoom
	*	-logout
	*
	*	#user1
	*	-logout
	**/
	$user1_name = "tata";
	$user1_id = "111";
	$user2_name = "titi";
	$user2_id = "222";
	$room_name = "room_test";
	$room_genre = "rock";
	
	$track1 = array(
		"track_url" => "https://www.youtube.com/watch?v=P6qFCqOy3HU",
		"track_title" => "Radioactive",
		"track_artist" => "Macy Kate",
		"track_album" => "Youtube",
		"track_genre" => "Pop",
		"track_length" => "224"
	);
	
	$track2 = array(
		"track_url" => "https://www.youtube.com/watch?v=PVzljDmoPVs",
		"track_title" => "She Wolf",
		"track_artist" => "David Guetta",
		"track_album" => "Youtube",
		"track_genre" => "Pop",
		"track_length" => "237"	
	);
	
	echo "START TESTS SUITE 2<br><br>";
	echo "user1<br>";
	
	//user1 : login
	prettyPrint(
		"user1 login - user_name:" . $user1_name . ", user_id:" . $user1_id, 
		sendJSONToURL(
			array(
				"user_name" => $user1_name,
				"user_id"	=> $user1_id
			),
			$host . "/login.php"
		)
	);
	printTable($db, "djmusic_users");
	
	//user1 : getRooms
	prettyPrint(
		"user1 getRooms", 
		sendJSONToURL(
			array(
			),
			$host . "/getRooms.php"
		)
	);
	
	//user1 : addRooms
	$room_id = prettyPrint(
		"user1 addRooms", 
		sendJSONToURL(
			array(
				"room_name" => $room_name,
				"room_genre" => $room_genre
			),
			$host . "/addRoom.php"
		),
		"room_id"
	);
	printTable($db, "djmusic_rooms");
	
	//user1 : addTracks
	$tracks = array();
	array_push($tracks, $track1);
	prettyPrint(
		"user1 : addTracks - room_id:" . $room_id . ", user_id:" . $user1_id, 
		sendJSONToURL(
			array(
				"room_id" => $room_id,
				"user_id"	=> $user1_id,
				"tracks" => $tracks
			),
			$host . "/addTracks.php"
		)
	);
	printTable($db, "djmusic_tracks");
	
	//user1 : subscribeRoom
	prettyPrint(
		"user1 : subscribeRoom - room_id:" . $room_id . ", user_id:" . $user1_id, 
		sendJSONToURL(
			array(
				"room_id" => $room_id,
				"user_id"	=> $user1_id
			),
			$host . "/subscribeRoom.php"
		)
	);
	printTable($db, "djmusic_usersinrooms");
	
	//user1 : getCurrentTrack
	prettyPrint(
		"user1 : getCurrentTrack - room_id:" . $room_id, 
		sendJSONToURL(
			array(
				"room_id" => $room_id
			),
			$host . "/getCurrentTrack.php"
		)
	);
	
	echo "user2<br>";
	
	//user2 : login
	prettyPrint(
		"user2 login - user_name:" . $user2_name . ", user_id:" . $user2_id, 
		sendJSONToURL(
			array(
				"user_name" => $user2_name,
				"user_id"	=> $user2_id
			),
			$host . "/login.php"
		)
	);
	printTable($db, "djmusic_users");
	
	//user2 : getRooms
	prettyPrint(
		"user2 : getRooms", 
		sendJSONToURL(
			array(
			),
			$host . "/getRooms.php"
		)
	);
	
	//user2 : subscribeRoom
	prettyPrint(
		"user2 : subscribeRoom - room_id:" . $room_id . ", user_id:" . $user2_id, 
		sendJSONToURL(
			array(
				"room_id" => $room_id,
				"user_id"	=> $user2_id
			),
			$host . "/subscribeRoom.php"
		)
	);
	printTable($db, "djmusic_usersinrooms");
	
	//user2 : getCurrentTrack
	prettyPrint(
		"user2 : getCurrentTrack - room_id:" . $room_id, 
		sendJSONToURL(
			array(
				"room_id" => $room_id
			),
			$host . "/getCurrentTrack.php"
		)
	);
	
	//user2 : addTracks
	$tracks = array();
	array_push($tracks, $track2);
	prettyPrint(
		"user2 : addTracks - room_id:" . $room_id . ", user_id:" . $user2_id, 
		sendJSONToURL(
			array(
				"room_id" => $room_id,
				"user_id"	=> $user2_id,
				"tracks" => $tracks
			),
			$host . "/addTracks.php"
		)
	);
	printTable($db, "djmusic_tracks");
	
	//user2 : unsubscribeRoom
	prettyPrint(
		"user2 : unsubscribeRoom - room_id:" . $room_id, 
		sendJSONToURL(
			array(
				"room_id" => $room_id,
				"user_id" => $user2_id
			),
			$host . "/unSubscribeRoom.php"
		)
	);
	printTable($db, "djmusic_usersinrooms");
	
	//user2 : logout
	prettyPrint(
		"user2 : logout - user_id:" . $user2_id, 
		sendJSONToURL(
			array(
				"user_id" => $user2_id
			),
			$host . "/logout.php"
		)
	);
	printTable($db, "djmusic_users");
	
	//user1 : logout
	prettyPrint(
		"user1 : logout - user_id:" . $user1_id, 
		sendJSONToURL(
			array(
				"user_id" => $user1_id
			),
			$host . "/logout.php"
		)
	);
	printTable($db, "djmusic_users");
	
	//delete room
	prettyPrint(
		"delete room - room_id:" . $room_id, 
		sendJSONToURL(
			array(
				"room_id" => $room_id
			),
			$host . "/deleteRoom.php"
		)
	);
	printTable($db, "djmusic_rooms");
	
	echo "//**********END TESTS SUITE 2**********//";
	
	
	
	
	
	//********Functions********//
	
	# Credits http://stackoverflow.com/questions/11079135/how-to-post-json-data-with-php-curl
	function sendJSONToURL($array, $url){
		$ch = curl_init( $url );
		
		# Setup request to send json via POST.
		$payload = json_encode( $array );
		curl_setopt( $ch, CURLOPT_POSTFIELDS, $payload );
		curl_setopt( $ch, CURLOPT_HTTPHEADER, array('Content-Type:application/json'));
		
		# Return response instead of printing.
		curl_setopt( $ch, CURLOPT_RETURNTRANSFER, true );
		
		# Send request.
		$result = curl_exec($ch);
		curl_close($ch);
		
		# Print response.
		return $result;
	}
	
	function prettyPrint($test, $data, $get = null){
		echo "Current Test :: " . $test;
		echo "<br>answer from the server : "; 
		echo "<pre>";
		$data_ws = stripslashes($data);
		echo json_encode(json_decode($data_ws), JSON_PRETTY_PRINT);
		echo "</pre>";
		
		if(!empty($get)){
			return json_decode($data)->$get;
		}
	}
	
	function printTable($db, $table){
		$db->request("SELECT * FROM " . $table . "");
		$result = $db->readAll();
		
		echo "/**** printing table " . $table . "****/";
		echo "<pre>";
		print_r($result);
		echo "</pre>";
	} 
?>