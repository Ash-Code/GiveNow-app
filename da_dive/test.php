<?php
require('common.php');
$link = connectDB ();

	$query = "select * from user";

	$result = mysql_query($query);
	if($result) echo "yeah";
        else echo 'row not inserted';

?>				
