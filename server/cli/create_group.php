<?php

/* 
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
 /* this file is used to register a new user in the face recognition api
  the command line format is php register_person.php <person_name> <pic1> <pic2>....
  */

require_once(__DIR__.DIRECTORY_SEPARATOR."../lib/php/facepp_utils/FacePPClientDemo.php");

if (count($argv) < 2)
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

// call api to create the group name
$api->group_create($group_name);


?>