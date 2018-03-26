<?php
	include_once("config.php");
	//$db->showDebug(true);

	$json_manager = new JSONManager(file_get_contents('php://input'));
	
	$user_id = $json_manager->get("user_id");
	$user_name = $json_manager->get("user_name");
	
	if(isLogged($user_id)){
		$db->request("SELECT user_name FROM users WHERE user_id=:userId", array('userId' => $user_id));
		$result = $db->read();
		
		if($result['user_name'] <> $user_name){
			$db->update("users", array("user_name" => $user_name), array("user_id" => $user_id));
			$json_manager->warning("username changed");
		} 
		/*else {
			$json_manager->warning("user already logged");
		}*/
	} else {		
		$db->insert(
			"users", 
			array(
				"user_id" => $user_id,
				"user_name" => $user_name
			)
		);
	}
	
	$json_manager->send();
	$db->writeLog("event", "user $user_id has login");
?>