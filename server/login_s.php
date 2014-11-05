<?php

/* 
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

include_once("./common.php");

//login
$username = trim($_POST['username']);
$password = trim($_POST['password']);


if (empty($username))
{
    echo "0,";
}else{

    //Check username and password
    $udb->query("SELECT * FROM `user_info` WHERE user_name='{$username}' AND
        password = '{$password}';");
    if ($udb->get_error_no())
    {
        echo "0,";
    }else{
        $result = $udb->fetch_assoc();

        if($result){
                //login successful
                echo $result['id'].','.$result['real_name'].','.$result['language'];
        } else {
                echo '0,';
        }
    }
}

?>