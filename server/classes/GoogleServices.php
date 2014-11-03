<?php

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 * Description of GoogleServices
 *
 * @author pswzyu
 */
require_once __DIR__.'/IServices.php';

class GoogleServices extends IServices {
    //put your code here
    
    function curl($url,$params = array(),$is_coockie_set = false)
    {

        if(!$is_coockie_set){
            /* STEP 1. let’s create a cookie file */
            $ckfile = tempnam ("/tmp", "CURLCOOKIE");

            /* STEP 2. visit the homepage to set the cookie properly */
            $ch = curl_init ($url);
            curl_setopt ($ch, CURLOPT_COOKIEJAR, $ckfile);
            curl_setopt ($ch, CURLOPT_RETURNTRANSFER, true);
            $output = curl_exec ($ch);
        }

        $str = ''; $str_arr= array();
        foreach($params as $key => $value)
        {
        $str_arr[] = urlencode($key)."=".urlencode($value);
        }
        if(!empty($str_arr))
        $str = '?'.implode('&',$str_arr);

        /* STEP 3. visit cookiepage.php */

        $Url = $url.$str;

        $ch = curl_init ($Url);
        curl_setopt ($ch, CURLOPT_COOKIEFILE, $ckfile);
        curl_setopt ($ch, CURLOPT_RETURNTRANSFER, true);

        $output = curl_exec ($ch);
        unlink($ckfile);
        return $output;
    }
    
    public function speechToText($fname)
    {
        //echo $fname;
        $reply = shell_exec("curl -X POST --data-binary @'{$fname}' --header 'Content-Type: audio/l16; rate=16000;' 'https://www.google.com/speech-api/v2/recognize?output=json&lang=en-us&key=AIzaSyBOti4mM-6x9WDnZIjIeyEU21OpBXqWBgw'");
        //echo $reply;
        $two_json = explode("}\n{", $reply);
        if(count($two_json) < 2)
        {
            return "";
        }else{
            $first_json = $two_json[0]."}";
            $second_json = "{".$two_json[1];
            $best = json_decode($first_json);
            $alter = json_decode($second_json);

            // dont know what's the first portion of the string do
    //        print_r($best->result);
    //        print_r($alter->result[0]->alternative[0]->transcript);

            return $alter->result[0]->alternative[0]->transcript;
        }
    }

    public function translate($input_str, $input_lang, $output_lang)
    {
        return $this->doTranslate($input_str, $input_lang.",".$output_lang);
    }
    function doTranslate($word,$conversion = 'zh_CN,en')
    {
            $word = str_replace("\"",",",$word);
            $word = urlencode($word);
            $arr_langs = explode(',', $conversion);
            $url = "http://translate.google.com/translate_a/t?client=t&text=$word&hl=".$arr_langs[1]."&sl=".$arr_langs[0]."&tl=".$arr_langs[1]."&ie=UTF-8&oe=UTF-8&multires=1&otf=1&pc=1&trs=1&ssel=3&tsel=6&sc=1";

            $name_en = $this->curl($url);
            //print_r($name_en);
            preg_match('/\[\[.*?\]\]/', $name_en, $matches);
            $whole_sentence = "";
            $parts1 = explode("],[",$matches[0]);
            foreach($parts1 as $key=>$value)
            {
                $parts2 = explode("\"",$value);
                $whole_sentence.= $parts2[1];
            }

            return $whole_sentence;
    }
}
