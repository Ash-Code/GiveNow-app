<?php
require('common.php');
//This script administers donation process
//Receives a JSON request from client mobile app donation value and id and returns true or false for ans key in JSON for if the donation is completely rejected or in process/done

if(isset($_POST))
{

/*
 donate_req -> returns true if it is possible to donate considering current account balance.
	AND
   CALLs transaction_processing.php for processing pending & current pairing requests

So the concept is that at each donate request the server receives from any one the server tries to pair that up or make it wait in buffer
*/


	/*$arr=array(
	"loginId"=>"osank@dce.edu",
	"donation"=>"6",
	"message"=>"auugaus");
	*/

	$data=json_decode(file_get_contents('php://input'),TRUE);
	$link = connectDB ();
	
	$arr=array();
	foreach($data AS $prop => $val) {$arr[$prop]=$val;}

	$a=$arr['loginId'];   
	$b=$arr['donation'];   
	$c=$arr['message'];

/*
	$a=(string)$data->{'loginId'};
	$b=(string)$data->{'donation'};
	$c=(string)$data->{'message'};
*/

	$query = "SELECT wallet_bal FROM user WHERE loginId='$a'";
	$result = mysql_query($query);


	if($result&&$row = mysql_fetch_array($result)) 
	{		
		if($row['wallet_bal']>=$b)	//validity of donating that amount
		{
			

			$query = "insert into trnsc (loginId, donation, message, flag) values('$a','$b','$c','0')";		
			$result = mysql_query($query);
			$query = "SELECT tr_id FROM trnsc WHERE loginId='$a' and donation='$b' and message='$c' and flag='0' ";
			$result = mysql_query($query);
			$row=mysql_fetch_array($result);
			$tr1=$row['tr_id'];

			//check for existing transactions that have pending pairings and not from same user
			$query = "SELECT * FROM trnsc WHERE !(loginId='$a') AND flag='0' ";
			$result = mysql_query($query);
			
			if($result&&$row = mysql_fetch_array($result)) 
			{	
				
				//pairing can be done
				$d=$row['donation'];	//second user
				$m=$row['message'];
				$l=$row['loginId'];
				$tr2=$row['tr_id'];

				//updation of donation_rec and message_rec and flag
				$query = "UPDATE trnsc SET donation_rec='$d' ,  message_rec='$m' , flag='1' WHERE tr_id='$tr1' ";
				$result = mysql_query($query);
			
		
				$query = "UPDATE trnsc SET donation_rec='$b' ,  message_rec='$c' , flag='1' WHERE tr_id='$tr2' ";
				$result2 = mysql_query($query);
	

				$query = "SELECT wallet_bal FROM user WHERE loginId='$a'";	
				$result = mysql_query($query);
				$row=mysql_fetch_array($result);


				$k=$row['wallet_bal'];
				$k=$k+$d-$b;
				$query = "UPDATE user SET wallet_bal='$k' WHERE loginId='$a' ";
				$result = mysql_query($query);
		

				
				$query = "SELECT wallet_bal FROM user WHERE loginId='$l'";	
				$result = mysql_query($query);
				$row=mysql_fetch_array($result);


				$w=$row['wallet_bal'];
				$w=$w+$b-$d;

				$query = "UPDATE user SET wallet_bal='$w' WHERE loginId='$l' ";
				$result= mysql_query($query);

			}

			print(json_encode(array('ans' =>'true')));			
		}
		else print(json_encode(array('ans' =>'false')));			
	}
	else print(json_encode(array('ans' =>'false')));			
mysql_close();
}

?>
	
