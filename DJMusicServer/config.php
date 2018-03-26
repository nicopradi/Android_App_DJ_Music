<?php
	include_once("DataBaseManager.class.php");
	include_once("serverFunctions.php");
	include_once("JSONManager.class.php");
	
	$connection = parse_ini_file("config_connection.ini", false);
	$NOTAVAILABLE = "Not available";
	
	$db = new DataBaseManager(
			$connection['host'], 
			$connection['database'], 
			$connection['login'], 
			$connection['password'],
			$connection['prefix_table']);
	
	//$db->showDebug(true);
?>
