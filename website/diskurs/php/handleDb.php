<?php
	include("config.php");
	include("database.php");

	$props = json_decode($_POST['props']);
	$database = new Database($db_url, $db_username, $db_password, $db_scheme);

	$mysql_Result;
	switch($_POST['loadPurpose']) {
		case "overview":
			$mysql_Result = $database->getOverview();
			echo json_encode($mysql_Result);
			echo "#";
			$mysql_Result = $database->getSumOfRows();
			echo json_encode($mysql_Result);
			break;
		case "saveCampaign":
			$database->newCampaign($_POST['Name']);
			$database->createTable($_POST['Name']);
			break;
		case "saveStichVarMeta":
			$database->saveStichVarMeta($_POST['stich'], $_POST['vari'], $_POST['campaign']);
			break;
		case "loadStichVarMeta":
			$mysql_Result = $database->loadStichVarMeta($_POST['campaign']);
			if (sizeof($mysql_Result) > 0) {
				echo json_encode($mysql_Result);
			}
			echo "#";
			$mysql_Result = $database->loadScoreStich($_POST['groupStich'], $_POST['campaign']);
			if (sizeof($mysql_Result) > 0) {
				echo json_encode($mysql_Result);
			}
			echo "#";
			$mysql_Result = $database->loadScoreVar($_POST['groupVar'], $_POST['campaign']);
			if (sizeof($mysql_Result) > 0) {
				echo json_encode($mysql_Result);
			}
			break;
		case "loadScoreStich":
			$mysql_Result = $database->loadScoreStich($_POST['group'], $_POST['campaign']);
			echo json_encode($mysql_Result);
			break;
		case "loadVarSolrSum":
			$mysql_Result = $database->loadVarSolrSum($_POST['campaign']);
			echo json_encode($mysql_Result);
			break;
		case "deleteElement":
			$mysql_Result = $database->deleteElement($_POST['element'], $_POST['campaign']);
			break;

	}
?>
