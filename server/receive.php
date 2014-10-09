<?php

include_once("./common.php");
include_once(FROOT."classes/ATTServices.php");
include_once(FROOT."classes/GoogleServices.php");
include_once(FROOT."classes/XunfeiServices.php");

$send_time = intval(microtime($get_as_float=true)*1000);
//print_r(microtime());
//echo $send_time;

if (empty($_GET["self_id"]))
{
    //echo "failed";
}else{
    
    $self_id = $_GET["self_id"];
    // get the file name from database
    $udb->query("SELECT * FROM `chat_message` WHERE to_user_id ='{$self_id}'
        ORDER BY `send_timestamp` LIMIT 0,1");
    $result = $udb->fetch_assoc();
    if (!$result)
    {
        //echo "empty";
    }else
    {
        // get the user info from the user_info table
        $udb->query("SELECT * FROM `user_info` WHERE id={$self_id}");
        $self_userinfo = $udb->fetch_assoc();
        $udb->query("SELECT * FROM `user_info` WHERE id={$result["from_user_id"]}");
        $from_userinfo = $udb->fetch_assoc();
        
        $att_services = new ATTServices();
        $google_services = new GoogleServices();
        $xunfei_services = new XunfeiServices($xunfei_bin_dir);
        
        if ($from_userinfo["language"] == "zh_CN")
        {
            $from_text = $xunfei_services->speechToText($result["sound_filename"]);
        }elseif($from_userinfo["language"] == "en")
        {
            $from_text = $att_services->speechToText($result["sound_filename"]);
        }else
        {
            
        }
        //$from_userinfo
        echo $from_text;
        
        // translate
        $to_text = $google_services->translate($from_text, $from_userinfo["language"],
                $self_userinfo["language"]);
        
        // text to speech
        $result_fname = $result["sound_filename"]."_done";
        
        if ($self_userinfo["language"] == "zh_CN")
        {
            $xunfei_services->textToSpeech($to_text, $result_fname);
        }elseif($self_userinfo["language"] == "en")
        {
            $att_services->textToSpeech($to_text, $result_fname);
        }else
        {
            
        }
        
        if (file_exists($result_fname))  
        {   
            
            header('Content-Description: File Transfer');     
            header('Content-Type: application/octet-stream');     
            header('Content-Disposition: attachment; filename='.basename($result_fname));     
            header('Content-Transfer-Encoding: binary');     
            header('Expires: 0');     
            header('Cache-Control: must-revalidate, post-check=0, pre-check=0');     
            header('Pragma: public');     
            header('Content-Length: ' . filesize($result_fname));     
            ob_clean();   //重要的就是这个函数的调用， 清空但不关闭输出缓存， 否则下载的文件头两个字符会是0a  
            flush();     
            readfile($result_fname);   // 输出文件内容
            
            $udb->query("DELETE FROM `chat_message` WHERE id='{$result["id"]}';");
            unlink($result["sound_filename"]);
            unlink($result_fname);
            
        }
    }

}

?>