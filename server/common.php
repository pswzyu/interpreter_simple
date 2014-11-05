<?php

@define('NOCHECK', TRUE);
//set_magic_quotes_runtime(0);
define('FROOT', dirname(__FILE__).DIRECTORY_SEPARATOR);
include_once(FROOT."common/functions.php");
include_once(FROOT."common/types.php");
include_once(FROOT."lib/db/mysql.inc.php");
include_once(FROOT."config/config.php");
@include_once(FROOT."config/config.private.php");
include_once(FROOT."config/lang.php");
//include_once(FROOT."../member/config.php");

//$ecms_userid = $cfg_ml->M_ID;
//echo "--".$ecms_userid;


$udb = new UDB();
$udb -> connect($config_database_server, $config_database_username, $config_database_password);
$udb -> query("USE `interpreter`");
$udb -> query("SET NAMES 'utf8';");
$udb -> query("SET CHARACTER SET utf8;");


$magic_quote = get_magic_quotes_gpc();
if(empty($magic_quote)) {
	$_GET = saddslashes($_GET);
	$_POST = saddslashes($_POST);
}


?>