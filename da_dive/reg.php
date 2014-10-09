<?php
require('common.php');
//This script administers registration process
//Receives a JSON request from client mobile app with entered fields in registration form and returns true or false for ans key in JSON for successful registration

if(isset($_POST))
{

	//print(file_get_contents('php://input'));

	$data=json_decode(file_get_contents('php://input'),TRUE);
	$link = connectDB ();

	$arr=array();
	foreach($data AS $prop => $val) {$arr[$prop]=$val;}
	

	$a=$arr['loginId'];    //(string)$dat->{'loginId'};  //casting in string type the attributes of JSON obkject
	$b=$arr['name'];       //(string)$dat->{'name'};
	$c=$arr['passw'];          //(string)$dat->{'passw'};
	$d=$arr['addr'];       //(string)$dat->{'addr'};
	$e=$arr['phno'];           //(string)$dat->{'phno'};
	$f=$arr['wallet_bal']; //(string)$dat->{'wallet_bal'};


	$query = "insert into user (loginId, name, passw, addr,phno,wallet_bal) values('$a','$b','$c','$d','$e','$f')";
	
	$result = mysql_query($query);
	if($result)
	{
		$data = array('ans' =>	'true');

		print(json_encode($data));
	}
	else{

		$data = array('ans' =>	'false');

		print(json_encode($data));

	}
	mysql_close();
}

?>						

