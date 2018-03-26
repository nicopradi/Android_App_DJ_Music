<?php
	include_once("config.php");
	
	$db->request("SELECT id FROM rooms");
	while($result = $db->read()){
		if($result['currentTrackId'] <> 0){
			checkTimeAndUpdate($result['id']);
		}
	}
?>