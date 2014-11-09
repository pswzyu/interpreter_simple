<?php

include_once("./common.php");
include_once(FROOT."classes/ATTServices.php");
include_once(FROOT."classes/GoogleServices.php");
include_once(FROOT."classes/XunfeiServices.php");
//include_once(FROOT."classes/MicrosoftServices.php");

$send_time = intval(microtime($get_as_float=true)*1000);
//print_r(microtime());
//echo $send_time."<br/>";

if (empty($_GET["self_id"]))
{
    //echo "failed";
}else{
    
    $self_id = $_GET["self_id"];
    // get the file name from database
    $udb->query("SELECT * FROM `chat_message` WHERE to_user_id ='{$self_id}'
        ORDER BY `send_timestamp` LIMIT 0,1");
    $result = $udb->fetch_assoc();
    $udb->query("DELETE FROM `chat_message` WHERE id='{$result["id"]}';");
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
        
        //print_r($self_userinfo);
        //print_r($from_userinfo);
        
        //$att_services = new ATTServices();
        $google_services = new GoogleServices();
        //$xunfei_services = new XunfeiServices($xunfei_bin_dir);
        //$microsoft_services = new MicrosoftServices();
        
//        if ($from_userinfo["language"] == "zh_CN")
//        {
//            $from_text = $xunfei_services->speechToText($result["sound_filename"]);
//            if ($from_text == "")
//                $from_text = "不可识别的音频";
//        }elseif($from_userinfo["language"] == "en")
//        {
//            $from_text = $google_services->speechToText($result["sound_filename"]);
//            if ($from_text == "")
//                $from_text = "unrecognized audio";
//        }else
//        {
//            echo "Unknown language from";
//        }
        //$from_userinfo
        //echo "from_test:".$from_text."  time".intval(microtime($get_as_float=true)*1000)."<br/>";
        // translate
        $from_text = $result['string_content'];
//        $to_text = $microsoft_services->translate($from_text, $from_userinfo["language"],
//                $self_userinfo["language"]);
        $to_text = $google_services->translate($from_text, $from_userinfo["language"],
                $self_userinfo["language"]);
        
        echo $to_text;
        
        //echo $to_text."  time:".intval(microtime($get_as_float=true)*1000)."<br/>";
        // text to speech
//        $file_info = pathinfo($result["sound_filename"]);
//        $fn_before_conv = $file_info['dirname'].DIRECTORY_SEPARATOR .
//                $file_info['filename']."_done.wav";
//        $fn_after_conv = $file_info['dirname'].DIRECTORY_SEPARATOR .
//                $file_info['filename']."_done.acc";
        
        
//        if ($self_userinfo["language"] == "zh_CN")
//        {
//            $xunfei_services->textToSpeech($to_text, $fn_before_conv);
//        }elseif($self_userinfo["language"] == "en")
//        {
//            $att_services->textToSpeech($to_text, $fn_before_conv);
//        }else
//        {
//            echo "Unknown language to";
//        }
        //echo "finish  ".intval(microtime($get_as_float=true)*1000)."<br/>";
        
        // convert to acc
        //shell_exec("ffmpeg -i {$fn_before_conv}  -acodec libfaac {$fn_after_conv}");
//        $fn_after_conv = $fn_before_conv;
//        
//        if (file_exists($fn_after_conv))  
//        {
//            
//            header('Content-Description: File Transfer');     
//            header('Content-Type: application/octet-stream');     
//            header('Content-Disposition: attachment; filename='.basename($fn_after_conv));     
//            header('Content-Transfer-Encoding: binary');     
//            header('Expires: 0');     
//            header('Cache-Control: must-revalidate, post-check=0, pre-check=0');     
//            header('Pragma: public');     
//            header('Content-Length: ' . filesize($fn_after_conv));     
//            ob_clean();   //重要的就是这个函数的调用， 清空但不关闭输出缓存， 否则下载的文件头两个字符会是0a  
//            flush();     
//            readfile($fn_after_conv);   // 输出文件内容
//            
//            unlink($result["sound_filename"]);
//            unlink($fn_before_conv);
//            
//            
//            //unlink($fn_after_conv);
//            
//            //$udb->query("DELETE FROM `chat_message` WHERE id='{$result["id"]}';");
//            
//        }else{
//            echo "File not exist";
//        }
    }

}

?>