<html>
<head>
<link rel="stylesheet" href="http://fleckenroller.cs.uni-potsdam.de/css/styles.css">
<link rel="stylesheet" href="https://maxcdn.bootstrapcdn.com/bootstrap/3.3.5/css/bootstrap.min.css">
<link rel="stylesheet" href="https://maxcdn.bootstrapcdn.com/bootstrap/3.3.5/css/bootstrap-theme.min.css">
<link href="http://code.jquery.com/ui/1.10.4/themes/ui-lightness/jquery-ui.css" rel="stylesheet">
<link rel="stylesheet" href="http://fleckenroller.cs.uni-potsdam.de/lib/ol2/theme/default/style.css" type="text/css">
<link rel="stylesheet" href="http://fleckenroller.cs.uni-potsdam.de/css/styles.css">
<style>
  table tr td {
    padding:5px;
  }
</style>
</head>
<body>
<div id="content" class="container">
		<div class="jumbotron" style="background-color: #2e6da4">
			<h1 id="" style="color:#FFF"><center>UniDisk</center></h1>
			<h3 id="" style="color:#FFF"><center>Das Tool zur Stichwortsuche auf Hochschulseiten</center></h3>
		</div>

  <table style="width:100%">
    <tr>
      <td style="width:33%;text-align:center;"><a href="http://fleckenroller.cs.uni-potsdam.de/diskurs/">Zur Startseitee</a></td>
      <td style="width:33%;text-align:center;"><a href="getStatistics.php">Zur Excel-Ausgabe</a></td>
      <td style="width:33%;text-align:center;"><a href="getStatisticsHTML.php">Zur HTML-Ausgabe</a></td>
    </tr>  
  </table>
  <br />
  <h1>Ausgabe als HTML-Seite</h1>
  <br /><br />
</div>

<div class="container">
		<div class="row">
      <div class="col-sm-3" style="width:100%;">
<?php 
	//debug mode
	//ini_set('display_errors', 'On');
	//error_reporting(E_ALL);
	
	include("config.php");
	
	$pdo = new PDO('mysql:host=localhost;dbname='.$db_scheme.';charset=utf8', $db_username, $db_password);
	$pdo-> query("SET character_set_results = 'utf8', character_set_client = 'utf8', character_set_connection = 'utf8', character_set_database = 'utf8', character_set_server = 'utf8'");
	
	if (isset($_POST['var']) || isset($_GET['hs'])){
	  if (isset($_GET['hs'])){
	    $sql = "SELECT id, SolRScore
      FROM `".$_GET['var']."_ScoreStich` 
      WHERE (id LIKE '".$_GET['hs']."%') 
      ORDER BY SolRScore DESC
      ;";
   
      $ausgabe='<table style="border-collapse:collapse;border=solid 1px;" border="1">';
      foreach ($pdo->query($sql) as $row) {
        $ausgabe .= '<tr>';
        $ausgabe .= '<td><a href="'.$row[0].'">'.$row[0].'</a></td>';
        $ausgabe .= '<td>'.$row[1].'</td>';
        $ausgabe .= '</tr>';
      }
      echo $ausgabe."</table>";  
	  
	  } else { 
    
      $sql = "SELECT A.Hochschulname, A.Homepage, COUNT(B.id) AS cnt, SUM(B.SolRScore)
      FROM `hochschulen_deutschland` A 
      JOIN `".$_POST['var']."_ScoreStich` B ON (B.id LIKE CONCAT(A.Homepage,'%'))
      GROUP BY A.Hochschulname
      ORDER BY cnt DESC
      ;";

      $ausgabe='<table style="border-collapse:collapse;border=solid 1px;" border="1">';
      foreach ($pdo->query($sql) as $row) {
        $ausgabe .= '<tr>';
        $ausgabe .= '<td><a href="getStatisticsHTML.php?hs='.$row[1].'&var='.$_POST['var'].'">'.$row[1].'</a></td>';
        $ausgabe .= '<td>'.$row[0].'</td>';
        $ausgabe .= '<td>'.$row[2].'</td>';
        $ausgabe .= '<td>'.$row[3].'</td>';
        $ausgabe .= '</tr>';
      }
      echo $ausgabe."</table>";  
	
	  }} else {
	  
      $sql = "SELECT `Name` FROM `overview`;";
    
      $ausgabe='<html><form action="getStatisticsHTML.php" method="post">';
      foreach ($pdo->query($sql) as $row) {
        $sql2 = "SELECT COUNT(*) FROM INFORMATION_SCHEMA.TABLES WHERE table_name = '".$row[0]."_ScoreStich';";
        foreach ($pdo->query($sql2) as $row2) {
          if ($row2[0] > 0){
             $ausgabe=$ausgabe.'<input type="radio" name="var" value="'.$row[0].'"> '.$row[0].'</input><br />' ;
          }
        }
        
      }
      $ausgabe .= '<br /><br />';
      $ausgabe .= '<input type="submit" value="Liste ausgeben">'; 
      $ausgabe .= '<br />Hinweis: Das Zusammenstellen der Daten kann einige Zeit dauern.';
    
    
    
      echo $ausgabe.'</form></html>';
    }  
    
	

	
	
	
?>
</div></div></div>
</body>
</html>