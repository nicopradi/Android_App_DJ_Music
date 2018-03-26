<?php
class JSONManager{	
	private $input;
	private $response = array();
	
	//Constructeur
	//$input 	: json input
	public function __construct($input, $canBeNull = false){
		if(empty($input) && !$canBeNull){
			$this->error("json input is null");
		} else {
			$this->input = json_decode($input);
			$this->response["confirmation"] = "OK";
		}
	}
	
	public function expect($key, $intVal = false, $error = true){
		if(!isset($this->input->$key) || $this->input->$key === ""){
			if($error){
				$this->error("expected " . $key);
			} else {
				$this->warning("expected " . $key);
			}
		}
		
		if($intVal && !is_int($this->input->$key)){
			$this->error("expected " . $key . " of type integer, found " . gettype($this->input->$key));
		}
	}
	
	public function expectIn($keyParent, $keyChild, $intVal = false, $error = true){
		if(!isset($this->input->$keyParent) || empty($this->input->$keyParent)){
			if($error){
				$this->error("expected " . $keyParent);
			} else {
				$this->warning("expected " . $keyParent);
			}
		} else {
			if(!is_array($this->input->$keyParent)){
				$this->error("expected " . $keyParent . " to be an array");
			}
			
			foreach($this->input->$keyParent as $element){
				if(!isset($element->$keyChild) || empty($element->$keyChild)){
					if($error){
						$this->error("expected " . $keyChild . " in " . $keyParent);
					} else {
						$this->warning("expected " . $keyChild . " in " . $keyParent);
					}
				}
				
				if($intVal && !is_int($element->$keyChild)){
					$this->error("expected " . $element->$keyChild . " of type integer, found " . gettype($element->$keyChild));
				}
			}
		}
	}
	
	public function get($key, $intVal = false, $error = true){
		$this->expect($key, $intVal, $error);
		return $this->input->$key;
	}
	
	public function put($key, $value) {
		$this->response[$key] = $value;
	}
	
	public function error($msg){
		echo json_encode(
			array(
				"confirmation" => "ERROR", 
				"error_msg" => $msg
			)
		);
		exit;
	}
	
	function warning($msg){
		$this->response["confirmation"] = "WARNING"; 
		$this->response["warning_msg"] = $msg;
	}
	
	public function send(){
		echo json_encode($this->response);
	}	
}
?>