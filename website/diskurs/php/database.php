<?php
class database {
	private $db;
	private $con;
	private $creds = array();
	private $props;

	//Constructor
	function __construct($db_url, $db_username, $db_password, $db_scheme) {
		global $props;
		$this->props = $props;

		$this -> creds = array("url" => $db_url, "username" => $db_username, "password" => $db_password, "scheme" => $db_scheme);
	}

	private function ensure_connection() {
		if (!$this -> con || !$this -> db) {
			$this -> connect();
		}
		mysqli_query($this->con,"SET character_set_results = 'utf8', character_set_client = 'utf8', character_set_connection = 'utf8', character_set_database = 'utf8', character_set_server = 'utf8'");
	}

	private function connect() {
		$this -> con = new mysqli($this -> creds["url"], $this -> creds["username"], $this -> creds["password"],$this -> creds["scheme"]);
		//$this -> con = mysqli_connect($this -> creds["url"], $this -> creds["username"], $this -> creds["password"]);

		if (!$this -> con) {
			die("Cannot connect. " . mysqli_error($this -> con));
		}

		mysqli_query($this->con,"set names 'utf8'");
		// The database name selection.
		$this -> db = mysqli_select_db($this->con,$this -> creds["scheme"]);
		if (!$this -> db) {

			die("Cannot select database " . mysqli_error($this -> con));
		}
	}

	public function getOverview() {
		$query = "SELECT * from " . $this->props->overview;
		return $this->querySelect($query, $this->props->overview);
	}
	public function getSumOfRows() {
		$query = "SELECT TABLE_NAME, TABLE_ROWS FROM INFORMATION_SCHEMA.TABLES WHERE TABLE_SCHEMA = 'UniDisk'";
		return $this->querySelect($query, $this->props->overview);
	}

	public function newCampaign($name) {
		$query= "INSERT INTO " . $this->props->overview . "(Name, Status) VALUE ('" . $name . "', 0)";
		$this->query($query);
	}

	public function createTable($name) {
		$query = "CREATE TABLE `" . $name . "_" . $this->props->stichWort . "` (Id MEDIUMINT NOT NULL AUTO_INCREMENT, Stichwort VARCHAR(50), Variable VARCHAR(50), PRIMARY KEY (Id))";
		return $this->query($query);
	}

	public function saveStichVarMeta($stich, $var, $camp) {
		$query = "INSERT INTO `" . $camp . "_" . $this->props->stichWort . "` (Stichwort, Variable) VALUE ('" . $stich . "', '" . $var . "')";
		$this->query($query);
	}
	public function loadStichVarMeta($camp) {
		$query = "SELECT * FROM `" . $camp . "_" . $this->props->stichWort . "`";
		return $this->querySelect($query, $camp . "_" . $this->props->stichWort);
	}

	public function loadScoreStich($group, $camp) {
		$query = "SELECT " . $group . ", SUM(SolrScore) as Score, Count(" . $group . ") as Count FROM `" . $camp . "_" . $this->props->scoreStich . "` GROUP BY " . $group;
		return $this->querySelect($query, $camp . "_" . $this->props->scoreStich);
	}

	public function loadScoreVar($group, $camp) {
		$query = "SELECT " . $group . ", SUM(SolrScore) as Score, Count(" . $group . ") as Count FROM `" . $camp . "_" . $this->props->varMeta . "` GROUP BY " . $group;
		return $this->querySelect($query, $camp . "_" . $this->props->varMeta);
	}

	public function loadVarSolrSum($camp) {
		$query = "SELECT SUM(SolrScore) as SolrSumme, Lat, Lon, Variable FROM `" . $camp . "_" . $this->props->varMeta . "` WHERE Lat != -1 AND Lon != -1 GROUP BY Lat, Lon, Stichworte";
		return $this->querySelect($query, $camp . "_" . $this->props->varMeta);
	}
	
	public function deleteElement($elem, $camp) {
		$query = "DELETE FROM `" . $camp . "_" . $this->props->stichWort . "` WHERE Id = \"" . $elem . "\"";
		echo $query;
		return $this->query($query);
	}

	private function querySelect($query, $table) {
		$this->ensure_connection();
		mysqli_query($this->con,"SET NAMES 'utf8'");
		if(mysqli_num_rows(mysqli_query($this->con, "SHOW TABLES LIKE '".$table."'"))==1) {
			$result = mysqli_query($this->con, $query) or die ("Can't query: " + $query);
		} else {
			return ;
		}
		return $this->resultToArray($result);
	}

	private function query($query) {
		$this->ensure_connection();
		mysqli_query($this->con,"SET NAMES 'utf8'");
		$result = mysqli_query($this->con, $query) or die ("Can't query: " + $query);
	}

	private function resultToArray($result) {
		$rows = [];
		while($row = $result->fetch_array()) {
			array_push($rows, $row);
		}
		return $rows;
	}
}
?>
