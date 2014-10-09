<form enctype="multipart/form-data" action="http://home.cnzy.me:8001/interpreter/test.php" method="POST">
<input name="testname" type="text" />
<input type="hidden" name="MAX_FILE_SIZE" value="30000000">
Send this file: <input name="userfile" type="file">
<input type="submit" value="Send File">
</form>

<?php

print_r($_GET);

print_r($_POST);

$uploaddir = '/var/www/interpreter/uploads/';
$uploadfile = $uploaddir. $_FILES['userfile']['name'];
print "<pre>";
if (move_uploaded_file($_FILES['userfile']['tmp_name'], $uploaddir . $_FILES['userfile']['name'])) {
    print "File is valid, and was successfully uploaded.  Here's some more debugging info:\n";
    print_r($_FILES);
} else {
    print "Possible file upload attack!  Here's some debugging info:\n";
    print_r($_FILES);
}
print "</pre>";
?>
