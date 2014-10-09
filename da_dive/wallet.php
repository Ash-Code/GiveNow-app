<?php
require('common.php');
//This script provides a service for the client to check for success of his past donations
//Client Application in android periodically checks for the change in the status


if(isset($_POST))
{

	//$arr=array("loginId"=>"mehtamohit987");

	//receives json data sent by client
  	$data=json_decode(file_get_contents('php://input'),TRUE);
	$link = connectDB ();

	//these for user to be currently calling the wallet.php to retrieve stauts of its previous trnsctions
	$arr=array();
	foreach($data AS $prop => $val) {$arr[$prop]=$val;}
	
	$a=$arr['loginId'];  

	//check for succcessful transactions done for current user
	$query = "SELECT tr_id,loginId,donation_rec,message_rec FROM trnsc WHERE loginId='$a' AND flag='1' ";
	$result = mysql_query($query);
	
	//removing the donation transactions that are completed for current user from trnsc table after user has been notified
	$query="DELETE from trnsc WHERE loginId='$a' AND flag='1' ";
        $result2 = mysql_query($query);

	//sending the transaction details to user application
	$row=mysql_fetch_array($result);
	print(json_encode($row));
	mysql_close();

}

?>		
