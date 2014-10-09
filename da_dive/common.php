<?php
define ('DB_HOST','localhost');
define ('DB_NAME', '786534');
define ('DB_USER', '786534');
define ('DB_PASS', 'qwerty987');


function connectDB ()
{
	$link = mysql_connect(DB_HOST, DB_USER, DB_PASS);
	if (!$link) 
	{
	    die('Could not connect: ' . mysql_error());
	}
	

	mysql_select_db(DB_NAME, $link);
	return $link;
}

?>
