<?php

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 * Description of Services
 *
 * @author pswzyu
 */
abstract class IServices {
    //put your code here
    public function speechToText($fname)
    {
        return "";
    }
    public function translate($input_str, $input_lang, $output_lang)
    {
        return "";
    }
    public function textToSpeech($input_str, $fname)
    {
        return "";
    }
}
