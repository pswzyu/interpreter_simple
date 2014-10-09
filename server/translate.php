<?php
 
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
 
function Translate($word,$conversion = 'zh_CN,en')
{
	$word = str_replace("\"",",",$word);
	$word = urlencode($word);
	$arr_langs = explode(',', $conversion);
	$url = "http://translate.google.com/translate_a/t?client=t&text=$word&hl=".$arr_langs[1]."&sl=".$arr_langs[0]."&tl=".$arr_langs[1]."&ie=UTF-8&oe=UTF-8&multires=1&otf=1&pc=1&trs=1&ssel=3&tsel=6&sc=1";
	 
	$name_en = curl($url);
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
?>
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8"/>
</head>
<body>
<?php

echo  Translate('','zh_CN,en');
?>
</body>
</html>
