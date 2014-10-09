<?php
require('common.php');
//This script validates login
//Receives a JSON request from client mobile app with entered login details and returns true or false for ans key in JSON
if(isset($_POST))
{
	/*$arr=array(
	"loginId"=>"mehtamohit987",
	"passw"=>"qwerty987");*/
	
	$link = connectDB ();
	$data=json_decode(file_get_contents('php://input'),TRUE);
	

	$arr=array();
	foreach($data AS $prop => $val) {$arr[$prop]=$val;}

	$a=$arr['loginId'];   
	$b=$arr['passw'];     

	$query = "SELECT COUNT(*) AS num FROM user WHERE loginId='$a'and passw='$b'";	

	$result = mysql_query($query);
	
	$row=mysql_fetch_array($result);
	print(json_encode($row));
	mysql_close();

}

?>
