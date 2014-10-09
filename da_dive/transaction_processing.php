function transaction_processing($a,$b,$c)
{


	$query = "insert into trnsc (loginId, donation, message, flag) values('$a','$b','$c','0')";		
	$result = mysql_query($query);
	//check for existing transactions that have pending pairings and not from same user
	$query = "SELECT * FROM trnsc WHERE !(loginId='$a') AND flag='0' ";
	$result = mysql_query($query);

	if($result && $row = mysql_fetch_array($result)) 
	{
		//pairing can be done
		$d=$row['donation'];	//second user
		$m=$row['message'];
		$l=$row['loginId'];
		$w=$row['wallet_bal'];

		
		//updation of donation_rec and message_rec and flag
		$query = "UPDATE trnsc SET donation_rec='$d',  message_rec='$m' , flag='1' WHERE loginId='$a' ";
		$result = mysql_query($query);
		
		
		$query = "UPDATE trnsc SET donation_rec='$b',  message_rec='$c' , flag='1' WHERE loginId='$l' ";
		$result2 = mysql_query($query);
	

		$query = "SELECT wallet_bal FROM user WHERE loginId='$a'";	
		$result = mysql_query($query);
		$row=mysql_fetch_array($result);


		$k=$row['wallet_bal'];
		$k=$k+$d-$b;
		$query = "UPDATE user SET wallet_bal='$k' WHERE loginId='$a' ";
		$result = mysql_query($query);
		


		$w=$w+$b-$d;
		$query = "UPDATE user SET wallet_bal='$w' WHERE loginId='$l' ";
		$result= mysql_query($query);


/*
		
		if($result && $result2) ;//return true; //both updated successfuly
		else{	
			//unsuccessful
			//rollback of transaction
			$query = "UPDATE trnsc SET donation_rec='0',  message_rec=' ' , flag='0' WHERE loginId='$a' ";
			
			$query = "UPDATE trnsc SET donation_rec='0',  message_rec=' ' , flag='0' WHERE loginId='$l' ";

		}
*/

	}
}

