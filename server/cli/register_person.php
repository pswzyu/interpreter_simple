<?php

/* 
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

require_once(__DIR__ . "/FacePPClientDemo.php");

// your api_key and api_secret
$api_key = "YOUR_API_KEY";
$api_secret = "YOUR_API_SECRET";
// initialize client object
$api = new FacePPClientDemo($api_key, $api_secret);

$person_name = $argv[0];


?>