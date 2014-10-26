<?php


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
train($api, $group_name);


/*
 *	train identification model for group
 */
function train(&$api, $group_name)
{
   	// train model
   	$session = $api->train_identify($group_name);
    if (empty($session->session_id))
    {
        // something went wrong, skip
        return false;
    }
    $session_id = $session->session_id;
    // wait until training process done
    while ($session=$api->info_get_session($session_id)) 
    {
        sleep(3);

        if (!empty($session->status)) {
            if ($session->status != "INQUEUE")
                break;
        }
    }
	// done
    return true;
}

?>