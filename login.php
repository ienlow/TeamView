<?php
require "conn.php";
$password = $_POST["team"];
$mysql_qry = "select * from restuarants where mlb_Team like '$password' or nfl_Team like '$password'";
$r = mysqli_query($conn, $mysql_qry);
if (mysqli_num_rows($r) > 0) {
	for ($x = 1; $x <= mysqli_num_rows($r); $x++) {
	$row=mysqli_fetch_assoc($r);
	$name=$row["Name"];
	$location=$row["Location"];
	echo " ".$name;
	echo " ".$location;
	}
}
else 
	echo "fail";

mysqli_close($conn);
?>