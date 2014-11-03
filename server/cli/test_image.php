<?php


require_once(__DIR__.DIRECTORY_SEPARATOR."../lib/php/facepp_utils/FacePPClientDemo.php");

if (count($argv) < 3)
{
	die("Parameter not enough. group_name");
}

// your api_key and api_secret
$api_key = "c0fee37d7814a5cd51ebcae97f7f7d25";
$api_secret = "XbuA2f4G3ZEzYPyCb_37af2QbLlfWZD1";
// initialize client object
$api = new FacePPClientDemo($api_key, $api_secret);

// get the group name from the command line parameter
$group_name = $argv[1];
$filename = $argv[2];

// call api to recognize person in the pic
$result = identify($api, $filename, $group_name);

echo $result;


function identify(&$api, $filename, $group_name)
{
	
	// recoginzation
	$result = $api->recognition_identify_post($filename, $group_name);
	
	// skip errors
	if (empty($result->face))
		return false;
	// skip photo with multiple faces
//	if (count($result->face) > 1)
//		return false;
	$face = $result->face[0];
	// skip if no person returned
	if (count($face->candidate) < 1)
		return false;
		
	// find the person with highest possibility
	$highest = 0;
	$name = "";
	foreach ($face->candidate as $candidate)
	{
		if ($candidate->confidence > $highest)
		{
			$highest = $candidate->confidence;
			$name = $candidate->person_name;
		}
	}
	
	return $name;
}

?>