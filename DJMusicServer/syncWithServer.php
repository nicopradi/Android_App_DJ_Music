<?
	include_once("config.php");

	$json_manager = new JSONManager(file_get_contents('php://input'), true);
	
	$seconds_from_sync = (5 - (time() % 5)) % 5;
	
	$json_manager->put("seconds_from_sync", $seconds_from_sync);
	
	$json_manager->send();
?>