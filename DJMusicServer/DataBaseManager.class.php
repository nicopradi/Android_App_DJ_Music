<?php
class DataBaseManager{
	private $host;
	private $dbname;
	private $login;
	private $password;
	private $prefix_table;
	
	private $db;
	private $req;
	private $reqII;
	
	private $showDebug=false;
	
	//Retourne la valeur du host
	public function getHost(){
		return $this->host;
	}
	
	//Retourne le nom de la base de données
	public function getDataBaseName(){
		return $this->dbname;
	}
	
	//Change de base de données
	public function changeDataBase($newDB){
		$this->dbname = $newDB;
		
		$this->connexionDB('mySQL');
	}
	
	//Retourne le login
	public function getLogin(){
		return $this->login;
	}
	
	//Retourne la PDO gérant la connexion
	public function getDataBaseConnexion(){
		return $this->db;
	}
	
	//Affiche le debug
	public function showDebug($value){
		$this->showDebug = $value;
		
		$status = 'enable';
		if(!$value)
			$status = 'disable';
	}	
	
	//Constructeur
	//$host 	: emplacement de la base de données
	//$dbname 	: nom de la base de données
	//$login	: identifiant pour se connecter à la base
	//$password : mot de passe
	public function __construct($host, $dbname, $login, $password, $prefix_table){
		$this->host = $host;
		$this->dbname = $dbname;
		$this->login = $login;
		$this->password = $password;
		$this->prefix_table = $prefix_table;
		
		$this->connexionDB('mySQL');
	}
	
	//Permet de créer la connexion avec le serveur
	public function connexionDB($type){
		switch($type){
			case 'mySQL' :
				try{
					$this->db = new PDO('mysql:host=' . $this->host . ';dbname=' . $this->dbname , $this->login, $this->password);
				}
				catch(Exception $e){
					$this->writeLog("error", $e->getMessage());
					die('Erreur : ' . $e->getMessage());
				}
			break;
		}
	}
	
	//Ferme la connexion à la base
	public function closeDB(){
		if(!empty($this->req))
			$this->req->closeCursor();
	}
	
	//Exécute la requête primaire passée en argument
	public function request($request, $args = array()){
		$this->closeDB();
		try{
			$request = preg_replace('/[F|f][R|r][O|o][M|m]\s+/', "FROM " . $this->prefix_table, $request);
			$request = preg_replace('/[J|j][O|o][I|i][N|n]\s+/', "JOIN " . $this->prefix_table, $request);
			$this->req = $this->db->prepare($request);
			$this->req->execute($args);
			//var_dump($this->req->errorInfo());
			//$this->req = $this->db->query($request);
			//$this->writeLog("new request : " . $request);
		}
		catch(Exception $e){
			$this->writeLog("error", $e->getMessage());
			die('error : ' . $e->getMessage());
		}
		
		if(empty($this->req)){
			//$this->writeLog("warning", "the response of the server was empty !");
			echo "this request was empty >>" . $request;
			//$this->writeLog("warning", "the response of the server was empty !>><<"); //" . $request . "
		}
			
		if($this->showDebug){
			echo "<br>request : " . $request . "<br>";
			print_r($args);
		}
	}
	
	//Exécute la requête secondaire passée en argument
	public function requestII($request, $args){
		try{
			$request = preg_replace('/[F|f][R|r][O|o][M|m]\s+/', "FROM " . $this->prefix_table, $request);
			$request = preg_replace('/[J|j][O|o][I|i][N|n]\s+/', "JOIN " . $this->prefix_table, $request);
			$this->reqII = $this->db->prepare($request);
			$this->reqII->execute($args);
			//$this->req = $this->db->query($request);
			//$this->writeLog("new request : " . $request);
		}
		catch(Exception $e){
			$this->writeLog("error", $e->getMessage());
			die('error : ' . $e->getMessage());
		}
		
		if(empty($this->reqII)){
			$this->writeLog("warning", "the response of the server was empty !");
			//echo $request;
			$this->writeLog("warning", "the response of the server was empty !>>" . $request . "<<");
		}
			
		if($this->showDebug)
			echo "<br>request : " . $request;
	}
	
	//Exécute une requête avec des variables
	/*public function requestWithVariables($request, $array){
		$this->req = $this->db->prepare($request);
		$this->req->execute($array);
		
		if(empty($this->req))
			$this->writeLog("warning", "the response of the server was empty !\n>>" . $request . "<<");
	}*/
	
	//Insérer dans la table $table les données contenues dans $array
	public function insert($table, $args){
		$part1 = 'INSERT INTO ' . $this->prefix_table . $table . '(';
		$part2 = 'VALUES(';
		
		foreach($args as $key => $value){
			$part1 = $part1 . $key . ',';
			$part2 = $part2 . ':' . $key . ',';
		}
		
		$part1 = substr_replace($part1, ')', strlen($part1)-1, 1);
		$part2 = substr_replace($part2, ')', strlen($part2)-1, 1);
		
		$this->request($part1 . ' ' . $part2, $args);
		
		//if($this->showDebug)
			//echo "<br>" . $part1 . ' ' . $part2;
	}
	
