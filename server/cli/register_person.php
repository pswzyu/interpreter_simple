<?php

/* 
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
 /* this file is used to register a new user in the face recognition api
  the command line format is php register_person.php <person_name> <group_name> <pic1> <pic2>....
  */

require_once(__DIR__.DIRECTORY_SEPARATOR."../lib/php/facepp_utils/FacePPClientDemo.php");

if ( count($argv) < 4)
{
	die("Parameter not enough. person_name, group_name, pic1, pic2 ...");
}

// your api_key and api_secret
$api_key = "c0fee37d7814a5cd51ebcae97f7f7d25";
$api_secret = "XbuA2f4G3ZEzYPyCb_37af2QbLlfWZD1";
// initialize client object
$api = new FacePPClientDemo($api_key, $api_secret);

// get the person name from the command line parameter
$person_name = $argv[1];
$group_name = $argv[2];
// rest of the parameters are the pic file names
$pics = array();
for ($step = 3; $step < count($argv); $step ++)
{
	$pics[] = $argv[$step];
}
// store the face_ids obtained by detection/detect API
$face_ids = array();
//detect($api, $person_name, $face_ids);

// if the person is already exist, delete him
// delete the person if exists
$api->person_delete($person_name);
// create a new person for this face
$api->person_create($person_name);
// add face into new person
$api->person_add_face($face_id, $person_name);

// for each picture, detect faces in the picture
foreach ($pics as $one_pic)
{
	$det_result = $api->face_detect_post($one_pic);
	// skip errors
    if (empty($det_result->face))
        continue;
    // skip photo with multiple faces (we are not sure which face to train)
   	if (count($det_result->face) != 1)
   		continue;
		
	// obtain the face_id
   	$face_id = $det_result->face[0]->face_id;
   	$face_ids[] = $face_id;
   	// add face into new person
   	$api->person_add_face($face_id, $person_name);
	// add the person to the group
	$api->group_add_person($person_name, $group_name);
}


?>