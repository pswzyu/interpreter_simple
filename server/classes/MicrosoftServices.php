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
require_once(FROOT.'lib/php/translate/class/ServicesJSON.class.php');
require_once(FROOT.'lib/php/translate/class/MicrosoftTranslator.class.php');

class MicrosoftServices extends IServices {
    //put your code here
    private $translator;
    public function __construct() {
       $this->translator = new MicrosoftTranslator(ACCOUNT_KEY);
    }

    public function translate($input_str, $input_lang, $output_lang)
    {
        $this->translator->translate($this->convertLangCode($input_lang),
                $this->convertLangCode($output_lang), $input_str);
        return $this->translator->response->parsed_result;
    }
    
    private function convertLangCode($ori_lang)
    {
        if ($ori_lang == "en_us") return "en";
        if ($ori_lang == "zh_CN") return "zh-CHS";
    }
    
}