	//Met à jour la table $table avec les données contenues dans $array où $condition est vraie
	public function update($table, $args, $condition){
		$command = 'UPDATE ' . $this->prefix_table . $table . ' SET ';
		
		foreach($args as $key => $value){
			$command .= $key . '= :' . $key . ',';
		}
		
		$command = substr_replace($command, ' WHERE ', strlen($command) - 1, 1);
		
		foreach($condition as $key => $value){
			$command .= $key . '= :' . $key . ' AND ';
		}
		
		$command = substr_replace($command, '', strlen($command) - 5, 5);
		
		try{
			$this->request($command, array_merge($args, $condition));
		} catch (Exception $e){
			die("error : " . $e->getMessage());
		}
		
		//if($this->showDebug)
			//echo "<br>" . $command;
	}
	
	//Supprime les entrés de la table $table dont $condition est vrai
	public function delete($table, $condition){
		if(empty($condition))
			die('! the condition is empty, that will erase the entire table !');
			
		$command = 'DELETE FROM ' . $table . ' WHERE ';
		
		foreach($condition as $key => $value){
			$command .= $key . '=:' . $key . ' AND ';
		}
		
		$command = substr_replace($command, '', strlen($command) - 5, 5);
		
		$this->request($command, $condition);
		
		if($this->showDebug)
			echo "<br>" . $command;
	}
	
	public function increase($table, $columnToIncrease, $condition){
		$this->add($table, $columnToIncrease, 1, $condition);
	}
	
	public function decrease($table, $columnToIncrease, $condition){
		$this->add($table, $columnToIncrease, -1, $condition);
	}
	
	public function add($table, $columnToChange, $value, $condition){
		if(!is_int($value)){
			echo "$value is not integer";
		}
		
		$request = "SELECT $columnToChange FROM $table WHERE ";
		foreach($condition as $key => $val){
			$request .= $key . "= :" . $key . " AND ";
		}
		$request = substr_replace($request, '', strlen($request) - 5, 5);
		
		$this->request($request, $condition);
		$result = $this->read();
		$currentValue = $result[$columnToChange];
		$this->update($table, array($columnToChange => $currentValue + $value), $condition);
	}
	
	//Retourne la prochaine valeur de la requête primaire
	public function read(){
		try{
			return $this->req->fetch();
		}
		catch(Exception $e){
			$this->writeLog("error", $e->getMessage());
			die('Erreur : ' . $e->getMessage());
		}
	}
	
	//Retourne la prochaine valeur de la requête secondaire
	public function readII(){
		return $this->reqII->fetch();
	}
	
	//Retourne toutes les valeurs de la requête primaire
	public function readAll(){
		try{
			return $this->req->fetchAll();
		}
		catch(Exception $e){
			$this->writeLog("error", $e->getMessage());
			die('Erreur : ' . $e->getMessage());
		}
	}
	
	//Retourne toutes les valeurs de la requête secondaire
	public function readAllII(){
		return $this->reqII->fetchAll();
	}
	
	//Affiche la requête principale
	public function showRequest(){
		$donnees = $this->readAll();
			
		echo '<pre>';
		print_r($donnees);
		echo '</pre>';
	}
	
	//Affiche la requête secondaire
	public function showRequestII(){
		$donnees = $this->readAllII();
			
		echo '<pre>';
		print_r($donnees);
		echo '</pre>';
	}
	
	//Affiche l'id du dernier élément inséré
	public function lastIdInsert(){
		return $this->db->lastInsertId();
	}
	
	public function writeLog($status, $message){
		//$file = 'logs.log';
		//$now = (new DateTime('now'))->format('Y-m-d H:i:s');
		if (!empty($_SERVER['HTTP_CLIENT_IP'])) {
			$ip = $_SERVER['HTTP_CLIENT_IP'];
		} elseif (!empty($_SERVER['HTTP_X_FORWARDED_FOR'])) {
			$ip = $_SERVER['HTTP_X_FORWARDED_FOR'];
		} else {
			$ip = $_SERVER['REMOTE_ADDR'];
		}
		
		/*// Ouvre un fichier pour lire un contenu existant
		$current = file_get_contents($file);
		
		$current .= "[" . $ip . "] at " . $now . " :: " . $message . "\n";
		// Écrit le résultat dans le fichier
		file_put_contents($file, $current);*/
		
		//in the db
		$this->insert(
			"logs", 
			array(
				"ip" => $ip,
				"status" => $status,
				"message" => $message
			)
		);
	}
}
?>