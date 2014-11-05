<?php

include_once("./common.php");


$from_user_id = $_POST["self_id"];
$to_user_id = $_POST["target_id"];
$content = trim($_POST["content"]);

$send_time = intval(microtime($get_as_float=true)*1000);
//print_r(microtime());
echo $send_time;

if (empty($content)||empty($from_user_id) || empty($to_user_id))
{
    echo "failed";
}else{
    
    //$fn_after_copy = $uploaddir.$from_user_id."_".$send_time."_".$_FILES['sound_file']['name'];
    
    //move_uploaded_file($_FILES['sound_file']['tmp_name'], $fn_after_copy);
    
    // convert the format of the uploaded file
    //$file_info = pathinfo($fn_after_copy);
    //$fn_after_conv = $file_info['dirname'].DIRECTORY_SEPARATOR .$file_info['filename'].".wav";
    //shell_exec("ffmpeg -i {$fn_after_copy} -ac 1 -ar 16000 -af 'volume=volume=5dB'  {$fn_after_conv}");
    // delete the original file
    //unlink($fn_after_copy);
    
    // insert this to database
    $udb->query("INSERT INTO `interpreter`.`chat_message` (`id`, `from_user_id`,
        `to_user_id`, `send_timestamp`, `sound_filename`, `string_content`) VALUES
        (NULL, '{$from_user_id}', '{$to_user_id}', '{$send_time}', '', '{$content}');");
}

/*
 * <form action="upload.php" method="post"
enctype="multipart/form-data">
<label for="file">Filename:</label>
<input type="file" name="sound_file" id="file"><br>
<a>self_id</a><input type="text" name="self_id"><br/>
<a>target_id</a><input type="text" name="target_id"><br/>
<input type="submit" name="submit" value="Submit">
</form>
 */


?>

