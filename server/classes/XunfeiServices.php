<?php

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 * Description of XunfeiService
 *
 * @author pswzyu
 */
require_once __DIR__.'/IServices.php';

class XunfeiServices extends IServices {
    //put your code here
    var $bin_dir;
    public function __construct($xunfei_bin_dir) {
        $this->bin_dir = $xunfei_bin_dir;
    }
    public function speechToText($fname)
    {
        // Translate file
        $cmd = "LD_LIBRARY_PATH=LD_LIBRARY_PATH:{$this->bin_dir} ".
            "{$this->bin_dir}/iatdemo {$fname}";
        $response = shell_exec($cmd);
        preg_match('/========(.*?)========/', $response, $matches);
        return $matches[1];
    }
    public function translate($input_str, $input_lang, $output_lang)
    {
        return "";
    }
    public function textToSpeech($input_str, $fname)
    {
        $cmd = "LD_LIBRARY_PATH=LD_LIBRARY_PATH:{$this->bin_dir} ".
            "{$this->bin_dir}/ttsdemo {$fname} {$input_str}";
        $response = shell_exec($cmd);
        return "";
    }
}
