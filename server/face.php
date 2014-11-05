<?php

/* 
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
include_once("./common.php");

require_once(__DIR__.DIRECTORY_SEPARATOR."./lib/php/facepp_utils/FacePPClientDemo.php");

$action = $_POST["action"];

if ($action == "recognition")
{
    $from_user_id = $_POST["self_id"];

    $send_time = intval(microtime($get_as_float=true)*1000);
    //print_r(microtime());
    //echo $send_time;

    if (empty($_FILES['pic_file'])|| empty($from_user_id))
    {
        echo "NULL";
    }else{

        $fn_after_copy = $uploaddir.$from_user_id."_".$send_time."_".$_FILES['pic_file']['name'];

        move_uploaded_file($_FILES['pic_file']['tmp_name'], $fn_after_copy);

        // recognize picture by using face api
        // your api_key and api_secret
        $api_key = "c0fee37d7814a5cd51ebcae97f7f7d25";
        $api_secret = "XbuA2f4G3ZEzYPyCb_37af2QbLlfWZD1";
        // initialize client object
        $api = new FacePPClientDemo($api_key, $api_secret);
        $group_name = "interpreter_group";
        $result = identify($api, $fn_after_copy, $group_name);
        //unlink($fn_after_copy);
        
        //
        if (!$result || empty($result))
            echo "NULL";
        else
            echo $result;
    }
}



function identify(&$api, $filename, $group_name)
{
	global $udb;
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
        
        if ($name != "")
        {
            $udb->query("SELECT `id` FROM `interpreter`.`user_info`
                    WHERE `real_name` = '{$name}';");
            $db_result = $udb->fetch_assoc();
            $user_id = $db_result['id'];
        }
	
	return $user_id.",".$name;
}

/*
 * <form action="face.php" method="post"
enctype="multipart/form-data">
<label for="file">Filename:</label>
<input type="file" name="pic_file" id="file"><br>
<a>self_id</a><input type="text" name="self_id"><br/>
<a>target_id</a><input type="text" name="target_id"><br/>
<a>action</a><input type="text" name="action" value="recognition"><br/>
<input type="submit" name="submit" value="Submit">
</form>
 */

?>



