<?php

include_once("./common.php");


$from_user_id = $_POST["self_id"];
$to_user_id = $_POST["target_id"];

$send_time = intval(microtime($get_as_float=true)*1000);
//print_r(microtime());
echo $send_time;

if (empty($_FILES['sound_file'])|| !$from_user_id || !$to_user_id)
{
    echo "failed";
}else{
    
    $file_name_after = $uploaddir.$from_user_id."_".$send_time."_".$_FILES['sound_file']['name'];
    
    move_uploaded_file($_FILES['sound_file']['tmp_name'], $file_name_after);
    
    // insert this to database
    $udb->query("INSERT INTO `interpreter`.`chat_message` (`id`, `from_user_id`,
        `to_user_id`, `send_timestamp`, `sound_filename`, `string_content`) VALUES
        (NULL, '{$from_user_id}', '{$to_user_id}', '{$send_time}', '{$file_name_after}', '{}');");
}

?>


<form action="upload.php" method="post"
enctype="multipart/form-data">
<label for="file">Filename:</label>
<input type="file" name="sound_file" id="file"><br>
<a>self_id</a><input type="text" name="self_id"><br/>
<a>target_id</a><input type="text" name="target_id"><br/>
<input type="submit" name="submit" value="Submit">
</form>